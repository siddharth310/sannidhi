package com.smaranam.app.ui.screens.onboarding

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
import com.smaranam.app.domain.model.Deity
import com.smaranam.app.domain.model.Script
import com.smaranam.app.domain.model.SeedDeities
import com.smaranam.app.ui.components.SmaranamPrimaryButton
import com.smaranam.app.ui.components.SmaranamSecondaryButton
import com.smaranam.app.ui.components.SacredName
import com.smaranam.app.ui.theme.SmaranamTheme

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
            style = SmaranamTheme.typography.displayL,
            color = SmaranamTheme.colors.textEmphasis,
        )
        Text(
            text = "This personalizes your Home. You can decide later.",
            style = SmaranamTheme.typography.body,
            color = SmaranamTheme.colors.muted,
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
        SmaranamPrimaryButton(
            text = "Continue",
            enabled = state.selectedDeityId != null,
            onClick = { viewModel.confirmDeity(onContinue) },
        )
        SmaranamSecondaryButton(
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
    val borderColor = if (selected) SmaranamTheme.colors.textEmphasis else SmaranamTheme.colors.hairline
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
            style = SmaranamTheme.typography.title,
            color = SmaranamTheme.colors.textEmphasis,
        )
        deity.nameTe?.let { te ->
            SacredName(text = te, script = Script.TELUGU, style = SmaranamTheme.typography.nameList)
        }
    }
}
