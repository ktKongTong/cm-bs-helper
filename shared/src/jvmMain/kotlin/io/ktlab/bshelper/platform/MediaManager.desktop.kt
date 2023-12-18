package io.ktlab.bshelper.platform

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

    actual fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    )
}

class DesktopMediaPlayerImpl : MediaPlayer {
    override fun play() {
        TODO()
    }

    override fun pause() {
        TODO()
    }

    override fun stop() {
        TODO()
    }

    override fun release() {
        TODO()
    }

    override fun seekTo(position: Long) {
        TODO()
    }

    override fun getCurrentPosition(): Long {
        TODO()
    }

    override fun getDuration(): Long {
        TODO()
    }

    override fun isPlaying(): Boolean {
        TODO()
    }

    override fun isPaused(): Boolean {
        TODO()
    }

    override fun isStopped(): Boolean {
        TODO()
    }

    override fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        TODO()
    }
}

actual fun createMediaPlayer(): MediaPlayer {
    return DesktopMediaPlayerImpl()
}
