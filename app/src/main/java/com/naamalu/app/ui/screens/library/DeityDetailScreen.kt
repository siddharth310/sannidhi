package com.naamalu.app.ui.screens.library

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.domain.util.RecitationLabels
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun DeityDetailScreen(
    deityId: String,
    deityName: String,
    viewModel: LibraryViewModel,
    onNaamavaliClick: (String) -> Unit,
) {
    val items by viewModel.naamavaliItems.collectAsStateWithLifecycle()

    LaunchedEffect(deityId) {
        viewModel.loadDeity(deityId)
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = deityName,
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Naamavalis",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(items, key = { it.naamavali.id }) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(16.dp))
                        .clickable { onNaamavaliClick(item.naamavali.id) }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = item.naamavali.title,
                        style = NaamaluTheme.typography.title,
                        color = NaamaluTheme.colors.textPrimary,
                    )
                    Text(
                        text = RecitationLabels.typeLabel(item.naamavali),
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.muted,
                    )
                    Text(
                        text = item.badge,
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.textEmphasis,
                    )
                }
            }
        }
    }
}
