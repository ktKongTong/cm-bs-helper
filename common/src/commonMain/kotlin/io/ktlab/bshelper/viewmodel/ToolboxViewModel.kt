package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.model.UserPreference
import io.ktlab.bshelper.model.vo.GlobalScanStateEnum
import io.ktlab.bshelper.model.vo.PlaylistScanState
import io.ktlab.bshelper.model.vo.PlaylistScanStateEnum
import io.ktlab.bshelper.model.vo.ScanState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import org.koin.java.KoinJavaComponent.inject
import java.util.UUID

sealed class ToolboxUIEvent: UIEvent() {
    data class SelectPlaylistTobeScan(val playlistScanState: PlaylistScanState) : UIEvent()
    data class ScanPlaylistTapped(val dirPath: String? = null) : ToolboxUIEvent()
    data object ScanSelectedPlaylist : UIEvent()
    data object ClearScanState : ToolboxUIEvent()
    data class UpdateDefaultManageDir(val path:String):ToolboxUIEvent()
    //    data class MapMultiSelectTapped : ToolboxUIEvent()

    data object DeleteAllDownloadTasks : ToolboxUIEvent()
//    data class PauseOrStartDownloadTask(val downloadTask: DownloadTask): ToolboxUIEvent()
    data class MsgShown(val msgId: Long): ToolboxUIEvent()
    data class ShowSnackBar(val message:String): ToolboxUIEvent()
}




data class ToolboxUiState(
    val isLoading: Boolean,
    val snackBarMessages: List<SnackBarMessage>,
    val scanState: ScanState = ScanState.getDefaultInstance(),
    val userPreferenceState: UserPreference,
//    val downloadTasks: List<DownloadTask> = emptyList(),
)

data class ToolboxViewModelState constructor(
    val isLoading: Boolean = false,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val userPreferenceState: UserPreference,
    val scanState: ScanState = ScanState.getDefaultInstance(),
//    val downloadTasks: List<DownloadTask> = emptyList(),
) {

    fun toUiState(): ToolboxUiState = ToolboxUiState(
        isLoading = isLoading,
        snackBarMessages = snackBarMessages,
        scanState = scanState,
        userPreferenceState = userPreferenceState,
//        downloadTasks = downloadTasks,
    )
}
class ToolboxViewModel(
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    viewModelCoroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val localViewModelScope = viewModelScope
    private val viewModelState = MutableStateFlow(
        ToolboxViewModelState(
            isLoading = true,
            snackBarMessages = emptyList(),
            userPreferenceState = UserPreference.getDefaultUserPreference()
        )
    )
    val uiState = viewModelState
        .map(ToolboxViewModelState::toUiState)
        .stateIn(
            localViewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        localViewModelScope.observeUserPreference()
        localViewModelScope.observeDownloadTasks()
    }

    private fun CoroutineScope.observeUserPreference() {
        launch {
            userPreferenceRepository.getUserPreference().collect{userPreference->
                viewModelState.update { state ->
                    state.copy(
                        userPreferenceState = userPreference
                    )
                }
            }
        }
    }

    private fun CoroutineScope.observeDownloadTasks(){
        launch {
//            downloaderRepository.getDownloadTaskFlow().collect{res->
//                when(res){
//                    is Result.Success -> {
//                        viewModelState.update { state ->
//                            state.copy(
//                                downloadTasks = res.data
//                            )
//                        }
//                    }
//                    is Result.Error -> {
//                    }
//                }
//            }
        }
    }

    fun dispatchUiEvents(event: UIEvent){
        when(event){
            is ToolboxUIEvent.ShowSnackBar -> {

            }
            is ToolboxUIEvent.ScanPlaylistTapped -> {
                onScanPlaylist(event.dirPath?:"")
            }
            is ToolboxUIEvent.ClearScanState -> {
                viewModelState.update { state ->
                    //  cancelScanJob()
                    state.copy(scanState = ScanState.getDefaultInstance())
                }
            }
            is ToolboxUIEvent.SelectPlaylistTobeScan -> {
                onSelectPlaylistToBeScan(event.playlistScanState)
            }
            is ToolboxUIEvent.ScanSelectedPlaylist -> {
                onScanSelectedPlaylist()
            }
            is ToolboxUIEvent.DeleteAllDownloadTasks -> {
//                localViewModelScope.launch(Dispatchers.IO) {
//                    downloaderRepository.clearDownloadDB()
//                }
            }
//            is ToolboxUIEvent.PauseOrStartDownloadTask -> {
//                localViewModelScope.launch(Dispatchers.IO) {
//                    if (event.downloadTask.status == DownloadStatus.PAUSED)
//                        downloaderRepository.resumeDownload(event.downloadTask.taskId)
//                    else if (event.downloadTask.status == DownloadStatus.DOWNLOADING)
//                        downloaderRepository.pauseDownload(event.downloadTask.taskId)
//                }
//            }
            is ToolboxUIEvent.UpdateDefaultManageDir -> {
//                localViewModelScope.launch {
//                    userPreferenceRepository.updateDefaultManagePath(event.path)
//                }
            }
            is ToolboxUIEvent.MsgShown -> {
                snackBarShown(event.msgId)
            }
            else -> {}
        }
    }
    private fun onScanSelectedPlaylist(){
        localViewModelScope.launch {
            playlistRepository.scanFSMapInPlaylists(viewModelState.value.scanState)
                .flowOn(Dispatchers.IO)
                .collect{
                    viewModelState.update { state ->
                        state.copy(scanState = state.scanState.copy(
                            state = if (it.state == GlobalScanStateEnum.SCAN_COMPLETE) GlobalScanStateEnum.SCAN_COMPLETE else GlobalScanStateEnum.SCANNING_MAPS,
                            playlistStates = it.playlistStates,
                        ))
                    }
                }
        }
    }
    private fun onSelectPlaylistToBeScan(playlistScanState: PlaylistScanState){
        viewModelState.value.scanState.playlistStates.find {
            it.value.playlistId == playlistScanState.playlistId
        }?.update { state ->
            state.copy(state = if (state.state== PlaylistScanStateEnum.SELECTED_BUT_NOT_START)
                PlaylistScanStateEnum.UNSELECTED
            else PlaylistScanStateEnum.SELECTED_BUT_NOT_START)
        }
//        }
//        viewModelState.update { state ->
//
//            state.copy(scanState = state.scanState.copy(
//                playlistStates = state.scanState.playlistStates.filter {
//                    it.value.playlistId == playlistScanState.playlistId
//                }.),
//            )
//        }
    }

    private fun onScanPlaylist(dirPath:String) {
        localViewModelScope.launch {
            playlistRepository.scanPlaylist(dirPath)
                .flowOn(Dispatchers.IO)
                .collect{
                    viewModelState.update { state ->
                        state.copy(scanState = it)
                    }
                }
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