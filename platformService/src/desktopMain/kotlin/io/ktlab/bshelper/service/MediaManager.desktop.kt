package io.ktlab.bshelper.service

actual interface MediaPlayer {
    actual fun play()
    actual fun pause()
    actual fun stop()
    actual fun release()
    actual fun seekTo(position: Long)
    actual fun getCurrentPosition(): Long
    actual fun getDuration(): Long
    actual fun isPlaying(): Boolean
    actual fun isPaused(): Boolean
    actual fun isStopped(): Boolean
    actual fun loadAndPlay(url: String, onPrepared: () -> Unit, onCompletion: () -> Unit)
}

actual fun createMediaPlayer(): MediaPlayer {
    TODO("Not yet implemented")
}