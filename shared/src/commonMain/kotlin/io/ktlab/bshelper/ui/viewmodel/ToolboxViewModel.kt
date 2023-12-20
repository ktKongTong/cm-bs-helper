package io.ktlab.bshelper.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.event.EventBus
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
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

data class ToolboxUiState(
    val isLoading: Boolean,
    val scanState: ScanStateV2,
    val downloadTasks: List<IDownloadTask> = emptyList(),
)

data class ToolboxViewModelState(
    val isLoading: Boolean = false,
    val scanState: ScanStateV2 = ScanStateV2(),
    val downloadTasks: List<IDownloadTask> = emptyList(),
) {
    fun toUiState(): ToolboxUiState =
        ToolboxUiState(
            isLoading = isLoading,
            scanState = scanState,
            downloadTasks = downloadTasks,
        )
}
private val logger = KotlinLogging.logger {}
class ToolboxViewModel(
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val downloaderRepository: DownloaderRepository,
) : ViewModel() {
    private val viewModelState =
        MutableStateFlow(ToolboxViewModelState(isLoading = true))
    val uiState =
        viewModelState
            .map(ToolboxViewModelState::toUiState)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )

    init {
        logger.debug { "init ToolboxViewModel" }
        viewModelScope.launch { EventBus.subscribe<ToolboxUIEvent> { dispatchUiEvents(it) } }
        viewModelScope.observeDownloadTasks()
    }

    private fun CoroutineScope.observeDownloadTasks() {
        launch {
            downloaderRepository.getDownloadTaskFlow().flowOn(Dispatchers.IO).collect { res ->
                viewModelState.update { state -> state.copy(downloadTasks = res)
                }
            }
        }
    }

    private fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is ToolboxUIEvent.ClearScanState -> {
                viewModelState.update { state ->
                    state.copy(scanState = ScanStateV2.getDefaultInstance())
                }
            }
            is ToolboxUIEvent.ClearLocalData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    playlistRepository.clear()
                }
            }
            is ToolboxUIEvent.ScanPlaylist -> {
                if(event.dirPath.isEmpty()) {
                    viewModelScope.launch { EventBus.publish(GlobalUIEvent.ShowSnackBar("请先选择文件夹")) }
                    return
                }
                viewModelScope.launch { EventBus.publish(
                    GlobalUIEvent.ShowSnackBar("扫描会清除当前数据库内已扫描到的数据，继续吗？", "继续") {
                        onScanPlaylist(event.dirPath)
                    }
                ) }
            }
            is ToolboxUIEvent.DeleteAllDownloadTasks -> {
                viewModelScope.launch(Dispatchers.IO) {
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
            is ToolboxUIEvent.UpdateManageDir -> {
                viewModelScope.launch {
                    userPreferenceRepository.updateCurrentManageDir(event.path)
//                    userPreferenceRepository.updateUserPreference(viewModelState.value.userPreferenceState.copy(event.path))
                }
            }
            is ToolboxUIEvent.UpdateThemeColor -> {
                viewModelScope.launch {
                    userPreferenceRepository.updateThemeColor(event.color)
                }
            }
            is ToolboxUIEvent.UpdateImageSource -> {
                viewModelScope.launch {
                    userPreferenceRepository.updateImageSource(event.type,event.source)
                }
            }
            else -> {}
        }
    }

    private fun onScanPlaylist(dirPath: String) {
        if (dirPath.isEmpty()) {
            viewModelScope.launch { EventBus.publish(GlobalUIEvent.ShowSnackBar("请选择文件夹")) }
            return
        }
        viewModelScope.launch {
            playlistRepository.clear()
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
