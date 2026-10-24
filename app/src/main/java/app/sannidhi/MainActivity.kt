package app.sannidhi

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowCompat
import app.sannidhi.ui.navigation.SannidhiNavHost
import app.sannidhi.ui.theme.SannidhiTheme
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val app = application as SannidhiApplication
        setContent {
            val settings by app.container.settingsStore.settings.collectAsState(initial = null)
            val lang = settings?.lang
            if (lang != null) {
                SideEffect {
                    val applied = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                        .substringBefore(",")
                        .substringBefore("-")
                    if (applied != lang) {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
                    }
                }
            }
            val localized = localizedContext(this, lang ?: "en")
            val dark = (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK) ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
            CompositionLocalProvider(
                LocalContext provides localized,
                LocalConfiguration provides localized.resources.configuration,
            ) {
                SannidhiTheme(darkTheme = dark) {
                    SannidhiNavHost(container = app.container)
                }
            }
        }
    }
}

private fun localizedContext(base: Context, tag: String): Context {
    val config = Configuration(base.resources.configuration)
    config.setLocales(LocaleList(Locale.forLanguageTag(tag)))
    return base.createConfigurationContext(config)
}
