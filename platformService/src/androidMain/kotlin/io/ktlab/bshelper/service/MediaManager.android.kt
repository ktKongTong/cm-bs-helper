package io.ktlab.bshelper.service

import io.github.oshai.kotlinlogging.KotlinLogging
import android.media.MediaPlayer as AndroidIMediaPlayer
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

private val logger = KotlinLogging.logger {}
class AndroidMediaPlayer : MediaPlayer {
    private val player = AndroidIMediaPlayer()

    override fun play() {
        if (player.isPlaying) {
            player.stop()
            player.reset()
        }
        player.start()
    }

    override fun pause() {
        player.pause()
    }

    override fun stop() {
        player.stop()
    }

    override fun release() {
        player.release()
    }

    override fun seekTo(position: Long) {
        player.seekTo(position.toInt())
    }

    override fun getCurrentPosition(): Long {
        return player.currentPosition.toLong()
    }

    override fun getDuration(): Long {
        return player.duration.toLong()
    }

    override fun isPlaying(): Boolean {
        return player.isPlaying
    }

    override fun isPaused(): Boolean {
        return !player.isPlaying
    }

    override fun isStopped(): Boolean {
        return !player.isPlaying
    }

    override fun loadAndPlay(
        url: String,
        onPrepared: () -> Unit,
        onCompletion: () -> Unit,
    ) {
        logger.debug { "loadAndPlay: url=$url" }
        try {
            player.stop()
        } catch (e: Exception) {
            logger.error { "loadAndPlay:stop player error: ${e.stackTraceToString()}" }
        }
        try {
            player.reset()
        } catch (e: Exception) {
            logger.error { "loadAndPlay:reset player error: ${e.stackTraceToString()}" }
        }
        player.setDataSource(url)
        player.prepareAsync()
        player.setOnCompletionListener {
            player.reset()
            onCompletion()
        }
        player.setOnPreparedListener {
            player.start()
            onPrepared()
        }
        player.setOnErrorListener { mp, what, extra ->
            logger.error { "loadAndPlay error: what=$what, extra=$extra" }
            false
        }
    }
}

actual fun createMediaPlayer(): MediaPlayer {
    return AndroidMediaPlayer()
}
