package com.naamalu.app.ui.screens.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naamalu.app.domain.model.PracticeStreak
import com.naamalu.app.domain.model.RecitationMode
import com.naamalu.app.domain.repository.RecitationRepository
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class PracticeSummary(
    val todayCount: Int = 0,
    val streak: PracticeStreak = PracticeStreak(0, 0),
)

class MainShellViewModel(
    private val recitationRepository: RecitationRepository,
    private val zoneId: ZoneId = ZoneId.systemDefault(),
) : ViewModel() {

    val summary = recitationRepository.observeRecitations()
        .map { recitations ->
            val today = LocalDate.now(zoneId)
            val todayCount = recitations.count { it.completedAt.atZone(zoneId).toLocalDate() == today }
            val practicedDates = recitations
                .map { it.completedAt.atZone(zoneId).toLocalDate() }
                .toSet()
            PracticeSummary(
                todayCount = todayCount,
                streak = computeStreak(practicedDates, today),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PracticeSummary(),
        )

    private fun computeStreak(practicedDates: Set<LocalDate>, asOf: LocalDate): PracticeStreak {
        if (practicedDates.isEmpty()) {
            return PracticeStreak(current = 0, longest = 0)
        }

        var current = 0
        var cursor = asOf
        while (practicedDates.contains(cursor)) {
            current++
            cursor = cursor.minusDays(1)
        }

        val sorted = practicedDates.sorted()
        var longest = 0
        var run = 0
        var previous: LocalDate? = null
        for (date in sorted) {
            run = if (previous != null && date == previous.plusDays(1)) run + 1 else 1
            longest = maxOf(longest, run)
            previous = date
        }

        return PracticeStreak(current = current, longest = longest)
    }
}
