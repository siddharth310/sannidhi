package com.naamalu.app.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.domain.model.Script
import com.naamalu.app.domain.util.RecitationLabels
import com.naamalu.app.ui.components.NaamaluPrimaryButton
import com.naamalu.app.ui.components.SacredName
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun NaamavaliDetailScreen(
    naamavaliId: String,
    userId: String,
    viewModel: LibraryViewModel,
) {
    val state by viewModel.detailState.collectAsStateWithLifecycle()
    val naamavali = state.naamavali

    LaunchedEffect(naamavaliId) {
        viewModel.loadNaamavali(naamavaliId)
    }

    if (naamavali == null) return

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = naamavali.title,
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "${state.deityName} · ${RecitationLabels.typeLabel(naamavali)}",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
        )
        Text(
            text = state.badge,
            style = NaamaluTheme.typography.caption,
            color = NaamaluTheme.colors.textEmphasis,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Script.entries.forEach { script ->
                FilterChip(
                    selected = state.script == script,
                    onClick = { viewModel.setScript(script) },
                    label = { Text(script.label, style = NaamaluTheme.typography.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NaamaluTheme.colors.surface,
                        selectedLabelColor = NaamaluTheme.colors.textEmphasis,
                    ),
                )
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.naamas, key = { it.id }) { naama ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "${naama.index}.",
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.muted,
                    )
                    SacredName(
                        text = naama.archanaForm(state.script),
                        script = state.script,
                        style = NaamaluTheme.typography.nameList,
                    )
                    naama.meaning?.let { meaning ->
                        Text(
                            text = meaning,
                            style = NaamaluTheme.typography.body,
                            color = NaamaluTheme.colors.muted,
                        )
                    }
                }
            }
        }
        NaamaluPrimaryButton(
            text = if (state.readLogged) "Read logged" else "Complete read",
            enabled = !state.readLogged,
            onClick = { viewModel.logRead(userId) },
        )
    }
}

private val Script.label: String
    get() = when (this) {
        Script.TELUGU -> "Telugu"
        Script.TAMIL -> "Tamil"
        Script.DEVANAGARI -> "Devanagari"
        Script.IAST -> "IAST"
    }
