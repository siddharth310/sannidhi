package com.naamalu.app.ui.screens.home

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
import com.naamalu.app.domain.model.Script
import com.naamalu.app.ui.components.SacredName
import com.naamalu.app.ui.theme.NaamaluTheme
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
            style = NaamaluTheme.typography.headline,
            color = NaamaluTheme.colors.muted,
        )
        Text(
            text = if (ishtaDevataName != null) "Mandir · $ishtaDevataName" else "Mandir",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Today's practice: $todayCount · Streak: $streakCurrent days",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
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
            style = NaamaluTheme.typography.nameList,
        )
        Text(
            text = "Continue, quick japa, and daily suggestions will appear here.",
            style = NaamaluTheme.typography.caption,
            color = NaamaluTheme.colors.muted,
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
            .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(16.dp))
            .clickable(onClick = onDismiss)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "Begin your saadhana",
            style = NaamaluTheme.typography.title,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Begin with $deity's Ashtottara — tap to dismiss.",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.textPrimary,
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
