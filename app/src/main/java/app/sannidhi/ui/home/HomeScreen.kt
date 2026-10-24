package app.sannidhi.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sannidhi.BuildConfig
import app.sannidhi.R
import app.sannidhi.content.ComingUp
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Labels
import app.sannidhi.content.Status
import app.sannidhi.practice.PracticeRepository
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.calendar.GoodKind
import app.sannidhi.ui.calendar.daysTouching
import app.sannidhi.ui.calendar.goodPhrase
import app.sannidhi.ui.calendar.goodTimes
import app.sannidhi.ui.calendar.shownGood
import app.sannidhi.ui.common.ComingUpCard
import app.sannidhi.ui.common.ModuleCard
import app.sannidhi.ui.common.ModuleGlyph
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Hill1
import app.sannidhi.ui.theme.Hill2
import app.sannidhi.ui.theme.Hill3
import app.sannidhi.ui.theme.Hill4
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Leaf
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Surface
import app.sannidhi.ui.theme.SkyBottom
import app.sannidhi.ui.theme.SkyTop
import app.sannidhi.ui.theme.TintCalendar
import app.sannidhi.ui.theme.TintPuja
import app.sannidhi.ui.theme.TintSankalpam
import app.sannidhi.ui.theme.TintStories
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class HomeModel(
    val greeting: Int,
    val infoLine: String,
    val infoLineShort: String,
    val comingKicker: String,
    val comingTitle: String,
    val comingSubtitle: String,
    val comingAction: Int,
    val comingPujaId: String?,
    val comingDraft: Boolean,
    val comingImageId: String?,
    val comingSymbolId: String?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    store: AppSettingsStore,
    settings: AppSettings,
    repository: ContentRepository,
    practice: PracticeRepository,
    onProfile: () -> Unit,
    onLanguage: (String) -> Unit,
    onPuja: () -> Unit,
    onCalendar: () -> Unit,
    onSankalpam: () -> Unit,
    onStories: () -> Unit,
    onOpenPuja: (String) -> Unit,
) {
    val settings by store.settings.collectAsState(initial = settings)
    val active by practice.activeJapams().collectAsState(initial = emptyList())
    var sadhanaTitles by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    LaunchedEffect(settings.lang, active) {
        val lang = settings.language()
        sadhanaTitles = active.associate { row ->
            val title = repository.text(row.textId)?.title?.pick(lang)
                ?: repository.mantra(row.textId)?.text?.pick(settings.textViewEnum(), lang)
                ?: row.textId
            row.id to title
        }
    }
    val firstTitle = sadhanaTitles[active.firstOrNull()?.id].orEmpty()
    val sadhanaSub = when {
        active.isEmpty() -> null
        active.size == 1 -> firstTitle
        else -> stringResource(R.string.sadhana_and_more, firstTitle, active.size - 1)
    }
    var pickLang by remember { mutableStateOf(false) }
    val todayLabel = stringResource(R.string.today)
    var model by remember { mutableStateOf<HomeModel?>(null) }
    val inDaysPattern = stringResource(R.string.in_n_days)
    val earlyLabel = stringResource(R.string.early_sadhana)
    val beginLabel = stringResource(R.string.good_to_begin)
    val todayGoodLabel = stringResource(R.string.good_time_today)
    val lampLabel = stringResource(R.string.evening_lamp)
    val tillPattern = stringResource(R.string.till_time)
    val rangePattern = stringResource(R.string.time_range)
    LaunchedEffect(settings.cityId, settings.lang, settings.debugToday, settings.name) {
        model = loadHome(
            repository, settings, todayLabel, inDaysPattern,
            mapOf(
                GoodKind.Early to earlyLabel,
                GoodKind.Begin to beginLabel,
                GoodKind.Today to todayGoodLabel,
                GoodKind.Lamp to lampLabel,
            ),
            tillPattern,
            rangePattern,
        )
    }
    val scroll = rememberScrollState()
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val maxHeader = screenHeight * 0.25f
    val density = LocalDensity.current
    val collapsePx = remember { mutableFloatStateOf(0f) }
    val maxCollapsePx = with(density) { (maxHeader - 72.dp).toPx() }
    val collapseConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            if (available.y >= 0f) return Offset.Zero
            val room = maxCollapsePx - collapsePx.floatValue
            if (room <= 0f) return Offset.Zero
            val take = minOf(-available.y, room)
            collapsePx.floatValue += take
            return Offset(0f, -take)
        }

        override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
            if (available.y <= 0f) return Offset.Zero
            val room = collapsePx.floatValue
            if (room <= 0f) return Offset.Zero
            val give = minOf(available.y, room)
            collapsePx.floatValue -= give
            return Offset(0f, give)
        }
    }
    val collapsed = with(density) { collapsePx.floatValue.toDp() }
    val headerHeight = (maxHeader - collapsed).coerceAtLeast(72.dp)
    val showDetail = headerHeight > 120.dp
    val measurer = rememberTextMeasurer()
    val infoStyle = MaterialTheme.typography.bodyLarge
    val infoMaxWidth = with(density) {
        (LocalConfiguration.current.screenWidthDp.dp - 32.dp).roundToPx()
    }
    val info = model?.let { card ->
        val full = measurer.measure(
            AnnotatedString(card.infoLine),
            style = infoStyle,
            constraints = Constraints(maxWidth = infoMaxWidth),
        )
        if (full.lineCount <= 1) card.infoLine else card.infoLineShort
    }.orEmpty()

    Column(Modifier.fillMaxSize().background(Ivory).nestedScroll(collapseConnection)) {
        SkyHeader(
            height = headerHeight,
            showDetail = showDetail,
            name = settings.name.ifBlank { stringResource(R.string.your_name) },
            greeting = model?.greeting?.let { stringResource(it) }.orEmpty(),
            info = info,
            lang = settings.lang,
            onProfile = onProfile,
            onOpenLanguage = { pickLang = true },
        )
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val debugToday = settings.debugToday
            if (BuildConfig.DEBUG && !debugToday.isNullOrBlank()) {
                val locale = when (settings.lang) {
                    "te" -> Locale.forLanguageTag("te")
                    "hi" -> Locale.forLanguageTag("hi")
                    else -> Locale.ENGLISH
                }
                val shown = runCatching {
                    LocalDate.parse(debugToday).format(DateTimeFormatter.ofPattern("d MMM", locale))
                }.getOrDefault(debugToday)
                Text(
                    stringResource(R.string.debug_date_chip, shown),
                    color = Maroon,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(Surface)
                        .border(1.dp, Gold, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
            }
            val card = model
            if (card?.comingPujaId != null) {
                ComingUpCard(
                    kicker = card.comingKicker,
                    title = card.comingTitle,
                    subtitle = card.comingSubtitle,
                    action = stringResource(card.comingAction),
                    showDraft = card.comingDraft && BuildConfig.DEBUG,
                    onClick = { onOpenPuja(card.comingPujaId) },
                    imageId = card.comingImageId,
                    fallbackSymbolId = card.comingSymbolId,
                    repository = repository,
                )
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleCard(stringResource(R.string.module_puja_script), stringResource(R.string.module_puja_en), TintPuja, ModuleKind.Puja, onPuja, Modifier.weight(1f))
                ModuleCard(stringResource(R.string.module_calendar_script), stringResource(R.string.module_calendar_en), TintCalendar, ModuleKind.Calendar, onCalendar, Modifier.weight(1f))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ModuleCard(stringResource(R.string.module_sankalpam_script), stringResource(R.string.module_sankalpam_en), TintSankalpam, ModuleKind.Sankalpam, onSankalpam, Modifier.weight(1f))
                ModuleCard(stringResource(R.string.module_stories_script), stringResource(R.string.module_stories_en), TintStories, ModuleKind.Stories, onStories, Modifier.weight(1f))
            }
        }
        Column(Modifier.fillMaxWidth().background(Ivory)) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(Gold))
            Row(
                Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clickable(onClick = onSankalpam)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ModuleGlyph(ModuleKind.Sankalpam, Modifier.size(28.dp))
                if (sadhanaSub == null) {
                    Text(stringResource(R.string.begin_sankalpam), color = Ink, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                } else {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.my_sadhana), color = Ink, style = MaterialTheme.typography.labelLarge)
                        Text(sadhanaSub, color = InkSoft, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                    }
                    Text(stringResource(R.string.open_sadhana), color = Leaf, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
    if (pickLang) {
        ModalBottomSheet(onDismissRequest = { pickLang = false }, containerColor = Ivory) {
            listOf("te" to R.string.lang_te, "hi" to R.string.lang_hi, "en" to R.string.lang_en).forEach { (code, label) ->
                Text(
                    stringResource(label),
                    color = if (settings.lang == code) Ivory else Ink,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (settings.lang == code) Maroon else Surface)
                        .clickable {
                            pickLang = false
                            onLanguage(code)
                        }
                        .padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun SkyHeader(
    height: androidx.compose.ui.unit.Dp,
    showDetail: Boolean,
    name: String,
    greeting: String,
    info: String,
    lang: String,
    onProfile: () -> Unit,
    onOpenLanguage: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(height)
            .background(Brush.verticalGradient(listOf(SkyTop, SkyBottom))),
    ) {
        Column(Modifier.fillMaxSize().statusBarsPadding().padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.app_name), color = Maroon, style = MaterialTheme.typography.headlineMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = (when (lang) {
                            "te" -> stringResource(R.string.lang_te)
                            "hi" -> stringResource(R.string.lang_hi)
                            else -> stringResource(R.string.lang_en)
                        }) + " ▾",
                        color = Maroon,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Ivory.copy(alpha = 0.72f))
                            .border(1.dp, Gold, RoundedCornerShape(999.dp))
                            .clickable(onClick = onOpenLanguage)
                            .padding(horizontal = 14.dp),
                    )
                    Text(
                        text = name,
                        color = Ink,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .heightIn(min = 56.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(Ivory.copy(alpha = 0.72f))
                            .border(1.dp, Gold, RoundedCornerShape(999.dp))
                            .clickable(onClick = onProfile)
                            .padding(horizontal = 14.dp),
                    )
                }
            }
            if (showDetail) {
                Text(greeting, color = Ink, style = MaterialTheme.typography.displayLarge)
                Text(info, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                Canvas(Modifier.fillMaxWidth().weight(1f)) {
                    val hills = listOf(Hill1, Hill2, Hill3, Hill4)
                    hills.forEachIndexed { index, color ->
                        val rise = size.height * (0.35f + index * 0.12f)
                        val path = Path().apply {
                            moveTo(0f, size.height)
                            lineTo(0f, rise)
                            quadraticTo(size.width * 0.35f, rise - 28f - index * 8f, size.width * 0.7f, rise)
                            quadraticTo(size.width * 0.88f, rise + 16f, size.width, rise - 8f)
                            lineTo(size.width, size.height)
                            close()
                        }
                        drawPath(path, color)
                    }
                }
            }
        }
    }
}

private suspend fun loadHome(
    repository: ContentRepository,
    settings: AppSettings,
    todayWord: String,
    inDaysPattern: String,
    goodLabels: Map<GoodKind, String>,
    tillPattern: String,
    rangePattern: String,
): HomeModel? {
    if (settings.cityId.isBlank()) return null
    val today = settings.today()
    val master = repository.master()
    val day = repository.day(settings.cityId, today) ?: return null
    val lang = settings.language()
    val view = settings.infoView()
    val city = master.cities.firstOrNull { it.id == settings.cityId }?.name?.pick(lang).orEmpty()
    val locale = when (settings.lang) {
        "te" -> Locale.forLanguageTag("te")
        "hi" -> Locale.forLanguageTag("hi")
        else -> Locale.ENGLISH
    }
    val date = today.format(DateTimeFormatter.ofPattern("EEE d MMM", locale))
    val tithi = Labels.tithi(master, day, view, lang)
    val masa = Labels.masa(master, day, view, lang).replaceFirstChar { it.titlecase(locale) }
    val hour = LocalTime.now().hour
    val greeting = when {
        hour in 4..11 -> R.string.good_morning
        hour in 12..16 -> R.string.good_afternoon
        hour in 17..20 -> R.string.good_evening
        else -> R.string.good_night
    }
    val coming = repository.comingUp(settings.cityId, today)
    val goodLine = if (coming?.daysAway == 0) {
        val now = LocalDateTime.of(today, LocalTime.now())
        val window = shownGood(goodTimes(daysTouching(repository, settings.cityId, today), today), now)
        window?.let { goodPhrase(it, now, lang, goodLabels.getValue(it.kind), tillPattern, rangePattern, openAsTill = true) }
    } else {
        null
    }
    val masaLine = coming?.let { "${Labels.masa(master, it.day, view, lang).replaceFirstChar { c -> c.titlecase(locale) }} · ${Labels.tithi(master, it.day, view, lang)}" }.orEmpty()
    return HomeModel(
        greeting = greeting,
        infoLine = "$date · $tithi · $masa · $city",
        infoLineShort = "$date · $tithi · $city",
        comingKicker = comingKicker(coming, todayWord, inDaysPattern, locale),
        comingTitle = coming?.observance?.title?.pick(lang).orEmpty(),
        comingSubtitle = listOf(masaLine, goodLine).filter { !it.isNullOrBlank() }.joinToString("\n"),
        comingAction = if (coming?.daysAway == 0) R.string.start_puja else R.string.prepare_puja,
        comingPujaId = coming?.observance?.links?.get("pujas")?.firstOrNull(),
        comingDraft = coming?.observance?.status == Status.draft,
        comingImageId = comingImage(repository, coming),
        comingSymbolId = coming?.observance?.deity?.let { repository.deity(it) }?.symbols?.firstOrNull()?.image,
    )
}

private suspend fun comingImage(repository: ContentRepository, coming: ComingUp?): String? {
    val observance = coming?.observance ?: return null
    if (repository.imagePath(observance.image) != null) return observance.image
    val deity = observance.deity?.let { repository.deity(it) } ?: return null
    return deity.images.card
}

private fun comingKicker(coming: ComingUp?, todayWord: String, inDaysPattern: String, locale: Locale): String {
    if (coming == null) return ""
    if (coming.daysAway == 0) return todayWord
    val whenDate = LocalDate.parse(coming.day.date).format(DateTimeFormatter.ofPattern("EEE d MMM", locale))
    return "${java.lang.String.format(locale, inDaysPattern, coming.daysAway)} · $whenDate"
}
