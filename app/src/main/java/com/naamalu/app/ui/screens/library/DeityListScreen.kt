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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.ui.theme.NaamaluTheme

@Composable
fun DeityListScreen(
    viewModel: LibraryViewModel,
    onDeityClick: (String) -> Unit,
) {
    val items by viewModel.deityList.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Naamalu",
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        Text(
            text = "Browse deities and naamavalis",
            style = NaamaluTheme.typography.body,
            color = NaamaluTheme.colors.muted,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            items(items, key = { it.deity.id }) { item ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(16.dp))
                        .clickable { onDeityClick(item.deity.id) }
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = if (item.isIshta) "${item.deity.name} · ishta devata" else item.deity.name,
                        style = NaamaluTheme.typography.title,
                        color = NaamaluTheme.colors.textEmphasis,
                    )
                    Text(
                        text = item.practiceBadge,
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.muted,
                    )
                }
            }
        }
    }
}
