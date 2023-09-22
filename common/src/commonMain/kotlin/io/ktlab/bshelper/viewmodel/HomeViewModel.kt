package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.model.BSHelperDatabase
import io.ktlab.bshelper.model.IMap
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.api.BeatSaverAPI
import io.ktlab.bshelper.model.enums.SortKey
import io.ktlab.bshelper.model.enums.SortType
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
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
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.repository.FSMapRepository

data class HomeViewModelState(
    val playlists: List<IPlaylist> = emptyList(),
    val selectedPlaylistId: String? = null,
//    val userPreferenceState: UserPreference,
    val selectedMapId: String? = null,
    val isPlaylistOpen: Boolean = false,
    val mapListState: MapListState,
    val isLoading: Boolean = false,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val searchInput: String = "",
){
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
    val selectedMap: IMap?=null,
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
        val selectedPlaylist: IPlaylist?=null,
        val isPlaylistOpen: Boolean,
        val mapListState: MapListState,
        override val isLoading: Boolean,
        override val snackBarMessages: List<SnackBarMessage> = emptyList(),
        override val searchInput: String
    ) : HomeUiState {
        fun isMapEmpty(): Boolean {
            return !this.isPlaylistOpen || this.selectedPlaylist == null
        }
    }
}

sealed class HomeUIEvent: UIEvent(){
    data class ShowSnackBar(val message:String): HomeUIEvent()
    data class PlaylistTapped(val playlistId: String) : HomeUIEvent()
    data class MapTapped(val mapId: String) : HomeUIEvent()
    data class ChangeMapListSortRule(val sortRule:Pair<SortKey,SortType>):HomeUIEvent()
    data class MapMultiSelected(val map:IMap):HomeUIEvent()
    data class ChangeMultiSelectMode(val checked:Boolean):HomeUIEvent()
    data class MultiDeleteAction(val mapSet:Set<IMap>):HomeUIEvent()
    data class MultiMoveAction(val mapSet:Set<IMap>,val targetPlaylist:IPlaylist):HomeUIEvent()
    data class MultiMapFullChecked(val mapMap:Map<String,IMap>):HomeUIEvent()
    data class MultiMapOppoChecked(val mapMap:Map<String,IMap>):HomeUIEvent()
    data class PlayPreviewMusicSegment(val map:IMap):HomeUIEvent()
    data class CreateNewPlaylist(val name:String):HomeUIEvent()
    data class MergePlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
    data class BuildBPList(val targetPlaylist:IPlaylist):HomeUIEvent()
    data class SharePlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
    data class ExportPlaylistAsKey(val playlist:IPlaylist):HomeUIEvent()
    data class DeletePlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
    data class EditPlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
    data class ImportPlaylist(val key:String,val targetPlaylist:IPlaylist):HomeUIEvent()
    data class SnackBarShown(val msgId:Long):HomeUIEvent()
//    data class DividePlaylist(val targetPlaylist:IPlaylist):HomeUIEvent()
}


class HomeViewModel(
    viewModelCoroutineScope: CoroutineScope? = null,
)
    : ViewModel()
{
    private val playlistRepository: PlaylistRepository by inject(PlaylistRepository::class.java)
    private val mapRepository: FSMapRepository by inject(FSMapRepository::class.java)
    private val localViewModelScope = viewModelCoroutineScope ?: viewModelScope

    private val viewModelState = MutableStateFlow(HomeViewModelState(
        isLoading = true,
        isPlaylistOpen = false,
//        userPreferenceState = UserPreference.getDefaultInstance(),
        mapListState = MapListState(
            isMapOpen = false,
            isLoading = false,
            mapFlow = null,
            isMapMultiSelectMode = false,
            multiSelectedMapHashMap = emptyMap(),
            searchInput = "",
        )
    ))
    val uiState = viewModelState
        .map(HomeViewModelState::toUiState)
        .stateIn(
            localViewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        refreshPlayLists()
    }

    fun dispatchUiEvents(event: UIEvent){
        when(event){
            is HomeUIEvent.ShowSnackBar -> {}
            is HomeUIEvent.SnackBarShown -> {

            }
//            is HomeUIEvent.ImportPlaylist ->{ importPlaylist(event.key,event.targetPlaylist) }
            is HomeUIEvent.DeletePlaylist -> {}
            is HomeUIEvent.EditPlaylist -> {}
            is HomeUIEvent.PlaylistTapped -> { onPlaylistTapped(event.playlistId) }
            is HomeUIEvent.MapTapped -> {}
//            is HomeUIEvent.ChangeMapListSortRule -> {onChangeMapListSortRule(event.sortRule) }
//            is HomeUIEvent.ChangeMultiSelectMode -> { onMapMultiSelectedModeChecked(event.checked) }
//            is HomeUIEvent.MultiMapFullChecked -> { onMultiMapFullChecked(event.mapMap) }
//            is HomeUIEvent.MultiMapOppoChecked -> { onMultiMapOppoChecked(event.mapMap) }
//            is HomeUIEvent.MapMultiSelected -> { onMapMultiSelected(event.map) }
//            is HomeUIEvent.MultiDeleteAction -> { onMultiDeleteAction(event.mapSet) }
//            is HomeUIEvent.MultiMoveAction -> { onMultiMoveAction(event.mapSet,event.targetPlaylist) }
            is HomeUIEvent.ExportPlaylistAsKey -> {
//                viewModelScope.launch subroutine@{
//                    playlistRepository
//                        .exportPlaylistAsKey(event.playlist)
//                        .collect {res->
//                            Log.e("HomeViewModel export:",res.successOr(""))
//                            showSnackBar(
//                                msg = res.successOr(""),
//                                actionLabel = "copy",
//                                action = {
//                                    copyToClipboard(res.successOr(""))
//                                },
//                                duration = SnackbarDuration.Long
//                            )
//                            this@subroutine.cancel()
//                        }
//                }
            }
            is HomeUIEvent.PlayPreviewMusicSegment -> {
//                viewModelScope.launch(Dispatchers.IO) {
//                    mediaPlayerManager.play(MediaPlayerManager.generateMapID(event.map),event.map.getMusicPreviewURI().toString())
//                }
            }
//            is HomeUIEvent.CreateNewPlaylist -> { onCreateNewPlaylist(event.name) }
//            is HomeUIEvent.MsgShown -> { snackBarShown(event.msgId) }
        }
    }

    private fun refreshPlayLists(managerDir:String = "") {
//        val managerPath = managerDir.ifEmpty { viewModelState.value.userPreferenceState.defaultManageDir }
        viewModelState.update { it.copy(isLoading = true) }
        localViewModelScope.launch {
            playlistRepository
                .getAllPlaylist()
//                .getAllPlaylistByManagerDir(managerPath)
                .flowOn(Dispatchers.IO)
                .collect {
                    when (it) {
                        is Result.Success -> {
                            viewModelState.update { vmState ->
                                vmState.copy(playlists = it.data, isLoading = false)
                            }
                        }
                        is Result.Error -> {
                            showSnackBar(
                                msg = "error loading playlists",
                                actionLabel = "retry",
                                action = { refreshPlayLists() }
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
    private fun interactedWithPlaylistDetails() = localViewModelScope.launch {
        val playlistId = viewModelState.value.selectedPlaylistId ?: return@launch
        viewModelState.update {
            it.copy(
                mapListState = it.mapListState.copy(
                    isLoading = true,
                    selectedMap = null,
                    isMapOpen = false,
                    isMapMultiSelectMode = false,
                    multiSelectedMapHashMap = emptyMap(),
                    mapFlow = mapRepository.getFlowMapsByPlaylistId(playlistId).flowOn(Dispatchers.IO)
                )
            )
        }
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