package com.naamalu.app.ui.screens.japa

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun JapaScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Japa counter",
            style = NaamaluTheme.typography.headline,
            color = NaamaluTheme.colors.textPrimary,
        )
    }
}
