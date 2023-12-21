package io.ktlab.bshelper.ui.viewmodel

import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.download.IDownloadTask
import io.ktlab.bshelper.model.enums.GameType
import io.ktlab.bshelper.model.scanner.ScanStateEventEnum
import io.ktlab.bshelper.model.scanner.ScanStateV2
import io.ktlab.bshelper.ui.event.EventBus
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.ToolboxUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
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

    val userPreference: UserPreferenceV2,
    val manageDirs: List<SManageFolder>
)

data class ToolboxViewModelState(
    val isLoading: Boolean = false,
    val scanState: ScanStateV2 = ScanStateV2(),
    val downloadTasks: List<IDownloadTask> = emptyList(),
    val userPreference: UserPreferenceV2,
    val manageDirs: List<SManageFolder>
) {
    fun toUiState(): ToolboxUiState =
        ToolboxUiState(
            isLoading = isLoading,
            scanState = scanState,
            downloadTasks = downloadTasks,
            userPreference = userPreference,
            manageDirs = manageDirs,
        )
}
private val logger = KotlinLogging.logger {}
class ToolboxViewModel(
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val downloaderRepository: DownloaderRepository,
) : ViewModel() {
    private val viewModelState =
        MutableStateFlow(ToolboxViewModelState(
            isLoading = true,
            userPreference = userPreferenceRepository.getCurrentUserPreference(),
            manageDirs = emptyList()
        ))
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
        viewModelScope.launch {
            playlistRepository.getAllManageFolder().collect {
                viewModelState.update { vmState ->
                    when(it) {
                        is Result.Success -> {
                            vmState.copy(manageDirs = it.data)
                        }
                        is Result.Error -> {
                            EventBus.publish(GlobalUIEvent.ShowSnackBar("获取管理目录失败, ${it.exception.message}"))
                            vmState.copy(manageDirs = emptyList())
                        }
                    }
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
                onScanPlaylist(event.dirPath,event.gameType)
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

    private fun onScanPlaylist(dirPath: String,gameType: GameType) {
        if (dirPath.isEmpty()) {
            viewModelScope.launch { EventBus.publish(GlobalUIEvent.ShowSnackBar("请选择文件夹")) }
            return
        }

        viewModelScope.launch {
            val res = playlistRepository.createManageDir(dirPath,gameType)
            when(res) {
                is Result.Success -> {
                    logger.debug { "createManageDir success" }
                    playlistRepository.scanPlaylist(dirPath,res.data.id)
                        .flowOn(Dispatchers.IO)
                        .collect {
                            if (it.state == ScanStateEventEnum.SCAN_COMPLETE) {
                                playlistRepository.updateActiveManageDirById(true,res.data.id)
                                viewModelState.update { state -> state.copy(scanState = it) }
                                if (userPreferenceRepository.getCurrentUserPreference().currentManageFolder == null) {
                                    userPreferenceRepository.updateCurrentManageFolder(res.data.copy(active = true))
                                }
                                this.cancel()
                            }else {
                                viewModelState.update { state -> state.copy(scanState = it) }
                            }
                        }
                }
                is Result.Error -> {
                    logger.debug { "createManageDir error" }
                    viewModelScope.launch { EventBus.publish(GlobalUIEvent.ReportError(res.exception,"创建管理目录失败")) }
                }
            }

        }
    }
}
