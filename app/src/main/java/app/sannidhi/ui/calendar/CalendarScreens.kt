@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.sannidhi.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import app.sannidhi.BuildConfig
import app.sannidhi.R
import app.sannidhi.content.CalendarDay
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Master
import app.sannidhi.content.Status
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.common.FloatingPills
import app.sannidhi.ui.common.ImageOrFallback
import app.sannidhi.ui.common.ModuleGlyph
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.puja.MantraScript
import app.sannidhi.ui.puja.mantraScript
import app.sannidhi.ui.puja.mantraStorage
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Leaf
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Peacock
import app.sannidhi.ui.theme.TintCalendar
import app.sannidhi.ui.theme.TintPuja
import app.sannidhi.ui.theme.TintStories
import app.sannidhi.ui.theme.CormorantFamily
import app.sannidhi.ui.theme.NotoSerifDevanagariFamily
import app.sannidhi.ui.theme.NotoSerifTeluguFamily
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.cos

private data class TodayUi(
    val day: CalendarDay?,
    val dateLabel: String,
    val samvatsara: String?,
    val masa: String?,
    val tithi: String?,
    val tithiTill: String?,
    val nakshatra: String?,
    val nakshatraTill: String?,
    val sunrise: String?,
    val sunset: String?,
    val rashiLabel: String?,
    val profile: String?,
    val note: String?,
    val tone: String?,
    val chips: List<Pair<Int, String>>,
    val goodLines: List<String> = emptyList(),
    val askCity: Boolean = false,
)

private sealed interface ObsLink {
    val id: String
    data class Puja(override val id: String) : ObsLink
    data class Story(override val id: String) : ObsLink
    data class Food(override val id: String) : ObsLink
}

private data class ObsAction(val kind: Int, val title: String?, val link: ObsLink)

@Composable
fun CalendarHomeScreen(
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onObservance: (String) -> Unit,
    onFindNakshatra: () -> Unit,
) {
    val lang = settings.language()
    val script = mantraScript(settings)
    val scope = rememberCoroutineScope()
    var dayTick by remember { mutableIntStateOf(0) }
    LaunchedEffect(settings.debugToday) {
        if (settings.debugToday != null) return@LaunchedEffect
        while (true) {
            val zone = java.time.ZoneId.systemDefault()
            val now = java.time.ZonedDateTime.now(zone)
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay(zone)
            val wait = java.time.Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000L)
            kotlinx.coroutines.delay(wait)
            dayTick += 1
        }
    }
    val today = if (settings.debugToday == null) {
        java.time.LocalDate.now().plusDays(if (dayTick < 0) 1 else 0)
    } else {
        settings.today()
    }
    val earlyLabel = stringResource(R.string.early_sadhana)
    val beginLabel = stringResource(R.string.good_to_begin)
    val todayLabel = stringResource(R.string.good_time_today)
    val lampLabel = stringResource(R.string.evening_lamp)
    val tillPattern = stringResource(R.string.till_time)
    val rangePattern = stringResource(R.string.time_range)
    var range by remember { mutableStateOf<CarriedRange?>(null) }
    var month by remember(today) { mutableStateOf(YearMonth.from(today)) }
    var beyond by remember { mutableStateOf(false) }
    var beyondFuture by remember { mutableStateOf(true) }
    var browsing by remember { mutableStateOf<Int?>(null) }
    var bubble by remember { mutableStateOf<CalendarDay?>(null) }
    var master by remember { mutableStateOf<Master?>(null) }
    var todayUi by remember { mutableStateOf<TodayUi?>(null) }
    var monthDays by remember { mutableStateOf<List<CalendarDay>>(emptyList()) }
    var observanceTitles by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    LaunchedEffect(settings.cityId) {
        range = carriedRange(repository)
    }
    LaunchedEffect(settings.cityId, today, settings.lang, settings.textView, settings.rashi, browsing, dayTick) {
        val loadedMaster = repository.master()
        master = loadedMaster
        val day = repository.day(settings.cityId, today)
        val year = repository.calendarYear(settings.cityId, today)
        val now = java.time.LocalDateTime.of(today, java.time.LocalTime.now())
        val labels = mapOf(
            GoodKind.Early to earlyLabel,
            GoodKind.Begin to beginLabel,
            GoodKind.Today to todayLabel,
            GoodKind.Lamp to lampLabel,
        )
        val goodLines = if (settings.cityId.isBlank()) {
            emptyList()
        } else {
            goodTimes(daysTouching(repository, settings.cityId, today), today).map { window ->
                goodPhrase(window, now, lang, labels.getValue(window.kind), tillPattern, rangePattern)
            }
        }
        val which = browsing ?: settings.rashi
        val note = if (which != null && day != null) repository.rashiNote(which, day, lang) else null
        val rashiId = which?.let { n -> loadedMaster.rashis.firstOrNull { it.n == n }?.id }
        val profile = rashiId?.let { repository.rashi().profiles[it]?.pick(lang) }?.takeIf { it.isNotBlank() }
        val sam = year?.let { samvatsaraName(loadedMaster, it.samvatsara.n) }
        todayUi = TodayUi(
            day = day,
            dateLabel = civilDate(today, lang),
            samvatsara = sam?.let { sacredName(it, script, lang) },
            masa = day?.let { masaName(loadedMaster, it, script, lang) },
            tithi = day?.let { tithiName(loadedMaster, it, script, lang) },
            tithiTill = clock12(day?.tithiEnds, lang),
            nakshatra = day?.let { nakshatraName(loadedMaster, it, script, lang) },
            nakshatraTill = clock12(day?.nakshatraEnds, lang),
            sunrise = day?.sunrise,
            sunset = day?.sunset,
            rashiLabel = which?.let { rashiName(loadedMaster, it, script, lang) },
            profile = profile,
            note = note?.text,
            tone = note?.tone,
            chips = loadedMaster.rashis.sortedBy { it.n }.map { it.n to rashiName(loadedMaster, it.n, script, lang) },
            goodLines = goodLines,
            askCity = settings.cityId.isBlank(),
        )
    }
    LaunchedEffect(settings.cityId, month, beyond) {
        monthDays = if (beyond) emptyList() else daysInMonth(repository, settings.cityId, month)
    }
    LaunchedEffect(settings.lang) {
        observanceTitles = repository.observances().associate { it.id to (it.title?.pick(lang) ?: it.id) }
    }

    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.module_calendar), onBack, onHome)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TodayCard(
                ui = todayUi,
                script = script,
                saved = settings.rashi,
                browsing = browsing,
                onScript = { scope.launch { store.setTextView(mantraStorage(it)) } },
                onChip = { browsing = it },
                onSave = {
                    val chosen = browsing ?: return@TodayCard
                    scope.launch { store.setRashi(chosen) }
                    browsing = null
                },
            )
            MonthBlock(
                month = month,
                beyond = beyond,
                lang = lang,
                script = script,
                master = master,
                days = monthDays,
                today = today,
                onStep = { forward ->
                    val bounds = range ?: return@MonthBlock
                    val first = YearMonth.from(bounds.start)
                    val last = YearMonth.from(bounds.end)
                    if (beyond) {
                        if (!forward && beyondFuture) {
                            beyond = false
                            month = last
                        } else if (forward && !beyondFuture) {
                            beyond = false
                            month = first
                        }
                        return@MonthBlock
                    }
                    val target = if (forward) month.plusMonths(1) else month.minusMonths(1)
                    when {
                        target.isAfter(last) -> {
                            beyond = true
                            beyondFuture = true
                        }
                        target.isBefore(first) -> {
                            beyond = true
                            beyondFuture = false
                        }
                        else -> month = target
                    }
                },
                birthNakshatra = master?.nakshatras?.firstOrNull { it.id == settings.nakshatra }?.n,
                onDay = { bubble = it },
            )
            if (!beyond && settings.nakshatra.isBlank() && !settings.findNakshatraDismissed) {
                Text(
                    stringResource(R.string.find_my_nakshatra),
                    color = Peacock,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onFindNakshatra).padding(vertical = 16.dp),
                )
            }
        }
    }

    val open = bubble
    val names = master
    if (open != null && names != null) {
        ModalBottomSheet(onDismissRequest = { bubble = null }, containerColor = Ivory) {
            val birth = names.nakshatras.firstOrNull { it.id == settings.nakshatra }?.n
            DayBubble(
                day = open,
                master = names,
                script = script,
                lang = lang,
                titles = observanceTitles,
                goodForYou = birth != null && goodForNakshatra(open.nakshatra, birth),
                onObservance = { id ->
                    bubble = null
                    onObservance(id)
                },
            )
        }
    }
}

@Composable
private fun TodayCard(
    ui: TodayUi?,
    script: MantraScript,
    saved: Int?,
    browsing: Int?,
    onScript: (MantraScript) -> Unit,
    onChip: (Int) -> Unit,
    onSave: () -> Unit,
) {
    val panel = ui ?: return
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintCalendar)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ScriptChips(script, onScript)
        Text(panel.dateLabel, color = Ink, style = MaterialTheme.typography.headlineMedium)
        SacredLine(listOfNotNull(panel.samvatsara, panel.masa).joinToString(" · "), script)
        if (panel.tithi != null) {
            NameAndTill(panel.tithi, panel.tithiTill, script)
        }
        if (panel.nakshatra != null) {
            NameAndTill(panel.nakshatra, panel.nakshatraTill, script)
        }
        val sun = listOfNotNull(
            panel.sunrise?.let { stringResource(R.string.sunrise_label, it) },
            panel.sunset?.let { stringResource(R.string.sunset_label, it) },
        ).joinToString(" · ")
        if (sun.isNotBlank()) {
            Text(sun, color = Ink, style = MaterialTheme.typography.bodyLarge)
        }
        if (panel.askCity) {
            Text(stringResource(R.string.pick_city_good_times), color = Ink, style = MaterialTheme.typography.bodyLarge)
        }
        panel.goodLines.forEach { line ->
            Text(line, color = Peacock, style = MaterialTheme.typography.bodyLarge)
        }
        Box(Modifier.fillMaxWidth().padding(vertical = 8.dp).height(1.dp).background(Gold))
        Text(stringResource(R.string.today_for_you), color = Ink, style = MaterialTheme.typography.headlineMedium)
        if (saved == null) {
            Text(stringResource(R.string.pick_rashi_note), color = Ink, style = MaterialTheme.typography.bodyLarge)
        }
        if (panel.rashiLabel != null && (saved != null || browsing != null)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ToneIcon(panel.tone)
                SacredLine(panel.rashiLabel, script, size = 22, fill = false)
            }
            panel.profile?.let { Text(it, color = InkSoft, style = MaterialTheme.typography.bodyLarge) }
            panel.note?.let { Text(it, color = Ink, style = MaterialTheme.typography.bodyLarge) }
        }
        if (browsing != null && browsing != saved) {
            OutlinedButton(onClick = onSave, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp)) {
                Text(stringResource(R.string.save_as_my_rashi), color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            panel.chips.forEach { (n, label) ->
                val viewed = (browsing ?: saved) == n
                val isSaved = saved == n
                val border = if (viewed) Maroon else Gold
                Text(
                    label,
                    color = if (viewed) Ivory else Ink,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (viewed) Maroon else if (isSaved) TintCalendar else Ivory)
                        .border(1.dp, border, RoundedCornerShape(999.dp))
                        .clickable { onChip(n) }
                        .padding(horizontal = 14.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun MonthBlock(
    month: YearMonth,
    beyond: Boolean,
    lang: Lang,
    script: MantraScript,
    master: Master?,
    days: List<CalendarDay>,
    today: LocalDate,
    birthNakshatra: Int?,
    onStep: (Boolean) -> Unit,
    onDay: (CalendarDay) -> Unit,
) {
    var drag by remember { mutableFloatStateOf(0f) }
    val threshold = with(LocalDensity.current) { 72.dp.toPx() }
    Column(
        Modifier
            .fillMaxWidth()
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { drag += it },
                onDragStopped = {
                    if (drag <= -threshold) onStep(true)
                    else if (drag >= threshold) onStep(false)
                    drag = 0f
                },
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MonthArrow(stringResource(R.string.month_previous)) { onStep(false) }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                if (!beyond) {
                    Text(civilMonth(month, lang), color = Ink, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                    val labels = master?.let { names ->
                        masasInOrder(days).joinToString(" · ") { masaName(names, it, script, lang) }
                    }.orEmpty()
                    if (labels.isNotBlank()) SacredLine(labels, script, align = TextAlign.Center)
                }
            }
            MonthArrow(stringResource(R.string.month_next)) { onStep(true) }
        }
        if (beyond) {
            Text(stringResource(R.string.ugadi_update), color = Ink, style = MaterialTheme.typography.bodyLarge)
        } else {
            MonthGrid(month, lang, days, today, birthNakshatra, onDay)
        }
    }
}

@Composable
private fun MonthArrow(label: String, onClick: () -> Unit) {
    Box(
        Modifier.size(56.dp).clip(CircleShape).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Peacock, style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
private fun MonthGrid(
    month: YearMonth,
    lang: Lang,
    days: List<CalendarDay>,
    today: LocalDate,
    birthNakshatra: Int?,
    onDay: (CalendarDay) -> Unit,
) {
    val start = weekStart(lang)
    val byDate = days.associateBy { it.date }
    val headers = (0 until 7).map { offset ->
        val day = java.time.DayOfWeek.of(((start.value - 1 + offset) % 7) + 1)
        weekdayShort(day, lang)
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth()) {
            headers.forEach { label ->
                Text(
                    label,
                    color = InkSoft,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        val blanks = leadingBlanks(month, start)
        val cells = List(blanks) { null as Int? } + (1..month.lengthOfMonth()).map { it as Int? }
        val padded = cells + List((7 - cells.size % 7) % 7) { null }
        padded.chunked(7).forEach { week ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                week.forEach { number ->
                    if (number == null) {
                        Box(Modifier.weight(1f).heightIn(min = 56.dp))
                    } else {
                        val date = month.atDay(number)
                        val day = byDate[date.toString()]
                        val good = day != null && birthNakshatra != null && goodForNakshatra(day.nakshatra, birthNakshatra)
                        DayCell(number, day, date == today, good, Modifier.weight(1f)) {
                            if (day != null) onDay(day)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    number: Int,
    day: CalendarDay?,
    today: Boolean,
    good: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(12.dp))
            .then(if (today) Modifier.border(1.dp, Gold, RoundedCornerShape(12.dp)) else Modifier)
            .clickable(enabled = day != null, onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            number.toString(),
            color = if (day == null) InkSoft else Ink,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
        )
        if (day != null) {
            MoonGlyph(pct = day.moonPct, waxing = day.isShukla, modifier = Modifier.size(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                day.observances.take(2).forEach { _ ->
                    Box(Modifier.padding(top = 2.dp).size(6.dp).clip(CircleShape).background(Maroon))
                }
                if (good) {
                    Box(Modifier.padding(top = 2.dp).size(6.dp).clip(CircleShape).background(Leaf))
                }
            }
        }
    }
}

@Composable
private fun DayBubble(
    day: CalendarDay,
    master: Master,
    script: MantraScript,
    lang: Lang,
    titles: Map<String, String>,
    goodForYou: Boolean,
    onObservance: (String) -> Unit,
) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(civilDate(LocalDate.parse(day.date), lang), color = Ink, style = MaterialTheme.typography.headlineMedium)
        SacredLine(masaName(master, day, script, lang), script, size = 22)
        val nextNumber = if (day.tithi >= 30) 1 else day.tithi + 1
        val nextName = tithiName(master, day.copy(tithi = nextNumber), script, lang)
        NameAndTill(tithiName(master, day, script, lang), clock12(day.tithiEnds, lang), script, nextName)
        NameAndTill(nakshatraName(master, day, script, lang), clock12(day.nakshatraEnds, lang), script)
        Text(stringResource(R.string.moon_percent, day.moonPct), color = Ink, style = MaterialTheme.typography.bodyLarge)
        val sun = listOfNotNull(
            day.sunrise.let { stringResource(R.string.sunrise_label, it) },
            day.sunset.let { stringResource(R.string.sunset_label, it) },
        ).joinToString(" · ")
        Text(sun, color = Ink, style = MaterialTheme.typography.bodyLarge)
        if (goodForYou) {
            Text(stringResource(R.string.good_for_you), color = Leaf, style = MaterialTheme.typography.bodyLarge)
        }
        if (day.observances.isNotEmpty()) {
            Text(stringResource(R.string.on_this_day), color = InkSoft, style = MaterialTheme.typography.labelLarge)
        }
        day.observances.forEach { id ->
            Text(
                titles[id] ?: id,
                color = Peacock,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable { onObservance(id) }.padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
fun ObservanceScreen(
    obsId: String,
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPuja: (String) -> Unit,
    onStory: (String) -> Unit,
    onNaivedyam: (String) -> Unit,
) {
    val lang = settings.language()
    val script = mantraScript(settings)
    var title by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<String?>(null) }
    var symbol by remember { mutableStateOf<String?>(null) }
    var draft by remember { mutableStateOf(false) }
    var whenDate by remember { mutableStateOf<String?>(null) }
    var whenTithi by remember { mutableStateOf<String?>(null) }
    var sections by remember { mutableStateOf<List<Pair<Int, String>>>(emptyList()) }
    var actions by remember { mutableStateOf<List<ObsAction>>(emptyList()) }

    LaunchedEffect(obsId, settings.cityId, settings.lang, settings.textView, settings.today()) {
        val obs = repository.observance(obsId)
        if (obs == null) {
            title = obsId
            return@LaunchedEffect
        }
        val prose = lang
        title = obs.title?.pick(prose) ?: obsId
        image = obs.image
        symbol = obs.deity?.let { repository.deity(it)?.symbols?.firstOrNull()?.image }
        draft = obs.status == Status.draft
        val day = nextObservanceDay(repository, settings.cityId, settings.today(), obsId)
        val names = repository.master()
        whenDate = day?.let { civilDate(LocalDate.parse(it.date), prose) }
        whenTithi = day?.let { tithiName(names, it, script, prose) }
        val body = obs.sections
        sections = listOfNotNull(
            body.special?.pick(prose)?.takeIf { it.isNotBlank() }?.let { R.string.section_special to it },
            body.familiesDo?.pick(prose)?.takeIf { it.isNotBlank() }?.let { R.string.section_families to it },
            body.history?.pick(prose)?.takeIf { it.isNotBlank() }?.let { R.string.section_history to it },
            body.regionalNotes?.pick(prose)?.takeIf { it.isNotBlank() }?.let { R.string.section_regional to it },
        )
        val pujaTitles = repository.pujaIndex().associate { it.id to it.title?.pick(prose).orEmpty() }
        val storyTitles = repository.storyIndex().associate { it.id to it.title?.pick(prose).orEmpty() }
        val foodIds = obs.links["naivedyam"].orEmpty()
        val foodTitles = foodIds.associateWith { id ->
            repository.naivedyamItem(id)?.regionalForms?.firstOrNull()?.name?.pick(prose).orEmpty()
        }
        actions = buildList {
            addAll(linked(obs.links["pujas"].orEmpty(), R.string.action_puja, pujaTitles) { ObsLink.Puja(it) })
            addAll(linked(obs.links["stories"].orEmpty(), R.string.action_story, storyTitles) { ObsLink.Story(it) })
            addAll(linked(foodIds, R.string.action_naivedyam, foodTitles) { ObsLink.Food(it) })
        }
    }

    var historyOpen by remember { mutableStateOf(false) }
    var regionalOpen by remember { mutableStateOf(false) }
    val openSections = sections.filter { it.first == R.string.section_special || it.first == R.string.section_families }
    val folded = sections.filter { it.first == R.string.section_history || it.first == R.string.section_regional }
    val tiles = actions.distinctBy { it.kind }
    Box(Modifier.fillMaxSize().background(Ivory)) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            ImageOrFallback(
                imageId = image,
                fallbackSymbolId = symbol,
                tint = TintCalendar,
                repository = repository,
                modifier = Modifier.fillMaxWidth().height(260.dp),
                corner = 0.dp,
                missingLabel = if (symbol == null && image == null) stringResource(R.string.art_coming) else null,
            )
            Column(
                Modifier
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Ivory)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (draft && BuildConfig.DEBUG) {
                    Text(stringResource(R.string.draft), color = InkSoft, style = MaterialTheme.typography.labelLarge)
                }
                Text(title, color = Ink, style = MaterialTheme.typography.displayLarge)
                if (whenDate != null) {
                    Text(
                        listOfNotNull(whenDate, whenTithi).joinToString(" · "),
                        color = Peacock,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (tiles.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tiles.forEach { action ->
                            val tint = when (action.kind) {
                                R.string.action_puja -> TintPuja
                                R.string.action_story -> TintStories
                                else -> TintCalendar
                            }
                            val glyph = when (action.kind) {
                                R.string.action_puja -> ModuleKind.Puja
                                R.string.action_story -> ModuleKind.Stories
                                else -> ModuleKind.Calendar
                            }
                            Column(
                                Modifier
                                    .weight(1f)
                                    .heightIn(min = 88.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(tint)
                                    .clickable {
                                        when (val link = action.link) {
                                            is ObsLink.Puja -> onPuja(link.id)
                                            is ObsLink.Story -> onStory(link.id)
                                            is ObsLink.Food -> onNaivedyam(link.id)
                                        }
                                    }
                                    .padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                ModuleGlyph(glyph, Modifier.size(28.dp))
                                Text(
                                    stringResource(action.kind).removeSuffix("›").trim(),
                                    color = Ink,
                                    style = MaterialTheme.typography.labelLarge,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                )
                            }
                        }
                    }
                }
                openSections.forEach { (heading, body) ->
                    Text(stringResource(heading), color = Ink, style = MaterialTheme.typography.headlineMedium)
                    Text(body, color = Ink, style = MaterialTheme.typography.bodyLarge)
                }
                folded.forEach { (heading, body) ->
                    val history = heading == R.string.section_history
                    val open = if (history) historyOpen else regionalOpen
                    Text(
                        stringResource(heading) + if (open) " −" else " +",
                        color = Ink,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable {
                            if (history) historyOpen = !historyOpen else regionalOpen = !regionalOpen
                        }.padding(vertical = 12.dp),
                    )
                    if (open) {
                        Text(body, color = Ink, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
        FloatingPills(onBack, onHome)
    }
}

@Composable
fun NaivedyamStubScreen(
    itemId: String,
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    var title by remember { mutableStateOf(itemId) }
    LaunchedEffect(itemId, settings.lang) {
        val item = repository.naivedyamItem(itemId)
        title = item?.regionalForms?.firstOrNull()?.name?.pick(settings.language())?.takeIf { it.isNotBlank() } ?: itemId
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(title, onBack, onHome)
    }
}

private fun linked(
    ids: List<String>,
    kind: Int,
    titles: Map<String, String>,
    link: (String) -> ObsLink,
): List<ObsAction> = ids.map { id ->
    val title = titles[id]?.takeIf { it.isNotBlank() }
    ObsAction(kind, if (ids.size == 1) null else title, link(id))
}

@Composable
private fun ScriptChips(selected: MantraScript, onSelect: (MantraScript) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            MantraScript.Telugu to R.string.view_telugu,
            MantraScript.Devanagari to R.string.view_devanagari,
            MantraScript.Iast to R.string.view_iast,
        ).forEach { (script, label) ->
            val on = script == selected
            Text(
                stringResource(label),
                color = if (on) Ivory else Ink,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (on) Maroon else Ivory)
                    .border(1.dp, if (on) Maroon else Gold, RoundedCornerShape(999.dp))
                    .clickable { onSelect(script) }
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
    }
}

@Composable
private fun SacredLine(
    text: String,
    script: MantraScript,
    size: Int = 18,
    align: TextAlign = TextAlign.Start,
    fill: Boolean = true,
) {
    if (text.isBlank()) return
    val family = when (script) {
        MantraScript.Telugu -> NotoSerifTeluguFamily
        MantraScript.Devanagari -> NotoSerifDevanagariFamily
        MantraScript.Iast -> CormorantFamily
    }
    Text(
        text,
        color = Ink,
        fontFamily = family,
        fontSize = size.sp,
        lineHeight = (size * 1.4f).sp,
        textAlign = align,
        modifier = if (fill) Modifier.fillMaxWidth() else Modifier,
    )
}

@Composable
private fun NameAndTill(name: String, till: String?, script: MantraScript, thenName: String? = null) {
    Column {
        SacredLine(name, script)
        if (!till.isNullOrBlank()) {
            val tillLine = stringResource(R.string.till_time, till)
            val line = if (thenName.isNullOrBlank()) tillLine else "$tillLine, ${stringResource(R.string.then_next, thenName)}"
            Text(line, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Same colour for every tone. The shape is the only difference. */
@Composable
private fun ToneIcon(tone: String?) {
    Canvas(Modifier.size(22.dp)) {
        val r = size.minDimension / 2f
        val c = center
        when (tone) {
            "steady" -> {
                drawLine(Peacock, Offset(c.x - r * 0.6f, c.y), Offset(c.x + r * 0.6f, c.y), strokeWidth = 2.dp.toPx())
            }
            "gentle" -> {
                drawCircle(Peacock, r * 0.35f, c, style = Stroke(1.5.dp.toPx()))
            }
            else -> {
                drawCircle(Peacock, r * 0.7f, c, style = Stroke(1.5.dp.toPx()))
            }
        }
    }
}

@Composable
private fun MoonGlyph(pct: Int, waxing: Boolean, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val r = size.minDimension / 2f
        val c = center
        val illum = pct.coerceIn(0, 100) / 100f
        val disk = Path().apply { addOval(Rect(c.x - r, c.y - r, c.x + r, c.y + r)) }
        drawPath(disk, Peacock.copy(alpha = 0.15f))
        if (illum > 0.02f) {
            val sign = if (waxing) 1f else -1f
            val rx = r * cos(Math.PI * illum).toFloat()
            val light = Path().apply {
                moveTo(c.x, c.y - r)
                arcTo(Rect(c.x - r, c.y - r, c.x + r, c.y + r), -90f, sign * 180f, false)
                val term = Rect(c.x - kotlin.math.abs(rx), c.y - r, c.x + kotlin.math.abs(rx), c.y + r)
                val termSweep = if (rx >= 0f) -sign * 180f else sign * 180f
                arcTo(term, -90f + sign * 180f, termSweep, false)
                close()
            }
            drawPath(light, Peacock)
        }
        drawCircle(Peacock, r, c, style = Stroke(1.dp.toPx()))
    }
}
