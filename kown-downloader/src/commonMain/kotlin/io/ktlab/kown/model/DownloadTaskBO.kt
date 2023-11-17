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
    var status: TaskStatus = TaskStatus.Queued(TaskStatus.Initial),
    var url: String = "",
    var eTag: String = "",
    var dirPath: String = "",
    var renameAble: Boolean = false,
    var renameStrategy: RenameStrategy = RenameStrategy.DEFAULT,
    var filename: String = "",
    internal var readTimeOut: Long = 0,
    internal var connectTimeOut: Long = 0,
    var totalBytes: Long = 0,
    var downloadedBytes: Long = 0,
    var lastModifiedAt: Long = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds(),
    var estimatedTime: Long = 0,
    var speed: Long = 0,
    internal var downloadListener: DownloadListener?,
    val relateEntityId: String? = null
) {
    internal lateinit var job: Job


    fun copyTask():DownloadTaskBO {
        val res = DownloadTaskBO(
            taskId = taskId,
            title = title,
            tag = tag,
            headers = headers,
            status = status,
            url = url,
            eTag = eTag,
            dirPath = dirPath,
            renameAble = renameAble,
            renameStrategy = renameStrategy,
            filename = filename,
            readTimeOut = readTimeOut,
            connectTimeOut = connectTimeOut,
            totalBytes = totalBytes,
            downloadedBytes = downloadedBytes,
            lastModifiedAt = lastModifiedAt,
            createdAt = createdAt,
            estimatedTime = estimatedTime,
            speed = speed,
            downloadListener = downloadListener,
            relateEntityId = relateEntityId
        )
//        res.job = job
        return res
    }

    fun regenTask():DownloadTaskBO {
        return DownloadTaskBO(
            taskId = taskId,
            title = title,
            tag = tag,
            headers = headers,
            status = TaskStatus.Queued(status),
            url = url,
            eTag = eTag,
            dirPath = dirPath,
            renameAble = renameAble,
            renameStrategy = renameStrategy,
            filename = filename,
            readTimeOut = readTimeOut,
            connectTimeOut = connectTimeOut,
            totalBytes = totalBytes,
            downloadedBytes = 0L,
            lastModifiedAt = lastModifiedAt,
            createdAt = Clock.System.now().toEpochMilliseconds(),
            estimatedTime = estimatedTime,
            speed = speed,
            downloadListener = downloadListener,
            relateEntityId = relateEntityId
        )
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
                downloadListener = downloadListener,
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