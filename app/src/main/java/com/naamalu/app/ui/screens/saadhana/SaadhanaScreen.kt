package com.naamalu.app.ui.screens.saadhana

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.naamalu.app.domain.model.RecitationMode
import com.naamalu.app.domain.util.RecitationLabels
import com.naamalu.app.ui.theme.NaamaluTheme
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun SaadhanaScreen(
    viewModel: SaadhanaViewModel,
    onDayClick: (LocalDate) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        item {
            Text(
                text = "Saadhana",
                style = NaamaluTheme.typography.displayL,
                color = NaamaluTheme.colors.textEmphasis,
            )
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${state.streak.current} day streak",
                        style = NaamaluTheme.typography.title,
                        color = NaamaluTheme.colors.textPrimary,
                    )
                    Text(
                        text = "Longest · ${state.streak.longest} days",
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.muted,
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "${state.totals.totalRecitations} sessions",
                        style = NaamaluTheme.typography.title,
                        color = NaamaluTheme.colors.textPrimary,
                    )
                    Text(
                        text = "read ${state.totals.readCount} · japa ${state.totals.japaBeadCount}",
                        style = NaamaluTheme.typography.caption,
                        color = NaamaluTheme.colors.muted,
                    )
                }
            }
        }
        item {
            PracticeCalendar(
                month = state.displayMonth,
                practicedDates = state.practicedDates,
                onDayClick = onDayClick,
                onPreviousMonth = viewModel::previousMonth,
                onNextMonth = viewModel::nextMonth,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeFilterChip(
                    label = "All",
                    selected = state.modeFilter == null,
                    onClick = { viewModel.setModeFilter(null) },
                )
                ModeFilterChip(
                    label = "Read",
                    selected = state.modeFilter == RecitationMode.READ,
                    onClick = { viewModel.setModeFilter(RecitationMode.READ) },
                )
                ModeFilterChip(
                    label = "Listen",
                    selected = state.modeFilter == RecitationMode.LISTEN,
                    onClick = { viewModel.setModeFilter(RecitationMode.LISTEN) },
                )
                ModeFilterChip(
                    label = "Japa",
                    selected = state.modeFilter == RecitationMode.JAPA,
                    onClick = { viewModel.setModeFilter(RecitationMode.JAPA) },
                )
            }
        }
        item {
            Text(
                text = "History",
                style = NaamaluTheme.typography.headline,
                color = NaamaluTheme.colors.textEmphasis,
            )
        }
        if (state.history.isEmpty()) {
            item {
                Text(
                    text = "No practice logged yet.",
                    style = NaamaluTheme.typography.body,
                    color = NaamaluTheme.colors.muted,
                )
            }
        } else {
            items(state.history, key = { it.id }) { row ->
                RecitationHistoryRow(row)
            }
        }
    }
}

@Composable
fun DayDetailScreen(
    dateIso: String,
    viewModel: SaadhanaViewModel,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val date = LocalDate.parse(dateIso)

    LaunchedEffect(dateIso) {
        viewModel.selectDay(date)
    }
    DisposableEffect(dateIso) {
        onDispose { viewModel.clearSelectedDay() }
    }

    val summary = state.daySummary
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = RecitationLabels.dateLabel(date),
            style = NaamaluTheme.typography.displayL,
            color = NaamaluTheme.colors.textEmphasis,
        )
        if (summary != null && summary.totalRecitations > 0) {
            Text(
                text = "${summary.totalRecitations} sessions · read ${summary.readCount} · japa ${summary.japaBeadCount} beads",
                style = NaamaluTheme.typography.body,
                color = NaamaluTheme.colors.muted,
            )
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.dayRows, key = { it.id }) { row ->
                    RecitationHistoryRow(row)
                }
            }
        } else {
            Text(
                text = "No practice on this day.",
                style = NaamaluTheme.typography.body,
                color = NaamaluTheme.colors.muted,
            )
        }
    }
}

@Composable
private fun RecitationHistoryRow(row: RecitationRowUi) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = row.title,
                style = NaamaluTheme.typography.title,
                color = NaamaluTheme.colors.textPrimary,
            )
            Text(
                text = row.timeLabel,
                style = NaamaluTheme.typography.caption,
                color = NaamaluTheme.colors.muted,
            )
        }
        Text(
            text = "${row.deityName} · ${row.modeLabel}",
            style = NaamaluTheme.typography.caption,
            color = NaamaluTheme.colors.muted,
        )
    }
}

@Composable
private fun ModeFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, style = NaamaluTheme.typography.label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = NaamaluTheme.colors.surface,
            selectedLabelColor = NaamaluTheme.colors.textEmphasis,
        ),
    )
}

@Composable
private fun PracticeCalendar(
    month: YearMonth,
    practicedDates: Set<LocalDate>,
    onDayClick: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    val monthLabel = month.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
    val firstDay = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.value % 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "‹",
                style = NaamaluTheme.typography.title,
                color = NaamaluTheme.colors.textEmphasis,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onPreviousMonth)
                    .padding(8.dp),
            )
            Text(
                text = monthLabel,
                style = NaamaluTheme.typography.title,
                color = NaamaluTheme.colors.textEmphasis,
            )
            Text(
                text = "›",
                style = NaamaluTheme.typography.title,
                color = NaamaluTheme.colors.textEmphasis,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onNextMonth)
                    .padding(8.dp),
            )
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { label ->
                Text(
                    text = label,
                    style = NaamaluTheme.typography.caption,
                    color = NaamaluTheme.colors.muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        val totalCells = startOffset + daysInMonth
        val rows = (totalCells + 6) / 7
        var day = 1
        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < startOffset || day > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val date = month.atDay(day)
                        val practiced = practicedDates.contains(date)
                        val isToday = date == LocalDate.now()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(2.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        practiced -> NaamaluTheme.colors.primary.copy(alpha = 0.15f)
                                        else -> NaamaluTheme.colors.background
                                    },
                                )
                                .then(
                                    if (isToday) {
                                        Modifier.border(1.dp, NaamaluTheme.colors.hairline, RoundedCornerShape(8.dp))
                                    } else {
                                        Modifier
                                    },
                                )
                                .clickable { onDayClick(date) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = day.toString(),
                                style = NaamaluTheme.typography.label,
                                color = if (practiced) {
                                    NaamaluTheme.colors.textEmphasis
                                } else {
                                    NaamaluTheme.colors.textPrimary
                                },
                            )
                        }
                        day++
                    }
                }
            }
        }
    }
}
