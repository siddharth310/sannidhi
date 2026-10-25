@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.sannidhi.ui.puja

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sannidhi.R
import app.sannidhi.content.Mantra
import app.sannidhi.content.PujaStep
import app.sannidhi.content.StepSentence
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Leaf
import app.sannidhi.ui.theme.LeafBorder
import app.sannidhi.ui.theme.LeafDark
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Surface
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

/** Set when a hands-free katha finishes, so the puja moves on when it is back. */
object KathaHandoff {
    var pending: Boolean = false
    fun arm() {
        pending = true
    }

    fun consume(): Boolean = pending.also { pending = false }
}

data class StepCue(
    val sentence: Int = -1,
    val mantraId: String? = null,
    val line: Int = -1,
    val word: Int = -1,
    val listIndex: Int? = null,
    val listTotal: Int = 0,
)

class Hold {
    var paused by mutableStateOf(false)
}

fun stepHasMedia(step: PujaStep, lang: String, mantras: Map<String, Mantra>, hasFile: (String) -> Boolean): Boolean {
    val instruction = step.audio?.instruction?.get(lang)
    if (instruction != null && instruction.file.isNotBlank() && hasFile(instruction.file)) return true
    return step.mantras.any { id ->
        val mantra = mantras[id] ?: return@any false
        val chant = mantra.audio
        val list = mantra.listAudio
        (chant != null && hasFile(chant.file)) || (list != null && list.namesMs.isNotEmpty() && hasFile(list.file))
    }
}

fun litSentences(body: String, sentences: List<StepSentence>, active: Int) = buildAnnotatedString {
    if (active !in sentences.indices) {
        append(body)
        return@buildAnnotatedString
    }
    var search = 0
    var range: IntRange? = null
    sentences.forEachIndexed { index, sentence ->
        val at = body.indexOf(sentence.text, search)
        if (at < 0) return@forEachIndexed
        if (index == active) range = at until (at + sentence.text.length)
        search = at + sentence.text.length
    }
    val hit = range
    if (hit == null) {
        append(body)
        return@buildAnnotatedString
    }
    append(body.substring(0, hit.first))
    withStyle(SpanStyle(color = LeafDark, textDecoration = TextDecoration.Underline)) {
        append(body.substring(hit.first, hit.last + 1))
    }
    append(body.substring(hit.last + 1))
}

fun litWords(text: String, word: Int) = buildAnnotatedString {
    val words = text.split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (word !in words.indices) {
        append(text)
        return@buildAnnotatedString
    }
    words.forEachIndexed { index, piece ->
        if (index > 0) append(" ")
        if (index == word) {
            withStyle(SpanStyle(color = LeafDark, textDecoration = TextDecoration.Underline)) { append(piece) }
        } else {
            append(piece)
        }
    }
}

private const val OFFER_GAP_MS = 3_000L
private const val COUNTDOWN_MS = 4_000

suspend fun playStepAudio(
    context: Context,
    step: PujaStep,
    number: Int,
    total: Int,
    title: String,
    mantras: Map<String, Mantra>,
    lang: String,
    hasFile: (String) -> Boolean,
    speed: () -> Float,
    hold: Hold,
    onCue: (StepCue) -> Unit,
    onPhase: (String) -> Unit,
): Boolean {
    val label = context.getString(R.string.step_notification, number, total, title)
    PujaPlayback.onInterrupted = { hold.paused = true }
    var played = false
    val instruction = step.audio?.instruction?.get(lang)
    if (instruction != null && hasFile(instruction.file)) {
        onPhase("instruction")
        if (!playClip(context, instruction.file, label, speed, hold) { at ->
                val index = instruction.sentences.indexOfLast { at >= it.startMs }
                onCue(StepCue(sentence = index))
            }
        ) {
            return false
        }
        played = true
    }
    if (!coroutineContext.isActive) return played
    onPhase("mantra")
    for (id in step.mantras) {
        if (!coroutineContext.isActive) return played
        val mantra = mantras[id] ?: continue
        val list = mantra.listAudio
        val chant = mantra.audio
        if (list != null && list.namesMs.isNotEmpty() && hasFile(list.file)) {
            if (!playList(context, id, list.file, list.namesMs, label, speed, hold, onCue)) return played
            played = true
        } else if (chant != null && hasFile(chant.file)) {
            if (!playClip(context, chant.file, label, speed, hold) { at ->
                    val index = chant.wordsMs.indexOfLast { at >= it }
                    onCue(StepCue(mantraId = id, line = 0, word = index))
                }
            ) {
                return played
            }
            played = true
        }
    }
    onCue(StepCue())
    return played
}

private suspend fun playClip(
    context: Context,
    file: String,
    title: String,
    speed: () -> Float,
    hold: Hold,
    onPosition: (Long) -> Unit,
): Boolean {
    if (!PujaPlayback.start(context, file, title, speed())) return false
    while (coroutineContext.isActive && !PujaPlayback.ended()) {
        if (!hold.paused) onPosition(PujaPlayback.position())
        delay(40)
    }
    return coroutineContext.isActive
}

private suspend fun playList(
    context: Context,
    mantraId: String,
    file: String,
    names: List<Long>,
    title: String,
    speed: () -> Float,
    hold: Hold,
    onCue: (StepCue) -> Unit,
): Boolean {
    if (!PujaPlayback.start(context, file, title, speed())) return false
    var index = 0
    while (coroutineContext.isActive && index < names.size) {
        onCue(StepCue(mantraId = mantraId, line = index, word = -1, listIndex = index + 1, listTotal = names.size))
        val next = names.getOrNull(index + 1)
        while (coroutineContext.isActive && !PujaPlayback.ended()) {
            if (!hold.paused && next != null && PujaPlayback.position() >= next) break
            delay(40)
        }
        if (!coroutineContext.isActive) return false
        val finished = PujaPlayback.ended() || next == null
        PujaPlayback.pause()
        waitOffer(hold)
        if (!coroutineContext.isActive) return false
        if (finished) break
        index += 1
        if (!hold.paused) PujaPlayback.resume(context)
    }
    return true
}

private suspend fun waitOffer(hold: Hold) {
    var left = OFFER_GAP_MS
    while (left > 0 && coroutineContext.isActive) {
        delay(40)
        if (!hold.paused) left -= 40
    }
}

@Composable
fun GuidedPlayback(
    pages: List<Pair<String, PujaStep>>,
    pager: PagerState,
    mantras: Map<String, Mantra>,
    lang: String,
    handsFree: Boolean,
    script: MantraScript,
    onScript: (MantraScript) -> Unit,
    hasFile: (String) -> Boolean,
    kathaReady: Boolean,
    onCue: (StepCue) -> Unit,
    onKatha: () -> Unit,
    kathaReturn: Boolean,
    onKathaReturnConsumed: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val hold = remember { Hold() }
    var job by remember { mutableStateOf<Job?>(null) }
    var phase by remember { mutableStateOf("idle") }
    var speed by rememberSaveable { mutableFloatStateOf(1f) }
    var armed by rememberSaveable { mutableStateOf(false) }
    var countdownMs by remember { mutableIntStateOf(0) }
    var countFrozen by remember { mutableStateOf(false) }
    var countJob by remember { mutableStateOf<Job?>(null) }
    var playToken by remember { mutableIntStateOf(0) }
    var running by remember { mutableStateOf(false) }
    var autoplayAfter by remember { mutableIntStateOf(-1) }
    var seenPage by remember { mutableIntStateOf(-1) }
    val handsFreeNow = rememberUpdatedState(handsFree)
    val kathaNow = rememberUpdatedState(kathaReady)
    val langNow = rememberUpdatedState(lang)
    val page = pager.currentPage
    val step = pages.getOrNull(page)?.second
    val playable = step != null && (
        stepHasMedia(step, lang, mantras, hasFile) || (handsFree && kathaReady && step.stories.size >= 2)
        )

    fun stopAll() {
        playToken += 1
        job?.cancel()
        job = null
        countJob?.cancel()
        countJob = null
        countdownMs = 0
        countFrozen = false
        hold.paused = false
        phase = "idle"
        running = false
        onCue(StepCue())
        PujaPlayback.stop()
    }

    fun goTo(index: Int, autoplay: Boolean) {
        if (step == null || index !in pages.indices || index == page) return
        autoplayAfter = if (autoplay && handsFreeNow.value && armed) index else -1
        scope.launch { pager.animateScrollToPage(index) }
    }

    fun beginCountdown() {
        countJob?.cancel()
        countFrozen = false
        countJob = scope.launch {
            var left = COUNTDOWN_MS
            while (left > 0 && isActive) {
                countdownMs = left
                delay(40)
                if (!countFrozen) left -= 40
            }
            countdownMs = 0
            if (isActive) goTo(page + 1, autoplay = true)
        }
    }

    fun startStep() {
        val current = pages.getOrNull(pager.currentPage)?.second ?: return
        val number = pager.currentPage + 1
        countJob?.cancel()
        countdownMs = 0
        countFrozen = false
        hold.paused = false
        armed = true
        running = true
        val token = playToken + 1
        playToken = token
        job?.cancel()
        job = scope.launch {
            try {
                val played = playStepAudio(
                    context = context,
                    step = current,
                    number = number,
                    total = pages.size,
                    title = current.titleIast,
                    mantras = mantras,
                    lang = langNow.value,
                    hasFile = hasFile,
                    speed = { speed },
                    hold = hold,
                    onCue = onCue,
                    onPhase = { phase = it },
                )
                if (!isActive || token != playToken) return@launch
                val still = pages.getOrNull(pager.currentPage)?.second
                if (still?.id != current.id) return@launch
                if (handsFreeNow.value && kathaNow.value && still.stories.size >= 2) {
                    PujaPlayback.stop()
                    onCue(StepCue())
                    onKatha()
                    return@launch
                }
                if (played && handsFreeNow.value) beginCountdown()
            } finally {
                if (token == playToken) running = false
                if (!isActive && token == playToken) PujaPlayback.stop()
            }
        }
    }

    LaunchedEffect(pager.currentPage) {
        if (seenPage < 0) {
            seenPage = pager.currentPage
            return@LaunchedEffect
        }
        if (seenPage == pager.currentPage) return@LaunchedEffect
        seenPage = pager.currentPage
        val play = autoplayAfter == pager.currentPage
        autoplayAfter = -1
        if (play) startStep() else stopAll()
    }
    LaunchedEffect(kathaReturn) {
        if (!kathaReturn) return@LaunchedEffect
        onKathaReturnConsumed()
        goTo(pager.currentPage + 1, autoplay = true)
    }
    LaunchedEffect(lang) {
        if (phase == "instruction" && job?.isActive == true) startStep()
    }
    SideEffect {
        PujaPlayback.onNext = {
            scope.launch {
                countJob?.cancel()
                countdownMs = 0
                goTo(pager.currentPage + 1, autoplay = true)
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            playToken += 1
            job?.cancel()
            countJob?.cancel()
            PujaPlayback.onNext = null
            PujaPlayback.stop()
        }
    }

    if (step == null) return
    Column(
        Modifier.fillMaxWidth().background(Surface).navigationBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (playable) {
            val mantraAudio = step.mantras.any { id ->
                val mantra = mantras[id]
                mantra?.audio != null || mantra?.listAudio != null
            }
            if (mantraAudio) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        MantraScript.Telugu to R.string.view_telugu,
                        MantraScript.Devanagari to R.string.view_devanagari,
                        MantraScript.Iast to R.string.view_iast,
                    ).forEach { (value, label) ->
                        val on = value == script
                        Text(
                            stringResource(label),
                            color = if (on) Ivory else Ink,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .heightIn(min = 56.dp)
                                .clip(RoundedCornerShape(999.dp))
                                .background(if (on) Leaf else Ivory)
                                .border(1.dp, if (on) Leaf else LeafBorder, RoundedCornerShape(999.dp))
                                .clickable { onScript(value) }
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                        )
                    }
                }
            }
            val showingPause = (running && !hold.paused && countdownMs == 0) || (countdownMs > 0 && !countFrozen)
            val playText = if (showingPause) stringResource(R.string.pause_label) else stringResource(R.string.play_step, page + 1)
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    stringResource(R.string.guided_back),
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clickable {
                            countJob?.cancel()
                            countdownMs = 0
                            goTo(page - 1, autoplay = false)
                        }
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Leaf)
                            .clickable {
                                when {
                                    countdownMs > 0 && !countFrozen -> countFrozen = true
                                    countdownMs > 0 && countFrozen -> countFrozen = false
                                    running && !hold.paused -> {
                                        hold.paused = true
                                        PujaPlayback.pause()
                                    }
                                    hold.paused && running -> {
                                        hold.paused = false
                                        PujaPlayback.resume(context)
                                    }
                                    else -> startStep()
                                }
                            }
                            .semantics { contentDescription = playText },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            if (showingPause) "II" else "▶",
                            color = Ivory,
                            fontSize = 22.sp,
                            modifier = Modifier.clearAndSetSemantics {},
                        )
                    }
                    Text(
                        stringResource(if (showingPause) R.string.pause_label else R.string.play_label),
                        color = Leaf,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Box(
                    Modifier.heightIn(min = 56.dp).clickable {
                        if (countdownMs > 0) {
                            countJob?.cancel()
                            countdownMs = 0
                            countFrozen = false
                        } else {
                            goTo(page + 1, autoplay = true)
                        }
                    }.padding(horizontal = 8.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    if (countdownMs > 0) {
                        val fraction = countdownMs / COUNTDOWN_MS.toFloat()
                        Canvas(Modifier.size(56.dp)) {
                            val stroke = 4.dp.toPx()
                            val inset = stroke / 2f
                            val arc = Size(size.width - stroke, size.height - stroke)
                            drawArc(
                                color = LeafBorder,
                                startAngle = 0f,
                                sweepAngle = 360f,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arc,
                                style = Stroke(stroke, cap = StrokeCap.Round),
                            )
                            drawArc(
                                color = Gold,
                                startAngle = -90f,
                                sweepAngle = 360f * fraction,
                                useCenter = false,
                                topLeft = Offset(inset, inset),
                                size = arc,
                                style = Stroke(stroke, cap = StrokeCap.Round),
                            )
                        }
                    }
                    Text(stringResource(R.string.guided_next), color = Maroon, style = MaterialTheme.typography.labelLarge)
                }
            }
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1f to R.string.speed_1, 1.25f to R.string.speed_125, 1.5f to R.string.speed_15).forEach { (value, label) ->
                    val on = value == speed
                    Text(
                        stringResource(label),
                        color = if (on) Ivory else Ink,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (on) Leaf else Ivory)
                            .border(1.dp, if (on) Leaf else LeafBorder, RoundedCornerShape(999.dp))
                            .clickable {
                                speed = value
                                PujaPlayback.setSpeed(value)
                            }
                            .padding(horizontal = 14.dp, vertical = 16.dp),
                    )
                }
            }
            Text(
                stringResource(R.string.repeat_step),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.heightIn(min = 56.dp).clickable { startStep() }.padding(vertical = 12.dp),
            )
        } else {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(R.string.guided_previous),
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clickable { goTo(page - 1, autoplay = false) }
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                )
                Text(
                    stringResource(R.string.guided_step, page + 1, pages.size),
                    color = Ink,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    stringResource(R.string.guided_next),
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clickable { goTo(page + 1, autoplay = true) }
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
fun LitLine(text: String, lit: Boolean, fontSize: TextUnit, style: TextStyle, modifier: Modifier = Modifier) {
    if (!lit) {
        Text(text, color = Ink, fontSize = fontSize, lineHeight = fontSize * 1.45f, style = style, modifier = modifier)
        return
    }
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(color = LeafDark, textDecoration = TextDecoration.Underline)) { append(text) }
        },
        fontSize = fontSize,
        lineHeight = fontSize * 1.45f,
        style = style,
        modifier = modifier,
    )
}
