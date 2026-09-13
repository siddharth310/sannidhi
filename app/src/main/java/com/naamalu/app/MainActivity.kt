package com.naamalu.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.ui.navigation.NaamaluNavHost
import com.naamalu.app.ui.theme.NaamaluTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as NaamaluApplication

        setContent {
            val profile by app.container.userSessionRepository.observeProfile()
                .collectAsStateWithLifecycle(initialValue = com.naamalu.app.domain.model.UserProfile.Guest)

            NaamaluTheme(darkTheme = profile.prefersDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NaamaluTheme.colors.background,
                ) {
                    NaamaluNavHost(container = app.container)
                }
            }
        }
    }
}
