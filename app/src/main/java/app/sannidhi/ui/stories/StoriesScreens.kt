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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
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
import app.sannidhi.ui.common.ImageOrFallback
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.puja.GodCard
import app.sannidhi.ui.puja.deityEnglishName
import app.sannidhi.ui.puja.deityScriptName
import app.sannidhi.ui.puja.matchesQuery
import app.sannidhi.ui.puja.searchParts
import app.sannidhi.ui.theme.CormorantFamily
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

private data class TodayStory(val story: StorySummary, val observanceTitle: String)

@Composable
fun StoriesHomeScreen(
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onDeity: (String) -> Unit,
    onStory: (String) -> Unit,
) {
    var deities by remember { mutableStateOf<List<Deity>>(emptyList()) }
    var stories by remember { mutableStateOf<List<StorySummary>>(emptyList()) }
    var today by remember { mutableStateOf<List<TodayStory>>(emptyList()) }
    var query by remember { mutableStateOf("") }
    val lang = settings.language()
    LaunchedEffect(settings.cityId, settings.lang, settings.debugToday) {
        deities = repository.deities().sortedBy { it.order }
        val index = repository.storyIndex().filter { it.status != Status.retired }
        stories = index
        val day = repository.day(settings.cityId, settings.today())
        today = day?.observances.orEmpty().flatMap { obsId ->
            val observance = repository.observance(obsId) ?: return@flatMap emptyList()
            val title = observance.title?.pick(lang).orEmpty()
            observance.links["stories"].orEmpty().mapNotNull { storyId ->
                index.firstOrNull { it.id == storyId }?.let { TodayStory(it, title) }
            }
        }
    }
    val storyHits = if (query.isBlank()) emptyList() else stories.filter { matchesQuery(query, storyParts(it)) }
    val gods = deities.filter { deity ->
        query.isBlank() || matchesQuery(query, searchParts(deity)) ||
            stories.any { it.deity == deity.id && matchesQuery(query, storyParts(it)) }
    }
    val symbols = deities.associate { it.id to it.symbols.firstOrNull()?.image }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.module_stories), onBack, onHome)
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBox(query) { query = it }
            }
            if (query.isBlank()) {
                items(today, key = { "today-${it.story.id}" }, span = { GridItemSpan(maxLineSpan) }) { card ->
                    StoryCard(
                        story = card.story,
                        lang = lang,
                        symbolId = symbols[card.story.deity],
                        repository = repository,
                        kicker = stringResource(R.string.for_today, card.observanceTitle),
                        onClick = { onStory(card.story.id) },
                    )
                }
            }
            items(storyHits, key = { "hit-${it.id}" }, span = { GridItemSpan(maxLineSpan) }) { story ->
                StoryCard(
                    story = story,
                    lang = lang,
                    symbolId = symbols[story.deity],
                    repository = repository,
                    kicker = null,
                    onClick = { onStory(story.id) },
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.choose_a_god), color = Ink, style = MaterialTheme.typography.headlineMedium)
            }
            items(gods, key = { it.id }) { deity ->
                val count = stories.count { it.deity == deity.id }
                val meta = if (count == 0) {
                    stringResource(R.string.coming_later)
                } else {
                    stringResource(if (count == 1) R.string.god_stories_one else R.string.god_stories, deityEnglishName(deity), count)
                }
                GodCard(deity, settings, repository, meta = meta, enabled = count > 0) {
                    if (count > 0) onDeity(deity.id)
                }
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
        stories = repository.storiesFor(deityId).filter { it.status != Status.retired }
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
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onPuja: (String) -> Unit,
    onBackToStories: (String) -> Unit,
) {
    var story by remember { mutableStateOf<Story?>(null) }
    var symbolId by remember { mutableStateOf<String?>(null) }
    var pujaId by remember { mutableStateOf<String?>(null) }
    var started by remember(storyId) { mutableStateOf(false) }
    var langCode by remember(storyId) { mutableStateOf(settings.lang) }
    LaunchedEffect(storyId) {
        val loaded = repository.story(storyId)
        story = loaded
        if (loaded != null) {
            val deity = repository.deity(loaded.deity)
            symbolId = deity?.symbols?.firstOrNull()?.image
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
        if (telling != null && !started) {
            CoverPage(
                story = telling,
                lang = langOf(langCode),
                symbolId = symbolId,
                repository = repository,
                onBegin = { started = true },
            )
        } else if (telling != null) {
            BeatPlayer(
                story = telling,
                langCode = langCode,
                symbolId = symbolId,
                repository = repository,
                pujaId = pujaId,
                onPuja = onPuja,
                onBackToStories = { onBackToStories(telling.deity) },
            )
        }
    }
}

@Composable
private fun CoverPage(
    story: Story,
    lang: Lang,
    symbolId: String?,
    repository: ContentRepository,
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
        ChoiceButton(stringResource(R.string.begin_action), onBegin)
    }
}

@Composable
private fun BeatPlayer(
    story: Story,
    langCode: String,
    symbolId: String?,
    repository: ContentRepository,
    pujaId: String?,
    onPuja: (String) -> Unit,
    onBackToStories: () -> Unit,
) {
    val beats = story.beats
    val pager = rememberPagerState(pageCount = { beats.size })
    val scope = rememberCoroutineScope()
    val lang = langOf(langCode)
    val hinted = remember(story.id) {
        mutableStateMapOf<Int, Boolean>().apply {
            beats.forEach { beat -> if (beat.touch != null) put(beat.n, true) }
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
                onImageTap = { hinted[beat.n] = false },
                pujaId = pujaId,
                onPuja = onPuja,
                onBackToStories = onBackToStories,
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
    onImageTap: () -> Unit,
    pujaId: String?,
    onPuja: (String) -> Unit,
    onBackToStories: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StoryLine(beat.short?.pick(lang).orEmpty(), lang, title = true)
                StoryLine(beat.more?.pick(lang).orEmpty(), lang, title = false)
            }
        }
        if (last) {
            if (pujaId != null) {
                ParentLink(stringResource(R.string.do_the_puja)) { onPuja(pujaId) }
            }
            ChoiceButton(stringResource(R.string.back_to_stories), onBackToStories)
        }
    }
}

@Composable
private fun TouchImage(
    imageId: String?,
    symbolId: String?,
    effect: String?,
    target: String?,
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
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .graphicsLayer { clip = false }
            .clickable {
                onTap()
                scope.launch {
                    // TODO: when beat art ships separate layers, apply this to touch.target only.
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
            },
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
            if (story.status == Status.draft && BuildConfig.DEBUG) {
                Text(stringResource(R.string.draft), color = Maroon, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StoryLine(text: String, lang: Lang, title: Boolean) {
    if (text.isBlank()) return
    val family: FontFamily = when (lang) {
        Lang.te -> NotoSerifTeluguFamily
        Lang.hi -> NotoSerifDevanagariFamily
        Lang.en -> if (title) CormorantFamily else InterFamily
    }
    val size = if (title) 22.sp else 18.sp
    Text(
        text = text,
        color = Ink,
        fontFamily = family,
        fontSize = size,
        lineHeight = size * 1.4f,
    )
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
