package app.sannidhi.ui.puja

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.media3.common.AudioAttributes as PlayerAudio
import androidx.media3.common.C
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import app.sannidhi.MainActivity
import kotlinx.coroutines.CompletableDeferred

/**
 * Plays one puja step. It does not write a practice entry.
 * A call, another app, or unplugged headphones pause it. It does not start again by itself.
 */
object PujaPlayback {
    private var host: PujaPlaybackService? = null
    private var player: ExoPlayer? = null
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null
    private var hasFocus = false
    private var gate: CompletableDeferred<ExoPlayer>? = null
    private var clip = 0
    private var endedClip = -1
    private var expectEnd = false
    var speed: Float = 1f
        private set
    var onNext: (() -> Unit)? = null
    var onInterrupted: (() -> Unit)? = null

    fun attach(service: PujaPlaybackService, exo: ExoPlayer) {
        host = service
        player = exo
        audioManager = service.getSystemService(AudioManager::class.java)
        exo.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED && expectEnd) {
                    expectEnd = false
                    endedClip = clip
                }
            }

            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                expectEnd = false
                endedClip = clip
            }
        })
        gate?.complete(exo)
        gate = null
    }

    fun detach(service: PujaPlaybackService) {
        if (host !== service) return
        player = null
        host = null
    }

    fun position(): Long = player?.currentPosition ?: 0L
    fun playing(): Boolean = player?.isPlaying == true
    fun ended(): Boolean = clip > 0 && endedClip == clip

    suspend fun start(context: Context, file: String, title: String, rate: Float): Boolean {
        context.startService(Intent(context, PujaPlaybackService::class.java))
        val exo = player ?: awaitPlayer()
        speed = rate
        expectEnd = false
        exo.stop()
        clip += 1
        endedClip = -1
        expectEnd = true
        exo.setPlaybackParameters(PlaybackParameters(rate, 1f))
        exo.setMediaItem(
            MediaItem.Builder()
                .setUri("asset:///content/audio/$file")
                .setMediaMetadata(MediaMetadata.Builder().setTitle(title).build())
                .build(),
        )
        exo.prepare()
        if (!requestFocus(context)) {
            expectEnd = false
            endedClip = clip
            return false
        }
        exo.play()
        return true
    }

    fun pause() {
        player?.pause()
    }

    fun resume(context: Context) {
        if (!requestFocus(context)) return
        player?.play()
    }

    fun setSpeed(value: Float) {
        speed = value
        player?.setPlaybackParameters(PlaybackParameters(value, 1f))
    }

    fun stop() {
        expectEnd = false
        onInterrupted = null
        player?.pause()
        player?.stop()
        abandonFocus()
    }

    private suspend fun awaitPlayer(): ExoPlayer {
        player?.let { return it }
        val pending = gate ?: CompletableDeferred<ExoPlayer>().also { gate = it }
        return pending.await()
    }

    private fun requestFocus(context: Context): Boolean {
        if (hasFocus) return true
        val manager = audioManager ?: context.getSystemService(AudioManager::class.java).also { audioManager = it }
        val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setOnAudioFocusChangeListener { change ->
                if (change != AudioManager.AUDIOFOCUS_GAIN) {
                    hasFocus = false
                    player?.pause()
                    onInterrupted?.invoke()
                }
            }
            .build()
            .also { focusRequest = it }
        hasFocus = manager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        return hasFocus
    }

    private fun abandonFocus() {
        val manager = audioManager ?: return
        focusRequest?.let { manager.abandonAudioFocusRequest(it) }
        hasFocus = false
    }
}

/** The notification's Next button. Play and pause stay on the player. */
private class NextPlayer(player: Player) : ForwardingPlayer(player) {
    override fun seekToNext() {
        PujaPlayback.onNext?.invoke()
    }

    override fun getAvailableCommands(): Player.Commands =
        super.getAvailableCommands().buildUpon().add(Player.COMMAND_SEEK_TO_NEXT).build()
}

class PujaPlaybackService : MediaSessionService() {
    private var player: ExoPlayer? = null
    private var session: MediaSession? = null
    private var noisy: BroadcastReceiver? = null

    override fun onCreate() {
        super.onCreate()
        val exo = ExoPlayer.Builder(this)
            .setAudioAttributes(
                PlayerAudio.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_SPEECH)
                    .build(),
                false,
            )
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
        player = exo
        val open = PendingIntent.getActivity(
            this,
            1,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val wrapped = NextPlayer(exo)
        val mediaSession = MediaSession.Builder(this, wrapped)
            .setId("puja")
            .setSessionActivity(open)
            .setCallback(object : MediaSession.Callback {
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                ): MediaSession.ConnectionResult {
                    val commands = Player.Commands.Builder()
                        .add(Player.COMMAND_PLAY_PAUSE)
                        .add(Player.COMMAND_PREPARE)
                        .add(Player.COMMAND_SEEK_TO_NEXT)
                        .add(Player.COMMAND_GET_CURRENT_MEDIA_ITEM)
                        .add(Player.COMMAND_GET_TIMELINE)
                        .add(Player.COMMAND_GET_METADATA)
                        .build()
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setAvailablePlayerCommands(commands)
                        .build()
                }
            })
            .build()
        session = mediaSession
        PujaPlayback.attach(this, exo)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                PujaPlayback.pause()
                PujaPlayback.onInterrupted?.invoke()
            }
        }
        noisy = receiver
        val filter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            ContextCompat.registerReceiver(this, receiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = session

    override fun onDestroy() {
        noisy?.let { runCatching { unregisterReceiver(it) } }
        PujaPlayback.detach(this)
        session?.release()
        player?.release()
        session = null
        player = null
        super.onDestroy()
    }
}
