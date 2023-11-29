package io.ktlab.bshelper.viewmodel

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.model.IPlaylist
import io.ktlab.bshelper.model.UserPreference
import io.ktlab.bshelper.repository.*
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

}
data class GlobalViewModelState(
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
    val isLoading: Boolean = false,
){
    fun toUiState(): GlobalUiState {
        return GlobalUiState(
            isLoading = isLoading,
            snackBarMessages = snackBarMessages,
        )
    }
}
data class GlobalUiState (
    val isLoading: Boolean,
    val snackBarMessages: List<SnackBarMessage> = emptyList(),
)
class GlobalViewModel(
    private val runtimeEventFlow: RuntimeEventFlow
) : ViewModel(){
    private val viewModelState = MutableStateFlow(GlobalViewModelState(
        isLoading = true,
//        userPreferenceState = UserPreference.getDefaultUserPreference(),
        snackBarMessages = emptyList(),
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