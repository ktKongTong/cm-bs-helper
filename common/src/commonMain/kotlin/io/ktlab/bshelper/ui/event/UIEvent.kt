package io.ktlab.bshelper.ui.event
import androidx.compose.material3.SnackbarDuration

open class UIEvent {

}

data class SnackBarMessage(
    val id: Long,
    val message: String,
    val actionLabel: String? = null,
    val action: (() -> Unit)? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
)

class Event {

}
