@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.smaranam.app.ui.puja

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smaranam.app.BuildConfig
import com.smaranam.app.R
import com.smaranam.app.content.CalendarDay
import com.smaranam.app.content.ContentRepository
import com.smaranam.app.content.Deity
import com.smaranam.app.content.DeitySymbol
import com.smaranam.app.content.Mantra
import com.smaranam.app.content.Master
import com.smaranam.app.content.Naivedyam
import com.smaranam.app.content.Puja
import com.smaranam.app.content.PujaStep
import com.smaranam.app.content.PujaSummary
import com.smaranam.app.content.SamagriItem
import com.smaranam.app.content.Status
import com.smaranam.app.settings.AppSettings
import com.smaranam.app.settings.AppSettingsStore
import com.smaranam.app.ui.common.ComingUpCard
import com.smaranam.app.ui.common.FloatingPills
import com.smaranam.app.ui.common.ImageOrFallback
import com.smaranam.app.ui.common.TopBar
import com.smaranam.app.ui.theme.Gold
import com.smaranam.app.ui.theme.Ink
import com.smaranam.app.ui.theme.InkSoft
import com.smaranam.app.ui.calendar.daysTouching
import com.smaranam.app.ui.calendar.goodPhrase
import com.smaranam.app.ui.calendar.goodTimes
import com.smaranam.app.ui.calendar.shownGood
import com.smaranam.app.ui.theme.Ivory
import com.smaranam.app.ui.theme.Maroon
import com.smaranam.app.ui.theme.Peacock
import com.smaranam.app.ui.theme.TintPuja
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class PujaBundle(
    val puja: Puja,
    val deity: Deity?,
    val master: Master,
    val today: CalendarDay?,
    val samvatsaraId: String?,
    val next: CalendarDay?,
    val mantras: Map<String, Mantra>,
    val samagri: Map<String, SamagriItem>,
    val naivedyam: Map<String, Naivedyam>,
    val textTitles: Map<String, String>,
)

@Composable
fun PujaHomeScreen(
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onDeity: (String) -> Unit,
    onOpenPuja: (String) -> Unit,
) {
    var deities by remember { mutableStateOf<List<Deity>>(emptyList()) }
    var pujas by remember { mutableStateOf<List<PujaSummary>>(emptyList()) }
    var coming by remember { mutableStateOf<com.smaranam.app.content.ComingUp?>(null) }
    var comingImage by remember { mutableStateOf<String?>(null) }
    var comingSymbol by remember { mutableStateOf<String?>(null) }
    var master by remember { mutableStateOf<Master?>(null) }
    var query by remember { mutableStateOf("") }
    val lang = settings.language()
    LaunchedEffect(settings.cityId, settings.lang, settings.debugToday, settings.stateCode) {
        master = repository.master()
        deities = repository.deities().sortedBy { it.order }
        pujas = repository.pujaIndex().filter { summary ->
            "all" in summary.regions || settings.stateCode in summary.regions
        }
        val today = settings.today()
        val next = repository.comingUp(settings.cityId, today)
        coming = next
        val deity = next?.observance?.deity?.let { repository.deity(it) }
        comingImage = when {
            repository.imagePath(next?.observance?.image) != null -> next?.observance?.image
            else -> deity?.images?.card
        }
        comingSymbol = deity?.symbols?.firstOrNull()?.image
    }
    val shownGods = deities.filter { matchesQuery(query, searchParts(it)) }
    val shownPujas = if (query.isBlank()) emptyList() else pujas.filter { matchesQuery(query, searchParts(it)) }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.module_puja), onBack, onHome)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(2) }) {
                val card = coming
                val data = master
                if (card != null && data != null) {
                    val pujaId = card.observance.links["pujas"]?.firstOrNull()
                    if (pujaId != null) {
                        ComingUpCard(
                            kicker = comingKicker(card, settings),
                            title = card.observance.title?.pick(lang).orEmpty(),
                            subtitle = formatDayLine(data, card.day, settings),
                            action = stringResource(if (card.daysAway == 0) R.string.start_puja else R.string.prepare_puja),
                            showDraft = card.observance.status == Status.draft && BuildConfig.DEBUG,
                            onClick = { onOpenPuja(pujaId) },
                            imageId = comingImage,
                            fallbackSymbolId = comingSymbol,
                            repository = repository,
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                SearchField(query) { query = it }
            }
            items(shownPujas, key = { it.id }, span = { GridItemSpan(maxLineSpan) }) { puja ->
                HandOff(puja.title?.pick(lang) ?: puja.id) { onOpenPuja(puja.id) }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.choose_a_god), color = Ink, style = MaterialTheme.typography.headlineMedium)
            }
            items(shownGods, key = { it.id }) { deity ->
                val count = pujas.count { it.deity == deity.id }
                val english = deityEnglishName(deity)
                val meta = if (count == 0) {
                    stringResource(R.string.coming_later)
                } else {
                    stringResource(if (count == 1) R.string.god_puja_one else R.string.god_pujas, english, count)
                }
                GodCard(deity, settings, repository, meta = meta, enabled = count > 0) {
                    if (count > 0) onDeity(deity.id)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GodPageScreen(
    deityId: String,
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPuja: (String) -> Unit,
    onStories: () -> Unit,
) {
    var deity by remember { mutableStateOf<Deity?>(null) }
    var pujas by remember { mutableStateOf<List<PujaSummary>>(emptyList()) }
    var nextByPuja by remember { mutableStateOf<Map<String, CalendarDay>>(emptyMap()) }
    var master by remember { mutableStateOf<Master?>(null) }
    var symbol by remember { mutableStateOf<DeitySymbol?>(null) }
    val lang = settings.language()
    LaunchedEffect(deityId, settings.cityId, settings.stateCode, settings.debugToday, settings.lang) {
        val loaded = repository.deity(deityId)
        deity = loaded
        master = repository.master()
        val list = repository.pujasFor(deityId, settings.stateCode)
        pujas = list
        val days = daysOnOrAfter(repository, settings.cityId, settings.today())
        nextByPuja = list.mapNotNull { puja ->
            nextObservance(days, puja.observance)?.let { puja.id to it }
        }.toMap()
    }
    val god = deity
    val title = god?.let { deityScriptName(it, lang) } ?: deityId
    val data = master
    Box(Modifier.fillMaxSize().background(Ivory)) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            val symbolId = god?.symbols?.firstOrNull()?.image
            ImageOrFallback(
                imageId = god?.images?.hero,
                fallbackSymbolId = symbolId,
                tint = TintPuja,
                repository = repository,
                modifier = Modifier.fillMaxWidth().height(380.dp),
                corner = 0.dp,
                missingLabel = if (symbolId == null) stringResource(R.string.art_coming) else null,
            )
            Column(
                Modifier
                    .offset(y = (-24).dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Ivory)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (god != null) {
                    val english = deityEnglishName(god)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                        Text(title, color = Ink, style = MaterialTheme.typography.headlineMedium)
                        if (english.isNotBlank() && english != title) {
                            Text(english, color = InkSoft, style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                }
                if (god?.intro != null) {
                    IntroBlock(god.intro.pick(lang))
                }
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    god?.symbols?.take(6)?.forEach { item ->
                        ImageOrFallback(
                            imageId = item.image,
                            fallbackSymbolId = item.image,
                            tint = Ivory,
                            repository = repository,
                            modifier = Modifier.size(56.dp).clickable { symbol = item },
                            corner = 28.dp,
                            containSymbol = true,
                            symbolFraction = 0.72f,
                        )
                    }
                }
                Text(stringResource(R.string.pujas_heading), color = Maroon, style = MaterialTheme.typography.headlineMedium)
                pujas.forEach { puja ->
                    val next = nextByPuja[puja.id]
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(TintPuja)
                            .clickable { onPuja(puja.id) }
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(puja.title?.pick(lang) ?: puja.id, color = Ink, style = MaterialTheme.typography.headlineMedium)
                        Text(puja.kind, color = Maroon, style = MaterialTheme.typography.labelLarge)
                        if (next != null && data != null) {
                            Text(formatDayLine(data, next, settings), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                HandOff(stringResource(R.string.stories_of, title), onStories)
            }
        }
        FloatingPills(onBack, onHome)
    }
    val picked = symbol
    if (picked != null) {
        ModalBottomSheet(onDismissRequest = { symbol = null }, containerColor = Ivory) {
            Text(
                text = picked.meaning?.pick(lang).orEmpty(),
                color = Ink,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(24.dp).heightIn(min = 56.dp),
            )
        }
    }
}

@Composable
fun PujaPageScreen(
    pujaId: String,
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onGuided: () -> Unit,
    onProfile: () -> Unit,
) {
    var bundle by remember { mutableStateOf<PujaBundle?>(null) }
    var openMeanings by remember { mutableStateOf(setOf<String>()) }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val date = settings.today().toString()
    val ticksFlow = remember(pujaId, date) { store.samagriTicks(pujaId, date) }
    val ticks by ticksFlow.collectAsState(initial = emptySet())
    val script = mantraScript(settings)
    LaunchedEffect(pujaId, settings.cityId, settings.lang, settings.debugToday, settings.stateCode) {
        bundle = loadPuja(repository, pujaId, settings)
    }
    val loaded = bundle
    val lang = settings.language()
    val title = loaded?.puja?.title?.pick(lang) ?: pujaId
    val anchors = remember(loaded?.puja?.id, loaded?.puja?.stages?.size) {
        val stages = loaded?.puja?.stages.orEmpty()
        var index = 2
        stages.associate { stage ->
            val at = index
            index += 1 + stage.steps.size
            stage.stage to at
        }
    }
    val selected by remember(anchors) {
        derivedStateOf {
            val visible = listState.layoutInfo.visibleItemsInfo.map { it.index }.filter { it >= 2 }
            if (visible.isEmpty()) anchors.keys.firstOrNull() ?: "pre"
            else {
                val probe = if (!listState.canScrollForward) visible.max() else visible.min()
                anchors.entries.lastOrNull { it.value <= probe }?.key ?: anchors.keys.firstOrNull() ?: "pre"
            }
        }
    }
    val pujaTimeLabel = stringResource(R.string.good_time_for_puja)
    val tillPattern = stringResource(R.string.till_time)
    val rangePattern = stringResource(R.string.time_range)
    var goodLine by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(loaded?.next?.date, settings.cityId, settings.lang, settings.debugToday) {
        val nextDay = loaded?.next
        val today = settings.today()
        if (nextDay == null || nextDay.date != today.toString() || settings.cityId.isBlank()) {
            goodLine = null
            return@LaunchedEffect
        }
        val now = LocalDateTime.of(today, LocalTime.now())
        val window = shownGood(goodTimes(daysTouching(repository, settings.cityId, today), today), now)
        goodLine = window?.let {
            goodPhrase(it, now, settings.language(), pujaTimeLabel, tillPattern, rangePattern, openAsTill = true)
        }
    }
    val firstMantraStep = loaded?.puja?.stages?.asSequence()?.flatMap { it.steps }?.firstOrNull { it.mantras.isNotEmpty() }?.id
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(title, onBack, onHome)
        if (loaded == null) return@Column
        LazyColumn(state = listState, modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)) {
            item(key = "header") {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (loaded.puja.status == Status.draft && BuildConfig.DEBUG) {
                        Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
                    }
                    loaded.next?.let { day ->
                        Text(formatDayLine(loaded.master, day, settings), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                    goodLine?.let { line ->
                        Text(line, color = Peacock, style = MaterialTheme.typography.bodyLarge)
                    }
                    loaded.puja.minutes?.let { minutes ->
                        Text(stringResource(R.string.about_minutes, minutes), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                    OutlinedButton(
                        onClick = onGuided,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        border = BorderStroke(1.dp, Maroon),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Maroon),
                    ) {
                        Text(stringResource(R.string.guided_mode), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            stickyHeader(key = "stages") {
                StageBar(selected) { stage ->
                    anchors[stage]?.let { index -> scope.launch { listState.animateScrollToItem(index) } }
                }
            }
            loaded.puja.stages.forEach { stage ->
                item(key = "anchor-${stage.stage}") { Spacer(Modifier.height(1.dp)) }
                stage.steps.forEachIndexed { index, step ->
                    item(key = step.id) {
                        StepCard(
                            step = step,
                            number = index + 1,
                            bundle = loaded,
                            settings = settings,
                            script = script,
                            showChips = step.id == firstMantraStep,
                            ticks = ticks,
                            meaningsOpen = openMeanings,
                            repository = repository,
                            onScript = { chosen -> scope.launch { store.setTextView(mantraStorage(chosen)) } },
                            onTick = { itemId, ticked ->
                                scope.launch { store.setSamagriTick(pujaId, date, itemId, ticked) }
                            },
                            onMeaning = { id ->
                                openMeanings = if (id in openMeanings) openMeanings - id else openMeanings + id
                            },
                            onProfile = onProfile,
                            guided = false,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuidedPujaScreen(
    pujaId: String,
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    onClose: () -> Unit,
) {
    var bundle by remember { mutableStateOf<PujaBundle?>(null) }
    val view = LocalView.current
    androidx.compose.runtime.DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }
    BackHandler(onBack = onClose)
    LaunchedEffect(pujaId, settings.cityId, settings.debugToday, settings.lang) {
        bundle = loadPuja(repository, pujaId, settings)
    }
    val loaded = bundle ?: return
    val pages = loaded.puja.stages.flatMap { stage -> stage.steps.map { stage.stage to it } }
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val script = mantraScript(settings)
    val date = settings.today().toString()
    val ticks by remember(pujaId, date) { store.samagriTicks(pujaId, date) }.collectAsState(initial = emptySet())
    Column(Modifier.fillMaxSize().background(Ivory).statusBarsPadding()) {
        val stage = pages.getOrNull(pager.currentPage)?.first ?: ""
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.guided_counter, stageLabel(stage).uppercase(), pager.currentPage + 1, pages.size),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
            )
            Text(
                text = "✕",
                color = Maroon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .clickable(onClick = onClose)
                    .padding(horizontal = 16.dp),
            )
        }
        val progress = if (pages.isEmpty()) 0f else (pager.currentPage + 1f) / pages.size
        Box(Modifier.fillMaxWidth().height(2.dp).background(Gold.copy(alpha = 0.3f))) {
            Box(Modifier.fillMaxWidth(progress).height(2.dp).background(Gold))
        }
        HorizontalPager(state = pager, modifier = Modifier.weight(1f).fillMaxWidth()) { page ->
            val (_, step) = pages[page]
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                StepCard(
                    step = step,
                    number = page + 1,
                    bundle = loaded,
                    settings = settings,
                    script = script,
                    showChips = false,
                    ticks = ticks,
                    meaningsOpen = emptySet(),
                    repository = repository,
                    onScript = {},
                    onTick = { itemId, ticked -> scope.launch { store.setSamagriTick(pujaId, date, itemId, ticked) } },
                    onMeaning = {},
                    onProfile = {},
                    guided = true,
                )
            }
        }
        Text(
            stringResource(R.string.screen_stays_on),
            color = InkSoft,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        )
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                stringResource(R.string.guided_previous),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .heightIn(min = 56.dp)
                    .clickable { if (pager.currentPage > 0) scope.launch { pager.animateScrollToPage(pager.currentPage - 1) } }
                    .padding(horizontal = 8.dp),
            )
            Text(
                stringResource(R.string.guided_step, pager.currentPage + 1, pages.size),
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
                    .clickable {
                        if (pager.currentPage < pages.lastIndex) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                    }
                    .padding(horizontal = 8.dp),
            )
        }
    }
}

@Composable
private fun StageBar(selected: String, onSelect: (String) -> Unit) {
    Row(Modifier.fillMaxWidth().background(Ivory).padding(horizontal = 8.dp)) {
        listOf("pre", "during", "post").forEach { stage ->
            Column(
                Modifier.weight(1f).clickable { onSelect(stage) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    stageLabel(stage),
                    color = if (selected == stage) Maroon else InkSoft,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.heightIn(min = 48.dp).padding(top = 12.dp),
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (selected == stage) Gold else Ivory),
                )
            }
        }
    }
}

@Composable
private fun stageLabel(stage: String): String = when (stage) {
    "pre" -> stringResource(R.string.stage_pre)
    "during" -> stringResource(R.string.stage_during)
    "post" -> stringResource(R.string.stage_post)
    else -> stage
}

@Composable
private fun StepCard(
    step: PujaStep,
    number: Int,
    bundle: PujaBundle,
    settings: AppSettings,
    script: MantraScript,
    showChips: Boolean,
    ticks: Set<String>,
    meaningsOpen: Set<String>,
    repository: ContentRepository,
    onScript: (MantraScript) -> Unit,
    onTick: (String, Boolean) -> Unit,
    onMeaning: (String) -> Unit,
    onProfile: () -> Unit,
    guided: Boolean,
) {
    val lang = settings.language()
    val bodySize = if (guided) 22.sp else MaterialTheme.typography.bodyLarge.fontSize
    val mantraSize = if (guided) 30.sp else 24.sp
    val symbol = bundle.deity?.symbols?.firstOrNull()?.image
    Column(
        Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintPuja)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                Modifier.size(48.dp).border(1.dp, Gold, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(number.toString(), color = Maroon, style = MaterialTheme.typography.headlineMedium)
            }
            Text(step.titleIast, color = Ink, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            step.repeat?.let { Chip(stringResource(R.string.repeat_chip, it)) }
            if (step.optional) Chip(stringResource(R.string.optional_chip))
        }
        if (step.image != null) {
            ImageOrFallback(
                imageId = step.image,
                fallbackSymbolId = symbol,
                tint = Ivory,
                repository = repository,
                modifier = Modifier.fillMaxWidth().height(180.dp),
            )
        }
        step.body?.pick(lang)?.takeIf { it.isNotBlank() }?.let { body ->
            Text(body, color = Ink, fontSize = bodySize, lineHeight = bodySize * 1.4f, style = MaterialTheme.typography.bodyLarge)
        }
        step.note?.pick(lang)?.takeIf { it.isNotBlank() }?.let { note ->
            Text(note, color = InkSoft, fontSize = 16.sp, fontStyle = FontStyle.Italic, lineHeight = 22.sp)
        }
        step.text?.let { textId ->
            Text(bundle.textTitles[textId] ?: textId, color = Ink, style = MaterialTheme.typography.headlineMedium)
        }
        if (step.samagri.isNotEmpty()) {
            val ready = step.samagri.count { it.item in ticks }
            Text(
                stringResource(R.string.samagri_ready, ready, step.samagri.size),
                color = InkSoft,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End,
            )
            step.samagri.forEach { row ->
                val item = bundle.samagri[row.item]
                val name = item?.name?.pick(lang) ?: row.item
                val instead = row.alternatives.mapNotNull { id -> bundle.samagri[id]?.name?.pick(lang) }
                val ticked = row.item in ticks
                Column(
                    Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Ivory)
                        .clickable { onTick(row.item, !ticked) }
                        .padding(12.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(
                            Modifier
                                .size(28.dp)
                                .border(1.dp, Gold, CircleShape)
                                .background(if (ticked) Maroon else Ivory, CircleShape),
                        )
                        Text("$name · ${row.qty}", color = Ink, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                    }
                    if (instead.isNotEmpty()) {
                        Text(stringResource(R.string.instead_of, instead.joinToString(", ")), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            Text(stringResource(R.string.offer_enough), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
        if (step.naivedyam.isNotEmpty()) {
            step.naivedyam.mapNotNull { bundle.naivedyam[it] }.forEach { offering ->
                NaivedyamCard(offering, settings, symbol, repository)
            }
        }
        val mantraIds = step.mantras
        if (showChips && mantraIds.isNotEmpty()) {
            ScriptChips(script, onScript)
        }
        mantraIds.forEach { id ->
            val mantra = bundle.mantras[id] ?: return@forEach
            val values = placeholderValues(bundle.master, bundle.today, bundle.samvatsaraId, settings, script)
            val lines = if (mantra.lines.isNotEmpty()) mantra.lines else listOfNotNull(mantra.text)
            lines.forEach { sacred ->
                Text(
                    text = displayMantra(sacred, script, lang, values),
                    color = Ink,
                    fontSize = mantraSize,
                    lineHeight = mantraSize * 1.45f,
                    fontFamily = when (script) {
                        MantraScript.Iast -> com.smaranam.app.ui.theme.CormorantFamily
                        MantraScript.Telugu -> com.smaranam.app.ui.theme.NotoSerifTeluguFamily
                        MantraScript.Devanagari -> com.smaranam.app.ui.theme.NotoSerifDevanagariFamily
                    },
                )
            }
            val meaning = mantra.meaning?.pick(lang).orEmpty()
            if (meaning.isNotBlank() && !guided) {
                Text(
                    stringResource(R.string.meaning_expand),
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.heightIn(min = 56.dp).clickable { onMeaning(id) }.padding(vertical = 12.dp),
                )
                if (id in meaningsOpen) {
                    Text(meaning, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        if (mantraIds.size > 1) {
            Text(stringResource(R.string.more_mantras_below, mantraIds.size - 1), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
        val needsDetails = step.fills.isNotEmpty() || mantraIds.any { id ->
            val mantra = bundle.mantras[id]
            mantra?.text?.sa?.contains('{') == true || mantra?.text?.iast?.contains('{') == true
        }
        if (needsDetails && !guided) {
            Text(
                stringResource(R.string.edit_my_details),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onProfile).padding(vertical = 12.dp),
            )
        }
    }
}

@Composable
private fun NaivedyamCard(
    offering: Naivedyam,
    settings: AppSettings,
    symbol: String?,
    repository: ContentRepository,
) {
    val lang = settings.language()
    val forms = offering.regionalForms.sortedBy { form -> if (settings.stateCode in form.region) 0 else 1 }
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Ivory).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ImageOrFallback(
            imageId = offering.image,
            fallbackSymbolId = symbol,
            tint = TintPuja,
            repository = repository,
            modifier = Modifier.fillMaxWidth().height(120.dp),
        )
        offering.why?.pick(lang)?.takeIf { it.isNotBlank() }?.let {
            Text(it, color = Ink, style = MaterialTheme.typography.bodyLarge)
        }
        forms.forEach { form ->
            val name = form.name?.pick(lang).orEmpty()
            val region = form.region.joinToString(", ")
            if (name.isNotBlank()) Chip(if (region.isBlank()) name else "$name · $region")
        }
    }
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
private fun Chip(label: String) {
    Text(
        label,
        color = Ink,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .border(1.dp, Gold, RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    )
}

@Composable
private fun IntroBlock(text: String) {
    var open by remember(text) { mutableStateOf(false) }
    val limit = 160
    val long = text.length > limit
    val shown = if (!long || open) text else text.take(limit).substringBeforeLast(' ', text.take(limit)).trimEnd() + "…"
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(shown, color = Ink, style = MaterialTheme.typography.bodyLarge)
        if (long) {
            Text(
                stringResource(R.string.more_actions),
                color = Peacock,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.heightIn(min = 48.dp).clickable { open = !open }.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun HandOff(label: String, onClick: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, color = Peacock, style = MaterialTheme.typography.bodyLarge)
        Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(1.dp).background(Gold))
    }
}

@Composable
fun GodCard(
    deity: Deity,
    settings: AppSettings,
    repository: ContentRepository,
    meta: String? = null,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val lang = settings.language()
    val symbolId = deity.symbols.firstOrNull()?.image
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintPuja)
            .clickable(enabled = enabled, onClick = onClick),
    ) {
        ImageOrFallback(
            imageId = deity.images.card,
            fallbackSymbolId = symbolId,
            tint = Ivory,
            repository = repository,
            modifier = Modifier.fillMaxWidth().height(140.dp),
            corner = 0.dp,
            missingLabel = if (symbolId == null) stringResource(R.string.art_coming) else null,
        )
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(deityScriptName(deity, lang), color = Ink, style = MaterialTheme.typography.headlineMedium)
            Text(meta ?: deityEnglishName(deity), color = InkSoft, style = MaterialTheme.typography.labelLarge)
            if (deity.status == Status.draft && BuildConfig.DEBUG) {
                Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun SearchField(value: String, onValue: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = TextStyle(color = Ink, fontSize = 18.sp),
        cursorBrush = SolidColor(Maroon),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TintPuja)
            .padding(16.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = InkSoft, modifier = Modifier.size(22.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(stringResource(R.string.search_god_or_festival), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
private fun comingKicker(coming: com.smaranam.app.content.ComingUp, settings: AppSettings): String {
    val todayWord = stringResource(R.string.today)
    if (coming.daysAway == 0) return todayWord
    val locale = when (settings.lang) {
        "te" -> Locale.forLanguageTag("te")
        "hi" -> Locale.forLanguageTag("hi")
        else -> Locale.ENGLISH
    }
    val whenDate = LocalDate.parse(coming.day.date).format(DateTimeFormatter.ofPattern("EEE d MMM", locale))
    return "${stringResource(R.string.in_n_days, coming.daysAway)} · $whenDate"
}

private suspend fun loadPuja(repository: ContentRepository, pujaId: String, settings: AppSettings): PujaBundle? {
    val puja = repository.puja(pujaId) ?: return null
    val master = repository.master()
    val todayDate = settings.today()
    val year = repository.calendarYear(settings.cityId, todayDate)
    val today = year?.days?.firstOrNull { it.date == todayDate.toString() }
    val days = daysOnOrAfter(repository, settings.cityId, todayDate)
    val mantraIds = puja.stages.flatMap { stage -> stage.steps.flatMap { it.mantras } }.distinct()
    val mantras = mantraIds.mapNotNull { id -> repository.mantra(id)?.let { id to it } }.toMap()
    val samagriIds = puja.stages.flatMap { stage ->
        stage.steps.flatMap { step -> step.samagri.flatMap { listOf(it.item) + it.alternatives } }
    }.distinct()
    val samagri = samagriIds.mapNotNull { id -> repository.samagriItem(id)?.let { id to it } }.toMap()
    val nvIds = puja.stages.flatMap { stage -> stage.steps.flatMap { it.naivedyam } }.distinct()
    val naivedyam = nvIds.mapNotNull { id -> repository.naivedyamItem(id)?.let { id to it } }.toMap()
    val textIds = puja.stages.flatMap { stage -> stage.steps.mapNotNull { it.text } }.distinct()
    val lang = settings.language()
    val textTitles = textIds.associateWith { id -> repository.text(id)?.title?.pick(lang).orEmpty().ifBlank { id } }
    return PujaBundle(
        puja = puja,
        deity = repository.deity(puja.deity),
        master = master,
        today = today,
        samvatsaraId = year?.samvatsara?.id,
        next = nextObservance(days, puja.observance),
        mantras = mantras,
        samagri = samagri,
        naivedyam = naivedyam,
        textTitles = textTitles,
    )
}
