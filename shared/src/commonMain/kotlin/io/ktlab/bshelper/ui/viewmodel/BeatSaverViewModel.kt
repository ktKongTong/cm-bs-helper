package io.ktlab.bshelper.ui.viewmodel

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.FSMapRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.dto.BSUserWithStatsDTO
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.dto.response.BSMapReviewDTO
import io.ktlab.bshelper.model.dto.response.BSMapperDetailDTO
import io.ktlab.bshelper.model.dto.response.successOr
import io.ktlab.bshelper.model.successOr
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.model.vo.BSPlaylistVO
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

data class LocalState(
    val localMapIdSet: Set<Pair<String, String>> = emptySet(),
    val selectableLocalPlaylists: List<IPlaylist> = emptyList(),
    val targetPlaylist: IPlaylist? = null,
)

enum class TabType(val human: String, val index: Int) {
    Map("map", 0),
    Playlist("playlist", 1),
    Mapper("mapper", 2),
    ;

    companion object {
        fun fromIndex(index: Int): TabType {
            return when (index) {
                0 -> Map
                1 -> Playlist
                2 -> Mapper
                else -> Map
            }
        }

        fun getIndexOf(tabType: TabType): Int {
            return when (tabType) {
                Map -> 0
                Playlist -> 1
                Mapper -> 2
            }
        }

        val tabs = listOf(Map, Playlist, Mapper)
    }
}

data class BeatSaverUiState(
    val isLoading: Boolean,
    val tabType: TabType,
    val selectedBSMap: IMap?,
    val localState: LocalState,
    val selectedBSMapReview: List<BSMapReviewDTO>,
    val selectedBSMapper: BSMapperDetailDTO?,
    val selectedBSMapperMapFlow: Flow<PagingData<IMap>>,
    val multiSelectMode: Boolean,
    val multiSelectedBSMap: Set<IMap>,
    val downloadTaskFlow: Flow<List<IDownloadTask>>,
    val mapFilterPanelState: MapFilterParam = MapFilterParam(),
    val mapFlow: Flow<PagingData<IMap>>,
    val playlistFilterPanelState: PlaylistFilterParam,
    val playlistFlow: Flow<PagingData<IPlaylist>>,
    val selectedBSPlaylist: IPlaylist?,
    val selectedBSPlaylistDetailMapFlow: Flow<PagingData<IMap>>,
//    data class MapQuery(
//        override val tabType: TabType = TabType.Map,
//        override val isLoading: Boolean = false,
//
//        override val localState: LocalState = LocalState(),
//
//        override val selectedBSMap: IMap?,
//        override val selectedBSMapReview: List<BSMapReviewDTO>,
//        override val selectedBSMapper: BSMapperDetailDTO?,
//        override val selectedBSMapperMapFlow: Flow<PagingData<IMap>>,
//
//        override val downloadTaskFlow: Flow<List<IDownloadTask>>,
//
//        override val multiSelectMode: Boolean = false,
//        override val multiSelectedBSMap: Set<IMap> = emptySet(),
//        val mapFilterPanelState: MapFilterParam = MapFilterParam(),
//        val mapFlow: Flow<PagingData<IMap>>,
//        ): BeatSaverUiState
//    data class PlaylistQuery(
//        override val tabType: TabType = TabType.Playlist,
//        override val isLoading: Boolean = false,
//        override val localState: LocalState,
//
//        override val selectedBSMap: IMap?,
//        override val selectedBSMapReview: List<BSMapReviewDTO>,
//        override val selectedBSMapper: BSMapperDetailDTO?,
//        override val selectedBSMapperMapFlow: Flow<PagingData<IMap>>,
//
//        override val downloadTaskFlow: Flow<List<IDownloadTask>>,
//
//        override val multiSelectMode: Boolean = false,
//        override val multiSelectedBSMap: Set<IMap> = emptySet(),
//
//        val playlistFilterPanelState: PlaylistFilterParam,
//        val mapFlow: Flow<PagingData<IMap>>,
//        val playlistFlow: Flow<PagingData<IPlaylist>>,
//        val selectedBSPlaylist: IPlaylist?,
//    ): BeatSaverUiState
    val mapperFlow: Flow<PagingData<BSUserWithStatsDTO>>,
//    data class MapperQuery(
//        override val tabType: TabType = TabType.Mapper,
//        override val isLoading: Boolean = false,
//        override val localState: LocalState = LocalState(),
//
//        override val selectedBSMap: IMap?,
//        override val selectedBSMapReview: List<BSMapReviewDTO>,
//        override val selectedBSMapper: BSMapperDetailDTO?,
//        override val selectedBSMapperMapFlow: Flow<PagingData<IMap>>,
//        val selectedBSPlaylist: IPlaylist?,
//
//        override val downloadTaskFlow: Flow<List<IDownloadTask>>,
//
//        override val multiSelectMode: Boolean = false,
//        override val multiSelectedBSMap: Set<IMap> = emptySet(),
//        // maybe current mapper map / playlist detail map / custom search map
//
//        val mapFlow: Flow<PagingData<IMap>>,
//        // todo: current mapper playlist
//        // custom search playlist
//        val playlistFlow: Flow<PagingData<IPlaylist>>,
//
//        val mapperFlow: Flow<PagingData<BSUserWithStatsDTO>>,
//
//    ): BeatSaverUiState
)

data class BeatSaverViewModelState(
    //
    val tabType: TabType = TabType.Map,
    val isLoading: Boolean = false,
    // 本地歌单
    val localPlaylists: List<IPlaylist> = emptyList(),
    // 本地MapID
    val localMapIdSet: Set<Pair<String, String>> = emptySet(),
    val localMapIdFlow: Flow<Set<Pair<String, String>>>,
    //
    val mapFilterPanelState: MapFilterParam = MapFilterParam(),
    val playlistFilterPanelState: PlaylistFilterParam = PlaylistFilterParam(),
    val mapFlow: Flow<PagingData<IMap>>? = null,
//
    val playlistFlow: Flow<PagingData<IPlaylist>>? = null,
    val mapperFlow: Flow<PagingData<BSUserWithStatsDTO>>,
    val mapperMapFlow: Flow<PagingData<IMap>>? = null,
    val playlistDetailMapFlow: Flow<PagingData<IMap>>? = null,
    val downloadTaskFlow: Flow<List<IDownloadTask>>? = null,
    //    可选择的目标歌单
    val selectableLocalPlaylists: List<IPlaylist>,
    //    已选择的目标歌单
    val selectedFSPlaylist: IPlaylist? = null,
    val multiSelectMode: Boolean = false,
    val multiSelectedBSMap: Set<IMap> = emptySet(),
    // 选中的BS歌单
    val selectedBSPlaylist: IPlaylist? = null,
    // 选中的BSMap
    val selectedBSMap: IMap? = null,
    // 选中的BSMapper
    val selectedBSMapper: BSMapperDetailDTO? = null,
    // 选中的BSMapReview
    val selectedBSMapReview: List<BSMapReviewDTO> = emptyList(),
) {
    fun toUiState(): BeatSaverUiState =
        BeatSaverUiState(
            tabType = tabType,
            isLoading = isLoading,
            mapFilterPanelState = mapFilterPanelState,
            mapFlow = mapFlow ?: emptyFlow(),
            localState =
                LocalState(
                    localMapIdSet = localMapIdSet,
                    selectableLocalPlaylists = selectableLocalPlaylists,
                    targetPlaylist = selectedFSPlaylist,
                ),
            downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
            multiSelectMode = multiSelectMode,
            multiSelectedBSMap = multiSelectedBSMap,
            selectedBSMap = selectedBSMap,
            selectedBSMapper = selectedBSMapper,
            selectedBSMapReview = selectedBSMapReview,
            selectedBSMapperMapFlow = mapperMapFlow ?: emptyFlow(),
            playlistFilterPanelState = playlistFilterPanelState,
            playlistFlow = playlistFlow ?: emptyFlow(),
            selectedBSPlaylist = selectedBSPlaylist,
            selectedBSPlaylistDetailMapFlow = playlistDetailMapFlow ?: emptyFlow(),
            mapperFlow = mapperFlow,
        )
//         TabType.Map -> {
//             BeatSaverUiState.MapQuery(
//                 isLoading = isLoading,
//                 mapFilterPanelState = mapFilterPanelState,
//                 mapFlow = mapFlow ?: emptyFlow(),
//                 localState = LocalState(
//                     localMapIdSet = localMapIdSet,
//                     selectableLocalPlaylists = selectableLocalPlaylists,
//                     targetPlaylist = selectedFSPlaylist,
//                 ),
//                 downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
//                 multiSelectMode = multiSelectMode,
//                 multiSelectedBSMap = multiSelectedBSMap,
//                 selectedBSMap = selectedBSMap,
//                 selectedBSMapper = selectedBSMapper,
//                 selectedBSMapReview = selectedBSMapReview,
//                selectedBSMapperMapFlow = mapperMapFlow ?: emptyFlow(),
//             )
//         }
//        TabType.Playlist -> {
//            BeatSaverUiState.PlaylistQuery(
//                isLoading = isLoading,
//                playlistFilterPanelState = playlistFilterPanelState,
//                mapFlow = playlistDetailMapFlow ?: emptyFlow(),
//                localState = LocalState(
//                    localMapIdSet = localMapIdSet,
//                    selectableLocalPlaylists = selectableLocalPlaylists,
//                    targetPlaylist = selectedFSPlaylist,
//                ),
//                downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
//                selectedBSPlaylist = selectedBSPlaylist,
//                selectedBSMap = selectedBSMap,
//                playlistFlow = playlistFlow ?: emptyFlow(),
//                multiSelectMode = multiSelectMode,
//                multiSelectedBSMap = multiSelectedBSMap,
//                selectedBSMapReview =   selectedBSMapReview,
//                selectedBSMapper = selectedBSMapper,
//                selectedBSMapperMapFlow = mapperMapFlow ?: emptyFlow(),
//            )
//        }
//        TabType.Mapper -> {
//            BeatSaverUiState.MapperQuery(
//                isLoading = isLoading,
//                mapFlow = mapFlow ?: emptyFlow(),
//                downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
//                playlistFlow = playlistFlow ?: emptyFlow(),
//                mapperFlow = mapperFlow,
//                localState = LocalState(
//                    localMapIdSet = localMapIdSet,
//                    selectableLocalPlaylists = selectableLocalPlaylists,
//                    targetPlaylist = selectedFSPlaylist,
//                ),
//                multiSelectMode = multiSelectMode,
//                multiSelectedBSMap = multiSelectedBSMap,
//                selectedBSMap = selectedBSMap,
//                selectedBSPlaylist = selectedBSPlaylist,
//                selectedBSMapReview = selectedBSMapReview,
//                selectedBSMapper = selectedBSMapper,
//                selectedBSMapperMapFlow = mapperMapFlow ?: emptyFlow(),
//            )
//        }
//    }
}

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

class BeatSaverViewModel(
    private val globalViewModel: GlobalViewModel,
    private val playlistRepository: PlaylistRepository,
    private val mapRepository: FSMapRepository,
    private val downloaderRepository: DownloaderRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {
    private val viewModelState =
        MutableStateFlow(
            BeatSaverViewModelState(
                isLoading = false,
                mapFlow = mapRepository.getPagingBSMap(MapFilterParam()).cachedIn(viewModelScope),
                playlistFlow = playlistRepository.getPagingBSPlaylist(PlaylistFilterParam()).cachedIn(viewModelScope),
                mapperFlow = playlistRepository.getPagingBSUser().cachedIn(viewModelScope),
                downloadTaskFlow = downloaderRepository.getDownloadTaskFlow().flowOn(Dispatchers.IO),
                localMapIdSet = emptySet(),
                localMapIdFlow = emptyFlow(),
                selectableLocalPlaylists = emptyList(),
            ),
        )
    val uiState =
        viewModelState
            .map(BeatSaverViewModelState::toUiState)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )

    init {
        viewModelScope.listenLocalMapFlow()
        viewModelScope.listenLocalPlaylistFlow()
    }

    private fun CoroutineScope.listenLocalMapFlow() {
        launch {
            mapRepository.getLocalMapIdSet()
                .flowOn(Dispatchers.IO)
                .collect { res ->
                    viewModelState.update {
                        it.copy(
                            localMapIdSet = it.localMapIdSet.plus(res),
                            isLoading = false,
                        )
                    }
                }
        }
    }

    private fun CoroutineScope.listenLocalPlaylistFlow() {
        launch {
            playlistRepository.getAllPlaylist()
                .flowOn(Dispatchers.IO)
                .collect { res ->
//                    logger.debug { "all playlist update" }
                    when (res) {
                        is Result.Success -> {
                            viewModelState.update {
                                it.copy(
                                    selectableLocalPlaylists = res.successOr(emptyList()),
                                    isLoading = false,
                                )
                            }
                        }
                        is Result.Error -> {
                            globalViewModel.showSnackBar("获取playlist失败")
                        }
                    }
                }
        }
    }

    private fun CoroutineScope.listenDownloadingTaskFlow() {
        launch {
            downloaderRepository
                .getDownloadTaskFlow()
                .flowOn(Dispatchers.IO)
                .collect { res ->
                    viewModelState.update {
                        it.copy(
                            downloadTaskFlow = flowOf(res),
                            isLoading = false,
                        )
                    }
                }
        }
    }

    fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is GlobalUIEvent -> {
                globalViewModel.dispatchUiEvents(event)
            }
            is BeatSaverUIEvent.SwitchTab -> {
                viewModelState.update {
                    it.copy(
                        tabType = event.tabType,
                        isLoading = false,
                        multiSelectMode = false,
                        selectedBSMapper = null,
                        selectedBSMap = null,
                    )
                }
            }

            is BeatSaverUIEvent.SearchMapWithFilter -> {
                onSearchMapWithFilter(event.mapQueryState)
            }
            is BeatSaverUIEvent.UpdateMapFilterParam -> {
                viewModelState.update { it.copy(mapFilterPanelState = event.mapQueryState) }
            }
            is BeatSaverUIEvent.UpdatePlaylistFilterParam -> {
                viewModelState.update { it.copy(playlistFilterPanelState = event.playlistQueryState) }
            }
            is BeatSaverUIEvent.SearchPlaylistWithFilter -> {
                onSearchPlaylistWithFilter(event.playlistQueryState ?: viewModelState.value.playlistFilterPanelState)
            }

            is BeatSaverUIEvent.ChangeMultiSelectMode -> {
                onMultiSelectChecked(event.checked)
            }
            is BeatSaverUIEvent.ChangeTargetPlaylist -> {
                onSelectedPlaylistChanged(event.playlist)
            }
            is BeatSaverUIEvent.OnMultiSelectMap -> {
                onMapMultiSelected(event.map)
            }
            // download event
            is BeatSaverUIEvent.MultiDownload -> {
                onBatchDownloadMaps(event.targetPlaylist)
            }
            is BeatSaverUIEvent.DownloadMap -> {
                onDownloadMap(event.bsMap)
            }
            is BeatSaverUIEvent.PauseDownload -> {
                onPauseDownload(event.downloadTask)
            }
            is BeatSaverUIEvent.ResumeDownload -> {
                onResumeDownload(event.downloadTask)
            }
            is BeatSaverUIEvent.DownloadPlaylist -> {
                onDownloadPlaylist(event.playlist)
            }

            is BeatSaverUIEvent.PlayPreviewMusicSegment -> {
                globalViewModel.playMedia(
                    IMedia.MapAudioPreview(
                        id = event.map.getID(),
                        url = event.map.getMusicPreviewURL(),
                        avatarUrl = event.map.getAvatar(),
                    ),
                )
            }
            // bs item event
            is BeatSaverUIEvent.OnSelectedBSMapper -> {
                if (viewModelState.value.selectedBSMapper?.id == event.mapperId) return
                viewModelState.update { it.copy(isLoading = true) }
                viewModelScope.launch {
                    val res = playlistRepository.getBSUserDetail(event.mapperId)

                    when (res) {
                        is Result.Success -> {
                            viewModelState.update {
                                it.copy(
                                    mapperMapFlow = mapRepository.getPagingBSMapByBSUserId(event.mapperId).cachedIn(viewModelScope),
                                    isLoading = false,
                                    selectedBSMapper = res.data,
                                )
                            }
                        }
                        is Result.Error -> {
                            viewModelState.update { it.copy(isLoading = false) }
                            globalViewModel.showSnackBar("获取mapper失败,${res.exception.message}")
                        }
                    }
                }
            }
            is BeatSaverUIEvent.OnSelectBSPlaylist -> {
                viewModelState.update { it.copy(selectedBSPlaylist = event.playlist) }
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.getPlaylistDetailPagingMaps(event.playlist.id).cachedIn(viewModelScope)
                        .let { maps -> viewModelState.update { it.copy(playlistDetailMapFlow = maps) } }
                }
            }
            is BeatSaverUIEvent.OnSelectBSMap -> {
                viewModelState.update { it.copy(selectedBSMap = event.map) }
            }
            is BeatSaverUIEvent.MapTapped -> {
                onMapTapped(event.map)
            }
            is BeatSaverUIEvent.MapLongTapped -> {
                onMapLongTapped(event.map)
            }

            is BeatSaverUIEvent.OnExitSelectedBSMap -> {
                viewModelState.update { it.copy(selectedBSMap = null) }
            }
            is BeatSaverUIEvent.OnExitSelectedBSPlaylist -> {
                viewModelState.update { it.copy(selectedBSPlaylist = null, selectedBSMap = null, playlistDetailMapFlow = emptyFlow()) }
            }
            is BeatSaverUIEvent.OnExitBSMapper -> {
                viewModelState.update {
                    it.copy(
                        selectedBSMapper = null,
                    )
                }
            }
        }
    }

    private fun onMapTapped(map: IMap) {
        if (viewModelState.value.multiSelectMode) {
            onMapMultiSelected(map)
        } else {
            showMapDetail(map)
        }
    }

    private fun onMapLongTapped(map: IMap) {
        if (!viewModelState.value.multiSelectMode) {
            onMultiSelectChecked(true)
            onMapMultiSelected(map)
        } else {
            viewModelState.update { it.copy(selectedBSMap = map) }
        }
    }

    private fun showMapDetail(map: IMap) {
        viewModelState.update { it.copy(selectedBSMap = map) }
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.getBSMapReviewsById(map.getID()).successOr(emptyList())
                .let { comments -> viewModelState.update { it.copy(selectedBSMapReview = comments) } }
        }
    }

    private fun onSearchMapWithFilter(filterState: MapFilterParam) {
        viewModelState.update {
            it.copy(
                mapFilterPanelState = filterState,
                mapFlow = mapRepository.getPagingBSMap(filterState).cachedIn(viewModelScope),
                multiSelectedBSMap = emptySet(),
                multiSelectMode = false,
            )
        }
    }

    private fun onSearchPlaylistWithFilter(filterState: PlaylistFilterParam) {
        viewModelState.update {
            it.copy(
                playlistFilterPanelState = filterState,
                playlistFlow = playlistRepository.getPagingBSPlaylist(filterState).cachedIn(viewModelScope),
                multiSelectedBSMap = emptySet(),
                multiSelectMode = false,
            )
        }
    }

    private fun onSelectedPlaylistChanged(playlist: IPlaylist?) {
        viewModelState.update {
            it.copy(
                selectedFSPlaylist = playlist,
            )
        }
    }

    private fun onMapMultiSelected(bsMap: IMap) {
        if (viewModelState.value.selectedFSPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        } else if (viewModelState.value.localMapIdSet.contains(viewModelState.value.selectedFSPlaylist!!.id to bsMap.getID())) {
            return
        }
        viewModelState.update {
            it.copy(
                multiSelectedBSMap =
                    if (viewModelState.value.multiSelectedBSMap.contains(bsMap)) {
                        viewModelState.value.multiSelectedBSMap.minus(bsMap)
                    } else {
                        viewModelState.value.multiSelectedBSMap.plus(bsMap)
                    },
            )
        }
    }

    private fun onMultiSelectChecked(checked: Boolean) {
        if (viewModelState.value.selectedFSPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        }
        viewModelState.update {
            it.copy(
                multiSelectMode = checked,
                multiSelectedBSMap = emptySet(),
            )
        }
    }

    // download event
    private fun onPauseDownload(downloadTask: IDownloadTask) {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.pause(downloadTask)
        }
    }

    private fun onResumeDownload(downloadTask: IDownloadTask) {
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.resume(downloadTask)
        }
    }

    private fun onDownloadPlaylist(playlist: IPlaylist) {
        val targetPlaylist = viewModelState.value.selectedFSPlaylist
        if (targetPlaylist == null) {
            globalViewModel.showSnackBar(
                "将自动创建歌单 ${playlist.title} 作为目标歌单, 要这样做吗？",
                actionLabel = "确定",
                action = {
                    viewModelScope.launch(Dispatchers.IO) {
                        downloaderRepository.createPlaylistAndDownloadBSPlaylist(playlist as BSPlaylistVO)
                    }
                },
            )
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.downloadBSPlaylist(targetPlaylist, playlist as BSPlaylistVO)
        }
    }

    private fun onBatchDownloadMaps(targetPlaylist: IPlaylist) {
        val mapToBeDownload = viewModelState.value.multiSelectedBSMap
        viewModelState.update {
            it.copy(
                multiSelectMode = false,
                multiSelectedBSMap = emptySet(),
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.batchInsertBSMap(mapToBeDownload.toList().map { it as BSMapVO })
            mapRepository.batchInsertBSMapAndFSMap(
                mapToBeDownload.toList().map { it as BSMapVO },
                viewModelState.value.selectedFSPlaylist!!,
            )
            downloaderRepository.batchDownloadMap(targetPlaylist, mapToBeDownload.map { it as BSMapVO }.toList())
        }
    }

    private fun onDownloadMap(bsMap: IMap) {
        if (viewModelState.value.multiSelectMode) {
            onMapMultiSelected(bsMap)
            return
        }
        if (viewModelState.value.selectedFSPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.batchInsertBSMapAndFSMap(listOf(bsMap).map { it as BSMapVO }, viewModelState.value.selectedFSPlaylist!!)
            downloaderRepository.downloadMap(viewModelState.value.selectedFSPlaylist!!, (bsMap as BSMapVO))
        }
    }
}
