package app.sannidhi.ui.profile

import android.content.res.AssetManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.sannidhi.BuildConfig
import app.sannidhi.R
import app.sannidhi.content.ContentRepository
import app.sannidhi.ui.common.TopBar
import app.sannidhi.ui.theme.Ink
import app.sannidhi.ui.theme.InkSoft
import app.sannidhi.ui.theme.Ivory

@Composable
fun AboutScreen(
    repository: ContentRepository,
    onBack: () -> Unit,
    onHome: () -> Unit,
) {
    var pack by remember { mutableStateOf("") }
    LaunchedEffect(Unit) { pack = repository.manifest().version }
    Page(stringResource(R.string.about), onBack, onHome) {
        Text(stringResource(R.string.made_with_care), color = Ink, style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.app_version, BuildConfig.VERSION_NAME), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        if (pack.isNotBlank()) {
            Text(stringResource(R.string.content_pack, pack), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun LicencesScreen(onBack: () -> Unit, onHome: () -> Unit) {
    val context = LocalContext.current
    val credits = remember {
        licenceFiles(context.assets).joinToString("\n\n") { (name, text) -> "$name\n$text" }
    }
    Page(stringResource(R.string.licences), onBack, onHome) {
        Text(stringResource(R.string.font_licences), color = Ink, style = MaterialTheme.typography.bodyLarge)
        Text(stringResource(R.string.library_licences), color = Ink, style = MaterialTheme.typography.bodyLarge)
        if (credits.isNotBlank()) {
            Text(credits, color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun PrivacyScreen(onBack: () -> Unit, onHome: () -> Unit) {
    val context = LocalContext.current
    val policy = remember {
        context.assets.open("legal/privacy-policy.md").bufferedReader().use { it.readText() }
    }
    Page(stringResource(R.string.privacy), onBack, onHome) {
        Text(stringResource(R.string.policy_in_english), color = InkSoft, style = MaterialTheme.typography.bodyLarge)
        Text(policy, color = Ink, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun Page(title: String, onBack: () -> Unit, onHome: () -> Unit, body: @Composable () -> Unit) {
    Column(Modifier.fillMaxSize().background(Ivory)) {
        TopBar(title, onBack, onHome)
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = { body() },
        )
    }
}

private fun licenceFiles(assets: AssetManager): List<Pair<String, String>> {
    val found = mutableListOf<Pair<String, String>>()
    fun walk(dir: String) {
        val children = assets.list(dir) ?: return
        children.forEach { name ->
            val path = "$dir/$name"
            if (name.endsWith(".licence.txt")) {
                val text = assets.open(path).bufferedReader().use { it.readText().trim() }
                found += name to text
            } else if (assets.list(path)?.isNotEmpty() == true) {
                walk(path)
            }
        }
    }
    walk("content")
    return found.distinctBy { it.second }
}
