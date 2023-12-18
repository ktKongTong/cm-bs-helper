package io.ktlab.bshelper.model.download

import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.kown.model.DownloadTaskVO

sealed interface IDownloadTask {
    val taskType: DownloadTaskType
    val targetPlaylist: IPlaylist

    data class MapDownloadTask(
        val downloadTaskModel: DownloadTaskVO,
        var relateMap: IMap,
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Map,
    ) : IDownloadTask {
        fun getProgress(): Float {
            if (downloadTaskModel.totalBytes == 0L) return 0f
            return downloadTaskModel.downloadedBytes.toFloat() / downloadTaskModel.totalBytes.toFloat()
        }
    }

    data class BatchDownloadTask(
        val tag: String,
        val taskList: List<MapDownloadTask> = listOf(),
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Batch,
    ) : IDownloadTask

    data class PlaylistDownloadTask(
        var playlist: IPlaylist,
        val tag: String,
        val taskList: List<MapDownloadTask> = listOf(),
        override val targetPlaylist: IPlaylist,
        override val taskType: DownloadTaskType = DownloadTaskType.Playlist,
    ) : IDownloadTask
}

enum class DownloadTaskType() {
    Map(),
    Batch(),
    Playlist(),
}
