package com.frontiir.dasher.listener

import android.content.Context
import android.util.Log

import com.frontiir.dasher.PlayerActivity
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray

class PlayerEventListener(private val context: Context) : ExoPlayer.EventListener {

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
        Log.d(TAG, "on player timeline changed---->" + timeline!!.toString())
    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
        Log.d(TAG, "on player tracks changed---->" + trackGroups + "---track selection---" + trackSelections!!.length)
    }

    override fun onLoadingChanged(isLoading: Boolean) {
        Log.d(TAG, "on player loading changed---->$isLoading")
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        Log.d(TAG, "on player state changed---->$playWhenReady--play back state---$playbackState")
    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        Log.d(TAG, "on player repeat mode changed---->$repeatMode")
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        Log.d(TAG, "on player shuffle mode enable--->$shuffleModeEnabled")
    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        Log.d(TAG, "on player error---->" + error!!)
    }

    override fun onPositionDiscontinuity(reason: Int) {
        Log.d(TAG, "on player position discontinuity---->$reason")
    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
        Log.d(TAG, "on player playback---->" + playbackParameters!!)
    }

    override fun onSeekProcessed() {
        Log.d(TAG, "on player is on seek process--->")
    }

    companion object {
        private val TAG = PlayerEventListener::class.java.name
    }
}
