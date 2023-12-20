package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.model.ImageSource
import io.ktlab.bshelper.model.download.IDownloadTask

sealed class ToolboxUIEvent : UIEvent() {
    data class ScanPlaylist(val dirPath: String) : ToolboxUIEvent()

    data object ClearScanState : ToolboxUIEvent()

    data object ClearLocalData : ToolboxUIEvent()

    data object DeleteAllDownloadTasks : ToolboxUIEvent()

    data class RemoveDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class ResumeDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class PauseDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class CancelDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class RetryDownloadMap(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class UpdateManageDir(val path: String) : ToolboxUIEvent()

    data class UpdateThemeColor(val color: Long) : ToolboxUIEvent()

    data class UpdateImageSource(val type: ImageSource, val source: String?=null) : ToolboxUIEvent()
}