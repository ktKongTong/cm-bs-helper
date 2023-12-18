package io.ktlab.bshelper.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.UserPreference
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.vo.ScanStateV2
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

sealed class ToolboxUIEvent : UIEvent() {

    data class ScanPlaylist(val dirPath: String) : ToolboxUIEvent()

    data object ClearScanState : ToolboxUIEvent()

    data object ClearLocalData : ToolboxUIEvent()

    data class UpdateDefaultManageDir(val path: String) : ToolboxUIEvent()
    //    data class MapMultiSelectTapped : ToolboxUIEvent()

    data object DeleteAllDownloadTasks : ToolboxUIEvent()

    data class RemoveDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class ResumeDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class PauseDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class CancelDownloadTask(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class RetryDownloadMap(val downloadTask: IDownloadTask) : ToolboxUIEvent()

    data class UpdateManageDir(val path: String) : ToolboxUIEvent()
}

data class ToolboxUiState(
    val isLoading: Boolean,
    val scanState: ScanStateV2,
    val userPreferenceState: UserPreference,
    val downloadTasks: List<IDownloadTask> = emptyList(),
)

data class ToolboxViewModelState constructor(
    val isLoading: Boolean = false,
    val userPreferenceState: UserPreference,
    val scanState: ScanStateV2 = ScanStateV2(),
    val downloadTasks: List<IDownloadTask> = emptyList(),
) {
    fun toUiState(): ToolboxUiState =
        ToolboxUiState(
            isLoading = isLoading,
            scanState = scanState,
            userPreferenceState = userPreferenceState,
            downloadTasks = downloadTasks,
        )
}
private val logger = KotlinLogging.logger {}
class ToolboxViewModel(
    private val globalViewModel: GlobalViewModel,
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val downloaderRepository: DownloaderRepository,
) : ViewModel() {
    private val localViewModelScope = viewModelScope
    private val viewModelState =
        MutableStateFlow(
            ToolboxViewModelState(
                isLoading = true,
                userPreferenceState = UserPreference.getDefaultUserPreference(),
            ),
        )
    val uiState =
        viewModelState
            .map(ToolboxViewModelState::toUiState)
            .stateIn(
                localViewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )

    init {
        logger.debug { "init ToolboxViewModel" }
        localViewModelScope.observeUserPreference()
        localViewModelScope.observeDownloadTasks()
    }

    private fun CoroutineScope.observeUserPreference() {
        launch {
            userPreferenceRepository.getUserPreference().collect { userPreference ->
                viewModelState.update { state ->
                    state.copy(
                        userPreferenceState = userPreference,
                    )
                }
            }
        }
    }

    private fun CoroutineScope.observeDownloadTasks() {
        launch {
            downloaderRepository.getDownloadTaskFlow().flowOn(Dispatchers.IO).collect { res ->
                viewModelState.update { state -> state.copy(downloadTasks = res)
                }
            }
        }
    }

    fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is GlobalUIEvent -> {
                globalViewModel.dispatchUiEvents(event)
            }
            is ToolboxUIEvent.ClearScanState -> {
                viewModelState.update { state ->
                    state.copy(scanState = ScanStateV2.getDefaultInstance())
                }
            }
            is ToolboxUIEvent.ClearLocalData -> {
                localViewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.clear()
                }
            }
            is ToolboxUIEvent.ScanPlaylist -> {
                onScanPlaylist(event.dirPath)
            }
            is ToolboxUIEvent.DeleteAllDownloadTasks -> {
                localViewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.clearHistory()
                }
            }
            is ToolboxUIEvent.RetryDownloadMap -> {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.retry(event.downloadTask)
                }
            }
            is ToolboxUIEvent.CancelDownloadTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.cancel(event.downloadTask)
                }
            }
            is ToolboxUIEvent.RemoveDownloadTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.remove(event.downloadTask)
                }
            }
            is ToolboxUIEvent.PauseDownloadTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.pause(event.downloadTask)
                }
            }
            is ToolboxUIEvent.ResumeDownloadTask -> {
                viewModelScope.launch(Dispatchers.IO) {
                    downloaderRepository.resume(event.downloadTask)
                }
            }
            is ToolboxUIEvent.UpdateDefaultManageDir -> {
                localViewModelScope.launch {
                    userPreferenceRepository.updateUserPreference(viewModelState.value.userPreferenceState.copy(event.path))
                }
            }
            else -> {}
        }
    }

    private fun onScanPlaylist(dirPath: String) {
        if (dirPath.isEmpty()) {
            globalViewModel.showSnackBar("请选择文件夹")
            return
        }
        localViewModelScope.launch {
            playlistRepository.scanPlaylist(dirPath)
                .flowOn(Dispatchers.IO)
                .collect {
                    viewModelState.update { state ->
                        state.copy(scanState = it)
                    }
                }
        }
    }
}