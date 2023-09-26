package io.ktlab.bshelper.service

import android.media.MediaDataSource
import android.media.MediaPlayer

class AndroidMediaPlayer {
    private val player = MediaPlayer()
    fun play() {
        if (player.isPlaying) {
            player.stop()
            player.reset()
        }
//        MediaDataSource()
//        player.setDataSource()
//        player.prepareAsync()
//        player.setOnPreparedListener {
//            player.start()
//        }

    }

}