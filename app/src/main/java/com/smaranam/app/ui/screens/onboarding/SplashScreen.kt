package com.smaranam.app.ui.screens.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smaranam.app.ui.theme.SmaranamTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigate: (String) -> Unit,
    resolveDestination: () -> String,
) {
    LaunchedEffect(Unit) {
        delay(1500)
        onNavigate(resolveDestination())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Smaranam",
                style = SmaranamTheme.typography.displayL,
                color = SmaranamTheme.colors.textEmphasis,
            )
            Text(
                text = "स्मरणम्",
                style = SmaranamTheme.typography.nameList,
                color = SmaranamTheme.colors.textPrimary,
            )
            Text(
                text = "oṁ śāntiḥ",
                style = SmaranamTheme.typography.body,
                color = SmaranamTheme.colors.muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
