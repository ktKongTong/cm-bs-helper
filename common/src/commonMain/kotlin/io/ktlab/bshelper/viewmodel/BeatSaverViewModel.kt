package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.ktlab.bshelper.model.*
import io.ktlab.bshelper.model.dto.request.MapFilterParam
import io.ktlab.bshelper.repository.FSMapRepository
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.java.KoinJavaComponent
import java.util.UUID

data class LocalState(
    val localMapIdSet: Set<Pair<String,String>> = emptySet(),
    val selectableLocalPlaylists: List<IPlaylist> = emptyList(),
    val targetPlaylist: IPlaylist? = null,

    )

sealed interface BeatSaverUiState {
    val isLoading: Boolean
    val snackBarMessages: List<SnackBarMessage>
    data class MapQuery(
        override val isLoading: Boolean = false,
        override val snackBarMessages: List<SnackBarMessage> = emptyList(),
        val localState: LocalState = LocalState(),
        val mapFilterPanelState: MapQueryState = MapQueryState(),
        val mapFlow: Flow<PagingData<IMap>> = emptyFlow(),
//        val downloadTaskFlow: Flow<List<DownloadTask>> =  emptyFlow(),
        val multiSelectMode: Boolean = false,
        val multiSelectedBSMap: Set<IMap> = emptySet(),
    ): BeatSaverUiState
}
data class MapQueryState(
    val queryKey: String = "",
    val tags: String? = null,
    val maxNps: Double? = null,
    val minNps: Double? = null,
//    date
    val from: String? = null,
    val to: String? = null,
    val mapper: String? = null,
    val automapper: Boolean? = null,
    val chroma: Boolean? = null,
    val noodle: Boolean? = null,
    val me: Boolean? = null,
    val cinema: Boolean? = null,
    val ranked: Boolean? = null,
    val curated: Boolean? = null,
    val verified: Boolean? = null,
    val fullSpread: Boolean? = null,
    val sortKey: String? = "Relevance",
){
    fun toFilterParam(): MapFilterParam {
        return MapFilterParam(
            queryKey = queryKey,
            tags = tags,
            maxNps = maxNps,
            minNps = minNps,
            from = from,
            to = to,
            mapper = mapper,
            automapper = automapper,
            chroma = chroma,
            noodle = noodle,
            me = me,
            cinema = cinema,
            ranked = ranked,
            curated = curated,
            verified = verified,
            fullSpread = fullSpread,
            sortKey = sortKey
        )
    }
}

data class MapperQueryState(
    val queryKey: String = "",
    val maxNps: Double? = null,
    val minNps: Double? = null,
)
data class PlaylistQueryState(
    val queryKey: String = "",
    val maxNps: Double? = null,
    val minNps: Double? = null,
)
data class BeatSaverViewModelState(
    //
    val isLoading: Boolean = false,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    // 本地歌单
    val localPlaylists: List<IPlaylist> = emptyList(),
    // 本地MapID
    val localMapIdSet: Set<Pair<String,String>> = emptySet(),
    //
    val mapFilterPanelState: MapQueryState = MapQueryState(),
    // 在线分页数据
    val mapFlow: Flow<PagingData<IMap>>? = null,

    val playlistFlow: Flow<PagingData<IPlaylist>>? = null,

//    val downloadTaskFlow: Flow<List<DownloadTask>>? = null,

    //    可选择的目标歌单
    val selectableLocalPlaylists: List<IPlaylist>,
    //    已选择的目标歌单
    val selectedPlaylist: IPlaylist? = null,
    val multiSelectMode: Boolean = false,
    val multiSelectedBSMap: Set<IMap> = emptySet(),
) {

    fun toUiState(): BeatSaverUiState =
        BeatSaverUiState.MapQuery(
            isLoading = isLoading,
            snackBarMessages = snackBarMessages,
            mapFilterPanelState = mapFilterPanelState,
            mapFlow = mapFlow ?: emptyFlow(),
            localState = LocalState(
                localMapIdSet = localMapIdSet,
                selectableLocalPlaylists = selectableLocalPlaylists,
                targetPlaylist = selectedPlaylist,
            ),
//            downloadTaskFlow = downloadTaskFlow ?: emptyFlow(),
            multiSelectMode = multiSelectMode,
            multiSelectedBSMap = multiSelectedBSMap,
        )
}

sealed class BeatSaverUIEvent: UIEvent(){
    data class MapTapped(val map: IMap): BeatSaverUIEvent()
    data class ChangeMultiSelectMode(val checked: Boolean): BeatSaverUIEvent()
    data class MapMultiSelected(val map: IMap): BeatSaverUIEvent()
    data class ChangeTargetPlaylist(val playlist: IPlaylist?): BeatSaverUIEvent()
    data class SearchMapWithFilter(val mapQueryState: MapQueryState): BeatSaverUIEvent()

    data class MultiDownload(val targetPlaylist: IPlaylist): BeatSaverUIEvent()
    data class DownloadMap(val bsMap: IMap,val targetPath:String,val targetPlaylistId:String): BeatSaverUIEvent()

    data class PlayPreviewMusicSegment(val map: IMap): BeatSaverUIEvent()

//    data class PauseDownload(val downloadTask: DownloadTask): BeatSaverUIEvent()
//    data class ResumeDownload(val downloadTask: DownloadTask) : BeatSaverUIEvent()
    data class MsgShown(val msgId: Long): BeatSaverUIEvent()

    data class ShowSnackBar(val message:String): BeatSaverUIEvent()
}

@OptIn(FlowPreview::class)
class BeatSaverViewModel constructor(
    private val viewModelCoroutineScope: CoroutineScope? = null,
) : ViewModel() {

    private val playlistRepository: PlaylistRepository by KoinJavaComponent.inject(PlaylistRepository::class.java)
    private val mapRepository: FSMapRepository by KoinJavaComponent.inject(FSMapRepository::class.java)
    private val userPreferenceRepository: UserPreferenceRepository by KoinJavaComponent.inject(UserPreferenceRepository::class.java)

    private val localViewModelScope = viewModelCoroutineScope ?: viewModelScope

    private val viewModelState = MutableStateFlow(
        BeatSaverViewModelState(
            isLoading = false,
            mapFlow = mapRepository.getPagingBSMap(MapFilterParam()).cachedIn(localViewModelScope),
            localMapIdSet = emptySet(),
            selectableLocalPlaylists = emptyList()
        )
    )
    val uiState = viewModelState
        .map(BeatSaverViewModelState::toUiState)
        .stateIn(
            localViewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init{
        localViewModelScope.listenLocalMapFlow()
        localViewModelScope.listenLocalPlaylistFlow()
        localViewModelScope.listenDownloadingTaskFlow()
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
                    when (res) {
                        is Result.Success -> {
                            viewModelState.update {
                                it.copy(
                                    selectableLocalPlaylists = res.successOr(emptyList()),
                                    isLoading = false
                                )
                            }
                        }
                        is Result.Error -> { showSnackBar("获取playlist失败") }
                    }
                }
        }
    }
    private fun CoroutineScope.listenDownloadingTaskFlow() {
//        launch {
//            downloaderRepository
//                .getDownloadTaskFlow()
//                .flowOn(Dispatchers.IO)
//                .collect{res->
//                    when(res){
//                        is Result.Success ->{
//                            val r = res.data.flatMap {task->
//                                if (task.taskType == DownloadType.MAP) {
//                                    listOf(task)
//                                } else {
//                                    task.subTasks!!.map {item->item.value }
//                                }
//                            }
//                            viewModelState.update {
//                                it.copy(
//                                    downloadTaskFlow = flowOf(r),
//                                    isLoading = false
//                                )
//                            }
//                        }
//                        else -> {}
//                    }
//                }
//        }
    }


    fun dispatchUiEvents(event: UIEvent){
        when(event){
            is BeatSaverUIEvent.SearchMapWithFilter -> { onSearchMapWithFilter(event.mapQueryState) }
            is BeatSaverUIEvent.ChangeMultiSelectMode -> { onMultiSelectChecked(event.checked) }
            is BeatSaverUIEvent.MapMultiSelected -> { onMapMultiSelected(event.map) }
            is BeatSaverUIEvent.ChangeTargetPlaylist -> { onSelectedPlaylistChanged(event.playlist) }
            is BeatSaverUIEvent.MultiDownload -> { onMultiDownload(event.targetPlaylist) }
            is BeatSaverUIEvent.DownloadMap -> { onDownloadMap(event.bsMap,event.targetPath,event.targetPlaylistId) }
            is BeatSaverUIEvent.PlayPreviewMusicSegment -> {
//                onPlayPreviewMusicSegment(event.map)
            }
//            is BeatSaverUIEvent.PauseDownload -> { onPauseDownload(event.downloadTask) }
//            is BeatSaverUIEvent.ResumeDownload -> { onResumeDownload(event.downloadTask) }
            is BeatSaverUIEvent.MapTapped -> {}
            is BeatSaverUIEvent.MsgShown -> { snackBarShown(event.msgId) }
            else -> {}
        }
    }

//    private fun onPauseDownload(downloadTask: DownloadTask) {
//        localViewModelScope.launch(Dispatchers.IO) {
//            downloaderRepository.pauseDownload(downloadTask.taskId)
//        }
//    }

//    private fun onResumeDownload(downloadTask: DownloadTask) {
//        localViewModelScope.launch(Dispatchers.IO) {
//           val res =  downloaderRepository.resumeDownload(downloadTask.taskId)
//            when(res){
//                is Result.Success -> {
//
//                }
//                else -> { showSnackBar("resume download failed"+res.errorMsg().message) }
//            }
//        }
//    }

//    private fun onPlayPreviewMusicSegment(map: IMap) {
//        localViewModelScope.launch(Dispatchers.IO) {
//            mediaPlayerManager.play(MediaPlayerManager.generateMapID(map),map.getMusicPreviewURI().toString())
//        }
//    }


    private fun onSearchMapWithFilter(filterState: MapQueryState) {
        viewModelState.update {
            it.copy(
                mapFilterPanelState = filterState,
                mapFlow = mapRepository.getPagingBSMap(filterState.toFilterParam()).cachedIn(localViewModelScope),
                selectedPlaylist = null,
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
        viewModelState.update {
            it.copy(
                multiSelectMode = checked,
                multiSelectedBSMap = emptySet()
            )
        }
    }
    private fun onMultiDownload(targetPlaylist: IPlaylist) {
//        val mapToBeDownload = viewModelState.value.multiSelectedBSMap
//        viewModelState.update {
//            it.copy(
//                multiSelectMode = false,
//                multiSelectedBSMap = emptySet()
//            )
//        }
//        localViewModelScope.launch(Dispatchers.IO) {
//            mapRepository.batchInsertBSMap(mapToBeDownload.toList().map { it as BSMapView })
//            downloaderRepository.batchEnqueue(targetPlaylist,mapToBeDownload.toList())
//        }
    }

    fun onDownloadMap(bsMap: IMap,targetPath:String,targetPlaylistId:String) {
//        if (downloader.isMapCurrentlyDownloading(bsMap.getID())) {
//            downloader.cancelDownload(downloader.getRunningDownload(bsMap.getID())?.downloadId)
//        } else {
//            downloadMap(bsMap,targetPath,targetPlaylistId)
//        }
    }


    private fun showSnackBar(
        msg: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: (() -> Unit)? = null
    ) {
        val snackBarMessages = viewModelState.value.snackBarMessages + SnackBarMessage(
            id = UUID.randomUUID().mostSignificantBits,
            message = msg,
            actionLabel = actionLabel,
            action = action,
            duration = duration
        )
        viewModelState.update { vmState ->
            vmState.copy(snackBarMessages = snackBarMessages, isLoading = false)
        }
    }

    fun snackBarShown(snackBarId: Long) {
        viewModelState.update { currentUiState ->
            val snackBarMessages = currentUiState.snackBarMessages.filterNot { it.id == snackBarId }
            currentUiState.copy(snackBarMessages = snackBarMessages)
        }
    }
}