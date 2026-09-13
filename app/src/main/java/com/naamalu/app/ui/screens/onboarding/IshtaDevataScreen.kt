package com.naamalu.app.ui.screens.onboarding

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.domain.model.Deity
import com.naamalu.app.domain.model.Script
import com.naamalu.app.domain.model.SeedDeities
import com.naamalu.app.ui.components.NaamaluPrimaryButton
import com.naamalu.app.ui.components.NaamaluSecondaryButton
import com.naamalu.app.ui.components.SacredName
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun IshtaDevataScreen(
    viewModel: OnboardingViewModel,
    onContinue: () -> Unit,
    onSkip: () -> Unit,
) {
    val state by viewModel.ishtaDevataState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "Choose your ishta devata",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "This personalizes your Home. You can decide later.",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
        )
        Spacer(modifier = Modifier.height(8.dp))
        SeedDeities.availableInOnboarding.forEach { deity ->
            DeityChoiceCard(
                deity = deity,
                selected = state.selectedDeityId == deity.id,
                onClick = { viewModel.selectDeity(deity.id) },
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        NaamaluPrimaryButton(
            text = "Continue",
            enabled = state.selectedDeityId != null,
            onClick = { viewModel.confirmDeity(onContinue) },
        )
        NaamaluSecondaryButton(
            text = "Decide later",
            onClick = { viewModel.skipDeity(onSkip) },
        )
    }
}

@Composable
private fun DeityChoiceCard(
    deity: Deity,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val borderColor = if (selected) NaamaluTheme.colors.textEmphasis else NaamaluTheme.colors.hairline
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = deity.name,
            style = NaamaluTheme.typography.title,
            color = NaamaluTheme.colors.textEmphasis,
        )
        deity.nameTe?.let { te ->
            SacredName(text = te, script = Script.TELUGU, style = NaamaluTheme.typography.nameList)
        }
    }
}
