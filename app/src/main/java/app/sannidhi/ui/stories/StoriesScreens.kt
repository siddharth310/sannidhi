package app.sannidhi.ui.stories

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.sannidhi.BuildConfig
import app.sannidhi.R
import app.sannidhi.content.Beat
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Deity
import app.sannidhi.content.Lang
import app.sannidhi.content.Status
import app.sannidhi.content.Story
import app.sannidhi.content.StorySummary
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.common.ImageOrFallback
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.puja.GodCard
import app.sannidhi.ui.puja.deityScriptName
import app.sannidhi.ui.puja.matchesQuery
import app.sannidhi.ui.puja.searchParts
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Peacock
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.InterFamily
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.NotoSerifDevanagariFamily
import app.sannidhi.ui.theme.NotoSerifTeluguFamily
import app.sannidhi.ui.theme.TintStories
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun StoriesHomeScreen(
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onDeity: (String) -> Unit,
    onKatha: (List<String>, Int, Int) -> Unit,
) {
    var deities by remember { mutableStateOf<List<Deity>>(emptyList()) }
    var stories by remember { mutableStateOf<List<StorySummary>>(emptyList()) }
    var katha by remember { mutableStateOf<List<StorySummary>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    val place by store.kathaPlace().collectAsState(initial = null)
    val lang = settings.language()
    LaunchedEffect(settings.cityId, settings.lang, settings.debugToday) {
        val all = repository.deities().sortedBy { it.order }
        val index = repository.storyIndex().filter { it.status != Status.retired }
        stories = index
        deities = all.filter { deity -> index.any { it.deity == deity.id } }
        val day = repository.day(settings.cityId, settings.today())
        val todayIds = day?.observances.orEmpty()
        val linkedPuja = todayIds.firstNotNullOfOrNull { obsId ->
            val links = repository.observance(obsId)?.links?.get("pujas").orEmpty()
            links.firstOrNull { pujaId -> index.any { it.pujaKatha == pujaId } }
        }
        val deityIds = todayIds.mapNotNull { repository.observance(it)?.deity }.toSet()
        katha = when {
            linkedPuja != null -> index.filter { it.pujaKatha == linkedPuja }.sortedBy { it.order }
            else -> index.filter { it.pujaKatha != null && it.deity in deityIds }.sortedBy { it.order }
        }
    }
    val gods = deities.filter { deity ->
        query.isBlank() || matchesQuery(query, searchParts(deity)) ||
            stories.any { it.deity == deity.id && matchesQuery(query, storyParts(it)) }
    }
    val showSearch = deities.size > 8 || (BuildConfig.DEBUG && settings.debugStorySearch)
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.module_stories), onBack, onHome)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (showSearch) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SearchBox(query) { query = it }
                }
            }
            if (query.isBlank() && katha.size >= 2) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(stringResource(R.string.for_today, stringResource(R.string.chavithi_katha)), color = Maroon, style = MaterialTheme.typography.labelLarge)
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ChavithiKathaCard(
                        coverId = katha.first().cover,
                        symbolId = deities.firstOrNull { it.id == katha.first().deity }?.symbols?.firstOrNull()?.image,
                        repository = repository,
                        place = place,
                        heading = stringResource(R.string.chavithi_katha),
                        onOpen = { part, beat -> onKatha(katha.map { it.id }, part, beat) },
                    )
                }
            }
            items(gods, key = { it.id }) { deity ->
                val count = stories.count { it.deity == deity.id }
                val name = deityScriptName(deity, lang)
                val meta = stringResource(if (count == 1) R.string.stories_count_one else R.string.stories_count, count)
                GodCard(
                    deity, settings, repository,
                    meta = meta,
                    talkBack = stringResource(R.string.god_stories_talkback, name, count),
                ) { onDeity(deity.id) }
            }
        }
    }
}

@Composable
fun DeityStoriesScreen(
    deityId: String,
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onStory: (String) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var stories by remember { mutableStateOf<List<StorySummary>>(emptyList()) }
    var symbolId by remember { mutableStateOf<String?>(null) }
    val lang = settings.language()
    LaunchedEffect(deityId, settings.lang) {
        val deity = repository.deity(deityId)
        title = deity?.let { deityScriptName(it, lang) } ?: deityId
        symbolId = deity?.symbols?.firstOrNull()?.image
        stories = repository.storiesFor(deityId).filter { it.status != Status.retired }.sortedBy { it.order }
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(title, onBack, onHome)
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(stories, key = { it.id }) { story ->
                StoryCard(story, lang, symbolId, repository, kicker = null) { onStory(story.id) }
            }
        }
    }
}

@Composable
fun StoryPlayerScreen(
    storyId: String,
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPuja: (String) -> Unit,
    onBackToStories: (String) -> Unit,
    series: List<String> = emptyList(),
    startPart: Int = 0,
    startBeat: Int = 0,
    origin: String = "",
) {
    var part by remember(storyId, startPart) { mutableIntStateOf(startPart) }
    val ids = if (series.isEmpty()) listOf(storyId) else series
    val currentId = ids.getOrElse(part) { storyId }
    var story by remember { mutableStateOf<Story?>(null) }
    var symbolId by remember { mutableStateOf<String?>(null) }
    var deityName by remember { mutableStateOf("") }
    var pujaId by remember { mutableStateOf<String?>(null) }
    var started by remember(currentId) { mutableStateOf(series.isNotEmpty()) }
    var langCode by remember(storyId) { mutableStateOf(settings.lang) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(currentId, langCode) {
        val loaded = repository.story(currentId)
        story = loaded
        if (loaded != null) {
            val deity = repository.deity(loaded.deity)
            symbolId = deity?.symbols?.firstOrNull()?.image
            deityName = deity?.let { deityScriptName(it, langOf(langCode)) } ?: loaded.deity
            val observanceId = loaded.observances.firstOrNull()
            pujaId = observanceId?.let { repository.observance(it)?.links?.get("pujas")?.firstOrNull() }
        }
    }
    val telling = story
    Column(Modifier.fillMaxSize().background(Ivory)) {
        Row(
            Modifier.fillMaxWidth().statusBarsPadding().heightIn(min = 64.dp).padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "‹",
                color = Maroon,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.size(56.dp).clickable(onClick = onBack).padding(12.dp),
            )
            LangChip("te", stringResource(R.string.lang_te_short), langCode) { langCode = it }
            LangChip("hi", stringResource(R.string.lang_hi_short), langCode) { langCode = it }
            LangChip("en", stringResource(R.string.lang_en_short), langCode) { langCode = it }
            Box(Modifier.weight(1f))
            Text(
                stringResource(R.string.home),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onHome).padding(12.dp),
            )
        }
        if (series.isNotEmpty()) {
            Text(
                stringResource(R.string.chavithi_katha_part, part + 1, ids.size),
                color = Ink,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }
        if (telling != null && !started) {
            CoverPage(
                story = telling,
                lang = langOf(langCode),
                symbolId = symbolId,
                repository = repository,
                readToMe = settings.readToMe && telling.hasNarration(langCode),
                showReadToMe = telling.hasNarration(langCode),
                music = settings.storyMusic,
                onReadToMe = { scope.launch { store.setReadToMe(it) } },
                onMusic = { scope.launch { store.setStoryMusic(it) } },
                onBegin = { started = true },
            )
        } else if (telling != null) {
            androidx.compose.runtime.key(telling.id) {
            BeatPlayer(
                story = telling,
                langCode = langCode,
                symbolId = symbolId,
                deityName = deityName,
                repository = repository,
                readToMe = settings.readToMe && telling.hasNarration(langCode),
                music = settings.storyMusic,
                pujaId = if (series.isEmpty()) pujaId else null,
                onPuja = onPuja,
                onBackToStories = { onBackToStories(telling.deity) },
                startBeat = if (part == startPart) startBeat else 0,
                katha = series.isNotEmpty(),
                lastPart = part >= ids.lastIndex,
                leaveLabel = stringResource(if (origin == "puja") R.string.back_to_the_puja else R.string.back),
                onAdvance = { part += 1 },
                onLeave = {
                    scope.launch { store.clearKathaPlace() }
                    onBack()
                },
                onBeat = { beat ->
                    if (series.isNotEmpty()) scope.launch { store.setKathaPlace(part, beat) }
                },
            )
            }
        }
    }
}

@Composable
private fun CoverPage(
    story: Story,
    lang: Lang,
    symbolId: String?,
    repository: ContentRepository,
    readToMe: Boolean,
    showReadToMe: Boolean,
    music: Boolean,
    onReadToMe: (Boolean) -> Unit,
    onMusic: (Boolean) -> Unit,
    onBegin: () -> Unit,
) {
    Column(
        Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ImageOrFallback(
            imageId = story.cover,
            fallbackSymbolId = symbolId,
            tint = TintStories,
            repository = repository,
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
            corner = 24.dp,
            module = ModuleKind.Stories,
        )
        Text(story.title?.pick(lang).orEmpty(), color = Ink, style = MaterialTheme.typography.displayLarge)
        val minutes = story.minutes
        Text(
            if (minutes != null) {
                stringResource(R.string.story_cover_meta, minutes, story.beats.size)
            } else {
                stringResource(R.string.story_pages, story.beats.size)
            },
            color = InkSoft,
            style = MaterialTheme.typography.bodyLarge,
        )
        val source = story.source.orEmpty()
        if (source.isNotBlank()) {
            Text(source, color = InkSoft, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, style = MaterialTheme.typography.bodyLarge)
        }
        if (showReadToMe) {
            SwitchRow(stringResource(R.string.read_to_me), readToMe, onReadToMe)
        }
        SwitchRow(stringResource(R.string.story_music), music, onMusic)
        ChoiceButton(stringResource(R.string.begin_action), onBegin)
    }
}

private sealed interface BeatEnding {
    data object Library : BeatEnding
    data class Next(val secondsLeft: Int, val onContinue: () -> Unit, val onStay: () -> Unit) : BeatEnding
    data class Leave(val label: String, val onLeave: () -> Unit) : BeatEnding
}

@Composable
private fun BeatPlayer(
    story: Story,
    langCode: String,
    symbolId: String?,
    deityName: String,
    repository: ContentRepository,
    readToMe: Boolean,
    music: Boolean,
    pujaId: String?,
    onPuja: (String) -> Unit,
    onBackToStories: () -> Unit,
    startBeat: Int = 0,
    katha: Boolean = false,
    lastPart: Boolean = true,
    leaveLabel: String = "",
    onAdvance: () -> Unit = {},
    onLeave: () -> Unit = {},
    onBeat: (Int) -> Unit = {},
) {
    val beats = story.beats
    val pager = rememberPagerState(initialPage = startBeat.coerceIn(0, beats.lastIndex), pageCount = { beats.size })
    var stayed by remember(story.id) { mutableStateOf(false) }
    var secondsLeft by remember(story.id) { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val lang = langOf(langCode)
    val context = LocalContext.current
    val audio = remember(story.id) { StoryAudio(context) }
    var sentence by remember { mutableIntStateOf(-1) }
    var turn by remember { mutableStateOf<Job?>(null) }
    var stage by remember(story.id, langCode) { mutableIntStateOf(0) }
    val hinted = remember(story.id) {
        mutableStateMapOf<Int, Boolean>().apply {
            beats.forEach { beat -> if (beat.touch != null) put(beat.n, true) }
        }
    }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(audio, lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) audio.hold()
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
            audio.release()
        }
    }
    LaunchedEffect(story.id) {
        audio.prepareBed(story.musicFile, story.musicLowFile)
    }
    LaunchedEffect(pager.currentPage) { onBeat(pager.currentPage) }
    LaunchedEffect(pager.currentPage, readToMe, stayed, story.id) {
        if (!katha || lastPart || stayed || !readToMe || pager.currentPage != beats.lastIndex) {
            secondsLeft = 0
            return@LaunchedEffect
        }
        for (left in 3 downTo 1) {
            secondsLeft = left
            delay(1000)
            if (stayed || pager.currentPage != beats.lastIndex) return@LaunchedEffect
        }
        secondsLeft = 0
        onAdvance()
    }
    LaunchedEffect(readToMe, music) { audio.switches(readToMe, music) }
    LaunchedEffect(pager.currentPage, langCode, readToMe, stage) {
        turn?.cancel()
        val page = pager.currentPage
        val beat = beats[page]
        sentence = -1
        val title = page == 0 && stage == 0 && readToMe && story.titleNarration[langCode] != null
        if (title) {
            audio.playTitle(story.titleNarration[langCode])
            audio.onNarrationFinished = { stage = 1 }
            return@LaunchedEffect
        }
        if (stage == 0) {
            stage = 1
            return@LaunchedEffect
        }
        val clip = beat.narration[langCode]
        audio.playBeat(clip, beat.musicLow)
        audio.onNarrationFinished = {
            turn = scope.launch {
                if (page == beats.lastIndex) {
                    audio.fadeOutBed()
                    return@launch
                }
                delay(1500)
                if (pager.currentPage == page) pager.animateScrollToPage(page + 1)
            }
        }
        if (!readToMe) {
            if (page == beats.lastIndex) {
                turn = scope.launch {
                    delay(3000)
                    if (pager.currentPage == page) audio.fadeOutBed()
                }
            }
            return@LaunchedEffect
        }
        val times = clip?.sentences.orEmpty()
        while (true) {
            val at = audio.positionMs()
            sentence = times.indexOfFirst { at in it.startMs.toLong()..it.endMs.toLong() }
            delay(50)
        }
    }
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f).fillMaxWidth(),
        ) { page ->
            val beat = beats[page]
            BeatPage(
                beat = beat,
                lang = lang,
                last = page == beats.lastIndex,
                symbolId = symbolId,
                repository = repository,
                showHint = hinted[beat.n] == true,
                activeSentence = beat.narration[langCode]?.sentences?.getOrNull(sentence)?.text,
                onImageTap = {
                    hinted[beat.n] = false
                    audio.playTouch(beat.touch?.sound)
                },
                deityName = deityName,
                pujaId = pujaId,
                onPuja = onPuja,
                onBackToStories = onBackToStories,
                ending = when {
                    page != beats.lastIndex || !katha -> BeatEnding.Library
                    !lastPart -> BeatEnding.Next(secondsLeft, onAdvance) { stayed = true; secondsLeft = 0 }
                    else -> BeatEnding.Leave(leaveLabel, onLeave)
                },
                onPrevious = {
                    if (pager.currentPage > 0) scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
                },
                onNext = {
                    if (pager.currentPage < beats.lastIndex) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                },
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(beats.size) { index ->
                val on = index == pager.currentPage
                Box(
                    Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (on) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(if (on) Gold else Ivory)
                        .border(1.dp, Gold, CircleShape),
                )
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            StepButton("‹", stringResource(R.string.previous_beat), Modifier.weight(1f)) {
                if (pager.currentPage > 0) scope.launch { pager.animateScrollToPage(pager.currentPage - 1) }
            }
            StepButton("›", stringResource(R.string.next_beat), Modifier.weight(1f)) {
                if (pager.currentPage < beats.lastIndex) scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
            }
        }
        Box(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            ChoiceButton(
                stringResource(if (audio.playing.value) R.string.pause_audio else R.string.play_audio),
            ) {
                if (audio.playing.value) audio.pause() else audio.resume()
            }
        }
    }
}

@Composable
private fun BeatPage(
    beat: Beat,
    lang: Lang,
    last: Boolean,
    symbolId: String?,
    repository: ContentRepository,
    showHint: Boolean,
    activeSentence: String?,
    onImageTap: () -> Unit,
    deityName: String,
    pujaId: String?,
    onPuja: (String) -> Unit,
    onBackToStories: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    ending: BeatEnding = BeatEnding.Library,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TouchImage(
            imageId = beat.image,
            symbolId = symbolId,
            effect = beat.touch?.effect,
            target = beat.touch?.target,
            box = beat.touch?.box,
            showHint = showHint,
            repository = repository,
            onTap = onImageTap,
        )
        Box(
            Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp)
                .pointerInput(beat.n) {
                    detectTapGestures { offset ->
                        if (offset.x < size.width / 3f) onPrevious() else onNext()
                    }
                }
                .padding(vertical = 8.dp),
        ) {
            StoryLine(beat.text?.pick(lang).orEmpty(), lang, activeSentence)
        }
        if (last) {
            when (ending) {
                BeatEnding.Library -> {
                    if (pujaId != null) {
                        ParentLink(stringResource(R.string.do_the_puja)) { onPuja(pujaId) }
                    }
                    ChoiceButton(stringResource(R.string.more_stories_of, deityName), onBackToStories)
                }
                is BeatEnding.Next -> {
                    Text(stringResource(R.string.part_2_syamantaka), color = Ink, style = MaterialTheme.typography.headlineMedium)
                    if (ending.secondsLeft > 0) {
                        Text(stringResource(R.string.continue_in_seconds, ending.secondsLeft), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                        ChoiceButton(stringResource(R.string.stay), ending.onStay)
                    }
                    ChoiceButton(stringResource(R.string.continue_step), ending.onContinue)
                }
                is BeatEnding.Leave -> ChoiceButton(ending.label, ending.onLeave)
            }
        }
    }
}

@Composable
private fun TouchImage(
    imageId: String?,
    symbolId: String?,
    effect: String?,
    target: String?,
    box: List<Float>?,
    showHint: Boolean,
    repository: ContentRepository,
    onTap: () -> Unit,
) {
    val density = LocalDensity.current
    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val halo = remember { Animatable(0f) }
    var hidden by remember(imageId) { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val hop = with(density) { 8.dp.toPx() }
    val hideDrop = with(density) { 12.dp.toPx() }
    val region = box?.takeIf { it.size == 4 }
    val play: () -> Unit = {
        onTap()
        scope.launch {
                    when (effect) {
                        "glow" -> {
                            halo.snapTo(0f)
                            halo.animateTo(0f, keyframes {
                                durationMillis = 1200
                                0f at 0
                                1f at 300
                                0f at 600
                                1f at 900
                                0f at 1200
                            })
                        }
                        "wiggle" -> {
                            rotation.snapTo(0f)
                            rotation.animateTo(0f, keyframes {
                                durationMillis = 600
                                0f at 0
                                4f at 50
                                -4f at 150
                                0f at 200
                                4f at 250
                                -4f at 350
                                0f at 400
                                4f at 450
                                -4f at 550
                                0f at 600
                            })
                        }
                        "hide" -> if (!hidden) {
                            coroutineScope {
                                launch { alpha.animateTo(0f, tween(500)) }
                                launch { offsetY.animateTo(hideDrop, tween(500)) }
                            }
                            hidden = true
                        } else {
                            coroutineScope {
                                launch { alpha.animateTo(1f, tween(500)) }
                                launch { offsetY.animateTo(0f, tween(500)) }
                            }
                            hidden = false
                        }
                        "appear" -> {
                            scale.snapTo(0.8f)
                            alpha.snapTo(0f)
                            coroutineScope {
                                launch { scale.animateTo(1f, tween(400)) }
                                launch { alpha.animateTo(1f, tween(400)) }
                            }
                        }
                        "bounce" -> {
                            offsetY.snapTo(0f)
                            offsetY.animateTo(0f, keyframes {
                                durationMillis = 600
                                0f at 0
                                -hop at 150
                                0f at 300
                                -hop at 450
                                0f at 600
                            })
                        }
                    }
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .graphicsLayer { clip = false }
            .then(if (region == null) Modifier.clickable(onClick = play) else Modifier),
    ) {
        if (halo.value > 0f) {
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = 1f + halo.value * 0.08f
                        scaleY = 1f + halo.value * 0.08f
                        this.alpha = halo.value
                    }
                    .background(Gold.copy(alpha = 0.45f), RoundedCornerShape(24.dp)),
            )
        }
        ImageOrFallback(
            imageId = imageId,
            fallbackSymbolId = symbolId,
            tint = TintStories,
            repository = repository,
            module = ModuleKind.Stories,
            modifier = Modifier
                .matchParentSize()
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    rotationZ = rotation.value
                    translationY = offsetY.value
                    this.alpha = alpha.value
                },
            corner = 24.dp,
        )
        if (region != null) {
            BoxWithConstraints(Modifier.matchParentSize()) {
                val min = 56.dp
                var x = maxWidth * region[0]
                var y = maxHeight * region[1]
                var w = maxWidth * region[2]
                var h = maxHeight * region[3]
                if (w < min) {
                    x -= (min - w) / 2
                    w = min
                }
                if (h < min) {
                    y -= (min - h) / 2
                    h = min
                }
                Box(
                    Modifier
                        .offset(x, y)
                        .size(w, h)
                        .clickable(onClick = play),
                )
            }
        }
        if (!target.isNullOrBlank()) {
            Text(
                stringResource(R.string.tap_the, target),
                color = Ink,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Ivory)
                    .border(1.dp, Gold, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
        if (showHint && effect != null) {
            val pulse by rememberInfiniteTransition(label = "hand").animateFloat(
                initialValue = 0.85f,
                targetValue = 1.15f,
                animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                label = "hand-scale",
            )
            Icon(
                imageVector = Icons.Outlined.TouchApp,
                contentDescription = stringResource(R.string.tap_picture),
                tint = Maroon,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
                    .size(40.dp)
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                    },
            )
        }
    }
}

@Composable
private fun StoryCard(
    story: StorySummary,
    lang: Lang,
    symbolId: String?,
    repository: ContentRepository,
    kicker: String?,
    onClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(TintStories)
            .clickable(onClick = onClick),
    ) {
        ImageOrFallback(
            imageId = story.cover,
            fallbackSymbolId = symbolId,
            tint = TintStories,
            repository = repository,
            modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
            corner = 0.dp,
            module = ModuleKind.Stories,
        )
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (!kicker.isNullOrBlank()) {
                Text(kicker, color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
            Text(story.title?.pick(lang).orEmpty(), color = Ink, style = MaterialTheme.typography.headlineMedium)
            Text(storyMeta(story.minutes), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
            if (lang.name in story.narrationLangs) {
                Text(stringResource(R.string.read_to_me), color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
            if (story.status == Status.draft && BuildConfig.DEBUG) {
                Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StoryLine(text: String, lang: Lang, active: String?) {
    if (text.isBlank()) return
    val family: FontFamily = when (lang) {
        Lang.te -> NotoSerifTeluguFamily
        Lang.hi -> NotoSerifDevanagariFamily
        Lang.en -> InterFamily
    }
    val shown = if (active.isNullOrBlank()) {
        androidx.compose.ui.text.AnnotatedString(text)
    } else {
        val at = text.indexOf(active)
        if (at < 0) {
            androidx.compose.ui.text.AnnotatedString(text)
        } else {
            buildAnnotatedString {
                append(text.substring(0, at))
                withStyle(SpanStyle(color = Maroon, fontWeight = FontWeight.Bold)) {
                    append(text.substring(at, at + active.length))
                }
                append(text.substring(at + active.length))
            }
        }
    }
    Text(
        text = shown,
        color = Ink,
        fontFamily = family,
        fontSize = 18.sp,
        lineHeight = 26.sp,
    )
}

private fun Story.hasNarration(lang: String): Boolean =
    titleNarration.containsKey(lang) || beats.any { it.narration.containsKey(lang) }

@Composable
private fun SwitchRow(label: String, on: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Ink, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Switch(checked = on, onCheckedChange = onChange)
    }
}

@Composable
private fun storyMeta(minutes: Int?): String =
    if (minutes == null) "" else stringResource(R.string.story_meta, minutes)

@Composable
private fun SearchBox(value: String, onValue: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValue,
        textStyle = TextStyle(color = Ink, fontSize = 18.sp),
        cursorBrush = SolidColor(Maroon),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Ivory)
            .border(1.dp, Gold, RoundedCornerShape(16.dp))
            .padding(16.dp),
        decorationBox = { inner ->
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = InkSoft, modifier = Modifier.size(22.dp))
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(stringResource(R.string.search_stories), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                    }
                    inner()
                }
            }
        },
    )
}

@Composable
private fun LangChip(code: String, label: String, selected: String, onLang: (String) -> Unit) {
    val on = code == selected
    Text(
        label,
        color = if (on) Ivory else Ink,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .heightIn(min = 56.dp)
            .padding(start = 8.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (on) Peacock else Ivory)
            .border(1.dp, if (on) Peacock else Gold, RoundedCornerShape(999.dp))
            .clickable { onLang(code) }
            .padding(horizontal = 14.dp, vertical = 16.dp),
    )
}

@Composable
private fun ChoiceButton(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = Ivory,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Maroon)
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}

@Composable
private fun ParentLink(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = Maroon,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
    )
}

@Composable
private fun StepButton(glyph: String, description: String, modifier: Modifier, onClick: () -> Unit) {
    Text(
        text = glyph,
        color = Maroon,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Gold, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = description }
            .padding(vertical = 12.dp),
    )
}

private fun storyParts(story: StorySummary): List<String> = listOfNotNull(
    story.title?.te,
    story.title?.hi,
    story.title?.en,
)

private fun langOf(code: String): Lang = when (code) {
    "te" -> Lang.te
    "hi" -> Lang.hi
    else -> Lang.en
}
