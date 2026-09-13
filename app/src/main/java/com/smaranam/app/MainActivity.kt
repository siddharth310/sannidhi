package com.smaranam.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smaranam.app.ui.navigation.SmaranamNavHost
import com.smaranam.app.ui.theme.SmaranamTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as SmaranamApplication

        setContent {
            val profile by app.container.userSessionRepository.observeProfile()
                .collectAsStateWithLifecycle(initialValue = com.smaranam.app.domain.model.UserProfile.Guest)

            SmaranamTheme(darkTheme = profile.prefersDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SmaranamTheme.colors.background,
                ) {
                    SmaranamNavHost(container = app.container)
                }
            }
        }
    }
}
