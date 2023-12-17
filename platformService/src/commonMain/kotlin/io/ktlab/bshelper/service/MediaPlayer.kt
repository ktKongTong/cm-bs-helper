package io.ktlab.bshelper.service

expect interface MediaPlayer {
    fun play()

    fun pause()

    fun stop()

    fun release()

    fun seekTo(position: Long)

    fun getCurrentPosition(): Long

    fun getDuration(): Long

    fun isPlaying(): Boolean

    fun isPaused(): Boolean

    fun isStopped(): Boolean

    fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    )
}

expect fun createMediaPlayer(): MediaPlayer
