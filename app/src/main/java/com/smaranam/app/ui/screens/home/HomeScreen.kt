package com.smaranam.app.ui.screens.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smaranam.app.domain.model.Script
import com.smaranam.app.ui.components.SacredName
import com.smaranam.app.ui.theme.SmaranamTheme
import java.time.LocalTime

@Composable
fun HomeScreen(
    todayCount: Int,
    streakCurrent: Int,
    ishtaDevataName: String?,
    showFirstRunNudge: Boolean,
    onDismissFirstRunNudge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = greetingForNow(),
            style = SmaranamTheme.typography.headline,
            color = SmaranamTheme.colors.muted,
        )
        Text(
            text = if (ishtaDevataName != null) "Mandir · $ishtaDevataName" else "Mandir",
            style = SmaranamTheme.typography.displayL,
            color = SmaranamTheme.colors.textEmphasis,
        )
        Text(
            text = "Today's practice: $todayCount · Streak: $streakCurrent days",
            style = SmaranamTheme.typography.body,
            color = SmaranamTheme.colors.muted,
        )
        if (showFirstRunNudge) {
            FirstRunNudgeCard(
                ishtaDevataName = ishtaDevataName,
                onDismiss = onDismissFirstRunNudge,
            )
        }
        SacredName(
            text = "oṁ gaṇānāntvaṃ gaṇapatiṃ havāmahe namaḥ",
            script = Script.IAST,
            style = SmaranamTheme.typography.nameList,
        )
        Text(
            text = "Continue, quick japa, and daily suggestions will appear here.",
            style = SmaranamTheme.typography.caption,
            color = SmaranamTheme.colors.muted,
        )
    }
}

@Composable
private fun FirstRunNudgeCard(
    ishtaDevataName: String?,
    onDismiss: () -> Unit,
) {
    val deity = ishtaDevataName ?: "your ishta devata"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SmaranamTheme.colors.hairline, RoundedCornerShape(16.dp))
            .clickable(onClick = onDismiss)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Begin your saadhana",
            style = SmaranamTheme.typography.title,
            color = SmaranamTheme.colors.textEmphasis,
        )
        Text(
            text = "Begin with $deity's Ashtottara — tap to dismiss.",
            style = SmaranamTheme.typography.body,
            color = SmaranamTheme.colors.textPrimary,
        )
    }
}

private fun greetingForNow(): String {
    val hour = LocalTime.now().hour
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }
}
