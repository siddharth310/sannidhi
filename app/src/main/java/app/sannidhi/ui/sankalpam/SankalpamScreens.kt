package app.sannidhi.ui.sankalpam

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sannidhi.BuildConfig
import app.sannidhi.R
import app.sannidhi.content.Chantable
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Deity
import app.sannidhi.content.Lang
import app.sannidhi.content.Status
import app.sannidhi.practice.PracticeRepository
import app.sannidhi.practice.Sankalpam
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.common.SacredText
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.puja.GodCard
import app.sannidhi.ui.puja.SearchField
import app.sannidhi.ui.puja.deityEnglishName
import app.sannidhi.ui.puja.deityScriptName
import app.sannidhi.ui.puja.daysOnOrAfter
import app.sannidhi.ui.puja.matchesQuery
import app.sannidhi.ui.puja.searchParts
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
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private data class JapamCard(
    val row: Sankalpam,
    val title: String,
    val god: String,
    val total: Int,
    val draft: Boolean,
)

@Composable
fun SankalpamHomeScreen(
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    practice: PracticeRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPlay: (String) -> Unit,
    onDone: (String) -> Unit,
    onNew: () -> Unit,
) {
    val tab by store.sankalpamTab().collectAsState(initial = "japam")
    val active by practice.activeJapams().collectAsState(initial = emptyList())
    val done by practice.completed().collectAsState(initial = emptyList())
    val totals by practice.totals().collectAsState(initial = emptyList())
    var cards by remember { mutableStateOf<List<JapamCard>>(emptyList()) }
    var doneCards by remember { mutableStateOf<List<JapamCard>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val lang = settings.language()
    val totalOf = totals.associate { it.sankalpamId to it.total }
    LaunchedEffect(active, done, totals, settings.lang) {
        cards = active.map { row -> repository.japamCard(row, totalOf[row.id] ?: 0, lang) }
        doneCards = done.map { row -> repository.japamCard(row, totalOf[row.id] ?: 0, lang) }
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.module_sankalpam), onBack, onHome)
        JapamTabs(tab) { chosen -> scope.launch { store.setSankalpamTab(chosen) } }
        when (tab) {
            "groups" -> GroupsSoon()
            "completed" -> CompletedList(doneCards, settings.lang, onDone)
            else -> JapamList(cards, settings, onPlay, onNew)
        }
    }
}

@Composable
private fun JapamTabs(selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Ivory).padding(horizontal = 8.dp)) {
        listOf("japam" to R.string.tab_japam, "groups" to R.string.tab_groups, "completed" to R.string.tab_completed)
            .forEach { (id, label) ->
                Column(
                    Modifier.weight(1f).clickable { onSelect(id) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(label),
                        color = if (selected == id) Leaf else InkSoft,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.heightIn(min = 48.dp).padding(top = 12.dp),
                    )
                    Box(
                        Modifier.fillMaxWidth().height(2.dp).background(if (selected == id) Leaf else Ivory),
                    )
                }
            }
    }
}

@Composable
private fun JapamList(cards: List<JapamCard>, settings: AppSettings, onPlay: (String) -> Unit, onNew: () -> Unit) {
    val today = settings.today()
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cards.isEmpty()) {
            LeafButton(stringResource(R.string.start_japam), onClick = onNew)
            Text(stringResource(R.string.japam_empty), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        } else {
            cards.forEach { card ->
                SankalpamCard(card, settings.lang, today) { onPlay(card.row.id) }
            }
            OutlineButton(stringResource(R.string.start_japam_plus), onNew)
        }
    }
}

@Composable
private fun CompletedList(cards: List<JapamCard>, lang: String, onDone: (String) -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cards.isEmpty()) {
            Text(stringResource(R.string.completed_empty), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        } else {
            cards.forEach { card ->
                val ended = card.row.completedAt?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).clickable { onDone(card.row.id) }.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(card.title, color = Ink, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        stringResource(
                            R.string.offered_line,
                            indian(card.total),
                            card.row.days,
                            ended?.let { civil(it, lang) }.orEmpty(),
                        ),
                        color = InkSoft,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupsSoon() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(R.string.join_prompt), color = Ink, style = MaterialTheme.typography.headlineMedium)
            Text(
                "——————",
                color = InkSoft,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(Ivory).padding(16.dp),
            )
            OutlineButton(stringResource(R.string.join_action), onClick = {}, enabled = false)
        }
        Text(stringResource(R.string.groups_empty), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.groups_soon), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlineButton(stringResource(R.string.find_open_groups), {}, enabled = false, modifier = Modifier.weight(1f))
            OutlineButton(stringResource(R.string.start_a_group), {}, enabled = false, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun SankalpamCard(card: JapamCard, lang: String, today: LocalDate, onClick: () -> Unit) {
    val start = runCatching { LocalDate.parse(card.row.startDate) }.getOrNull()
    val waiting = start != null && today.isBefore(start)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintSankalpam)
            .border(1.dp, LeafBorder, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(card.title, color = Ink, style = MaterialTheme.typography.headlineMedium)
        if (card.god.isNotBlank()) Text(card.god, color = InkSoft, style = MaterialTheme.typography.labelLarge)
        if (waiting && start != null) {
            Text(stringResource(R.string.card_starts, civil(start, lang)), color = LeafDark, style = MaterialTheme.typography.bodyLarge)
        } else {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(indian(card.total), color = LeafDark, fontSize = 28.sp, style = MaterialTheme.typography.headlineMedium)
                Text(stringResource(R.string.card_so_far, indian(card.row.goal)), color = Ink, style = MaterialTheme.typography.bodyLarge)
            }
            if (start != null) {
                Text(stringResource(R.string.card_day_of, dayNumber(start, today), card.row.days), color = Ink, style = MaterialTheme.typography.bodyLarge)
            }
            if (card.total > 0 && card.row.goal > 0) {
                Box(Modifier.fillMaxWidth().height(2.dp).background(LeafBorder)) {
                    Box(
                        Modifier
                            .fillMaxWidth((card.total.toFloat() / card.row.goal).coerceIn(0f, 1f))
                            .height(2.dp)
                            .background(Gold),
                    )
                }
            }
        }
        if (card.draft && BuildConfig.DEBUG) {
            Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartJapamScreen(
    settings: AppSettings,
    repository: ContentRepository,
    practice: PracticeRepository,
    prefillTextId: String,
    prefillGoal: Int,
    prefillDays: Int,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onStarted: (String) -> Unit,
) {
    val prefill = prefillTextId.isNotBlank()
    var step by remember { mutableIntStateOf(if (prefill) 3 else 1) }
    var query by remember { mutableStateOf("") }
    var deities by remember { mutableStateOf<List<Deity>>(emptyList()) }
    var counts by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var deityId by remember { mutableStateOf("") }
    var choices by remember { mutableStateOf<List<Chantable>>(emptyList()) }
    var chosen by remember { mutableStateOf<Chantable?>(null) }
    var goalText by remember { mutableStateOf(if (prefillGoal > 0) prefillGoal.toString() else "") }
    var days by remember { mutableIntStateOf(if (prefillDays > 0) prefillDays else 41) }
    val oddDays = prefillDays > 0 && prefillDays !in setOf(11, 21, 41)
    var otherDays by remember { mutableStateOf(if (oddDays) prefillDays.toString() else "") }
    var otherOpen by remember { mutableStateOf(oddDays) }
    var start by remember { mutableStateOf(settings.today()) }
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<Int?>(null) }
    var masam by remember { mutableIntStateOf(30) }
    var observance by remember { mutableStateOf<Pair<LocalDate, String>?>(null) }
    var picking by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val lang = settings.language()
    LaunchedEffect(settings.lang) {
        val gods = repository.deities().sortedBy { it.order }
        deities = gods
        counts = gods.associate { god -> god.id to repository.textsAndMantrasFor(god.id).size }
    }
    LaunchedEffect(deityId) {
        if (deityId.isNotBlank()) choices = repository.textsAndMantrasFor(deityId)
    }
    LaunchedEffect(prefillTextId) {
        if (!prefill) return@LaunchedEffect
        val text = repository.text(prefillTextId)
        val mantra = repository.mantra(prefillTextId)
        deityId = text?.deity ?: mantra?.deity ?: ""
        chosen = text?.let {
            Chantable(it.id, it.deity, it.unit == "repetition" || it.type == "mantra", it.type, it.unit, it.title, status = it.status, mantraId = it.mantra)
        } ?: mantra?.let {
            Chantable(it.id, it.deity, true, "mantra", "repetition", sacred = it.text, status = it.status, mantraId = it.id)
        }
        if (goalText.isBlank()) goalText = if (chosen?.mantra == true) "10008" else "108"
    }
    LaunchedEffect(deityId, settings.cityId, settings.debugToday) {
        val today = settings.today()
        val year = repository.calendarYear(settings.cityId, today)
        val todayDay = year?.days?.firstOrNull { it.date == today.toString() }
        masam = if (year != null && todayDay != null) {
            year.days.count { it.masa == todayDay.masa && it.adhika == todayDay.adhika }.takeIf { it >= 27 } ?: 30
        } else {
            30
        }
        val limit = today.plusDays(30)
        val upcoming = daysOnOrAfter(repository, settings.cityId, today).filter { day ->
            val date = runCatching { LocalDate.parse(day.date) }.getOrNull()
            date != null && !date.isAfter(limit) && day.observances.isNotEmpty()
        }
        val hit = upcoming.firstOrNull { day ->
            day.observances.any { id -> repository.observance(id)?.deity == deityId }
        } ?: upcoming.firstOrNull()
        val obsId = hit?.observances?.firstOrNull { id ->
            deityId.isBlank() || repository.observance(id)?.deity == deityId
        } ?: hit?.observances?.firstOrNull()
        val title = obsId?.let { repository.observance(it)?.title?.pick(lang) }.orEmpty()
        val date = hit?.date?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
        observance = if (date != null && title.isNotBlank()) date to title else null
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.start_japam), {
            when {
                step == 1 || (step == 3 && prefill) -> onBack()
                else -> step -= 1
            }
        }, onHome)
        Text(
            stringResource(R.string.step_of, step, 3),
            color = InkSoft,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        when (step) {
            1 -> {
                val shown = deities.filter { matchesQuery(query, searchParts(it)) }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item(span = { GridItemSpan(2) }) { SearchField(query) { query = it } }
                    item(span = { GridItemSpan(2) }) {
                        Text(stringResource(R.string.choose_a_god), color = Ink, style = MaterialTheme.typography.headlineMedium)
                    }
                    items(shown, key = { it.id }) { deity ->
                        val count = counts[deity.id] ?: 0
                        GodCard(
                            deity,
                            settings,
                            repository,
                            meta = if (count == 0) stringResource(R.string.coming_later) else deityEnglishName(deity),
                            enabled = count > 0,
                        ) {
                            if (count > 0) {
                                deityId = deity.id
                                step = 2
                            }
                        }
                    }
                }
            }
            2 -> {
                Column(
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(stringResource(R.string.choose_what_to_chant), color = Ink, style = MaterialTheme.typography.headlineMedium)
                    choices.forEach { item ->
                        Column(
                            Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable {
                                chosen = item
                                goalText = if (item.mantra) "10008" else "108"
                                days = 41
                                step = 3
                            }.padding(vertical = 8.dp),
                        ) {
                            if (item.sacred != null) {
                                SacredText(item.sacred, settings.textViewEnum(), lang, fontSize = 22.sp)
                            } else {
                                Text(item.title?.pick(lang) ?: item.id, color = Ink, style = MaterialTheme.typography.headlineMedium)
                            }
                            val kind = kindLabel(item.type)
                            val mark = if (item.hasRecording) "$kind · ${stringResource(R.string.has_recording)}" else kind
                            Text(mark, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                            if (item.status == Status.draft && BuildConfig.DEBUG) {
                                Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
                            }
                            Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(1.dp).background(Gold))
                        }
                    }
                    LeafButton(stringResource(R.string.continue_step), Modifier.padding(top = 16.dp)) { step = 3 }
                }
            }
            else -> {
                val item = chosen
                Column(Modifier.fillMaxSize()) {
                    Column(
                        Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        if (item != null) {
                            Column(
                                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(TintSankalpam).padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(item.title?.pick(lang) ?: item.sacred?.let { sacredName(it, settings, lang) } ?: item.id, color = Ink, style = MaterialTheme.typography.headlineMedium)
                                Text(
                                    stringResource(R.string.change_choice),
                                    color = Leaf,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.heightIn(min = 56.dp).clickable { if (!prefill) step = 2 }.padding(vertical = 12.dp),
                                )
                            }
                        }
                        Text(stringResource(R.string.goal_label), color = Ink, style = MaterialTheme.typography.labelLarge)
                        NumberField(goalText) { goalText = it.filter { ch -> ch.isDigit() }.take(7) }
                        ChipRow(
                            (if (item?.mantra == true) listOf(108, 1008, 10008, 100000) else listOf(11, 21, 108, 1008))
                                .map { it to indian(it) },
                            goalText.toIntOrNull(),
                        ) { goalText = it.toString() }
                        Text(stringResource(R.string.days_label), color = Ink, style = MaterialTheme.typography.labelLarge)
                        ChipRow(
                            listOf(11 to "11", 21 to "21", 41 to "41", masam to stringResource(R.string.one_masam)),
                            if (otherOpen) null else days,
                        ) {
                            otherOpen = false
                            days = it
                        }
                        LeafChip(stringResource(R.string.other_number), otherOpen) { otherOpen = true }
                        if (otherOpen) {
                            NumberField(otherDays) {
                                otherDays = it.filter { ch -> ch.isDigit() }.take(4)
                                days = otherDays.toIntOrNull() ?: days
                            }
                        }
                        Text(stringResource(R.string.starts_label), color = Ink, style = MaterialTheme.typography.labelLarge)
                        val today = settings.today()
                        val todayWord = stringResource(R.string.today)
                        val tomorrowWord = stringResource(R.string.starts_tomorrow)
                        val observanceChip = observance?.let { (date, title) ->
                            date to "$title · ${civil(date, settings.lang, "EEE d")}"
                        }
                        val chips = listOf(today to todayWord, today.plusDays(1) to tomorrowWord) + listOfNotNull(observanceChip)
                        ChipRow(chips.map { it.first.toEpochDay().toInt() to it.second }, start.toEpochDay().toInt()) { epoch ->
                            start = LocalDate.ofEpochDay(epoch.toLong())
                        }
                        Text(
                            stringResource(R.string.starts_pick_date),
                            color = Leaf,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.heightIn(min = 56.dp).clickable { picking = true }.padding(vertical = 12.dp),
                        )
                        Text(stringResource(R.string.reason_optional), color = Ink, style = MaterialTheme.typography.labelLarge)
                        NumberField(reason, digitsOnly = false) { reason = it }
                        Text(stringResource(R.string.reason_help), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                        val goalNow = goalText.toIntOrNull() ?: 0
                        if (item == null) {
                            Text(stringResource(R.string.choose_chant_error), color = Maroon, style = MaterialTheme.typography.bodyLarge)
                        } else if (goalNow <= 0 || days <= 0) {
                            Text(stringResource(R.string.goal_zero_error), color = Maroon, style = MaterialTheme.typography.bodyLarge)
                        }
                        error?.let { Text(stringResource(it), color = Maroon, style = MaterialTheme.typography.bodyLarge) }
                    }
                    val goalNow = goalText.toIntOrNull() ?: 0
                    LeafButton(
                        stringResource(R.string.start_action),
                        Modifier.navigationBarsPadding().padding(16.dp),
                        enabled = item != null && goalNow > 0 && days > 0,
                    ) {
                        val goal = goalText.toIntOrNull() ?: 0
                        val chant = chosen
                        error = when {
                            chant == null -> R.string.choose_chant_error
                            goal <= 0 || days <= 0 -> R.string.goal_zero_error
                            else -> null
                        }
                        if (error == null && chant != null) {
                            scope.launch {
                                val id = practice.beginJapam(chant.id, chant.unit, goal, days, start, reason)
                                onStarted(id)
                            }
                        }
                    }
                }
            }
        }
    }
    if (picking) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { picking = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        start = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    picking = false
                }) { Text(stringResource(R.string.save_plan)) }
            },
        ) { DatePicker(state) }
    }
}

@Composable
fun CompletedJapamScreen(
    id: String,
    settings: AppSettings,
    repository: ContentRepository,
    practice: PracticeRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onStartAgain: (textId: String, goal: Int, days: Int) -> Unit,
) {
    val row by practice.observe(id).collectAsState(initial = null)
    val total by practice.total(id).collectAsState(initial = 0)
    var title by remember { mutableStateOf("") }
    val current = row
    LaunchedEffect(current?.textId, settings.lang) {
        val textId = current?.textId ?: return@LaunchedEffect
        title = repository.chantTitle(textId, settings)
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(title.ifBlank { stringResource(R.string.tab_completed) }, onBack, onHome)
        if (current != null) {
            val start = runCatching { LocalDate.parse(current.startDate) }.getOrNull()
            val end = current.completedAt?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp).navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Surface).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(indian(total), color = LeafDark, fontSize = 28.sp, style = MaterialTheme.typography.headlineMedium)
                    Text(
                        stringResource(
                            R.string.offered_line,
                            indian(total),
                            current.days,
                            end?.let { civil(it, settings.lang) }.orEmpty(),
                        ),
                        color = Ink,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    if (start != null && end != null) {
                        Text("${civil(start, settings.lang)} – ${civil(end, settings.lang)}", color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                    current.reason?.takeIf { it.isNotBlank() }?.let { reason ->
                        Text("🔒  $reason · ${stringResource(R.string.about_only_you)}", color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                }
                if (current.kind == "japam") {
                    LeafButton(stringResource(R.string.start_again)) {
                        onStartAgain(current.textId, current.goal, current.days)
                    }
                }
            }
        }
    }
}

private suspend fun ContentRepository.japamCard(row: Sankalpam, total: Int, lang: Lang): JapamCard {
    val text = text(row.textId)
    val mantra = if (text == null) mantra(row.textId) else null
    val deity = deity(text?.deity ?: mantra?.deity ?: "")
    val title = text?.title?.pick(lang)
        ?: mantra?.text?.let { sacred ->
            sacred.pick(
                when (lang) {
                    Lang.te -> app.sannidhi.content.TextView.Script
                    Lang.hi -> app.sannidhi.content.TextView.Devanagari
                    Lang.en -> app.sannidhi.content.TextView.Iast
                },
                lang,
            )
        }
        ?: row.textId
    return JapamCard(
        row = row,
        title = title,
        god = deity?.let { deityScriptName(it, lang) }.orEmpty(),
        total = total,
        draft = (text?.status ?: mantra?.status) == Status.draft,
    )
}

private suspend fun ContentRepository.chantTitle(id: String, settings: AppSettings): String {
    val lang = settings.language()
    text(id)?.title?.pick(lang)?.let { return it }
    mantra(id)?.text?.let { return sacredName(it, settings, lang) }
    return id
}

@Composable
private fun kindLabel(type: String): String = when (type) {
    "ashtottara" -> stringResource(R.string.kind_ashtottara)
    "sahasranama" -> stringResource(R.string.kind_sahasranama)
    "stotram" -> stringResource(R.string.kind_stotram)
    else -> stringResource(R.string.kind_mantra)
}

private fun sacredName(sacred: app.sannidhi.content.Sacred, settings: AppSettings, lang: Lang): String =
    sacred.pick(settings.textViewEnum(), lang)

@Composable
internal fun LeafButton(label: String, modifier: Modifier = Modifier, enabled: Boolean = true, onClick: () -> Unit) {
    Box(
        modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (enabled) Leaf else LeafBorder)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) Ivory else InkSoft, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
internal fun OutlineButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true, color: androidx.compose.ui.graphics.Color = Leaf) {
    Box(
        modifier
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (enabled) color else InkSoft, RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = if (enabled) color else InkSoft, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun LeafChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        label,
        color = if (selected) Ivory else Ink,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Leaf else Ivory)
            .border(1.dp, if (selected) Leaf else LeafBorder, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
    )
}

@Composable
private fun <T> ChipRow(options: List<Pair<T, String>>, selected: T?, onSelect: (T) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { (value, label) ->
            LeafChip(label, value == selected) { onSelect(value) }
        }
    }
}

@Composable
private fun NumberField(value: String, digitsOnly: Boolean = true, onValue: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = TextStyle(color = Ink, fontSize = 18.sp),
        cursorBrush = SolidColor(Leaf),
        singleLine = true,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(Surface).padding(16.dp),
    )
    if (!digitsOnly) Unit
}
