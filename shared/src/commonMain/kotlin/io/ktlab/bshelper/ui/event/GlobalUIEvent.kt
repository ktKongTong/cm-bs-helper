package io.ktlab.bshelper.ui.event

import androidx.compose.material3.SnackbarDuration
import io.ktlab.bshelper.model.FSPlaylist
import io.ktlab.bshelper.ui.viewmodel.IMedia
import io.ktlab.bshelper.ui.viewmodel.MediaEvent

sealed class GlobalUIEvent : UIEvent() {
    data class ShowSnackBar(
        val message: String,
        val actionLabel: String? = null,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val action: (() -> Unit)? = null,
    ) : GlobalUIEvent()

    data class SnackBarShown(val msgId: Long) : GlobalUIEvent()

    data object CheckVersion : GlobalUIEvent()

    data class WriteToClipboard(val text: String) : GlobalUIEvent()

    data class ReportError(val throwable: Throwable, val shortDescription: String? = null) : GlobalUIEvent()

    data class PlayMedia(val media: IMedia) : GlobalUIEvent()

    data class OnMediaEvent(val event: MediaEvent) : GlobalUIEvent()

    data class CreatePlaylist(val playlist: FSPlaylist?) : GlobalUIEvent()
}