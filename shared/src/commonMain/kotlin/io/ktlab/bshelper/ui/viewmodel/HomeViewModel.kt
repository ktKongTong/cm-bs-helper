package io.ktlab.bshelper.ui.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.RuntimeEventFlow
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.FSMapRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.model.enums.SortType
import io.ktlab.bshelper.model.errorMsg
import io.ktlab.bshelper.model.successOr
import io.ktlab.bshelper.model.vo.FSMapVO
import io.ktlab.bshelper.model.vo.FSPlaylistVO
import io.ktlab.bshelper.ui.event.EventBus
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.HomeUIEvent
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
    val userPreferenceState: UserPreferenceV2,
    val selectedMapId: String? = null,
    val isPlaylistOpen: Boolean = false,
    val mapListState: MapListState,
    val isLoading: Boolean = false,
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
    val searchInput: String
    data class Playlist(
        val playlists: List<IPlaylist> = emptyList(),
        val selectedPlaylist: IPlaylist? = null,
        val isPlaylistOpen: Boolean,
        val mapListState: MapListState,
        override val isLoading: Boolean,
        override val searchInput: String,
    ) : HomeUiState {
        fun isMapEmpty(): Boolean {
            return !this.isPlaylistOpen || this.selectedPlaylist == null
        }
    }
}
private val logger = KotlinLogging.logger {}
class HomeViewModel(
    private val runtimeEventFlow: RuntimeEventFlow,
    private val playlistRepository: PlaylistRepository,
    private val mapRepository: FSMapRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val downloaderRepository: DownloaderRepository,
) :
    ViewModel() {
    private val viewModelState =
        MutableStateFlow(
            HomeViewModelState(
                isLoading = true,
                isPlaylistOpen = false,
                userPreferenceState = UserPreferenceV2.getDefaultUserPreference(),
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
                viewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )

    init {
        viewModelScope.launch {
            EventBus.subscribe<HomeUIEvent> { dispatchUiEvents(it) }
        }

        viewModelScope.launch {
            userPreferenceRepository.getUserPreference().flowOn(Dispatchers.IO).collect {
                logger.debug { "user preference changed, manageFolder:${it.currentManageFolder},current:${viewModelState.value.userPreferenceState.currentManageFolder}" }
                if (it.currentManageFolder != viewModelState.value.userPreferenceState.currentManageFolder) {
                    logger.debug { "user preference changed, refresh playlist" }
                    refreshPlayLists()
                }
                viewModelState.update { state -> state.copy(userPreferenceState = it) }
            }
        }
        refreshPlayLists()
    }

    fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is HomeUIEvent.RefreshPlaylist -> {

                refreshPlayLists()
            }
            is HomeUIEvent.DeletePlaylist -> {
                deletePlaylist(event.targetPlaylist)
            }
            is HomeUIEvent.EditPlaylist -> {
                viewModelScope.launch {
                    playlistRepository.editPlaylist(event.resPlaylist)
                }
            }
            is HomeUIEvent.SyncPlaylist -> {
                val id = userPreferenceRepository.getCurrentUserPreference().currentManageFolder?.id
                if (id == null) {
                    viewModelScope.launch {
                        EventBus.publish(GlobalUIEvent.ShowSnackBar("意外错误，ManageDirId 不应为空，可能因修改配置文件所导致"))
                    }
                    return
                }
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.scanSinglePlaylist(event.targetPlaylist.id,event.targetPlaylist.getTargetPath(),id)
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
                    viewModelScope.launch {
                        EventBus.publish(GlobalUIEvent.ShowSnackBar("请先选择文件夹"))
                    }
                    return
                }
                onExportPlaylistAsBPList(event.playlist,event.targetPath)
            }
            is HomeUIEvent.PlayPreviewMusicSegment -> {
                viewModelScope.launch(Dispatchers.IO) {
                    EventBus.publish(
                        GlobalUIEvent.PlayMedia(
                            IMedia.MapAudioPreview(
                                id = event.map.getID(),
                                url = event.map.getMusicPreviewURL(),
                                avatarUrl = event.map.getAvatar(),
                            ),
                        ),
                    )
                }
            }
            is HomeUIEvent.CreateNewPlaylist -> {
                onCreateNewPlaylist(event.name, event.description, event.customTags)
            }
        }
    }

    private fun refreshPlayLists(managerDir: String = "") {
        val manageDirId = userPreferenceRepository.getCurrentUserPreference().currentManageFolder?.id
        if (manageDirId == null) {
            viewModelState.update { it.copy(playlists = emptyList()) }
            return
        }
        viewModelState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            playlistRepository.getAllPlaylistByManageDirId(manageDirId)
                .flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            viewModelState.update { vmState ->
                                vmState.copy(playlists = it.data, isLoading = false)
                            }
                        }
                        is Result.Error -> {
                            EventBus.publish(
                                GlobalUIEvent.ShowSnackBar(
                                    message = "error loading playlists",
                                    actionLabel = "retry",
                                    action = { refreshPlayLists() },
                                ),
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
        viewModelScope.launch {
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
                EventBus.publish(
                    GlobalUIEvent.ShowSnackBar(
                        message = "export playlist as key succeed ${res.data}",
                        actionLabel = "copy",
                        action = {
                            viewModelScope.launch { EventBus.publish(GlobalUIEvent.WriteToClipboard(res.data)) }
                        },
                        duration = SnackbarDuration.Long,
                    ),
                )
            } else {
                EventBus.publish(
                    GlobalUIEvent.ReportError(
                        res.errorMsg(),
                        "export playlist as key failed"
                    ),
                )
            }
        }
    }

    private fun onExportPlaylistAsBPList(playlist: IPlaylist, targetPath:String) {
        viewModelScope.launch {
            val res = playlistRepository.exportPlaylistAsBPList(playlist,targetPath.toPath())
            EventBus.publish(GlobalUIEvent.ShowSnackBar(res.successOr("export failed")))
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
                    EventBus.publish(
                        GlobalUIEvent.ShowSnackBar(
                            message = "将导入 ${res.data.size} 个 map 至 ${targetPlaylist.title}",
                        ),
                    )
                    downloaderRepository.downloadMapByMapIds(targetPlaylist, res.data)
                }
                is Result.Error -> {
                    EventBus.publish(GlobalUIEvent.ShowSnackBar(message = "import failed"))
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

            if ((playlist as FSPlaylistVO).topPlaylist) {
                EventBus.publish(GlobalUIEvent.ShowSnackBar("无法删除首级歌单，应在setting > 目录管理中删除"))
                return@launch
            }
            try {
                playlistRepository.deletePlaylistByBasePath(playlist.basePath)
            }catch (e:Exception) {
                EventBus.publish(GlobalUIEvent.ReportError(e, "delete playlist error"))
            }
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
                            EventBus.publish(
                                GlobalUIEvent.ShowSnackBar(
                                    message = "move ${mapToBeMoved.size} maps to ${targetPlaylist.title} succeed",
                                ),
                            )
                        } else {
                            EventBus.publish(
                                GlobalUIEvent.ShowSnackBar(
                                    message = "move ${mapToBeMoved.size} maps to ${targetPlaylist.title} failed",
                                )
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
