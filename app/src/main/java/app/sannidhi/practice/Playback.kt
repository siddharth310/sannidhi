package app.sannidhi.practice

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
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import app.sannidhi.MainActivity
import app.sannidhi.SannidhiApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

/**
 * One player for Sankalpam. Counting happens here so it continues with the screen locked.
 * A recitation that does not reach its end is never added.
 */
object Playback {
    private var host: PracticePlaybackService? = null
    private var player: ExoPlayer? = null
    private var audioManager: AudioManager? = null
    private var focusRequest: AudioFocusRequest? = null
    private var hasFocus = false
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private var mantra = false
    private var sankalpamId = ""
    private var today: LocalDate = LocalDate.now()
    private var sessionId: String? = null
    private var sessionCount = 0
    private var lastFinishAt = 0L
    private var textSaved = false
    private var pending: (() -> Unit)? = null
    var speed: Float = 1f
        private set

    fun attach(service: PracticePlaybackService, exo: ExoPlayer) {
        host = service
        player = exo
        audioManager = service.getSystemService(AudioManager::class.java)
        exo.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                if (mantra && reason == Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT) finishedRepetition()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState != Player.STATE_ENDED) return
                if (mantra) {
                    finishedRepetition()
                    exo.seekTo(0)
                    if (hasFocus) exo.play()
                } else {
                    finishedText()
                }
            }
        })
        pending?.invoke()
        pending = null
    }

    fun detach(service: PracticePlaybackService) {
        if (host !== service) return
        player = null
        host = null
    }

    fun position(): Long = player?.currentPosition ?: 0L
    fun playing(): Boolean = player?.isPlaying == true

    fun prepare(
        context: Context,
        file: String,
        title: String,
        countLine: String,
        forSankalpam: String,
        date: LocalDate,
        looping: Boolean,
    ) {
        context.startService(Intent(context, PracticePlaybackService::class.java))
        val exo = player
        if (exo == null) {
            pending = { prepare(context, file, title, countLine, forSankalpam, date, looping) }
            return
        }
        mantra = looping
        sankalpamId = forSankalpam
        today = date
        textSaved = false
        sessionId = null
        sessionCount = 0
        exo.repeatMode = if (looping) Player.REPEAT_MODE_ONE else Player.REPEAT_MODE_OFF
        exo.setPlaybackParameters(PlaybackParameters(speed, 1f))
        exo.setMediaItem(item(file, title, countLine))
        exo.prepare()
        exo.pause()
    }

    fun retitle(title: String, countLine: String) {
        val exo = player ?: return
        if (exo.isPlaying) return
        val current = exo.currentMediaItem ?: return
        exo.replaceMediaItem(
            exo.currentMediaItemIndex,
            current.buildUpon()
                .setMediaMetadata(MediaMetadata.Builder().setTitle(title).setArtist(countLine).build())
                .build(),
        )
    }

    fun play(context: Context) {
        val exo = player ?: return
        if (!requestFocus(context)) return
        textSaved = false
        exo.play()
    }

    fun pause() {
        player?.pause()
    }

    fun setSpeed(value: Float) {
        speed = value
        player?.setPlaybackParameters(PlaybackParameters(value, 1f))
    }

    /** Leave the player. Finished counts stay; the one in progress is dropped. */
    fun stop() {
        player?.pause()
        player?.seekTo(0)
        abandonFocus()
        sessionId = null
        sessionCount = 0
    }

    private fun finishedRepetition() {
        val now = android.os.SystemClock.elapsedRealtime()
        if (now - lastFinishAt < 400) return
        lastFinishAt = now
        val id = sessionId ?: UUID.randomUUID().toString().also { sessionId = it }
        sessionCount += 1
        val count = sessionCount
        val sankalpam = sankalpamId
        val date = today
        scope.launch {
            host?.repository()?.upsertSession(id, sankalpam, count, date)
        }
    }

    private fun finishedText() {
        if (textSaved) return
        textSaved = true
        player?.pause()
        player?.seekTo(0)
        val sankalpam = sankalpamId
        val date = today
        scope.launch {
            host?.repository()?.add(sankalpam, 1, "play", date)
        }
    }

    private fun requestFocus(context: Context): Boolean {
        if (hasFocus) return true
        val manager = audioManager ?: context.getSystemService(AudioManager::class.java)
        val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
            )
            .setOnAudioFocusChangeListener { change ->
                if (change != AudioManager.AUDIOFOCUS_GAIN) {
                    hasFocus = false
                    player?.pause()
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

    private fun item(file: String, title: String, countLine: String): MediaItem =
        MediaItem.Builder()
            .setUri("asset:///content/audio/$file")
            .setMediaMetadata(MediaMetadata.Builder().setTitle(title).setArtist(countLine).build())
            .build()
}

class PracticePlaybackService : MediaSessionService() {
    private var player: ExoPlayer? = null
    private var session: MediaSession? = null
    private var noisy: BroadcastReceiver? = null

    fun repository(): PracticeRepository? =
        (application as? SannidhiApplication)?.container?.practiceRepository

    override fun onCreate() {
        super.onCreate()
        val exo = ExoPlayer.Builder(this)
            .setAudioAttributes(
                PlayerAudio.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                false,
            )
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build()
        player = exo
        val open = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val mediaSession = MediaSession.Builder(this, exo)
            .setSessionActivity(open)
            .setCallback(object : MediaSession.Callback {
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo,
                ): MediaSession.ConnectionResult {
                    val commands = Player.Commands.Builder()
                        .add(Player.COMMAND_PLAY_PAUSE)
                        .add(Player.COMMAND_PREPARE)
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
        Playback.attach(this, exo)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Playback.pause()
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
        Playback.detach(this)
        session?.release()
        player?.release()
        session = null
        player = null
        super.onDestroy()
    }
}
