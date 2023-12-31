package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.model.ImageSource
import io.ktlab.bshelper.model.ManageFolderBackup
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.enums.GameType

sealed class ToolboxUIEvent : UIEvent() {
    data class ScanPlaylist(val dirPath: String,val gameType: GameType) : ToolboxUIEvent()

    data object ClearScanState : ToolboxUIEvent()

    data object ClearLocalData : ToolboxUIEvent()
    data object HealthyCheck : ToolboxUIEvent()

    data object DeleteAllDownloadTasks : ToolboxUIEvent()

    data class RemoveDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class ResumeDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class PauseDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class CancelDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class RetryDownloadMap(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class UpdateThemeColor(val color: Long) : ToolboxUIEvent()

    data class UpdateImageSource(val type: ImageSource, val source: String?=null) : ToolboxUIEvent()

    data class BackUpManageFolder(val manageFolder: SManageFolder) : ToolboxUIEvent()
    data class RestoreManageFolder(val backup: ManageFolderBackup) : ToolboxUIEvent()
}