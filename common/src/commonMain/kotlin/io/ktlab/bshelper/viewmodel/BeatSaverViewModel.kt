package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
//import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.model.dto.request.PlaylistFilterParam
import io.ktlab.bshelper.model.vo.BSMapVO
import io.ktlab.bshelper.repository.*
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.UUID

//private val logger = KotlinLogging.logger("BeatSaverViewModel")

data class LocalState(
    val localMapIdSet: Set<Pair<String,String>> = emptySet(),
    val selectableLocalPlaylists: List<IPlaylist> = emptyList(),
    val targetPlaylist: IPlaylist? = null,
)
enum class TabType(val human:String,val index : Int){
    Map("map",0),
    Playlist("playlist",1);
    companion object {
        fun fromIndex(index: Int): TabType {
            return when (index) {
                0 -> Map
                1 -> Playlist
                else -> Map
            }
        }
        fun getIndexOf(tabType: TabType): Int {
            return when (tabType) {
                Map -> 0
                Playlist -> 1
            }
        }
        val tabs = listOf(Map,Playlist)
    }

}


sealed interface BeatSaverUiState {
    val isLoading: Boolean
    val tabType: TabType
    data class MapQuery(
        override val tabType: TabType = TabType.Map,
        override val isLoading: Boolean = false,
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
        val selectedBSPlaylist: IPlaylist?,
        val mapFlow: Flow<PagingData<IMap>>,
        val playlistFlow: Flow<PagingData<IPlaylist>>,
        val downloadTaskFlow: Flow<List<IDownloadTask>>,
        val multiSelectMode: Boolean,
        val multiSelectedBSMap: Set<IMap>,
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
    //
    val mapFilterPanelState: MapFilterParam = MapFilterParam(),

    val playlistFilterPanelState: PlaylistFilterParam = PlaylistFilterParam(),
    // 在线分页数据
    val mapFlow: Flow<PagingData<IMap>>? = null,
//
    val playlistFlow: Flow<PagingData<IPlaylist>>? = null,

    val downloadTaskFlow: Flow<List<IDownloadTask>>? = null,

    //    可选择的目标歌单
    val selectableLocalPlaylists: List<IPlaylist>,
    //    已选择的目标歌单
    val selectedPlaylist: IPlaylist? = null,
    val selectedBSPlaylist: IPlaylist? = null,
    val multiSelectMode: Boolean = false,
    val multiSelectedBSMap: Set<IMap> = emptySet(),
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
             )
         }
        TabType.Playlist -> {
            BeatSaverUiState.PlaylistQuery(
                isLoading = isLoading,
                playlistFilterPanelState = playlistFilterPanelState,
                mapFlow = mapFlow ?: emptyFlow(),
                localState = LocalState(
                    localMapIdSet = localMapIdSet,
                    selectableLocalPlaylists = selectableLocalPlaylists,
                    targetPlaylist = selectedPlaylist,
                ),
                downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
                selectedBSPlaylist = selectedBSPlaylist,
                playlistFlow = playlistFlow ?: emptyFlow(),
                multiSelectMode = multiSelectMode,
                multiSelectedBSMap = multiSelectedBSMap,
            )
        }
    }

}

sealed class BeatSaverUIEvent: UIEvent(){
    data class MapTapped(val map: IMap): BeatSaverUIEvent()
    data class MapLongTapped(val map: IMap): BeatSaverUIEvent()
    data class ChangeMultiSelectMode(val checked: Boolean): BeatSaverUIEvent()
    data class MapMultiSelected(val map: IMap): BeatSaverUIEvent()
    data class ChangeTargetPlaylist(val playlist: IPlaylist?): BeatSaverUIEvent()


    data class SearchMapWithFilter(val mapQueryState: MapFilterParam): BeatSaverUIEvent()
    data class SearchPlaylistWithFilter(val playlistQueryState: PlaylistFilterParam): BeatSaverUIEvent()

    data class SwitchTab(val tabType: TabType): BeatSaverUIEvent()

    data class MultiDownload(val targetPlaylist: IPlaylist): BeatSaverUIEvent()
    data class DownloadMap( val bsMap: IMap): BeatSaverUIEvent()
    data class PauseDownload(val downloadTask: IDownloadTask): BeatSaverUIEvent()
    data class ResumeDownload(val downloadTask: IDownloadTask) : BeatSaverUIEvent()

    data class PlayPreviewMusicSegment(val map: IMap): BeatSaverUIEvent()
    data class MsgShown(val msgId: Long): BeatSaverUIEvent()
    data class ShowSnackBar(val message:String): BeatSaverUIEvent()
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
            downloadTaskFlow = downloaderRepository.getDownloadTaskFlow().flowOn(Dispatchers.IO),
            localMapIdSet = emptySet(),
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
            is BeatSaverUIEvent.SearchMapWithFilter -> { onSearchMapWithFilter(event.mapQueryState) }
            is BeatSaverUIEvent.ChangeMultiSelectMode -> { onMultiSelectChecked(event.checked) }
            is BeatSaverUIEvent.MapMultiSelected -> { onMapMultiSelected(event.map) }
            is BeatSaverUIEvent.ChangeTargetPlaylist -> { onSelectedPlaylistChanged(event.playlist) }
            is BeatSaverUIEvent.MultiDownload -> { onBatchDownloadMaps(event.targetPlaylist) }
            is BeatSaverUIEvent.DownloadMap -> { onDownloadMap(event.bsMap) }
            is BeatSaverUIEvent.SearchPlaylistWithFilter -> { onSearchPlaylistWithFilter(event.playlistQueryState) }
            is BeatSaverUIEvent.PlayPreviewMusicSegment -> {
//                onPlayPreviewMusicSegment(event.map)
            }
            is BeatSaverUIEvent.PauseDownload -> { onPauseDownload(event.downloadTask) }
            is BeatSaverUIEvent.ResumeDownload -> { onResumeDownload(event.downloadTask) }
            is BeatSaverUIEvent.MapTapped -> {onMapTapped(event.map)}
            is BeatSaverUIEvent.MapLongTapped -> { onMapLongTapped(event.map) }
            is BeatSaverUIEvent.SwitchTab -> { viewModelState.update { it.copy(tabType = event.tabType) } }
            else -> {}
        }
    }
    private fun onMapTapped(map: IMap) {
        if (viewModelState.value.multiSelectMode) {
            onMapMultiSelected(map)
        }
    }
    private fun onMapLongTapped(map: IMap) {
        if (!viewModelState.value.multiSelectMode) {
            onMultiSelectChecked(true)
            onMapMultiSelected(map)
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

//    private fun onPlayPreviewMusicSegment(map: IMap) {
//        viewModelScope.launch(Dispatchers.IO) {
//            mediaPlayerManager.play(MediaPlayerManager.generateMapID(map),map.getMusicPreviewURI().toString())
//        }
//    }


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
        viewModelState.update {
            it.copy(
                multiSelectMode = checked,
                multiSelectedBSMap = emptySet()
            )
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
            mapRepository.batchInsertBSMap(listOf(bsMap).map { it as BSMapVO })
//            mapRepository.batchInsertBSMapAsFSMap(listOf(bsMap).map { it as BSMapVO },viewModelState.value.selectedPlaylist!!)
            downloaderRepository.downloadMap(viewModelState.value.selectedPlaylist!!,(bsMap as BSMapVO))
        }
    }
}