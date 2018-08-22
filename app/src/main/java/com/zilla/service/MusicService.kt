package com.zilla.service

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.zilla.Album
import com.zilla.Application
import com.zilla.Song
import javax.inject.Inject

class MusicService : Service() {

    private val Tag = "MusicService"
    // The volume we set the media player to when we lose audio focus, but are allowed to reduce
    // the volume instead of stopping playback.
    private val DUCK_VOLUME = 0.1f


    private val audioManager by lazy {getSystemService(Context.AUDIO_SERVICE) as AudioManager}

    private var mediaPlayer: MediaPlayer? = null

    private var playedAlbum: Album? = null
    private val playlist = ArrayList<Song>()
    private var currentTrack: Int = 0
    private var currentSongInfo: Song? = null
	private var noisyReceiverRegistered = false

    private val becomingNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.i(Tag, "Received ACTION_AUDIO_BECOMING_NOISY intent")

        }
    }
    private val afChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.i(Tag, "AUDIOFOCUS_GAIN")
                mediaPlayer?.setVolume(1f, 1f)

            }
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.i(Tag, "AUDIOFOCUS_LOSS, AUDIOFOCUS_LOSS_TRANSIENT")

            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.i(Tag, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK")
                mediaPlayer?.setVolume(DUCK_VOLUME, DUCK_VOLUME)
            }
        }
    }

    class MusicBinder(val service: MusicService) : Binder()

    override fun onBind(intent: Intent?) = MusicBinder(this)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int) =  START_STICKY


    private fun playbackStopped() {
		if (noisyReceiverRegistered) {
            try {
                unregisterReceiver(becomingNoisyReceiver)
            } catch (t: Throwable) {
            }
		}
        audioManager.abandonAudioFocus(afChangeListener)
    }

    private fun playbackStarted(callback: () -> Unit) {
        val result = audioManager.requestAudioFocus(afChangeListener, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            registerReceiver(becomingNoisyReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
			noisyReceiverRegistered = true
            callback()
        }
    }



}
