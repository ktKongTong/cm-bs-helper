package io.ktlab.kown

import io.ktlab.kown.model.DownloadListener
import io.ktlab.kown.model.DownloadTaskBO
import io.ktlab.kown.model.TaskStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlin.concurrent.timer


class Kownloader(private val config: KownConfig) {

    private val dbScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + CoroutineExceptionHandler { coroutineContext, throwable ->  throwable.printStackTrace() })

    private val dbHelper = config.dbHelper
    private lateinit var  taskQueueFlow: MutableStateFlow<List<DownloadTaskBO>>
    private val dispatcher = DownloadTaskDispatcher(config)

    private var downloadingCnt = 0

    private val mutex = Mutex()

    private val onJobCompleteAction = {
        runBlockingWithLock{
            downloadingCnt--
        }
    }

    init {
        runBlocking {
            dbScope.async{
                dbHelper.getAllDownloadTask().let {tasks ->
                    taskQueueFlow = MutableStateFlow(tasks)
                }
            }.await()
            val job = scope.launch {
                taskQueueFlow.collect {
                        mutex.lock()
                        if (downloadingCnt > config.concurrentDownloads) {
                            // pause task when downloadingCnt > max
                            it.firstOrNull { it.status == TaskStatus.Running }?.let {
                                pauseTasks(listOf(it))
                            }
                        }else if (downloadingCnt < config.concurrentDownloads && it.any { it.status is TaskStatus.Queued }) {
                            val task = it.first { it.status is TaskStatus.Queued }
                            task.status = TaskStatus.Running
                            downloadingCnt++
                            dispatcher.download(task,onJobCompleteAction)
                        }
                        mutex.unlock()
                    }
            }
            timer("kownSyncTask", false, 0L, 1000L) {
                if (job.isCompleted) {
                    cancel()
                }
                if (downloadingCnt != 0) {
                    syncTask(taskQueueFlow.value)
                    taskQueueFlow.update { it.reversed() }
                }
            }
        }
    }

    fun  getStatus(taskId: String):TaskStatus {
        return taskQueueFlow.value.firstOrNull { it.taskId == taskId }?.status ?: TaskStatus.Unknown
    }

    fun setMaxConcurrentDownloads(max: Int) {
        assert(max > 0) { "max must be greater than 0" }
        config.concurrentDownloads = max
    }

    private fun runBlockingWithLock(block: () -> Unit) = runBlocking {
        mutex.lock()
        block()
        mutex.unlock()
    }
    private fun blockingOpsById(taskId: String, block : (DownloadTaskBO) -> Unit) = runBlockingWithLock {
        taskQueueFlow.value.firstOrNull { it.taskId == taskId }?.let { block(it) }
    }
    private fun blockingOpsByTag(tag: String, block : (List<DownloadTaskBO>) -> Unit) = runBlockingWithLock { block(taskQueueFlow.value.filter { it.tag == tag }) }
    private fun blockingOpsAll(block : (List<DownloadTaskBO>) -> Unit) = runBlockingWithLock { block(taskQueueFlow.value) }


    fun enqueue(task: DownloadTaskBO, listener: DownloadListener = DownloadListener()) {
        task.downloadListener = listener
        dbScope.launch {
            dbHelper.insert(task)
        }
        runBlocking {
            mutex.lock()
            task.status = TaskStatus.Queued(task.status)
//            updateTaskFlow {
//                it.filter { it.taskId != task.taskId } + task
//            }
            taskQueueFlow.update { it.filter { it.taskId != task.taskId } + task }
            mutex.unlock()
        }
    }
    fun enqueue(tasks: List<DownloadTaskBO>) {
        val mappedTasks = tasks.map {
            it.status = TaskStatus.Queued(it.status)
            it
        }
        dbScope.launch {
            dbHelper.batchInsert(mappedTasks)
        }
        taskQueueFlow.update { it + mappedTasks }
    }
    fun enqueue(tasks: List<DownloadTaskBO>, listener: DownloadListener? = null) {
        val mappedTasks = tasks.map {
            // assert(it.status !is TaskStatus.Running) { "task is running" }
            if (listener != null) {
                it.downloadListener = listener
            }
            it.status = TaskStatus.Queued(it.status)
            it
        }
        dbScope.launch {
            dbHelper.batchInsert(mappedTasks)
        }
            taskQueueFlow.update { it + mappedTasks }
    }



    fun retryById(taskId: String, listener: DownloadListener? = null) = blockingOpsById(taskId) { retryTasks(it, listener) }
    fun retryByTag(tag: String, listener: DownloadListener? = null) = blockingOpsByTag(tag){
        retryTasks(it)
    }

    fun retryTasks(tasks: List<DownloadTaskBO>, listener: DownloadListener? = null) {
        val mappedTasks = tasks.filter { it.status is TaskStatus.Failed }.map { it.regenTask(listener) }
        dbScope.launch {
            dbHelper.batchUpdate(mappedTasks)
        }
        taskQueueFlow.update {
            val filtered = it.filter { it.taskId !in mappedTasks.map { it.taskId } }
            filtered + mappedTasks
        }
    }

    fun cancelById(taskId: String) = blockingOpsById(taskId) { cancelTasks(listOf(it)) }
    fun cancelByTag(tag: String)  = blockingOpsByTag(tag) { cancelTasks(it) }
    fun cancelAll() = blockingOpsAll { cancelTasks(it) }

    private fun cancelTasks(tasks: List<DownloadTaskBO>) {
        val filteredTask = tasks
            .map {
                when (it.status) {
                    is TaskStatus.Queued -> {
                        it.status = TaskStatus.Failed("task cancelled", (it.status as TaskStatus.Queued).lastStatus)
                        it
                    }
                    is TaskStatus.Running, is TaskStatus.PostProcessing -> {
                        dispatcher.cancel(it)
                        it
                    }
                    is TaskStatus.Paused -> {
                        it.status = TaskStatus.Failed("task cancelled", (it.status as TaskStatus.Paused).lastStatus)
                        it
                    }
                    else -> { it }
                }
            }
        syncTask(filteredTask)
        taskQueueFlow.update {
            it.filter { task -> filteredTask.none { it.taskId == task.taskId } } + filteredTask
        }
    }

    private fun updateTaskFlow(block: (List<DownloadTaskBO>) -> List<DownloadTaskBO>) {
        taskQueueFlow.update {
            block(it
//                .filter { it.tag != "kown.guard" }
            )
//            + genGuardTask()
        }
    }

    fun pauseById(taskId: String) = blockingOpsById(taskId) { pauseTasks(listOf(it)) }
    fun pauseByTag(tag: String) = blockingOpsByTag (tag) { pauseTasks(it) }
    fun pauseAll()  = blockingOpsAll { pauseTasks(it) }

    private fun pauseTasks(tasks: List<DownloadTaskBO>) {
        val filteredTask = tasks
            .filter { it.status is TaskStatus.Queued || it.status is TaskStatus.Running || it.status is TaskStatus.PostProcessing }
            .map {
                when (it.status) {
                    is TaskStatus.Queued -> {
                        it.status = TaskStatus.Paused((it.status as TaskStatus.Queued).lastStatus)
                        it
                    }
                    is TaskStatus.Running, is TaskStatus.PostProcessing -> {
                        dispatcher.pause(it)
                        it
                    }
                    else -> { it }
                }
            }
        syncTask(filteredTask)
    }

    fun resumeById(taskId: String, listener: DownloadListener? = null) = blockingOpsById(taskId) { resumeTasks(listOf(it), listener) }
    fun resumeByTag(tag: String, listener: DownloadListener? = null) = blockingOpsByTag(tag) { resumeTasks(it, listener) }
    fun resumeAll(listener: DownloadListener? = null) = blockingOpsAll { resumeTasks(it, listener) }

    private fun resumeTasks(tasks: List<DownloadTaskBO>, listener: DownloadListener? = null) {
        val mappedTasks = tasks.filter { it.status is TaskStatus.Paused }.map {
            if (listener != null) {
                it.downloadListener = listener
            }
            it.status = TaskStatus.Queued(it.status)
            it
        }
        dbScope.launch {
            dbHelper.batchUpdate(mappedTasks)
        }
        taskQueueFlow.update {
            val notUpdate = it.filter { it.taskId !in mappedTasks.map { it.taskId } }
            val res = notUpdate + mappedTasks
            return@update res
        }
    }



    fun removeById(taskId: String) = blockingOpsById(taskId) { removeTasks(listOf(it)) }
    fun removeByTag(tag: String) = blockingOpsByTag(tag) { removeTasks(it) }
    fun removeAll() = blockingOpsAll { removeTasks(it) }

    fun removeTasks(tasks: List<DownloadTaskBO>) {
        val filteredTask = tasks.filter { it.status !is TaskStatus.Running && it.status !is TaskStatus.PostProcessing }
        syncTask(filteredTask)
        taskQueueFlow.update { it.filter { task -> filteredTask.none { it.taskId == task.taskId } } }
    }

    private fun syncTask(tasks: List<DownloadTaskBO>) {
        dbScope.launch{
            dbHelper.batchUpdate(tasks)
        }
    }

    fun clear() {
        runBlocking {
            cancelAll()
            mutex.lock()
            dbScope.async {
                dbHelper.removeAll()
            }.await()
            taskQueueFlow.update { listOf() }
            mutex.unlock()
        }
    }

    fun getAllDownloadTaskFlow(): Flow<List<DownloadTaskBO>> = taskQueueFlow


    fun newRequestBuilder(url: String, dirPath: String, filename: String): DownloadTaskBO.Builder {
        return DownloadTaskBO.Builder(url, dirPath, filename)
            .readTimeout(Constants.DEFAULT_READ_TIMEOUT)
            .connectTimeout(Constants.DEFAULT_CONNECT_TIMEOUT)
    }

    companion object {
        fun new(): KownloaderBuilder {
            return KownloaderBuilder()
        }
    }
}

