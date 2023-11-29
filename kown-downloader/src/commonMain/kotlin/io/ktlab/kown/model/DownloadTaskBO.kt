package io.ktlab.kown.model

import io.ktlab.kown.Constants
import io.ktlab.kown.getUniqueId
import kotlinx.coroutines.Job
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

enum class RenameStrategy {
    DEFAULT,
    APPEND_INDEX
}

fun DownloadTaskBO.reset() {
    speed = 0
    downloadedBytes = 0
    estimatedTime = 0
}

data class DownloadTaskBO(
    val taskId: String,
    val title: String,
    val tag: String?,
    val headers: Map<String, String>?,

    var status: TaskStatus = TaskStatus.Initial,
    var url: String = "",
    var dirPath: String = "",
    var renameAble: Boolean = false,
    var renameStrategy: RenameStrategy = RenameStrategy.DEFAULT,
    var filename: String = "",
    var totalBytes: Long = 0,
    var downloadedBytes: Long = 0,
    var lastModifiedAt: Long = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    var estimatedTime: Long = 0,
    var speed: Long = 0,
    val relateEntityId: String? = null,
    internal var eTag: String = "",
    internal var readTimeOut: Long = 0,
    internal var connectTimeOut: Long = 0,
    internal var downloadListener: DownloadListener,
) {
    internal lateinit var job: Job


//    private val backupDownloadTaskBO: DownloadTaskBO by lazy { this.copy() }

    fun copyTask():DownloadTaskBO {
        return this.copy(
            status = status,
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            lastModifiedAt = lastModifiedAt,
            estimatedTime = estimatedTime,
            speed = speed,
        )
    }


    fun regenTask(listener: DownloadListener? = null):DownloadTaskBO {
        val copied = this.copy(
            status = TaskStatus.Queued(status),
            downloadedBytes = 0,
            lastModifiedAt = 0,
            estimatedTime = 0,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            speed = 0,
        )
        listener?.let { copied.downloadListener = it }
        return copied
    }

    data class Builder(
        private val url: String, private val dirPath: String, private val filename: String
    ){
        private var tag: String? = null
        private var downloadListener: DownloadListener? = null
        private var headers: Map<String, String>? = null
        private var readTimeOut: Long = Constants.DEFAULT_READ_TIMEOUT
        private var connectTimeOut: Long = Constants.DEFAULT_CONNECT_TIMEOUT
        private var userAgent: String = Constants.DEFAULT_USER_AGENT
        private var relateEntityId : String? = null
        private var title: String = ""
        /**
         * set download request Tag, could be used to operate all requests with the same tag
         */
        fun setTag(tag: String) = apply {
            this.tag = tag
        }

        fun setDownloadListener(downloadListener: DownloadListener) = apply {
            this.downloadListener = downloadListener
        }

        /**
         * set download request headers if needed, some headers will be added automatically
         */
        fun headers(headers: Map<String, String>) = apply {
            this.headers = headers
        }

        /**
         * set download request read timeout
         */
        fun readTimeout(timeout: Long) = apply {
            this.readTimeOut = timeout
        }

        fun setTitle(title: String) = apply {
            this.title = title
        }

        /**
         * set download request connect timeout
         */
        fun connectTimeout(timeout: Long) = apply {
            this.connectTimeOut = timeout
        }
        fun setRelateEntityId(relateEntityId: String) = apply {
            this.relateEntityId = relateEntityId
        }
        fun build(): DownloadTaskBO {
            return DownloadTaskBO(
                url = url,
                tag = tag,
                downloadListener = downloadListener?: DownloadListener.DEFAULT,
                headers = headers,
                dirPath = dirPath,
                taskId = getUniqueId(url, dirPath, filename),
                title = title,
                filename = filename,
                readTimeOut = readTimeOut,
                connectTimeOut = connectTimeOut,
                relateEntityId = relateEntityId,
            )
        }
    }
}