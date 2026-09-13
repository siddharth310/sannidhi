package com.naamalu.app.ui.screens.saadhana

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.naamalu.app.domain.model.DayRecitationSummary
import com.naamalu.app.domain.model.PracticeStreak
import com.naamalu.app.domain.model.Recitation
import com.naamalu.app.domain.model.RecitationMode
import com.naamalu.app.domain.model.SaadhanaTotals
import com.naamalu.app.domain.repository.RecitationRepository
import com.naamalu.app.domain.util.RecitationLabels
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RecitationRowUi(
    val id: String,
    val timeLabel: String,
    val modeLabel: String,
    val title: String,
    val deityName: String,
)

data class SaadhanaUiState(
    val streak: PracticeStreak = PracticeStreak(0, 0),
    val totals: SaadhanaTotals = SaadhanaTotals(0, 0, 0, 0, 0),
    val practicedDates: Set<LocalDate> = emptySet(),
    val displayMonth: YearMonth = YearMonth.now(),
    val history: List<RecitationRowUi> = emptyList(),
    val modeFilter: RecitationMode? = null,
    val daySummary: DayRecitationSummary? = null,
    val dayRows: List<RecitationRowUi> = emptyList(),
)

class SaadhanaViewModel(
    private val recitationRepository: RecitationRepository,
) : ViewModel() {

    private val recitations = recitationRepository.observeRecitations()
    private val _modeFilter = MutableStateFlow<RecitationMode?>(null)
    private val _displayMonth = MutableStateFlow(YearMonth.now())
    private val _selectedDay = MutableStateFlow<LocalDate?>(null)

    val uiState = combine(recitations, _modeFilter, _displayMonth, _selectedDay) { logs, filter, month, selected ->
        SaadhanaUiState(
            streak = computeStreak(logs),
            totals = computeTotals(logs),
            practicedDates = logs.map { it.completedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate() }.toSet(),
            displayMonth = month,
            history = buildRows(logs, filter),
            modeFilter = filter,
            daySummary = selected?.let { date -> summarizeDay(logs, date) },
            dayRows = selected?.let { date -> buildRows(logs.filter { 
                it.completedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate() == date 
            }, null) } ?: emptyList(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SaadhanaUiState())

    fun setModeFilter(mode: RecitationMode?) {
        _modeFilter.value = if (_modeFilter.value == mode) null else mode
    }

    fun previousMonth() {
        _displayMonth.value = _displayMonth.value.minusMonths(1)
    }

    fun nextMonth() {
        _displayMonth.value = _displayMonth.value.plusMonths(1)
    }

    fun selectDay(date: LocalDate) {
        _selectedDay.value = date
    }

    fun clearSelectedDay() {
        _selectedDay.value = null
    }

    private fun computeStreak(logs: List<Recitation>): PracticeStreak {
        val dates = logs.map { it.completedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate() }.toSet()
        if (dates.isEmpty()) return PracticeStreak(0, 0)
        val today = LocalDate.now()
        var current = 0
        var cursor = today
        while (dates.contains(cursor)) {
            current++
            cursor = cursor.minusDays(1)
        }
        val sorted = dates.sorted()
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in sorted) {
            run = if (previous != null && date == previous.plusDays(1)) run + 1 else 1
            longest = maxOf(longest, run)
            previous = date
        }
        return PracticeStreak(current, longest)
    }

    private fun computeTotals(logs: List<Recitation>): SaadhanaTotals {
        val japaBeads = logs.filter { it.mode == RecitationMode.JAPA }.sumOf { it.count }
        return SaadhanaTotals(
            totalRecitations = logs.size,
            readCount = logs.count { it.mode == RecitationMode.READ },
            listenCount = logs.count { it.mode == RecitationMode.LISTEN },
            japaBeadCount = japaBeads,
            japaMalas = japaBeads / 108,
        )
    }

    private fun summarizeDay(logs: List<Recitation>, date: LocalDate): DayRecitationSummary {
        val dayLogs = logs.filter {
            it.completedAt.atZone(java.time.ZoneId.systemDefault()).toLocalDate() == date
        }
        return DayRecitationSummary(
            date = date,
            totalRecitations = dayLogs.size,
            readCount = dayLogs.count { it.mode == RecitationMode.READ },
            listenCount = dayLogs.count { it.mode == RecitationMode.LISTEN },
            japaBeadCount = dayLogs.filter { it.mode == RecitationMode.JAPA }.sumOf { it.count },
        )
    }

    private fun buildRows(logs: List<Recitation>, filter: RecitationMode?): List<RecitationRowUi> {
        return logs
            .filter { filter == null || it.mode == filter }
            .sortedByDescending { it.completedAt }
            .map { recitation ->
                RecitationRowUi(
                    id = recitation.id,
                    timeLabel = RecitationLabels.timeLabel(recitation.completedAt),
                    modeLabel = RecitationLabels.modeLabel(recitation.mode, recitation.count),
                    title = RecitationLabels.title(recitation),
                    deityName = RecitationLabels.deityName(recitation.deityId),
                )
            }
    }
}

class SaadhanaViewModelFactory(
    private val recitationRepository: RecitationRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaadhanaViewModel::class.java)) {
            return SaadhanaViewModel(recitationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
