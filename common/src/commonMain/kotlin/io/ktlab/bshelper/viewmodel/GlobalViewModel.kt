package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.text.AnnotatedString
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.model.UserPreference
import io.ktlab.bshelper.repository.Event
import io.ktlab.bshelper.repository.PlaylistRepository
import io.ktlab.bshelper.repository.RuntimeEventFlow
import io.ktlab.bshelper.repository.UserPreferenceRepository
import io.ktlab.bshelper.service.IBSClipBoardManager
import io.ktlab.bshelper.service.MediaPlayer
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.*

sealed class GlobalUIEvent: UIEvent(){
    data class ShowSnackBar(val message:String): GlobalUIEvent()
    data class SnackBarShown(val msgId:Long):GlobalUIEvent()
    data class ShowFormDialog(val formDialogState: FormDialogState):GlobalUIEvent()
    data object OnFormDialogDismiss:GlobalUIEvent()
    data class WriteToClipboard(val text:String):GlobalUIEvent()
//    data class MediaEvent(val media: IMedia):GlobalUIEvent()
    data class ReportError(val throwable: Throwable,val shortDescription:String?=null):GlobalUIEvent()
    data class PlayMedia(val media: IMedia):GlobalUIEvent()
    data class OnMediaEvent(val event: MediaEvent):GlobalUIEvent()
    data class CreatePlaylist(val playlist:FSPlaylist?):GlobalUIEvent()
}

sealed class IMedia{
    data class MapAudioPreview(
        val id: String,
        val url: String,
        val avatarUrl: String? = null,
    ): IMedia()
    data class MapPreview(
        val id: String,
        val url: String,
        val avatarUrl: String? = null,
    ): IMedia()

    data object None: IMedia()
}

enum class CurrentMediaState{
    Playing,
    Paused,
    Stopped,
}

sealed interface MediaEvent {
    data object Play: MediaEvent
    data object Pause: MediaEvent
}

data class GlobalViewModelState(
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val isLoading: Boolean = false,
    val currentMedia: IMedia,
    val currentMediaState : CurrentMediaState,
    val errorDialogState: ErrorDialogState? = null,
    val userPreference: UserPreference
){
    fun toUiState(): GlobalUiState {
        return GlobalUiState(
            isLoading = isLoading,
            snackBarMessages = snackBarMessages,
            currentMedia = currentMedia,
            currentMediaState = currentMediaState,
            userPreference = userPreference,
            errorDialogState = errorDialogState,
        )
    }
}
data class FormDialogState(
    val title: String,
    val message: String,
    val confirmLabel: String?=null,
    val cancelLabel: String?=null,
    val onConfirm: (() -> Unit)?=null,
    val onCancel: (() -> Unit)?=null,
)
data class ErrorDialogState(
    val title: String,
    val message: String,
    val confirmLabel: String?=null,
    val cancelLabel: String?=null,
    val onConfirm: (() -> Unit)?=null,
    val onCancel: (() -> Unit)?=null,
)
data class GlobalUiState (
    val isLoading: Boolean,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val currentMedia: IMedia,
    val currentMediaState : CurrentMediaState = CurrentMediaState.Stopped,
    val errorDialogState: ErrorDialogState? = null,
    val userPreference: UserPreference
)
class GlobalViewModel(
    private val runtimeEventFlow: RuntimeEventFlow,
    private val mediaPlayer: MediaPlayer,
    private val clipboardManager: IBSClipBoardManager,
    private val playlistRepository: PlaylistRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
) : ViewModel(){
//    private lateinit var userPreference: UserPreference

    private val viewModelState = MutableStateFlow(GlobalViewModelState(
        isLoading = true,
        snackBarMessages = emptyList(),
        currentMedia = IMedia.None,
        currentMediaState = CurrentMediaState.Stopped,
        userPreference = UserPreference.getDefaultUserPreference()
    ))
    val uiState = viewModelState
        .map(GlobalViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )
    // add error handler for viewModelScope

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        runtimeEventFlow.sendEvent(Event.ExceptionEvent(throwable))
    }
    init {
        viewModelScope.launch {
            userPreferenceRepository.getUserPreference().collect {
//                userPreference = it
                viewModelState.update { vmState ->
                    vmState.copy(userPreference = it)
                }
            }
        }
        runtimeEventFlow.subscribeEvent { event ->
            when (event) {
                is Event.ExceptionEvent -> {
                    dispatchUiEvents(GlobalUIEvent.ReportError(event.throwable,event.shortDescription))
                }
                is Event.MessageEvent -> {
                    dispatchUiEvents(GlobalUIEvent.ShowSnackBar(event.message))
                }

                else -> {}
            }
        }
    }

    fun dispatchUiEvents(event: UIEvent){
        when(event){
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
                        it.copy(errorDialogState = ErrorDialogState(
                            title = "Error",
                            message = (event.shortDescription?:"")+": "+event.throwable.stackTraceToString(),
                            confirmLabel = "确认",
                            cancelLabel = "复制以报告",
                            onConfirm = { clearErrorDialog() },
                            onCancel = {
                                writeToClipboard(text = event.throwable.stackTraceToString())
                                clearErrorDialog()
                            }
                        ))
                    }
                })
            }
            is GlobalUIEvent.CreatePlaylist -> {

                    event.playlist?.let {
                        viewModelScope.launch(exceptionHandler) {
                            playlistRepository.createNewPlaylist(it.name,it.bsPlaylistId,it.description,it.customTags)
                        }
                    }
            }
        }
    }
    private fun clearErrorDialog(){
        viewModelState.update { vmState ->
            vmState.copy(errorDialogState = null)
        }
    }
    private fun playMediaEvent(event: MediaEvent){
        when(event){
            is MediaEvent.Play -> {
                if (viewModelState.value.currentMediaState != CurrentMediaState.Paused){
                    return
                }
                viewModelState.update { vmState ->
                    vmState.copy(currentMediaState = CurrentMediaState.Playing)
                }
                mediaPlayer.play()
            }
            is MediaEvent.Pause -> {
                if (viewModelState.value.currentMediaState != CurrentMediaState.Playing){
                    return
                }
                viewModelState.update { vmState ->
                    vmState.copy(currentMediaState = CurrentMediaState.Paused)
                }
                mediaPlayer.pause()
            }
        }
    }

    fun playMedia(media: IMedia){
        viewModelState.update { vmState ->
            vmState.copy(currentMedia = media, currentMediaState = CurrentMediaState.Stopped)
        }
        if (media is IMedia.MapAudioPreview){
            viewModelScope.launch(exceptionHandler) {
                mediaPlayer.loadAndPlay(media.url,
                    {
                        viewModelState.update { vmState ->
                            vmState.copy(currentMediaState = CurrentMediaState.Playing)
                        }
                    },
                    {
                        viewModelState.update { vmState ->
                            vmState.copy(currentMediaState = CurrentMediaState.Stopped, currentMedia = IMedia.None)
                        }
                    }
                )
            }
        }
//        else if (media is IMedia.MapPreview){
//            // a map preview component
//        }


    }
    fun writeToClipboard(text: String, label: String = "") {
        clipboardManager.setText(AnnotatedString(text))
        showSnackBar(msg = "已复制到剪贴板")
//        clipboardManager.setPrimaryClip(ClipData.newPlainText(label,text))
    }
    fun showSnackBar(
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