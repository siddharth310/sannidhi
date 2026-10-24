package app.sannidhi.ui.onboarding

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.sannidhi.R
import app.sannidhi.content.City
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Master
import app.sannidhi.content.Rashi
import app.sannidhi.content.StateInfo
import app.sannidhi.settings.AppSettings
import app.sannidhi.settings.AppSettingsStore
import app.sannidhi.ui.calendar.FindNakshatraScreen
import app.sannidhi.ui.theme.Gold
import app.sannidhi.ui.theme.Hill1
import app.sannidhi.ui.theme.Hill2
import app.sannidhi.ui.theme.Hill4
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.Peacock
import app.sannidhi.ui.theme.SkyBottom
import app.sannidhi.ui.theme.SkyTop
import app.sannidhi.ui.theme.Surface
import app.sannidhi.ui.theme.TintPuja
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun OnboardingScreen(
    repository: ContentRepository,
    store: AppSettingsStore,
    lang: String,
    onFinished: () -> Unit,
) {
    val device = Locale.getDefault().language
    var step by remember { mutableIntStateOf(0) }
    var chosenLang by remember { mutableStateOf(if (device == "te" || device == "hi" || device == "en") device else "en") }
    var master by remember { mutableStateOf<Master?>(null) }
    var stateCode by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var query by remember { mutableStateOf("") }
    var rashi by remember { mutableStateOf<Int?>(null) }
    var finding by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val settings by store.settings.collectAsState(initial = AppSettings(lang = lang))
    LaunchedEffect(Unit) { master = repository.master() }
    val data = master
    if (finding) {
        FindNakshatraScreen(
            settings = settings,
            store = store,
            repository = repository,
            fromMasam = false,
            onBack = { finding = false },
            onHome = { finding = false },
            onSaved = { found ->
                rashi = found
                finding = false
            },
        )
        return
    }
    Column(Modifier.fillMaxSize().background(Ivory)) {
        when (step) {
            0 -> LanguageStep(chosenLang, onChoose = { chosenLang = it }) {
                scope.launch {
                    store.setLang(chosenLang)
                    step = 1
                }
            }
            1 -> if (data != null) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    StepHead(step = 2, onBack = { step = 0 })
                    PlaceStep(
                        states = data.states,
                        cities = data.cities,
                        lang = lang,
                        stateCode = stateCode,
                        query = query,
                        onQuery = { query = it },
                        onState = { stateCode = it; query = "" },
                        onCity = { city ->
                            scope.launch {
                                store.setPlace(city.state, city.id)
                                step = 2
                            }
                        },
                    )
                }
            }
            2 -> Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                StepHead(step = 3, onBack = { step = 1 })
                NameStep(
                    name = name,
                    onName = { name = it },
                    onContinue = {
                        scope.launch {
                            store.setName(name.trim())
                            step = 3
                        }
                    },
                )
            }
            else -> if (data != null) {
                val language = when (lang) { "te" -> Lang.te; "hi" -> Lang.hi; else -> Lang.en }
                RashiStep(
                    rashis = data.rashis,
                    lang = language,
                    selected = rashi,
                    onSelect = { rashi = it },
                    onBack = { step = 2 },
                    onFind = { finding = true },
                    onFinish = { picked ->
                        scope.launch {
                            if (picked != null) store.setRashi(picked)
                            store.setOnboarded(true)
                            onFinished()
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun LanguageStep(selected: String, onChoose: (String) -> Unit, onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Brush.verticalGradient(listOf(SkyTop, SkyBottom))),
        ) {
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(48.dp).background(Hill1))
            Box(Modifier.align(Alignment.BottomStart).padding(start = 24.dp).size(72.dp, 36.dp).clip(RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)).background(Hill2))
            Box(Modifier.align(Alignment.BottomEnd).padding(end = 36.dp).size(56.dp, 28.dp).clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)).background(Hill4))
            Text(
                stringResource(R.string.app_name),
                color = Maroon,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.align(Alignment.Center).statusBarsPadding(),
            )
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.step_n_of, 1, 4), color = InkSoft, style = MaterialTheme.typography.labelLarge)
            Text(stringResource(R.string.onboarding_language), color = Ink, style = MaterialTheme.typography.displayLarge)
            Text(stringResource(R.string.onboarding_language_hint), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
            LanguageTile(stringResource(R.string.lang_te), stringResource(R.string.lang_te_note), selected == "te") { onChoose("te") }
            LanguageTile(stringResource(R.string.lang_hi), stringResource(R.string.lang_hi_note), selected == "hi") { onChoose("hi") }
            LanguageTile(stringResource(R.string.lang_en), stringResource(R.string.lang_en_note), selected == "en") { onChoose("en") }
        }
        Text(
            stringResource(R.string.continue_action),
            color = Ivory,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected.isNotBlank()) Maroon else Maroon.copy(alpha = 0.4f))
                .clickable(enabled = selected.isNotBlank(), onClick = onContinue)
                .padding(16.dp),
        )
    }
}

@Composable
private fun LanguageTile(title: String, note: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Surface)
            .border(if (selected) 2.dp else 1.dp, if (selected) Maroon else Gold, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Ink, style = MaterialTheme.typography.headlineMedium)
            Text(note, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
        if (selected) {
            Box(
                Modifier.size(28.dp).clip(CircleShape).background(Maroon),
                contentAlignment = Alignment.Center,
            ) {
                Text("✓", color = Ivory, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun StepHead(step: Int, onBack: () -> Unit) {
    Text(
        "‹  ${stringResource(R.string.back)}",
        color = Maroon,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onBack).padding(vertical = 16.dp),
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(4) { index ->
            val current = index == step - 1
            val done = index < step - 1
            Box(
                Modifier
                    .weight(1f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(when {
                        current -> Maroon
                        done -> Gold
                        else -> Gold.copy(alpha = 0.35f)
                    }),
            )
        }
    }
    Text(stringResource(R.string.step_n_of, step, 4), color = InkSoft, style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun PlaceStep(
    states: List<StateInfo>,
    cities: List<City>,
    lang: String,
    stateCode: String,
    query: String,
    onQuery: (String) -> Unit,
    onState: (String) -> Unit,
    onCity: (City) -> Unit,
) {
    val language = when (lang) { "te" -> Lang.te; "hi" -> Lang.hi; else -> Lang.en }
    Text(stringResource(R.string.onboarding_place), color = Ink, style = MaterialTheme.typography.displayLarge)
    if (stateCode.isBlank()) {
        Text(stringResource(R.string.choose_state), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            states.forEach { state ->
                Choice(state.name?.pick(language) ?: state.code) { onState(state.code) }
            }
        }
    } else {
        Text(stringResource(R.string.choose_city), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        BasicTextField(
            value = query,
            onValueChange = onQuery,
            textStyle = TextStyle(color = Ink, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
            cursorBrush = SolidColor(Maroon),
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(TintPuja).padding(16.dp),
            decorationBox = { inner ->
                if (query.isEmpty()) Text(stringResource(R.string.search_city), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                inner()
            },
        )
        val filtered = cities.filter { it.state == stateCode }.filter { city ->
            val name = city.name?.pick(language).orEmpty()
            query.isBlank() || name.contains(query, ignoreCase = true) ||
                city.name?.te.orEmpty().contains(query) ||
                city.name?.hi.orEmpty().contains(query) ||
                city.name?.en.orEmpty().contains(query, ignoreCase = true)
        }
        Column(Modifier.verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            filtered.forEach { city ->
                Choice(city.name?.pick(language) ?: city.id) { onCity(city) }
            }
        }
    }
}

@Composable
private fun NameStep(name: String, onName: (String) -> Unit, onContinue: () -> Unit) {
    Text(stringResource(R.string.onboarding_name), color = Ink, style = MaterialTheme.typography.displayLarge)
    Text(stringResource(R.string.onboarding_name_hint), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
    BasicTextField(
        value = name,
        onValueChange = onName,
        textStyle = TextStyle(color = Ink, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
        cursorBrush = SolidColor(Maroon),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(TintPuja).padding(16.dp),
    )
    Choice(stringResource(R.string.continue_action), onContinue)
    Choice(stringResource(R.string.skip), onContinue)
}

@Composable
private fun RashiStep(
    rashis: List<Rashi>,
    lang: Lang,
    selected: Int?,
    onSelect: (Int) -> Unit,
    onBack: () -> Unit,
    onFind: () -> Unit,
    onFinish: (Int?) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        StepHead(step = 4, onBack = onBack)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(stringResource(R.string.onboarding_rashi), color = Ink, style = MaterialTheme.typography.displayLarge)
            Text(stringResource(R.string.optional_mark), color = InkSoft, style = MaterialTheme.typography.headlineMedium)
        }
        Text(stringResource(R.string.onboarding_rashi_note), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        Text(
            stringResource(R.string.find_my_nakshatra),
            color = Peacock,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onFind).padding(vertical = 16.dp),
        )
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            rashis.chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { rashi ->
                        val script = if (lang == Lang.te) rashi.name.te else rashi.name.sa
                        val on = selected == rashi.n
                        Column(
                            Modifier
                                .weight(1f)
                                .heightIn(min = 72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface)
                                .border(if (on) 2.dp else 1.dp, if (on) Maroon else Gold, RoundedCornerShape(16.dp))
                                .clickable { onSelect(rashi.n) }
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(script, color = Ink, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                            Text(rashi.name.iast, color = InkSoft, style = MaterialTheme.typography.labelLarge, textAlign = TextAlign.Center)
                        }
                    }
                    repeat(3 - row.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
        Text(
            stringResource(R.string.i_dont_know_skip),
            color = Maroon,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Maroon, RoundedCornerShape(16.dp))
                .clickable { onFinish(null) }
                .padding(16.dp),
        )
        Text(
            stringResource(R.string.finish_action),
            color = Ivory,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Maroon)
                .clickable { onFinish(selected) }
                .padding(16.dp),
        )
    }
}

@Composable
private fun Choice(label: String, onClick: () -> Unit) {
    Text(
        text = label,
        color = Ink,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TintPuja)
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}
