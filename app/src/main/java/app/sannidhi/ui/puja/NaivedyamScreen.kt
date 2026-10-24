package app.sannidhi.ui.puja

import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.sannidhi.R
import app.sannidhi.content.ContentRepository
import app.sannidhi.content.Lang
import app.sannidhi.content.Naivedyam
import app.sannidhi.content.RegionalForm
import app.sannidhi.settings.AppSettings
import app.sannidhi.ui.common.ImageOrFallback
import app.sannidhi.ui.common.ModuleKind
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory
import app.sannidhi.ui.theme.Maroon
import app.sannidhi.ui.theme.TintPuja

private data class OfferingLink(val title: String, val open: () -> Unit)

@Composable
fun NaivedyamScreen(
    itemId: String,
    settings: AppSettings,
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onObservance: (String) -> Unit,
    onPuja: (String) -> Unit,
) {
    var item by remember { mutableStateOf<Naivedyam?>(null) }
    var symbol by remember { mutableStateOf<String?>(null) }
    var offered by remember { mutableStateOf<List<OfferingLink>>(emptyList()) }
    var regionNames by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val lang = settings.language()
    val context = LocalContext.current
    val online by produceState(initialValue = false, context) {
        val manager = context.getSystemService(ConnectivityManager::class.java)
        value = manager.hasInternet()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) { value = manager.hasInternet() }
            override fun onLost(network: Network) { value = manager.hasInternet() }
        }
        manager.registerDefaultNetworkCallback(callback)
        awaitDispose { manager.unregisterNetworkCallback(callback) }
    }
    LaunchedEffect(itemId, settings.lang, settings.stateCode) {
        val loaded = repository.naivedyamItem(itemId)
        item = loaded
        val master = repository.master()
        regionNames = master.states.associate { it.code to it.name.pick(lang) }
        symbol = loaded?.offeredTo?.firstOrNull()?.let { repository.deity(it)?.symbols?.firstOrNull()?.image }
        if (loaded == null) return@LaunchedEffect
        val days = repository.observances().filter { obs ->
            obs.id in loaded.occasions || itemId in obs.links["naivedyam"].orEmpty()
        }
        val rites = repository.pujaIndex().mapNotNull { summary ->
            val puja = repository.puja(summary.id) ?: return@mapNotNull null
            val used = puja.stages.any { stage -> stage.steps.any { itemId in it.naivedyam } }
            if (used) summary else null
        }
        offered = days.map { obs ->
            OfferingLink(obs.title?.pick(lang) ?: obs.id) { onObservance(obs.id) }
        } + rites.map { puja ->
            OfferingLink(puja.title?.pick(lang) ?: puja.id) { onPuja(puja.id) }
        }
    }
    val loaded = item
    val forms = loaded?.regionalForms?.sortedBy { form -> if (settings.stateCode in form.region) 0 else 1 }.orEmpty()
    val lead = forms.firstOrNull()?.name?.pick(lang)?.takeIf { it.isNotBlank() } ?: itemId
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(lead, onBack, onHome)
        if (loaded == null) return@Column
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ImageOrFallback(
                imageId = loaded.image,
                fallbackSymbolId = symbol,
                tint = TintPuja,
                repository = repository,
                modifier = Modifier.fillMaxWidth().heightIn(min = 180.dp),
                corner = 24.dp,
                module = ModuleKind.Puja,
            )
            forms.forEach { form -> FormCard(form, regionNames, lang) }
            Text(stringResource(R.string.why_offering), color = Maroon, style = MaterialTheme.typography.labelLarge)
            loaded.why?.pick(lang)?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = Ink, style = MaterialTheme.typography.bodyLarge)
            }
            if (offered.isNotEmpty()) {
                Text(stringResource(R.string.offered_on), color = Maroon, style = MaterialTheme.typography.labelLarge)
                offered.forEach { link ->
                    Text(
                        link.title,
                        color = Maroon,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClick = link.open).padding(vertical = 16.dp),
                    )
                }
            }
            Text(stringResource(R.string.recipes_differ, lead), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
            if (online) {
                Text(
                    stringResource(R.string.search_offering),
                    color = Ivory,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Maroon)
                        .clickable {
                            val query = Uri.encode(lead)
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query")))
                        }
                        .padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun FormCard(form: RegionalForm, regionNames: Map<String, String>, lang: Lang) {
    val name = form.name?.pick(lang).orEmpty()
    if (name.isBlank()) return
    val region = form.region.map { regionNames[it] ?: it }.joinToString(", ")
    val note = form.note?.pick(lang).orEmpty()
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(TintPuja).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(name, color = Ink, style = MaterialTheme.typography.headlineMedium)
        if (region.isNotBlank()) Text(region, color = Maroon, style = MaterialTheme.typography.labelLarge)
        if (note.isNotBlank()) Text(note, color = Ink, style = MaterialTheme.typography.bodyLarge)
    }
}

private fun ConnectivityManager.hasInternet(): Boolean {
    val network = activeNetwork ?: return false
    val caps = getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}
