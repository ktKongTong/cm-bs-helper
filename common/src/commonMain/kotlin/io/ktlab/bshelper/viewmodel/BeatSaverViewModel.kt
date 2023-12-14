package io.ktlab.bshelper.viewmodel

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
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
import io.ktlab.bshelper.repository.DownloaderRepository
import io.ktlab.bshelper.repository.FSMapRepository
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope


data class LocalState(
    val localMapIdSet: Set<Pair<String,String>> = emptySet(),
    val selectableLocalPlaylists: List<IPlaylist> = emptyList(),
    val targetPlaylist: IPlaylist? = null,
)
enum class TabType(val human:String,val index : Int){
    Map("map",0),
    Playlist("playlist",1),
    Mapper("mapper",2);
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
        val tabs = listOf(Map,Playlist,Mapper)
    }
}


sealed interface BeatSaverUiState {
    val isLoading: Boolean
    val tabType: TabType
    data class MapQuery(
        override val tabType: TabType = TabType.Map,
        override val isLoading: Boolean = false,
        val selectedBSMap: IMap?,
        val selectedBSMapReview: List<BSMapReviewDTO> = emptyList(),
        val localState: LocalState = LocalState(),
        val mapFilterPanelState: MapFilterParam = MapFilterParam(),
        val mapFlow: Flow<PagingData<IMap>> = emptyFlow(),
        val downloadTaskFlow: Flow<List<IDownloadTask>> =  emptyFlow(),
        val multiSelectMode: Boolean = false,
        val multiSelectedBSMap: Set<IMap> = emptySet(),
    ): BeatSaverUiState

    data class PlaylistQuery(
        override val tabType: TabType = TabType.Playlist,
        override val isLoading: Boolean = false,
        val localState: LocalState = LocalState(),
        val playlistFilterPanelState: PlaylistFilterParam,
        val mapFlow: Flow<PagingData<IMap>>,
        val playlistFlow: Flow<PagingData<IPlaylist>>,
        val downloadTaskFlow: Flow<List<IDownloadTask>>,
        val multiSelectMode: Boolean,
        val multiSelectedBSMap: Set<IMap>,
        val selectedBSPlaylist: IPlaylist?,
        val selectedBSMap: IMap? = null,
        val selectedBSMapReview: List<BSMapReviewDTO> = emptyList(),
    ): BeatSaverUiState
    data class MapperQuery(
        override val tabType: TabType = TabType.Mapper,
        override val isLoading: Boolean = false,
        val localState: LocalState = LocalState(),
        val mapFlow: Flow<PagingData<IMap>>,
        val playlistFlow: Flow<PagingData<IPlaylist>>,
        val mapperFlow: Flow<PagingData<BSUserWithStatsDTO>>,

        val downloadTaskFlow: Flow<List<IDownloadTask>>,
        val multiSelectMode: Boolean,
        val multiSelectedBSMap: Set<IMap>,
        val selectedBSPlaylist: IPlaylist?,
        val selectedBSMap: IMap? = null,
        val selectedBSMapReview: List<BSMapReviewDTO> = emptyList(),
        val selectedBSMapper: BSMapperDetailDTO? = null,
    ): BeatSaverUiState

}

data class BeatSaverViewModelState(
    //
    val tabType: TabType = TabType.Map,
    val isLoading: Boolean = false,
    // 本地歌单
    val localPlaylists: List<IPlaylist> = emptyList(),
    // 本地MapID
    val localMapIdSet: Set<Pair<String,String>> = emptySet(),
    val localMapIdFlow : Flow<Set<Pair<String,String>>>,
    //
    val mapFilterPanelState: MapFilterParam = MapFilterParam(),

    val playlistFilterPanelState: PlaylistFilterParam = PlaylistFilterParam(),
    val mapFlow: Flow<PagingData<IMap>>? = null,
//
    val playlistFlow: Flow<PagingData<IPlaylist>>? = null,
    val mapperFlow: Flow<PagingData<BSUserWithStatsDTO>>,
    val playlistDetailMapFlow: Flow<PagingData<IMap>>? = null,
    val downloadTaskFlow: Flow<List<IDownloadTask>>? = null,

    //    可选择的目标歌单
    val selectableLocalPlaylists: List<IPlaylist>,
    //    已选择的目标歌单
    val selectedPlaylist: IPlaylist? = null,
    // 选中的在线歌单
    val selectedBSPlaylist: IPlaylist? = null,
    // 选中的在线Map
    val selectedBSMap: IMap? = null,
    val selectedBSMapReview: List<BSMapReviewDTO> = emptyList(),
    val multiSelectMode: Boolean = false,
    val multiSelectedBSMap: Set<IMap> = emptySet(),
    val selectedBSMapper: BSMapperDetailDTO? = null,
) {

    fun toUiState(): BeatSaverUiState = when (tabType) {
         TabType.Map -> {
             BeatSaverUiState.MapQuery(
                 isLoading = isLoading,
                 mapFilterPanelState = mapFilterPanelState,
                 mapFlow = mapFlow ?: emptyFlow(),
                 localState = LocalState(
                     localMapIdSet = localMapIdSet,
                     selectableLocalPlaylists = selectableLocalPlaylists,
                     targetPlaylist = selectedPlaylist,
                 ),
                 downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
                 multiSelectMode = multiSelectMode,
                 multiSelectedBSMap = multiSelectedBSMap,
                 selectedBSMap = selectedBSMap,
                 selectedBSMapReview = selectedBSMapReview,
             )
         }
        TabType.Playlist -> {
            BeatSaverUiState.PlaylistQuery(
                isLoading = isLoading,
                playlistFilterPanelState = playlistFilterPanelState,
                mapFlow = playlistDetailMapFlow ?: emptyFlow(),
                localState = LocalState(
                    localMapIdSet = localMapIdSet,
                    selectableLocalPlaylists = selectableLocalPlaylists,
                    targetPlaylist = selectedPlaylist,
                ),
                downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
                selectedBSPlaylist = selectedBSPlaylist,
                selectedBSMap = selectedBSMap,
                playlistFlow = playlistFlow ?: emptyFlow(),
                multiSelectMode = multiSelectMode,
                multiSelectedBSMap = multiSelectedBSMap,
                selectedBSMapReview =   selectedBSMapReview,
            )
        }
        TabType.Mapper -> {
            BeatSaverUiState.MapperQuery(
                isLoading = isLoading,
                mapFlow = mapFlow ?: emptyFlow(),
                downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
                playlistFlow = playlistFlow ?: emptyFlow(),
                mapperFlow = mapperFlow,
                localState = LocalState(
                    localMapIdSet = localMapIdSet,
                    selectableLocalPlaylists = selectableLocalPlaylists,
                    targetPlaylist = selectedPlaylist,
                ),
                multiSelectMode = multiSelectMode,
                multiSelectedBSMap = multiSelectedBSMap,
                selectedBSMap = selectedBSMap,
                selectedBSPlaylist = selectedBSPlaylist,
                selectedBSMapReview = selectedBSMapReview,
                selectedBSMapper = selectedBSMapper,
            )
        }
    }

}

sealed class BeatSaverUIEvent: UIEvent(){

    data class ChangeMultiSelectMode(val checked: Boolean): BeatSaverUIEvent()
    data class OnMultiSelectMap(val map: IMap): BeatSaverUIEvent()
    data class ChangeTargetPlaylist(val playlist: IPlaylist?): BeatSaverUIEvent()
    data class OnSelectBSPlaylist(val playlist: IPlaylist): BeatSaverUIEvent()
    data class OnSelectBSMap(val map: IMap): BeatSaverUIEvent()
    data class OnSelectedBSMapper(val mapper: BSUserWithStatsDTO): BeatSaverUIEvent()
    data object OnExitBSMapper: BeatSaverUIEvent()
    data class MapTapped(val map: IMap): BeatSaverUIEvent()
    data class MapLongTapped(val map: IMap): BeatSaverUIEvent()
    data object OnExitSelectedBSMap: BeatSaverUIEvent()
    data object OnExitSelectedBSPlaylist: BeatSaverUIEvent()
    data class SearchMapWithFilter(val mapQueryState: MapFilterParam): BeatSaverUIEvent()
    data class UpdateMapFilterParam(val mapQueryState: MapFilterParam): BeatSaverUIEvent()

    data class SearchPlaylistWithFilter(val playlistQueryState: PlaylistFilterParam): BeatSaverUIEvent()

    data class SwitchTab(val tabType: TabType): BeatSaverUIEvent()

    data class DownloadMap( val bsMap: IMap): BeatSaverUIEvent()
    data class MultiDownload(val targetPlaylist: IPlaylist): BeatSaverUIEvent()
    data class PauseDownload(val downloadTask: IDownloadTask): BeatSaverUIEvent()
    data class ResumeDownload(val downloadTask: IDownloadTask) : BeatSaverUIEvent()
    data class DownloadPlaylist(val playlist: IPlaylist): BeatSaverUIEvent()
    data class PlayPreviewMusicSegment(val map: IMap): BeatSaverUIEvent()

}

class BeatSaverViewModel(
    private val globalViewModel: GlobalViewModel,
    private val playlistRepository: PlaylistRepository,
    private val mapRepository: FSMapRepository,
    private val downloaderRepository: DownloaderRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(
        BeatSaverViewModelState(
            isLoading = false,
            mapFlow = mapRepository.getPagingBSMap(MapFilterParam()).cachedIn(viewModelScope),
            playlistFlow = playlistRepository.getPagingBSPlaylist(PlaylistFilterParam()).cachedIn(viewModelScope),
            mapperFlow = playlistRepository.getPagingBSUser().cachedIn(viewModelScope),
            downloadTaskFlow = downloaderRepository.getDownloadTaskFlow().flowOn(Dispatchers.IO),
            localMapIdSet = emptySet(),
            localMapIdFlow = emptyFlow(),
            selectableLocalPlaylists = emptyList()
        )
    )
    val uiState = viewModelState
        .map(BeatSaverViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init{
//        logger.debug { "init BeatSaverViewModel" }
        viewModelScope.listenLocalMapFlow()
        viewModelScope.listenLocalPlaylistFlow()
//        viewModelScope.listenDownloadingTaskFlow()
    }

    private fun CoroutineScope.listenLocalMapFlow(){
        launch {
            mapRepository.getLocalMapIdSet()
                .flowOn(Dispatchers.IO)
                .collect {res->
                    viewModelState.update {
                        it.copy(
                            localMapIdSet = it.localMapIdSet.plus(res),
                            isLoading = false
                        )
                    }
                }
        }
    }
    private fun CoroutineScope.listenLocalPlaylistFlow() {
        launch {
            playlistRepository.getAllPlaylist()
                .flowOn(Dispatchers.IO)
                .collect {res->
//                    logger.debug { "all playlist update" }
                    when (res) {
                        is Result.Success -> {
                            viewModelState.update {
                                it.copy(
                                    selectableLocalPlaylists = res.successOr(emptyList()),
                                    isLoading = false
                                )
                            }
                        }
                        is Result.Error -> { globalViewModel.showSnackBar("获取playlist失败") }
                    }
                }
        }
    }

    private fun CoroutineScope.listenDownloadingTaskFlow() {
        launch {
            downloaderRepository
                .getDownloadTaskFlow()
                .flowOn(Dispatchers.IO)
                .collect{res->
                    viewModelState.update {
                        it.copy(
                            downloadTaskFlow = flowOf(res),
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun dispatchUiEvents(event: UIEvent){
        when(event){
            is BeatSaverUIEvent.SwitchTab -> {
                viewModelState.update {
                    it.copy(
                        tabType = event.tabType,
                        multiSelectMode = false,
                        multiSelectedBSMap = setOf()
                    )
                }
            }
            is BeatSaverUIEvent.OnSelectedBSMapper -> {
                viewModelScope.launch {
                    val res = playlistRepository.getBSUserDetail(event.mapper.id)
                    when (res) {
                        is Result.Success -> {
                            viewModelState.update { it.copy(
                                mapFlow = mapRepository.getPagingBSMapByBSUserId(event.mapper.id).cachedIn(viewModelScope),
                                selectedBSMapper = res.data)
                            }
                        }
                        is Result.Error -> { globalViewModel.showSnackBar("获取mapper失败,${res.exception.message}") }
                    }
                }
            }
            is BeatSaverUIEvent.SearchMapWithFilter -> { onSearchMapWithFilter(event.mapQueryState) }
            is BeatSaverUIEvent.UpdateMapFilterParam -> { viewModelState.update { it.copy(mapFilterPanelState = event.mapQueryState) } }
            is BeatSaverUIEvent.SearchPlaylistWithFilter -> { onSearchPlaylistWithFilter(event.playlistQueryState) }

            is BeatSaverUIEvent.ChangeMultiSelectMode -> { onMultiSelectChecked(event.checked) }
            is BeatSaverUIEvent.ChangeTargetPlaylist -> { onSelectedPlaylistChanged(event.playlist) }
            is BeatSaverUIEvent.OnMultiSelectMap -> { onMapMultiSelected(event.map) }

            is BeatSaverUIEvent.MultiDownload -> { onBatchDownloadMaps(event.targetPlaylist) }
            is BeatSaverUIEvent.DownloadMap -> { onDownloadMap(event.bsMap) }

            is BeatSaverUIEvent.PauseDownload -> { onPauseDownload(event.downloadTask) }
            is BeatSaverUIEvent.ResumeDownload -> { onResumeDownload(event.downloadTask) }
            is BeatSaverUIEvent.DownloadPlaylist -> { onDownloadPlaylist(event.playlist) }
            is BeatSaverUIEvent.PlayPreviewMusicSegment -> {
//                onPlayPreviewMusicSegment(event.map)
                globalViewModel.playMedia(IMedia.MapAudioPreview(
                        id = event.map.getID(),
                    url = event.map.getMusicPreviewURL(),
                    avatarUrl = event.map.getAvatar(),
                ))
            }

            is BeatSaverUIEvent.OnSelectBSPlaylist -> {
                viewModelState.update { it.copy(selectedBSPlaylist = event.playlist) }
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.getPlaylistDetailPagingMaps(event.playlist.id).cachedIn(viewModelScope)
                        .let {maps ->  viewModelState.update { it.copy(playlistDetailMapFlow = maps) } }
                }
            }
            is BeatSaverUIEvent.OnSelectBSMap -> { viewModelState.update { it.copy(selectedBSMap = event.map) } }
            is BeatSaverUIEvent.MapTapped -> {onMapTapped(event.map)}
            is BeatSaverUIEvent.MapLongTapped -> { onMapLongTapped(event.map) }

            is BeatSaverUIEvent.OnExitSelectedBSMap -> { viewModelState.update { it.copy(selectedBSMap = null) } }
            is BeatSaverUIEvent.OnExitSelectedBSPlaylist -> {
                viewModelState.update { it.copy(selectedBSPlaylist = null, selectedBSMap = null) }
            }
                is BeatSaverUIEvent.OnExitBSMapper -> { viewModelState.update { it.copy(
                    selectedBSMapper = null,
                    mapFlow = mapRepository.getPagingBSMap(it.mapFilterPanelState).cachedIn(viewModelScope))
                }
            }
        }
    }
    private fun onMapTapped(map: IMap) {
        if (viewModelState.value.multiSelectMode) {
            onMapMultiSelected(map)
        }else {
            viewModelState.update {
                it.copy(
                    selectedBSMap = map
                )
            }
            viewModelScope.launch(Dispatchers.IO) {
                mapRepository.getBSMapReviewsById(map.getID()).successOr(emptyList())
                    .let {comments ->  viewModelState.update { it.copy(selectedBSMapReview = comments) } }
            }
        }
    }
    private fun onMapLongTapped(map: IMap) {
        if (!viewModelState.value.multiSelectMode) {
            onMultiSelectChecked(true)
            onMapMultiSelected(map)
        }else {
            viewModelState.update { it.copy(selectedBSMap = map) }

        }
    }

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

    private fun onSearchMapWithFilter(filterState: MapFilterParam) {
        viewModelState.update {
            it.copy(
                mapFilterPanelState = filterState,
                mapFlow = mapRepository.getPagingBSMap(filterState).cachedIn(viewModelScope),
                multiSelectedBSMap = emptySet(),
                multiSelectMode = false
            )
        }
    }
//    onSearcPlaylistWithFilter
    private fun onSearchPlaylistWithFilter(filterState: PlaylistFilterParam) {
        viewModelState.update {
            it.copy(
                playlistFilterPanelState = filterState,
                playlistFlow = playlistRepository.getPagingBSPlaylist(filterState).cachedIn(viewModelScope),
                multiSelectedBSMap = emptySet(),
                multiSelectMode = false
            )
        }
    }
    private fun onSelectedPlaylistChanged(playlist: IPlaylist?) {
        viewModelState.update {
            it.copy(
                selectedPlaylist = playlist,
            )
        }
    }

    private fun onMapMultiSelected(bsMap: IMap) {
        if (viewModelState.value.selectedPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        }else if (viewModelState.value.localMapIdSet.contains(viewModelState.value.selectedPlaylist!!.id to bsMap.getID())) {
            return
        }
        viewModelState.update {
            it.copy(
                multiSelectedBSMap = if (viewModelState.value.multiSelectedBSMap.contains(bsMap)) {
                    viewModelState.value.multiSelectedBSMap.minus(bsMap)
                }else {
                    viewModelState.value.multiSelectedBSMap.plus(bsMap)
                }
            )
        }
    }

    private fun onMultiSelectChecked(checked: Boolean) {
//        logger.debug { "multiselect switch to $checked" }
        if (viewModelState.value.selectedPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        }
        viewModelState.update {
            it.copy(
                multiSelectMode = checked,
                multiSelectedBSMap = emptySet()
            )
        }
    }

    private fun onDownloadPlaylist(playlist: IPlaylist) {
        val targetPlaylist = viewModelState.value.selectedPlaylist
        if (targetPlaylist == null) {
            globalViewModel.showSnackBar("将自动创建歌单 ${playlist.title} 作为目标歌单, 要这样做吗？",
                actionLabel = "确定",
                action = {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.createPlaylistAndDownloadBSPlaylist(playlist as BSPlaylistVO)
                }
            })
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            downloaderRepository.downloadBSPlaylist(targetPlaylist,playlist as BSPlaylistVO)
        }
    }

    private fun onBatchDownloadMaps(targetPlaylist: IPlaylist) {
        val mapToBeDownload = viewModelState.value.multiSelectedBSMap
        viewModelState.update {
            it.copy(
                multiSelectMode = false,
                multiSelectedBSMap = emptySet()
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.batchInsertBSMap(mapToBeDownload.toList().map { it as BSMapVO })
            mapRepository.batchInsertBSMapAndFSMap(mapToBeDownload.toList().map { it as BSMapVO },viewModelState.value.selectedPlaylist!!)
            downloaderRepository.batchDownloadMap(targetPlaylist,mapToBeDownload.map { it as BSMapVO }.toList())
        }
    }
    private fun onDownloadMap(bsMap: IMap) {
        if (viewModelState.value.multiSelectMode) {
            onMapMultiSelected(bsMap)
            return
        }
        if (viewModelState.value.selectedPlaylist == null) {
            globalViewModel.showSnackBar("请先选择目标歌单")
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            mapRepository.batchInsertBSMapAndFSMap(listOf(bsMap).map { it as BSMapVO },viewModelState.value.selectedPlaylist!!)
            downloaderRepository.downloadMap(viewModelState.value.selectedPlaylist!!,(bsMap as BSMapVO))
        }
    }
}