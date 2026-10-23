package com.smaranam.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.smaranam.app.BuildConfig
import com.smaranam.app.R
import com.smaranam.app.content.ContentRepository
import com.smaranam.app.content.Lang
import com.smaranam.app.content.Master
import com.smaranam.app.content.TextView
import com.smaranam.app.settings.AppSettings
import com.smaranam.app.settings.AppSettingsStore
import com.smaranam.app.ui.common.TopBar
import com.smaranam.app.ui.theme.Ink
import com.smaranam.app.ui.theme.InkSoft
import com.smaranam.app.ui.theme.Ivory
import com.smaranam.app.ui.theme.Maroon
import com.smaranam.app.ui.theme.TintPuja
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ProfileScreen(
    settings: AppSettings,
    repository: ContentRepository,
    store: AppSettingsStore,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onDeleted: () -> Unit,
    onFindNakshatra: () -> Unit,
) {
    val settings by store.settings.collectAsState(initial = settings)
    var master by remember { mutableStateOf<Master?>(null) }
    var confirm by remember { mutableStateOf(false) }
    var debugDate by remember(settings.debugToday) { mutableStateOf(settings.debugToday.orEmpty()) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { master = repository.master() }
    val lang = settings.language()
    val view = settings.infoView()
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.profile), onBack, onHome)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Section(stringResource(R.string.name))
            Field(settings.name) { scope.launch { store.setName(it) } }
            Section(stringResource(R.string.language))
            listOf("te" to R.string.lang_te, "hi" to R.string.lang_hi, "en" to R.string.lang_en).forEach { (code, label) ->
                SelectRow(stringResource(label), settings.lang == code) {
                    scope.launch { store.setLang(code) }
                }
            }
            Section(stringResource(R.string.state_city))
            master?.states?.forEach { state ->
                SelectRow(state.name?.pick(lang) ?: state.code, settings.stateCode == state.code) {
                    scope.launch { store.setPlace(state.code, "") }
                }
            }
            master?.cities?.filter { it.state == settings.stateCode }?.forEach { city ->
                SelectRow(city.name?.pick(lang) ?: city.id, settings.cityId == city.id) {
                    scope.launch { store.setPlace(city.state, city.id) }
                }
            }
            Section(stringResource(R.string.rashi))
            SelectRow(stringResource(R.string.i_dont_know), settings.rashi == null) {
                scope.launch { store.setRashi(null) }
            }
            master?.rashis?.forEach { rashi ->
                SelectRow(rashi.name.pick(view, lang).ifBlank { rashi.en }, settings.rashi == rashi.n) {
                    scope.launch { store.setRashi(rashi.n) }
                }
            }
            Section(stringResource(R.string.nakshatra))
            SelectRow(stringResource(R.string.find_my_nakshatra), false, onFindNakshatra)
            master?.nakshatras?.forEach { star ->
                val base = star.name.pick(view, lang)
                val pada = settings.pada
                val label = if (settings.nakshatra == star.id && pada != null) {
                    "$base · " + stringResource(R.string.pada_label, pada)
                } else {
                    base
                }
                SelectRow(label, settings.nakshatra == star.id) {
                    scope.launch { store.setNakshatra(star.id) }
                }
            }
            Section(stringResource(R.string.gotra))
            Field(settings.gotra) { scope.launch { store.setGotra(it) } }
            Section(stringResource(R.string.show_mantras_in))
            listOf(
                TextView.Script to R.string.view_telugu,
                TextView.Devanagari to R.string.view_devanagari,
                TextView.Iast to R.string.view_iast,
            ).forEach { (value, label) ->
                SelectRow(stringResource(label), settings.textViewEnum() == value) {
                    scope.launch { store.setTextView(value.name) }
                }
            }
            if (BuildConfig.DEBUG) {
                Section(stringResource(R.string.debug_today))
                Field(debugDate) { debugDate = it }
                SelectRow(stringResource(R.string.save), false) {
                    val parsed = runCatching { LocalDate.parse(debugDate) }.getOrNull()
                    scope.launch { store.setDebugToday(parsed?.toString()) }
                }
                SelectRow(stringResource(R.string.debug_clear), settings.debugToday == null) {
                    scope.launch { store.setDebugToday(null) }
                }
            }
            Text(stringResource(R.string.stays_on_phone), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = stringResource(R.string.delete_data),
                color = Maroon,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable { confirm = true }.padding(vertical = 16.dp),
            )
        }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(stringResource(R.string.delete_data_title)) },
            text = { Text(stringResource(R.string.delete_data_body)) },
            confirmButton = {
                TextButton(onClick = {
                    confirm = false
                    onDeleted()
                }) { Text(stringResource(R.string.delete_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) }
            },
        )
    }
}

@Composable
private fun Section(text: String) {
    Text(text, color = Maroon, style = MaterialTheme.typography.labelLarge)
}

@Composable
private fun SelectRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        color = if (selected) Ivory else Ink,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) Maroon else TintPuja)
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}

@Composable
private fun Field(value: String, onValue: (String) -> Unit) {
    var text by remember { mutableStateOf(value) }
    BasicTextField(
        value = text,
        onValueChange = {
            text = it
            onValue(it)
        },
        textStyle = TextStyle(color = Ink, fontSize = MaterialTheme.typography.bodyLarge.fontSize),
        cursorBrush = SolidColor(Maroon),
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clip(RoundedCornerShape(16.dp)).background(TintPuja).padding(16.dp),
        decorationBox = { inner ->
            if (text.isEmpty()) Text(stringResource(R.string.name), color = InkSoft)
            inner()
        },
    )
}
