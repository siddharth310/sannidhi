package com.naamalu.app.ui.screens.onboarding

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
import com.naamalu.app.ui.theme.NaamaluTheme
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
                text = "Naamalu",
                style = NaamaluTheme.typography.displayL,
                color = NaamaluTheme.colors.textEmphasis,
            )
            Text(
                text = "నామాలు",
                style = NaamaluTheme.typography.nameList,
                color = NaamaluTheme.colors.textPrimary,
            )
            Text(
                text = "oṁ śāntiḥ",
                style = NaamaluTheme.typography.body,
                color = NaamaluTheme.colors.muted,
                textAlign = TextAlign.Center,
            )
        }
    }
}
