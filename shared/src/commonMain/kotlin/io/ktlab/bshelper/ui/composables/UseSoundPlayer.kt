package io.ktlab.bshelper.ui.composables

import androidx.compose.runtime.Composable
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

sealed interface MediaEvent {
    data object Play : MediaEvent

    data object Pause : MediaEvent
}
enum class CurrentMediaState {
    Playing,
    Paused,
    Stopped,
}
@Composable
fun UseSoundPlayer() {
//    val currentPlayer = remember { mutableStateOf<SoundPlayer?>(null) }
}