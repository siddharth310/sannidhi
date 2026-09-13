package com.naamalu.app.ui.screens.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.naamalu.app.ui.components.NaamaluPrimaryButton
import com.naamalu.app.ui.components.NaamaluSecondaryButton
import com.naamalu.app.ui.theme.NaamaluTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreferencesScreen(
    viewModel: OnboardingViewModel,
    onComplete: () -> Unit,
) {
    val state by viewModel.preferencesState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Reading & appearance",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Optional — sensible defaults are pre-selected.",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
        )
        Text(
            text = "Default script",
            style = NaamaluTheme.typography.headline,
            color = NaamaluTheme.colors.textPrimary,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Script.entries.forEach { script ->
                FilterChip(
                    selected = state.selectedScript == script,
                    onClick = { viewModel.selectScript(script) },
                    label = {
                        Text(
                            text = script.label,
                            style = NaamaluTheme.typography.label,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NaamaluTheme.colors.surface,
                        selectedLabelColor = NaamaluTheme.colors.textEmphasis,
                        containerColor = NaamaluTheme.colors.background,
                        labelColor = NaamaluTheme.colors.muted,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = state.selectedScript == script,
                        borderColor = NaamaluTheme.colors.hairline,
                        selectedBorderColor = NaamaluTheme.colors.textEmphasis,
                    ),
                )
            }
        }
        Text(
            text = "Theme",
            style = NaamaluTheme.typography.headline,
            color = NaamaluTheme.colors.textPrimary,
        )
        ThemeChoiceRow(
            selectedDark = state.prefersDarkTheme,
            onSelectLight = { if (state.prefersDarkTheme) viewModel.toggleTheme() },
            onSelectDark = { if (!state.prefersDarkTheme) viewModel.toggleTheme() },
        )
        Spacer(modifier = Modifier.weight(1f))
        NaamaluPrimaryButton(
            text = "Continue",
            onClick = { viewModel.savePreferences(onComplete) },
        )
        NaamaluSecondaryButton(
            text = "Skip",
            onClick = { viewModel.skipPreferences(onComplete) },
        )
    }
}

@Composable
private fun ThemeChoiceRow(
    selectedDark: Boolean,
    onSelectLight: () -> Unit,
    onSelectDark: () -> Unit,
) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ThemeOption(
            label = "Dina (light)",
            selected = !selectedDark,
            onClick = onSelectLight,
            modifier = Modifier.weight(1f),
        )
        ThemeOption(
            label = "Ratri (dark)",
            selected = selectedDark,
            onClick = onSelectDark,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) NaamaluTheme.colors.textEmphasis else NaamaluTheme.colors.hairline
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        style = NaamaluTheme.typography.label,
        color = if (selected) NaamaluTheme.colors.textEmphasis else NaamaluTheme.colors.muted,
    )
}

private val Script.label: String
    get() = when (this) {
        Script.TELUGU -> "Telugu"
        Script.TAMIL -> "Tamil"
        Script.DEVANAGARI -> "Devanagari"
        Script.IAST -> "Transliteration"
    }
