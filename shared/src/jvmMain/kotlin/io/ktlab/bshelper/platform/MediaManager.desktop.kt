package io.ktlab.bshelper.platform

import javazoom.jl.decoder.JavaLayerException
import javazoom.jl.player.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.net.URL


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

    actual suspend fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    )
}

class DesktopMediaPlayerImpl : MediaPlayer {
    var player: Player? = null
    override fun play() {
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
    private val mutex = Mutex()
    override suspend fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        withContext(Dispatchers.IO) {
            player?.close()
            mutex.lock()
            try {
                val urlConnection = URL(url).openConnection()
                urlConnection.connect()
                player = Player(urlConnection.getInputStream())
                onPrepared()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                player!!.play()
                onCompletion()
            } catch (e: JavaLayerException) {
                e.printStackTrace()
            }
            mutex.unlock()
        }
    }
}

actual fun createMediaPlayer(): MediaPlayer {
    return DesktopMediaPlayerImpl()
}
