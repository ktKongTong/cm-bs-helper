package io.ktlab.bshelper.ui.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.FSMapRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.UserPreference
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.model.enums.SortType
import io.ktlab.bshelper.model.errorMsg
import io.ktlab.bshelper.model.successOr
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import okio.Path.Companion.toPath

data class HomeViewModelState(
    val playlists: List<IPlaylist> = emptyList(),
    val selectedPlaylistId: String? = null,
    val userPreferenceState: UserPreference,
    val selectedMapId: String? = null,
    val isPlaylistOpen: Boolean = false,
    val mapListState: MapListState,
    val isLoading: Boolean = false,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val searchInput: String = "",
) {
    fun toUiState(): HomeUiState {
//        if (playlists.isEmpty()) {
//            return HomeUiState.Empty(
//                isLoading = isLoading,
//                snackBarMessages = snackBarMessages,
//                searchInput = searchInput
//            )
//        }
        return HomeUiState.Playlist(
            playlists = playlists,
            selectedPlaylist = playlists.find { it.id == selectedPlaylistId },
            isPlaylistOpen = isPlaylistOpen,
            mapListState = mapListState,
            isLoading = isLoading,
            searchInput = searchInput,
            snackBarMessages = snackBarMessages,
        )
    }
}

data class MapListState(
    val maps: List<IMap> = emptyList(),
    val mapFlow: Flow<Result<List<IMap>>>? = null,
    val selectedMap: IMap? = null,
    val isMapOpen: Boolean = false,
    val isMapMultiSelectMode: Boolean,
    val multiSelectedMapHashMap: Map<String, IMap> = emptyMap(),
    val sortRule: Pair<SortKey, SortType> = Pair(SortKey.DEFAULT, SortType.ASC),
    val searchInput: String = "",
    val isLoading: Boolean = false,
)

sealed interface HomeUiState {
    val isLoading: Boolean
    val snackBarMessages: List<SnackBarMessage>
    val searchInput: String

    data class Playlist(
        val playlists: List<IPlaylist> = emptyList(),
        val selectedPlaylist: IPlaylist? = null,
        val isPlaylistOpen: Boolean,
        val mapListState: MapListState,
        override val isLoading: Boolean,
        override val snackBarMessages: List<SnackBarMessage> = emptyList(),
        override val searchInput: String,
    ) : HomeUiState {
        fun isMapEmpty(): Boolean {
            return !this.isPlaylistOpen || this.selectedPlaylist == null
        }
    }
}

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

    data class ExportPlaylistAsBPList(val playlist: IPlaylist,val targetPath:String) : HomeUIEvent()

    data class DeletePlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class EditPlaylist(val targetPlaylist: IPlaylist, val resPlaylist: FSPlaylist) : HomeUIEvent()

    data class SyncPlaylist(val targetPlaylist: IPlaylist) : HomeUIEvent()

    data class ImportPlaylist(val key: String, val targetPlaylist: IPlaylist) : HomeUIEvent()
//    data class DividePlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
}

class HomeViewModel(
    private val globalViewModel: GlobalViewModel,
    private val playlistRepository: PlaylistRepository,
    private val mapRepository: FSMapRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val downloaderRepository: DownloaderRepository,
) :
    ViewModel() {
    private val localViewModelScope = viewModelScope

    private val viewModelState =
        MutableStateFlow(
            HomeViewModelState(
                isLoading = true,
                isPlaylistOpen = false,
                userPreferenceState = UserPreference.getDefaultUserPreference(),
                mapListState =
                    MapListState(
                        isMapOpen = false,
                        isLoading = false,
                        mapFlow = null,
                        isMapMultiSelectMode = false,
                        multiSelectedMapHashMap = emptyMap(),
                        searchInput = "",
                    ),
            ),
        )
    val uiState =
        viewModelState
            .map(HomeViewModelState::toUiState)
            .stateIn(
                localViewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )

    init {
        refreshPlayLists()
    }

    fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is GlobalUIEvent.ShowSnackBar -> {
                globalViewModel.showSnackBar(msg = event.message)
            }
            is HomeUIEvent.DeletePlaylist -> {
                deletePlaylist(event.targetPlaylist)
            }
            is HomeUIEvent.EditPlaylist -> {
//                viewModelScope.launch {
//                    playlistRepository.updatePlaylist(event.resPlaylist)
//                }
            }
            is HomeUIEvent.SyncPlaylist -> {
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.scanSinglePlaylist(event.targetPlaylist.getTargetPath())
                }
            }
            is HomeUIEvent.PlaylistTapped -> {
                onPlaylistTapped(event.playlistId)
            }
            is HomeUIEvent.MapTapped -> {}
            is HomeUIEvent.ChangeMapListSortRule -> {
                onChangeMapListSortRule(event.sortRule)
            }
            is HomeUIEvent.ChangeMultiSelectMode -> {
                onMapMultiSelectedModeChecked(event.checked)
            }
            is HomeUIEvent.MultiMapFullChecked -> {
                onMultiMapFullChecked(event.mapMap)
            }
            is HomeUIEvent.MultiMapOppoChecked -> {
                onMultiMapOppoChecked(event.mapMap)
            }
            is HomeUIEvent.MapMultiSelected -> {
                onMapMultiSelected(event.map)
            }
            is HomeUIEvent.MultiDeleteAction -> {
                onMultiDeleteAction(event.mapSet)
            }
            is HomeUIEvent.MultiMoveAction -> {
                onMultiMoveAction(event.mapSet, event.targetPlaylist)
            }
            is HomeUIEvent.ExportPlaylistAsKey -> {
                onExportPlaylistAsKey(event.playlist)
            }

            is HomeUIEvent.ImportPlaylist -> {
                onImportPlaylist(event.key, event.targetPlaylist)
            }
            is HomeUIEvent.ExportPlaylistAsBPList -> {
                if (event.targetPath.isBlank()) {
                    globalViewModel.showSnackBar(msg = "请先选择文件夹")
                    return
                }
                onExportPlaylistAsBPList(event.playlist,event.targetPath)
            }
            is HomeUIEvent.PlayPreviewMusicSegment -> {
                viewModelScope.launch(Dispatchers.IO) {
                    globalViewModel.playMedia(
                        IMedia.MapAudioPreview(
                            id = event.map.getID(),
                            url = event.map.getMusicPreviewURL(),
                            avatarUrl = event.map.getAvatar(),
                        ),
                    )
                }
            }
            is GlobalUIEvent -> {
                globalViewModel.dispatchUiEvents(event)
            }
            is HomeUIEvent.CreateNewPlaylist -> {
                onCreateNewPlaylist(event.name, event.description, event.customTags)
            }
        }
    }

    fun copyToClipboard(
        text: String,
        label: String = "",
    ) {
//        clipboardManager.setPrimaryClip(ClipData.newPlainText(label,text))
    }

    private fun refreshPlayLists(managerDir: String = "") {
        val managerPath = managerDir.ifEmpty { viewModelState.value.userPreferenceState.currentManageDir }
        viewModelState.update { it.copy(isLoading = true) }
        localViewModelScope.launch {
            playlistRepository
                .let {
                    if (managerPath.isEmpty()) {
                        it.getAllPlaylist()
                    } else {
                        it.getAllPlaylistByManageDir(managerPath)
                    }
                }
                .flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            viewModelState.update { vmState ->
                                vmState.copy(playlists = it.data, isLoading = false)
                            }
                        }
                        is Result.Error -> {
                            globalViewModel.showSnackBar(
                                msg = "error loading playlists",
                                actionLabel = "retry",
                                action = { refreshPlayLists(managerPath) },
                            )
                        }
                    }
                }
        }
    }

    private fun onPlaylistTapped(playlistId: String) {
        if (viewModelState.value.selectedPlaylistId != playlistId) {
            viewModelState.update {
                it.copy(
                    isPlaylistOpen = true,
                    selectedPlaylistId = playlistId,
                )
            }
            interactedWithPlaylistDetails()
        }
    }

    private fun interactedWithPlaylistDetails() =
        localViewModelScope.launch {
            val playlistId = viewModelState.value.selectedPlaylistId ?: return@launch
            val flow = mapRepository.getFlowMapsByPlaylistId(playlistId).flowOn(Dispatchers.IO)
            viewModelState.update {
                it.copy(
                    mapListState =
                        it.mapListState.copy(
                            isLoading = true,
                            selectedMap = null,
                            isMapOpen = false,
                            isMapMultiSelectMode = false,
                            multiSelectedMapHashMap = emptyMap(),
                            mapFlow = flow,
                        ),
                )
            }
        }

    private fun onExportPlaylistAsKey(playlist: IPlaylist) {
        viewModelScope.launch {
            val res = playlistRepository.exportPlaylistAsKey(playlist)
            if (res is Result.Success) {
                globalViewModel.showSnackBar(
                    msg = res.data,
                    actionLabel = "copy",
                    action = { globalViewModel.writeToClipboard(res.data) },
                    duration = SnackbarDuration.Long,
                )
            } else {
                globalViewModel.dispatchUiEvents(
                    GlobalUIEvent.ReportError(res.errorMsg(), "export playlist as key failed"),
                )
            }
        }
    }

    private fun onExportPlaylistAsBPList(playlist: IPlaylist, targetPath:String) {
        viewModelScope.launch {
            val res = playlistRepository.exportPlaylistAsBPList(playlist,targetPath.toPath())
            globalViewModel.showSnackBar(msg = res.successOr("export failed"))
        }
    }

    private fun onImportPlaylist(
        key: String,
        targetPlaylist: IPlaylist,
    ) {
        viewModelScope.launch {
            val res = playlistRepository.importPlaylistByKey(key, targetPlaylist)
            when (res) {
                is Result.Success -> {
                    globalViewModel.showSnackBar(msg = "将导入 ${res.data.size} 个 map 至 ${targetPlaylist.title}")
                    downloaderRepository.downloadMapByMapIds(targetPlaylist, res.data)
                }
                is Result.Error -> {
                    globalViewModel.showSnackBar(msg = "import failed")
                }
            }
        }
    }

    private fun onChangeMapListSortRule(sortRule: Pair<SortKey, SortType>) {
        viewModelState.update {
            it.copy(
                mapListState = it.mapListState.copy(sortRule = sortRule),
            )
        }
    }

    private fun deletePlaylist(playlist: IPlaylist) {
        viewModelScope.launch {
            playlistRepository.deletePlaylistById(playlist.id)
        }
    }

    private fun onCreateNewPlaylist(
        name: String,
        description: String?,
        customTags: String?,
    ) {
        viewModelScope.launch {
            playlistRepository.createNewPlaylist(name, description = description, customTags = customTags)
        }
    }

    private fun onMultiDeleteAction(mapSet: Set<IMap>) {
        viewModelScope.launch(Dispatchers.IO) {
            val mapToBeDelete = mapSet.map { (it as FSMapVO).fsMap }
            viewModelState.update {
                it.copy(
                    mapListState =
                        it.mapListState.copy(
                            multiSelectedMapHashMap = emptyMap(),
                            isMapMultiSelectMode = false,
                        ),
                )
            }
            mapRepository.deleteFSMapsByPath(viewModelState.value.selectedPlaylistId!!, mapToBeDelete)
        }
    }

    private fun onMultiMoveAction(
        mapSet: Set<IMap>,
        targetPlaylist: IPlaylist,
    ) {
        viewModelScope.launch {
            val mapToBeMoved = mapSet.toList().map { (it as FSMapVO).fsMap }
            viewModelState.update {
                it.copy(
                    mapListState =
                        it.mapListState.copy(
                            multiSelectedMapHashMap = emptyMap(),
                            isMapMultiSelectMode = false,
                        ),
                )
            }
            launch subroutine@{
                mapRepository
                    .moveFSMapsToPlaylist(targetPlaylist, mapToBeMoved)
                    .flowOn(Dispatchers.IO)
                    .collect {
                        if (it is Result.Success) {
                            globalViewModel.showSnackBar(
                                msg = "move ${mapToBeMoved.size} maps to ${targetPlaylist.title} succeed",
                            )
                        } else {
                            globalViewModel.showSnackBar(
                                msg = "move ${mapToBeMoved.size} maps to ${targetPlaylist.title} failed",
                            )
                        }
                    }
            }
        }
    }

    private fun onMapMultiSelectedModeChecked(isChecked: Boolean) {
        viewModelState.update {
            it.copy(
                mapListState =
                    it.mapListState.copy(
                        isMapMultiSelectMode = isChecked,
                        multiSelectedMapHashMap = emptyMap(),
                    ),
            )
        }
    }

    private fun onMultiMapOppoChecked(multiSelectedMapHashMap: Map<String, IMap>) {
        viewModelState.update { state ->
            state.copy(
                mapListState =
                    state.mapListState.copy(
                        multiSelectedMapHashMap = multiSelectedMapHashMap,
                    ),
            )
        }
    }

    private fun onMultiMapFullChecked(multiSelectedMapHashMap: Map<String, IMap>) {
        viewModelState.update { state ->
            state.copy(
                mapListState =
                    state.mapListState.copy(
                        multiSelectedMapHashMap = multiSelectedMapHashMap,
                    ),
            )
        }
    }

    /*
     * TODO:
     * problem: if first view the local map list,
     * the map list is getting online data.
     *
     * */
    private fun onMapMultiSelected(fsMap: IMap) {
        viewModelState.update {
            it.copy(
                mapListState =
                    it.mapListState.copy(
                        multiSelectedMapHashMap =
                            if (it.mapListState.multiSelectedMapHashMap.containsKey(fsMap.getID())) {
                                it.mapListState.multiSelectedMapHashMap - fsMap.getID()
                            } else {
                                it.mapListState.multiSelectedMapHashMap + Pair(fsMap.getID(), fsMap)
                            },
                    ),
            )
        }
    }
}
