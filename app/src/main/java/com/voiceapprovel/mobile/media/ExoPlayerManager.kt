package com.voiceapprovel.mobile.media

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

/**
 * Created by Ajay Vamsee on 8/14/2023.
 * Time : 17:49
 */
class ExoPlayerManager(private val context: Context) {

    private var player: ExoPlayer? = null

    fun initializePlayer(): ExoPlayer {
        Log.d(TAG, "initializePlayer: ")
        player = ExoPlayer.Builder(context).build()
        return player!!
    }

    fun releasePlayer() {
        Log.d(TAG, "releasePlayer: ")
        player?.release()
        player = null
    }

    fun prepareMediaItem(mediaItemUri: Uri) {
        Log.d(TAG, "prepareMediaItem: ")
        val mediaItem = MediaItem.fromUri(mediaItemUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }

    fun replay() {
        if (player?.isPlaying == true) {
            player?.seekTo(0)
        } else {
            player?.play()
        }
    }

    fun pausePlayer() {
        player?.pause()
    }

    fun startPlay() {
        player?.play()
    }

    fun isPlaying(): Boolean {
        return player?.isPlaying ?: false
    }

    companion object {
        private const val TAG = "ExoPlayerManager"
    }


}