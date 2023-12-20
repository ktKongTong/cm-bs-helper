package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.ui.viewmodel.TabType

sealed class BeatSaverUIEvent : UIEvent() {
    data class OnMultiSelectMap(val map: IMap) : BeatSaverUIEvent()

    data class ChangeMultiSelectMode(val checked: Boolean) : BeatSaverUIEvent()

    data class ChangeTargetPlaylist(val playlist: IPlaylist?) : BeatSaverUIEvent()

    data class OnSelectBSPlaylist(val playlist: IPlaylist) : BeatSaverUIEvent()

    data class OnSelectBSMap(val map: IMap) : BeatSaverUIEvent()

    data class OnSelectedBSMapper(val mapperId: Int) : BeatSaverUIEvent()

    data class MapTapped(val map: IMap) : BeatSaverUIEvent()

    data class MapLongTapped(val map: IMap) : BeatSaverUIEvent()

    data object OnExitBSMapper : BeatSaverUIEvent()

    data object OnExitSelectedBSMap : BeatSaverUIEvent()

    data object OnExitSelectedBSPlaylist : BeatSaverUIEvent()

    data class UpdateMapFilterParam(val mapQueryState: MapFilterParam) : BeatSaverUIEvent()

    data class SearchMapWithFilter(val mapQueryState: MapFilterParam) : BeatSaverUIEvent()

    data class UpdatePlaylistFilterParam(val playlistQueryState: PlaylistFilterParam) : BeatSaverUIEvent()

    data class SearchPlaylistWithFilter(val playlistQueryState: PlaylistFilterParam? = null) : BeatSaverUIEvent()

    data class SwitchTab(val tabType: TabType) : BeatSaverUIEvent()

    data class DownloadMap(val bsMap: IMap) : BeatSaverUIEvent()

    data class MultiDownload(val targetPlaylist: IPlaylist) : BeatSaverUIEvent()

    data class PauseDownload(val downloadTask: IDownloadTask) : BeatSaverUIEvent()

    data class ResumeDownload(val downloadTask: IDownloadTask) : BeatSaverUIEvent()

    data class DownloadPlaylist(val playlist: IPlaylist) : BeatSaverUIEvent()

    data class PlayPreviewMusicSegment(val map: IMap) : BeatSaverUIEvent()
}