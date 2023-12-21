package io.ktlab.bshelper.ui.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.text.AnnotatedString
import io.github.oshai.kotlinlogging.KotlinLogging
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import io.ktlab.bshelper.BuildConfig
import io.ktlab.bshelper.data.Event
import io.ktlab.bshelper.data.RuntimeEventFlow
import io.ktlab.bshelper.data.api.ToolAPI
import io.ktlab.bshelper.data.repository.DownloaderRepository
import io.ktlab.bshelper.data.repository.ManageFolderRepository
import io.ktlab.bshelper.data.repository.PlaylistRepository
import io.ktlab.bshelper.data.repository.UserPreferenceRepository
import io.ktlab.bshelper.model.Result
import io.ktlab.bshelper.model.SManageFolder
import io.ktlab.bshelper.model.UserPreferenceV2
import io.ktlab.bshelper.model.dto.response.APIRespResult
import io.ktlab.bshelper.platform.IBSClipBoardManager
import io.ktlab.bshelper.platform.MediaPlayer
import io.ktlab.bshelper.ui.event.EventBus
import io.ktlab.bshelper.ui.event.GlobalUIEvent
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.*

sealed class IMedia {
    data class MapAudioPreview(
        val id: String,
        val url: String,
        val avatarUrl: String? = null,
    ) : IMedia()

    data class MapPreview(
        val id: String,
        val url: String,
        val avatarUrl: String? = null,
    ) : IMedia()

    data object None : IMedia()
}

enum class CurrentMediaState {
    Playing,
    Paused,
    Stopped,
}

sealed interface MediaEvent {
    data object Play : MediaEvent

    data object Pause : MediaEvent
}

data class GlobalViewModelState(
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val isLoading: Boolean = false,
    val currentMedia: IMedia,
    val currentMediaState: CurrentMediaState,
    val errorDialogState: ErrorDialogState? = null,
    val userPreference: UserPreferenceV2,
    val manageDirs: List<SManageFolder>
) {
    fun toUiState(): GlobalUiState {

        logger.debug { "ui changed $currentMedia" }
        return GlobalUiState(
            isLoading = isLoading,
            snackBarMessages = snackBarMessages,
            currentMedia = currentMedia,
            currentMediaState = currentMediaState,
            userPreference = userPreference,
            errorDialogState = errorDialogState,
            manageDirs = manageDirs,
        )
    }
}

data class FormDialogState(
    val title: String,
    val message: String,
    val confirmLabel: String? = null,
    val cancelLabel: String? = null,
    val onConfirm: (() -> Unit)? = null,
    val onCancel: (() -> Unit)? = null,
)
data class SnackBarMessage(
    val id: Long,
    val message: String,
    val actionLabel: String? = null,
    val action: (() -> Unit)? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)
data class ErrorDialogState(
    val title: String,
    val message: String,
    val confirmLabel: String? = null,
    val cancelLabel: String? = null,
    val onConfirm: (() -> Unit)? = null,
    val onCancel: (() -> Unit)? = null,
)

data class GlobalUiState(
    val isLoading: Boolean,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val currentMedia: IMedia,
    val currentMediaState: CurrentMediaState = CurrentMediaState.Stopped,
    val errorDialogState: ErrorDialogState? = null,
    val userPreference: UserPreferenceV2,
    val manageDirs: List<SManageFolder>
)

private val logger = KotlinLogging.logger {}

class GlobalViewModel(
    private val runtimeEventFlow: RuntimeEventFlow,
    private val mediaPlayer: MediaPlayer,
    private val clipboardManager: IBSClipBoardManager,
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val manageFolderRepository: ManageFolderRepository,
    private val downloaderRepository: DownloaderRepository,
    private val toolAPI: ToolAPI,
) : ViewModel() {

    private val viewModelState =
        MutableStateFlow(
            GlobalViewModelState(
                isLoading = true,
                snackBarMessages = emptyList(),
                currentMedia = IMedia.None,
                currentMediaState = CurrentMediaState.Stopped,
                userPreference = UserPreferenceV2.getDefaultUserPreference(),
                manageDirs = emptyList(),
            ),
        )
    val uiState =
        viewModelState
            .map(GlobalViewModelState::toUiState)
            .stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                viewModelState.value.toUiState(),
            )
    // add error handler for viewModelScope

    private val exceptionHandler =
        CoroutineExceptionHandler { coroutineContext, throwable ->
            runtimeEventFlow.sendEvent(Event.ExceptionEvent(throwable))
        }
    init {

        logger.debug { "init GlobalViewModel" }
        viewModelScope.launch {
            EventBus.subscribe<GlobalUIEvent> { dispatchUiEvents(it) }
        }

        viewModelScope.launch {
            playlistRepository.getAllManageFolder().collect {
                viewModelState.update { vmState ->
                    when(it) {
                        is Result.Success -> {
                            vmState.copy(manageDirs = it.data)
                        }
                        is Result.Error -> {
                            showSnackBar("获取管理目录失败, ${it.exception.message}")
                            vmState.copy(manageDirs = emptyList())
                        }
                    }
                }
            }
        }

        viewModelScope.launch {
            userPreferenceRepository.getUserPreference().collect {
                logger.debug { "userPreference Update" }
                viewModelState.update { vmState ->
                    vmState.copy(userPreference = it)
                }
            }
        }
        runtimeEventFlow.subscribeEvent { event ->
            logger.debug { "runtime event:$event" }
            when (event) {
                is Event.ExceptionEvent -> {
                    dispatchUiEvents(GlobalUIEvent.ReportError(event.throwable, event.shortDescription))
                }
                is Event.MessageEvent -> {
                    dispatchUiEvents(GlobalUIEvent.ShowSnackBar(event.message))
                }

                else -> {}
            }
        }
    }

    fun dispatchUiEvents(event: UIEvent) {
        when (event) {
            is GlobalUIEvent.CheckVersion -> {
                onCheckVersion()
            }
            is GlobalUIEvent.ShowSnackBar -> {
                showSnackBar(msg = event.message)
            }
            is GlobalUIEvent.SnackBarShown -> {
                snackBarShown(snackBarId = event.msgId)
            }
            is GlobalUIEvent.WriteToClipboard -> {
                writeToClipboard(text = event.text)
            }
            is GlobalUIEvent.PlayMedia -> {
                playMedia(media = event.media)
            }
            is GlobalUIEvent.OnMediaEvent -> {
                playMediaEvent(event = event.event)
            }
            is GlobalUIEvent.ReportError -> {
                showSnackBar(msg = "Error: ${event.throwable.message}", actionLabel = "Check Details", action = {
                    viewModelState.update {
                        it.copy(
                            errorDialogState =
                                ErrorDialogState(
                                    title = "Error",
                                    message = (event.shortDescription ?: "") + ": " + event.throwable.stackTraceToString(),
                                    confirmLabel = "确认",
                                    cancelLabel = "复制以报告",
                                    onConfirm = { clearErrorDialog() },
                                    onCancel = {
                                        writeToClipboard(text = event.throwable.stackTraceToString())
                                        clearErrorDialog()
                                    },
                                ),
                        )
                    }
                })
            }
            is GlobalUIEvent.CreatePlaylist -> {
                event.playlist?.let {
                    viewModelScope.launch(exceptionHandler) {
                        playlistRepository.createNewPlaylist(it.name, it.bsPlaylistId, it.description, it.customTags)
                    }
                }
            }
            is GlobalUIEvent.UpdateManageFolder -> {
                viewModelScope.launch(exceptionHandler) {
                    viewModelState.value.userPreference.currentManageFolder?.let {
                        downloaderRepository.pauseAllByManageFolderId(it.id)
                    }
                    userPreferenceRepository.updateCurrentManageFolder(event.manageFolder)
                }
            }
            is GlobalUIEvent.DeleteManageFolder -> {
                viewModelScope.launch(exceptionHandler) {
                    val first = viewModelState.value.manageDirs.firstOrNull { it.id != event.manageFolder.id }
                    userPreferenceRepository.updateCurrentManageFolder(first)
                    manageFolderRepository.deleteManageFolder(event.manageFolder)
                }
            }
            is GlobalUIEvent.ClearAllData -> {
                viewModelScope.launch(exceptionHandler) {
                    userPreferenceRepository.updateCurrentManageFolder(null)
                    manageFolderRepository.clearAllData()
                }
            }
        }
    }

    private fun clearErrorDialog() {
        viewModelState.update { vmState ->
            vmState.copy(errorDialogState = null)
        }
    }

    private fun playMediaEvent(event: MediaEvent) {
        when (event) {
            is MediaEvent.Play -> {
                if (viewModelState.value.currentMediaState != CurrentMediaState.Paused) {
                    return
                }
                viewModelState.update { vmState ->
                    vmState.copy(currentMediaState = CurrentMediaState.Playing)
                }
                mediaPlayer.play()
            }
            is MediaEvent.Pause -> {
                if (viewModelState.value.currentMediaState != CurrentMediaState.Playing) {
                    return
                }
                viewModelState.update { vmState ->
                    vmState.copy(currentMediaState = CurrentMediaState.Paused)
                }
                mediaPlayer.pause()
            }
        }
    }

    fun playMedia(media: IMedia) {
        viewModelState.update { vmState ->
            vmState.copy(currentMedia = media, currentMediaState = CurrentMediaState.Stopped)
        }
        if (media is IMedia.MapAudioPreview) {
            viewModelScope.launch(exceptionHandler+Dispatchers.IO) {
                mediaPlayer.loadAndPlay(
                    media.url,
                    {
                        viewModelState.update { vmState ->
                            logger.debug { "mediaPlayer prepared url:${media.url}" }
                            vmState.copy(currentMediaState = CurrentMediaState.Playing, currentMedia = media)
                        }
                    },
                    {
                        viewModelState.update { vmState ->
                            logger.debug { "mediaPlayer completion url:${media.url}" }
                            vmState.copy(currentMediaState = CurrentMediaState.Stopped, currentMedia = IMedia.None)
                        }
                    },
                )
            }
        }
//        else if (media is IMedia.MapPreview){
//            // a map preview component
//        }
    }

    fun writeToClipboard(
        text: String,
        label: String = "",
    ) {
        clipboardManager.setText(AnnotatedString(text))
        showSnackBar(msg = "已复制到剪贴板")
//        clipboardManager.setPrimaryClip(ClipData.newPlainText(label,text))
    }

    private fun onCheckVersion() {
        //
        viewModelScope.launch(Dispatchers.IO) {
            val res = toolAPI.getLatestVersion()
            when(res) {
                is APIRespResult.Success -> {
                    val latestVersion = res.data
                    val currentVersion = BuildConfig.APP_VERSION
                    latestVersion.toVersionOrNull()?.let {
                        if (it > currentVersion.toVersion()) {
                            showSnackBar("有新版本可用：$latestVersion, 可访问: https://github.com/ktKongTong/cm-bs-helper/releases 获取最新版本")
                        } else {
                            showSnackBar("当前已是最新版本")
                        }
                    }
                }
                is APIRespResult.Error -> {
                    showSnackBar("当前已是最新版本")
                }
            }
        }
    }

    fun showSnackBar(
        msg: String,
        actionLabel: String? = null,
        duration: SnackbarDuration = SnackbarDuration.Short,
        action: (() -> Unit)? = null,
    ) {
        val snackBarMessages =
            viewModelState.value.snackBarMessages +
                SnackBarMessage(
                    id = UUID.randomUUID().mostSignificantBits,
                    message = msg,
                    actionLabel = actionLabel,
                    action = action,
                    duration = duration,
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
