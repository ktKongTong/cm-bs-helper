package io.ktlab.bshelper.ui.event

import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.model.enums.SortType

sealed class HomeUIEvent : UIEvent() {
    data class PlaylistTapped(val playlistId: String) : HomeUIEvent()

    data class MapTapped(val mapId: String) : HomeUIEvent()

    data class ChangeMapListSortRule(val sortRule: Pair<SortKey, SortType>) : HomeUIEvent()

    data class MapMultiSelected(val map: IMap) : HomeUIEvent()

    data class ChangeMultiSelectMode(val checked: Boolean) : HomeUIEvent()

    data class MultiDeleteAction(val mapSet: Set<IMap>) : HomeUIEvent()

    data class MultiMoveAction(val mapSet: Set<IMap>, val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class MultiMapFullChecked(val mapMap: Map<String, IMap>) : HomeUIEvent()

    data class MultiMapOppoChecked(val mapMap: Map<String, IMap>) : HomeUIEvent()

    data class PlayPreviewMusicSegment(val map: IMap) : HomeUIEvent()

    data class CreateNewPlaylist(val name: String, val description: String? = null, val customTags: String? = null) : HomeUIEvent()

    data class MergePlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class BuildBPList(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class SharePlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class ExportPlaylistAsKey(val playlist: IPlaylist) : HomeUIEvent()

    data class ExportPlaylistAsBPList(val playlist: IPlaylist, val targetPath:String) : HomeUIEvent()

    data class DeletePlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class EditPlaylist(val targetPlaylist: IPlaylist, val resPlaylist: FSPlaylist) : HomeUIEvent()

    data class SyncPlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class ImportPlaylist(val key: String, val targetPlaylist: IPlaylist) : HomeUIEvent()
}