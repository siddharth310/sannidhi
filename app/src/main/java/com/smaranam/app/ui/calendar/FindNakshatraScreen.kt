package com.smaranam.app.ui.calendar

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smaranam.app.R
import com.smaranam.app.content.City
import com.smaranam.app.content.ContentRepository
import com.smaranam.app.content.Master
import com.smaranam.app.settings.AppSettings
import com.smaranam.app.settings.AppSettingsStore
import com.smaranam.app.ui.common.TopBar
import com.smaranam.app.ui.puja.mantraScript
import com.smaranam.app.ui.theme.Ink
import com.smaranam.app.ui.theme.InkSoft
import com.smaranam.app.ui.theme.Ivory
import com.smaranam.app.ui.theme.Leaf
import com.smaranam.app.ui.theme.Maroon
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun FindNakshatraScreen(
    settings: AppSettings,
    store: AppSettingsStore,
    repository: ContentRepository,
    fromMasam: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onSaved: (Int) -> Unit,
) {
    val lang = settings.language()
    val script = mantraScript(settings)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var master by remember { mutableStateOf<Master?>(null) }
    var date by remember { mutableStateOf<LocalDate?>(null) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var place by remember { mutableStateOf<City?>(null) }
    var dateNote by remember { mutableStateOf<String?>(null) }
    var timeNote by remember { mutableStateOf<String?>(null) }
    var placeNote by remember { mutableStateOf<String?>(null) }
    var reading by remember { mutableStateOf<StarReading?>(null) }
    val passed = stringResource(R.string.date_has_passed)
    val needTime = stringResource(R.string.enter_birth_time)
    val needPlace = stringResource(R.string.choose_birth_place)
    LaunchedEffect(Unit) { master = repository.master() }
    val names = master
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(stringResource(R.string.find_my_nakshatra), onBack, onHome)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(stringResource(R.string.birth_date), color = Ink, style = MaterialTheme.typography.bodyLarge)
            ChoiceLine(date?.format(java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy")) ?: stringResource(R.string.pick_a_date)) {
                val seed = date ?: LocalDate.now().minusYears(30)
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        date = LocalDate.of(year, month + 1, day)
                        dateNote = null
                        reading = null
                    },
                    seed.year,
                    seed.monthValue - 1,
                    seed.dayOfMonth,
                ).show()
            }
            dateNote?.let { Text(it, color = InkSoft, style = MaterialTheme.typography.bodyLarge) }
            Text(stringResource(R.string.birth_time), color = Ink, style = MaterialTheme.typography.bodyLarge)
            ChoiceLine(time?.let { clock12(it.toString(), lang) } ?: stringResource(R.string.pick_a_time)) {
                val seed = time ?: LocalTime.of(6, 0)
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        time = LocalTime.of(hour, minute)
                        timeNote = null
                        reading = null
                    },
                    seed.hour,
                    seed.minute,
                    false,
                ).show()
            }
            timeNote?.let { Text(it, color = InkSoft, style = MaterialTheme.typography.bodyLarge) }
            Text(stringResource(R.string.birth_place), color = Ink, style = MaterialTheme.typography.bodyLarge)
            names?.cities?.forEach { city ->
                val on = place?.id == city.id
                Text(
                    city.name.pick(lang).ifBlank { city.id },
                    color = if (on) Ivory else Ink,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (on) Maroon else Ivory)
                        .clickable {
                            place = city
                            placeNote = null
                            reading = null
                        }
                        .padding(16.dp),
                )
            }
            placeNote?.let { Text(it, color = InkSoft, style = MaterialTheme.typography.bodyLarge) }
            Text(
                stringResource(R.string.show_result),
                color = Ivory,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Leaf)
                    .clickable {
                        val chosenDate = date
                        val chosenTime = time
                        val chosenPlace = place
                        var ready = true
                        if (chosenDate == null || chosenDate.isAfter(LocalDate.now())) {
                            dateNote = passed
                            ready = false
                        }
                        if (chosenTime == null) {
                            timeNote = needTime
                            ready = false
                        }
                        if (chosenPlace == null) {
                            placeNote = needPlace
                            ready = false
                        }
                        if (ready && chosenDate != null && chosenTime != null) {
                            reading = readStar(chosenDate.atTime(chosenTime))
                            date = null
                            time = null
                            place = null
                        } else {
                            reading = null
                        }
                    }
                    .padding(16.dp),
            )
            val found = reading
            if (found != null && names != null) {
                StarLine(names, found.primary, script, lang)
                found.also?.let { StarLine(names, it, script, lang) }
                if (found.also != null) {
                    Text(stringResource(R.string.close_to_change), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
                }
                Text(
                    stringResource(R.string.save_to_profile),
                    color = Ivory,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Leaf)
                        .clickable {
                            val star = names.nakshatras.firstOrNull { it.n == found.primary.nakshatra } ?: return@clickable
                            scope.launch {
                                store.setFoundStar(star.id, found.primary.pada, found.primary.rashi)
                                onSaved(found.primary.rashi)
                            }
                        }
                        .padding(16.dp),
                )
            }
            Text(
                stringResource(R.string.leave_without_saving),
                color = Maroon,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.heightIn(min = 56.dp).clickable(onClick = onBack).padding(vertical = 16.dp),
            )
            if (fromMasam) {
                Text(
                    stringResource(R.string.not_now),
                    color = InkSoft,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.heightIn(min = 56.dp).clickable {
                        scope.launch {
                            store.setFindNakshatraDismissed(true)
                            onBack()
                        }
                    }.padding(vertical = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun StarLine(master: Master, result: StarResult, script: com.smaranam.app.ui.puja.MantraScript, lang: com.smaranam.app.content.Lang) {
    val star = master.nakshatras.firstOrNull { it.n == result.nakshatra }
    val starName = star?.let { sacredName(it.name, script, lang) } ?: result.nakshatra.toString()
    val sign = rashiName(master, result.rashi, script, lang)
    Text(
        "$starName · $sign · " + stringResource(R.string.pada_label, result.pada),
        color = Ink,
        style = MaterialTheme.typography.headlineMedium,
    )
}

@Composable
private fun ChoiceLine(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = Ink,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(com.smaranam.app.ui.theme.TintCalendar)
            .clickable(onClick = onClick)
            .padding(16.dp),
    )
}
