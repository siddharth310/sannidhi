package com.naamalu.app.ui.screens.settings

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.domain.model.Script
import com.naamalu.app.ui.components.NaamaluSecondaryButton
import com.naamalu.app.ui.theme.NaamaluTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onLogOut: () -> Unit,
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text(
            text = "Settings",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "Account",
                style = NaamaluTheme.typography.label,
                color = NaamaluTheme.colors.muted,
            )
            Text(
                text = profile.phone ?: "—",
                style = NaamaluTheme.typography.title,
                color = NaamaluTheme.colors.textPrimary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Reading script",
                style = NaamaluTheme.typography.label,
                color = NaamaluTheme.colors.textPrimary,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Script.entries.forEach { script ->
                    FilterChip(
                        selected = profile.defaultScript == script,
                        onClick = { viewModel.setScript(script) },
                        label = { Text(script.label, style = NaamaluTheme.typography.label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaamaluTheme.colors.surface,
                            selectedLabelColor = NaamaluTheme.colors.textEmphasis,
                        ),
                    )
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Appearance",
                style = NaamaluTheme.typography.label,
                color = NaamaluTheme.colors.textPrimary,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemeChip(
                    label = "Dina",
                    selected = !profile.prefersDarkTheme,
                    onClick = { viewModel.setDarkTheme(false) },
                )
                ThemeChip(
                    label = "Ratri",
                    selected = profile.prefersDarkTheme,
                    onClick = { viewModel.setDarkTheme(true) },
                )
            }
        }
        NaamaluSecondaryButton(
            text = "Log out",
            onClick = { viewModel.logOut(onLogOut) },
        )
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        style = NaamaluTheme.typography.label,
        color = if (selected) NaamaluTheme.colors.textEmphasis else NaamaluTheme.colors.muted,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = if (selected) NaamaluTheme.colors.textEmphasis else NaamaluTheme.colors.hairline,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

private val Script.label: String
    get() = when (this) {
        Script.TELUGU -> "Telugu"
        Script.TAMIL -> "Tamil"
        Script.DEVANAGARI -> "Devanagari"
        Script.IAST -> "IAST"
    }
