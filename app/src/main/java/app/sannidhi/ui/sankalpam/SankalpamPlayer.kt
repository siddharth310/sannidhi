package app.sannidhi.ui.sankalpam

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sannidhi.R
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Sacred
import app.sannidhi.practice.Playback
import app.sannidhi.practice.PracticeRepository
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.common.SacredText
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.puja.MantraScript
import app.sannidhi.ui.puja.mantraScript
import app.sannidhi.ui.puja.mantraView
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Leaf
import app.sannidhi.ui.theme.LeafBorder
import app.sannidhi.ui.theme.LeafDark
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Surface
import app.sannidhi.ui.theme.TintSankalpam
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

private data class Playable(
    val title: String,
    val mantra: Boolean,
    val names: List<Sacred>,
    val line: Sacred?,
    val file: String?,
    val nameStarts: List<Long>,
    val wordStarts: List<Long>,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SankalpamPlayerScreen(
    id: String,
    settings: AppSettings,
    repository: ContentRepository,
    practice: PracticeRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    val row by practice.observe(id).collectAsState(initial = null)
    val total by practice.total(id).collectAsState(initial = 0)
    val todayKey = settings.today().toString()
    val todayCount by practice.dayTotal(id, todayKey).collectAsState(initial = 0)
    var playable by remember { mutableStateOf<Playable?>(null) }
    var script by remember { mutableStateOf(mantraScript(settings)) }
    var speed by remember { mutableStateOf(Playback.speed) }
    var position by remember { mutableLongStateOf(0L) }
    var playing by remember { mutableStateOf(false) }
    var sheet by remember { mutableStateOf<String?>(null) }
    var goalShown by remember(id) { mutableStateOf(false) }
    var primed by remember(id) { mutableStateOf(false) }
    var baseline by remember(id) { mutableIntStateOf(0) }
    var added by remember { mutableStateOf(false) }
    var glow by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    val lang = settings.language()
    val listState = rememberLazyListState()
    var follow by remember { mutableStateOf(true) }
    var autoUntil by remember { mutableLongStateOf(0L) }
    val current = row
    val loaded = playable

    LaunchedEffect(current?.textId, settings.lang) {
        val sankalpam = current ?: return@LaunchedEffect
        playable = repository.loadPlayable(sankalpam.textId, settings)
    }
    LaunchedEffect(id) {
        practice.total(id).collect { value ->
            val sankalpam = practice.sankalpam(id) ?: return@collect
            if (sankalpam.goalNoted) return@collect
            val goal = sankalpam.goal
            if (!primed) {
                baseline = value
                primed = true
            } else if (!goalShown && baseline < goal && value >= goal) {
                goalShown = true
                practice.noteGoal(id)
            }
        }
    }
    LaunchedEffect(loaded?.file, current?.id) {
        val file = loaded?.file ?: return@LaunchedEffect
        val sankalpam = current ?: return@LaunchedEffect
        val start = runCatching { LocalDate.parse(sankalpam.startDate) }.getOrNull()
        if (start != null && settings.today().isBefore(start)) return@LaunchedEffect
        Playback.prepare(context, file, loaded.title, loaded.title, sankalpam.id, settings.today(), loaded.mantra)
    }
    LaunchedEffect(Unit) {
        while (true) {
            position = Playback.position()
            playing = Playback.playing()
            delay(50)
        }
    }
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
            Playback.stop()
        }
    }

    val start = current?.let { runCatching { LocalDate.parse(it.startDate) }.getOrNull() }
    val future = start != null && settings.today().isBefore(start)
    val goal = current?.goal ?: 0
    val countLine = if (goalShown && goal > 0) {
        stringResource(R.string.goal_reached_line, indian(goal))
    } else {
        stringResource(R.string.player_today_total, indian(todayCount), indian(total), indian(goal))
    }
    val lit = when {
        loaded == null -> -1
        loaded.mantra -> litIndex(loaded.wordStarts, position)
        else -> litIndex(loaded.nameStarts, position)
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { moving ->
            if (moving && System.currentTimeMillis() > autoUntil) {
                follow = false
            }
        }
    }
    LaunchedEffect(follow) {
        if (!follow) {
            delay(5000)
            follow = true
        }
    }
    LaunchedEffect(lit, follow) {
        if (follow && lit >= 0 && loaded?.mantra == false) {
            autoUntil = System.currentTimeMillis() + 800
            val viewport = listState.layoutInfo.viewportEndOffset - listState.layoutInfo.viewportStartOffset
            listState.animateScrollToItem(lit, scrollOffset = -(viewport * 0.4f).toInt())
        }
    }
    LaunchedEffect(total) {
        if (loaded?.mantra == true && loaded.wordStarts.isEmpty() && playing) {
            glow = true
            delay(400)
            glow = false
        }
    }

    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(loaded?.title ?: stringResource(R.string.module_sankalpam), onBack, onHome)
        if (current != null && start != null) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    stringResource(R.string.player_japam_day, dayNumber(start, settings.today()), current.days),
                    color = InkSoft,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    stringResource(R.string.about_action),
                    color = Leaf,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.heightIn(min = 56.dp).clickable { sheet = "about" }.padding(vertical = 12.dp),
                )
            }
        }
        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (loaded != null && !loaded.mantra) {
                LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                    itemsIndexed(loaded.names, key = { index, _ -> index }) { index, name ->
                        LitRow(index + 1, name, script, lang, lit == index)
                    }
                    if (loaded.file == null && !future) {
                        item {
                            LeafButton(stringResource(R.string.done_reading), Modifier.padding(16.dp)) {
                                scope.launch {
                                    practice.add(id, 1, "done", settings.today())
                                    added = true
                                    delay(2000)
                                    listState.scrollToItem(0)
                                    added = false
                                }
                            }
                        }
                    }
                }
            } else if (loaded?.line != null) {
                Column(
                    Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val shown = loaded.line.pick(mantraView(script), lang)
                    val words = shown.split(Regex("\\s+")).filter { it.isNotEmpty() }
                    val text = buildAnnotatedString {
                        words.forEachIndexed { index, word ->
                            if (index > 0) append(" ")
                            if (lit == index) {
                                withStyle(SpanStyle(color = LeafDark, textDecoration = TextDecoration.Underline)) { append(word) }
                            } else {
                                append(word)
                            }
                        }
                    }
                    Text(
                        text,
                        color = if (glow) LeafDark else Ink,
                        fontSize = 30.sp,
                        lineHeight = 42.sp,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    val face = ringFace(total)
                    Ring(face.first, face.second, enabled = !playing && !future) {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        if ((total + 1) % 108 == 0) view.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK)
                        scope.launch { practice.add(id, 1, "ring", settings.today()) }
                    }
                }
            }
        }
        if (added) {
            Text(
                stringResource(R.string.reading_added),
                color = LeafDark,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        if (future && start != null) {
            val whenLabel = if (start == settings.today().plusDays(1)) {
                stringResource(R.string.starts_tomorrow)
            } else {
                civil(start, settings.lang)
            }
            Text(
                stringResource(R.string.player_starts, whenLabel),
                color = InkSoft,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )
        } else {
            Text(countLine, color = Ink, fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
        }
        if (!future) {
            PlayerControls(
                script = script,
                speed = speed,
                playing = playing,
                showTransport = loaded?.file != null,
                onScript = { script = it },
                onSpeed = {
                    speed = it
                    Playback.setSpeed(it)
                },
                onPlay = {
                    if (playing) Playback.pause() else Playback.play(context)
                },
                onOutside = { sheet = "hand" },
            )
        }
    }

    val open = sheet
    if (open != null && current != null) {
        ModalBottomSheet(
            onDismissRequest = { sheet = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Ivory,
        ) {
            when (open) {
                "hand" -> HandSheet(current, settings, practice) { sheet = null }
                "edit" -> EditSheet(current, total) { nextGoal, nextDays ->
                    scope.launch {
                        practice.updatePlan(current.id, nextGoal, nextDays)
                        sheet = null
                    }
                }
                else -> AboutSheet(current, settings, practice, total, onEdit = { sheet = "edit" }) {
                    scope.launch {
                        practice.markComplete(current.id, settings.today())
                        onBack()
                    }
                }
            }
        }
    }
}

@Composable
private fun LitRow(number: Int, name: Sacred, script: MantraScript, lang: Lang, lit: Boolean) {
    val fill by animateColorAsState(if (lit) TintSankalpam else Ivory, tween(120), label = "lit")
    val ink by animateColorAsState(if (lit) LeafDark else Ink, tween(120), label = "ink")
    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp).background(fill),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(3.dp).height(56.dp).background(if (lit) Gold else Ivory))
        Text(number.toString(), color = InkSoft, modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.labelLarge)
        SacredText(name, mantraView(script), lang, fontSize = 22.sp, color = ink, modifier = Modifier.weight(1f).padding(end = 16.dp))
    }
}

@Composable
private fun Ring(shown: Int, round: Int, enabled: Boolean, onTap: () -> Unit) {
    val fraction = (shown / 108f).coerceIn(0f, 1f)
    Box(Modifier.size(220.dp).clickable(enabled = enabled, onClick = onTap), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize().padding(8.dp)) {
            val stroke = 6.dp.toPx()
            drawArc(LeafBorder, 0f, 360f, false, style = Stroke(stroke, cap = StrokeCap.Round))
            drawArc(Gold, -90f, 360f * fraction, false, style = Stroke(stroke, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(shown.toString(), color = LeafDark, fontSize = 48.sp)
            Text(stringResource(R.string.ring_of, 108, round), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun PlayerControls(
    script: MantraScript,
    speed: Float,
    playing: Boolean,
    showTransport: Boolean,
    onScript: (MantraScript) -> Unit,
    onSpeed: (Float) -> Unit,
    onPlay: () -> Unit,
    onOutside: () -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().background(Surface).navigationBarsPadding().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Gold))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
        if (showTransport) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                .clickable { onSpeed(value) }
                                .padding(horizontal = 14.dp, vertical = 16.dp),
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        Modifier.size(72.dp).clip(CircleShape).background(Leaf).clickable(onClick = onPlay),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(if (playing) "II" else "▶", color = Ivory, fontSize = 22.sp)
                    }
                    Text(stringResource(if (playing) R.string.pause_label else R.string.play_label), color = Leaf, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
        Text(
            stringResource(R.string.outside_app),
            color = Leaf,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onOutside).padding(vertical = 12.dp),
        )
    }
}

@Composable
private fun HandSheet(row: app.sannidhi.practice.Sankalpam, settings: AppSettings, practice: PracticeRepository, onDone: () -> Unit) {
    val scope = rememberCoroutineScope()
    val today = settings.today()
    val start = runCatching { LocalDate.parse(row.startDate) }.getOrNull() ?: today
    val earliest = if (start.isAfter(today.minusDays(7))) start else today.minusDays(7)
    val dates = generateSequence(today) { it.minusDays(1) }.takeWhile { !it.isBefore(earliest) }.toList()
    var count by remember { mutableIntStateOf(if (row.unit == "repetition") 108 else 1) }
    var date by remember { mutableStateOf(today) }
    Column(Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.hand_title), color = Ink, style = MaterialTheme.typography.headlineMedium)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlineButton("−", { if (count > 0) count -= 1 }, modifier = Modifier.size(56.dp))
            Text(indian(count), color = Ink, fontSize = 28.sp, style = MaterialTheme.typography.headlineMedium)
            OutlineButton("+", { count += 1 }, modifier = Modifier.size(56.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(1, 11, 108, 1008).forEach { chip ->
                Text(
                    indian(chip),
                    color = if (count == chip) Ivory else Ink,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (count == chip) Leaf else Ivory)
                        .border(1.dp, LeafBorder, RoundedCornerShape(999.dp))
                        .clickable { count = chip }
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                )
            }
        }
        Text(stringResource(R.string.hand_which_day), color = Ink, style = MaterialTheme.typography.labelLarge)
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            dates.forEach { day ->
                val label = when (day) {
                    today -> stringResource(R.string.today)
                    today.minusDays(1) -> stringResource(R.string.day_yesterday)
                    else -> civil(day, settings.lang, "EEE d")
                }
                Text(
                    label,
                    color = if (date == day) Ivory else Ink,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (date == day) Leaf else Ivory)
                        .clickable { date = day }
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                )
            }
        }
        Text(stringResource(R.string.hand_note), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        LeafButton(stringResource(R.string.hand_add, indian(count)), enabled = count > 0) {
            scope.launch {
                practice.add(row.id, count, "hand", date)
                onDone()
            }
        }
    }
}

@Composable
private fun AboutSheet(
    row: app.sannidhi.practice.Sankalpam,
    settings: AppSettings,
    practice: PracticeRepository,
    total: Int,
    onEdit: () -> Unit,
    onComplete: () -> Unit,
) {
    val prefix = settings.today().toString().take(7)
    val entries by practice.monthEntries(row.id, prefix).collectAsState(initial = emptyList())
    val days = entries.groupBy { it.localDate }.entries
        .map { (date, list) -> Triple(date, list.sumOf { it.count }, list.any { it.source == "hand" }) }
        .filter { it.second > 0 }
        .sortedByDescending { it.first }
    val start = runCatching { LocalDate.parse(row.startDate) }.getOrNull()
    val end = start?.let { lastDay(it, row.days) }
    Column(
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        row.reason?.takeIf { it.isNotBlank() }?.let { reason ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = InkSoft)
                Text("$reason · ${stringResource(R.string.about_only_you)}", color = Ink, style = MaterialTheme.typography.bodyLarge)
            }
        }
        if (start != null && end != null) {
            Text(
                stringResource(R.string.about_plan, indian(row.goal), row.days, civil(start, settings.lang), civil(end, settings.lang)),
                color = Ink,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Text(stringResource(R.string.this_month), color = Ink, style = MaterialTheme.typography.headlineMedium)
        days.forEach { (date, count, hand) ->
            val parsed = runCatching { LocalDate.parse(date) }.getOrNull()
            Row(Modifier.fillMaxWidth().heightIn(min = 56.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(parsed?.let { civil(it, settings.lang, "EEE d MMM") } ?: date, color = Ink, style = MaterialTheme.typography.bodyLarge)
                    if (hand) Text(stringResource(R.string.added_by_hand), color = InkSoft, style = MaterialTheme.typography.labelLarge)
                }
                Text(indian(count), color = LeafDark, style = MaterialTheme.typography.headlineMedium)
            }
        }
        OutlineButton(stringResource(R.string.change_goal_days), onEdit, modifier = Modifier.fillMaxWidth())
        OutlineButton(stringResource(R.string.mark_complete), onComplete, modifier = Modifier.fillMaxWidth(), color = Maroon)
    }
}

@Composable
private fun EditSheet(row: app.sannidhi.practice.Sankalpam, total: Int, onSave: (Int, Int) -> Unit) {
    var goalText by remember { mutableStateOf(row.goal.toString()) }
    var daysText by remember { mutableStateOf(row.days.toString()) }
    var error by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.change_goal_days), color = Ink, style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.goal_label), color = InkSoft, style = MaterialTheme.typography.labelLarge)
        app.sannidhi.ui.sankalpam.GoalField(goalText) { goalText = it.filter { ch -> ch.isDigit() }.take(7) }
        Text(stringResource(R.string.days_label), color = InkSoft, style = MaterialTheme.typography.labelLarge)
        app.sannidhi.ui.sankalpam.GoalField(daysText) { daysText = it.filter { ch -> ch.isDigit() }.take(4) }
        Text(stringResource(R.string.edit_keeps, indian(total)), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        if (error) Text(stringResource(R.string.goal_zero_error), color = Maroon, style = MaterialTheme.typography.bodyLarge)
        LeafButton(stringResource(R.string.save_plan)) {
            val goal = goalText.toIntOrNull() ?: 0
            val days = daysText.toIntOrNull() ?: 0
            if (goal <= 0 || days <= 0) error = true else onSave(goal, days)
        }
    }
}

@Composable
internal fun GoalField(value: String, onValue: (String) -> Unit) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = androidx.compose.ui.text.TextStyle(color = Ink, fontSize = 18.sp),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(Leaf),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(Surface).padding(16.dp),
    )
}

private suspend fun ContentRepository.loadPlayable(id: String, settings: AppSettings): Playable {
    val lang = settings.language()
    val text = text(id)
    if (text != null) {
        val linked = text.mantra?.let { mantra(it) }
        val mantraMode = text.unit == "repetition" || text.type == "mantra"
        val mantraAudio = linked?.audio
        val file = when {
            mantraMode && hasAssetAudio(mantraAudio?.file) -> mantraAudio?.file
            hasAssetAudio(text.audio?.file) -> text.audio?.file
            else -> null
        }
        val title = text.title?.pick(lang) ?: id
        return if (mantraMode) {
            Playable(title, true, emptyList(), linked?.text, file, emptyList(), mantraAudio?.wordsMs.orEmpty())
        } else {
            Playable(title, false, text.names.mapNotNull { it.text }, null, file, nameStarts(text.audio, text.names.size), emptyList())
        }
    }
    val mantra = mantra(id)
    val sacred = mantra?.text
    val title = sacred?.pick(settings.textViewEnum(), lang) ?: id
    return Playable(
        title,
        true,
        emptyList(),
        sacred,
        mantra?.audio?.file?.takeIf { hasAssetAudio(it) },
        emptyList(),
        mantra?.audio?.wordsMs.orEmpty(),
    )
}
