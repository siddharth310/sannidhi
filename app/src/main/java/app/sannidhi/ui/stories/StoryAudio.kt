package app.sannidhi.ui.stories

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.SoundPool
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import app.sannidhi.content.NarrationClip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Narration, a looping music bed, and touch sounds for one story.
 * Pausing for a lock, a phone call, or leaving the screen stays paused until Play.
 */
class StoryAudio(context: Context) {
    private val app = context.applicationContext
    private val audioManager = app.getSystemService(AudioManager::class.java)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val voice = ExoPlayer.Builder(app).build()
    private val music = ExoPlayer.Builder(app).build()
    private val pool = SoundPool.Builder()
        .setMaxStreams(2)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
        .build()

    private var focusRequest: AudioFocusRequest? = null
    private var bedJob: Job? = null
    private var fadeJob: Job? = null
    private var replacing = false
    private var held = false
    private var readOn = true
    private var musicOn = true
    private var bedFile: String? = null
    private var fullFile: String? = null
    private var lowFile: String? = null
    private val loaded = mutableMapOf<String, Int>()
    private val waiting = mutableSetOf<Int>()

    val playing = mutableStateOf(false)
    var onNarrationFinished: (() -> Unit)? = null

    init {
        music.repeatMode = Player.REPEAT_MODE_ONE
        music.volume = BED
        voice.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (replacing || playbackState != Player.STATE_ENDED) return
                music.volume = BED
                onNarrationFinished?.invoke()
                sync()
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!replacing) music.volume = if (isPlaying && musicOn) DUCKED else BED
                sync()
            }
        })
        music.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) = sync()
        })
        pool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0 && sampleId in waiting) {
                waiting.remove(sampleId)
                pool.play(sampleId, 1f, 1f, 1, 0, 1f)
            }
        }
    }

    fun switches(read: Boolean, withMusic: Boolean) {
        readOn = read
        musicOn = withMusic
        if (!withMusic) {
            music.pause()
            music.volume = 0f
        } else if (music.mediaItemCount > 0 && !held) {
            music.volume = if (voice.isPlaying) DUCKED else BED
            music.play()
        }
        if (!read) voice.pause()
        sync()
    }

    fun prepareBed(full: String?, low: String?) {
        fullFile = full
        lowFile = low
    }

    fun playTitle(clip: NarrationClip?) {
        playClip(clip?.file, low = false)
    }

    fun playBeat(clip: NarrationClip?, low: Boolean) {
        held = false
        playClip(clip?.file, low)
    }

    fun pause() {
        held = true
        voice.pause()
        music.pause()
        sync()
    }

    fun resume() {
        held = false
        if (!requestFocus()) return
        if (musicOn && music.mediaItemCount > 0) music.play()
        if (readOn && voice.mediaItemCount > 0 && voice.playbackState != Player.STATE_ENDED) voice.play()
        sync()
    }

    /** Lock, background, or another app taking the sound. Does not start again by itself. */
    fun hold() = pause()

    fun positionMs(): Long = voice.currentPosition

    fun playTouch(sound: String?) {
        if (sound.isNullOrBlank()) return
        val ready = loaded[sound]
        if (ready != null) {
            pool.play(ready, 1f, 1f, 1, 0, 1f)
            return
        }
        val path = "content/sfx/$sound.m4a"
        val id = runCatching { app.assets.openFd(path).use { pool.load(it, 1) } }.getOrNull() ?: return
        loaded[sound] = id
        waiting.add(id)
    }

    fun fadeOutBed() {
        fadeJob?.cancel()
        fadeJob = scope.launch {
            val from = music.volume
            repeat(30) {
                music.volume = from * (1f - (it + 1) / 30f)
                delay(100)
            }
            music.pause()
            sync()
        }
    }

    fun release() {
        bedJob?.cancel()
        fadeJob?.cancel()
        voice.release()
        music.release()
        pool.release()
        focusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
    }

    private fun playClip(file: String?, low: Boolean) {
        fadeJob?.cancel()
        setBed(low)
        replacing = true
        voice.stop()
        voice.clearMediaItems()
        replacing = false
        if (!readOn || file == null) {
            sync()
            return
        }
        voice.setMediaItem(MediaItem.fromUri("asset:///content/$file"))
        voice.prepare()
        if (!held && requestFocus()) voice.play()
        sync()
    }

    private fun setBed(low: Boolean) {
        if (!musicOn) {
            music.pause()
            return
        }
        val file = (if (low) lowFile else fullFile) ?: return
        if (file == bedFile && music.mediaItemCount > 0) {
            if (!held) music.play()
            return
        }
        bedJob?.cancel()
        bedJob = scope.launch {
            val from = if (music.isPlaying) music.volume else 0f
            if (music.isPlaying) {
                repeat(6) {
                    music.volume = from * (1f - (it + 1) / 6f)
                    delay(70)
                }
            }
            bedFile = file
            music.setMediaItem(MediaItem.fromUri("asset:///content/$file"))
            music.prepare()
            if (!held) music.play()
            val target = if (voice.isPlaying) DUCKED else BED
            repeat(6) {
                music.volume = target * (it + 1) / 6f
                delay(70)
            }
            sync()
        }
    }

    private fun requestFocus(): Boolean {
        val request = focusRequest ?: AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
            )
            .setOnAudioFocusChangeListener { change ->
                if (change != AudioManager.AUDIOFOCUS_GAIN) hold()
            }
            .build()
            .also { focusRequest = it }
        return audioManager.requestAudioFocus(request) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    private fun sync() {
        playing.value = voice.isPlaying || music.isPlaying
    }

    companion object {
        private const val BED = 0.40f
        private const val DUCKED = 0.22f
    }
}
