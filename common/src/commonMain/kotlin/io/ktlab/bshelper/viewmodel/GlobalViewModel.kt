package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.repository.EventType
import io.ktlab.bshelper.repository.RuntimeEventFlow
import io.ktlab.bshelper.service.MediaPlayer
import io.ktlab.bshelper.ui.event.SnackBarMessage
import io.ktlab.bshelper.ui.event.UIEvent
import kotlinx.coroutines.flow.*
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import java.util.*

sealed class GlobalUIEvent: UIEvent(){
    data class ShowSnackBar(val message:String): GlobalUIEvent()
    data class SnackBarShown(val msgId:Long):GlobalUIEvent()
    data class WriteToClipboard(val text:String):GlobalUIEvent()
//    data class MediaEvent(val media: IMedia):GlobalUIEvent()
    data class PlayMedia(val media: IMedia):GlobalUIEvent()
    data class OnMediaEvent(val event: MediaEvent):GlobalUIEvent()
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
    val currentMediaState : CurrentMediaState
){
    fun toUiState(): GlobalUiState {
        return GlobalUiState(
            isLoading = isLoading,
            snackBarMessages = snackBarMessages,
            currentMedia = currentMedia,
            currentMediaState = currentMediaState
        )
    }
}
data class GlobalUiState (
    val isLoading: Boolean,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val currentMedia: IMedia,
    val currentMediaState : CurrentMediaState = CurrentMediaState.Stopped
)
class GlobalViewModel(
    private val runtimeEventFlow: RuntimeEventFlow,
    private val mediaPlayer: MediaPlayer
) : ViewModel(){
    private val viewModelState = MutableStateFlow(GlobalViewModelState(
        isLoading = true,
        snackBarMessages = emptyList(),
        currentMedia = IMedia.None,
        currentMediaState = CurrentMediaState.Stopped
    ))
    val uiState = viewModelState
        .map(GlobalViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        runtimeEventFlow.subscribeEvent { event ->
            when (event.type) {
                EventType.Exception -> {
                    dispatchUiEvents(GlobalUIEvent.ShowSnackBar((event.data as Exception).message ?: "Unknown Error"))
                }
                EventType.Message -> {
                    dispatchUiEvents(GlobalUIEvent.ShowSnackBar(event.data as String))
                }
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
        }else if (media is IMedia.MapPreview){
            // a map preview component
        }


    }
    private fun writeToClipboard(text: String, label: String = "") {
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