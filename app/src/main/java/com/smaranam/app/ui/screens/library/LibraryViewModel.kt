package com.smaranam.app.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smaranam.app.domain.model.Deity
import com.smaranam.app.domain.model.Naamavali
import com.smaranam.app.domain.model.NaamavaliRecitationStats
import com.smaranam.app.domain.model.Recitation
import com.smaranam.app.domain.model.RecitationMode
import com.smaranam.app.domain.model.RecitationSource
import com.smaranam.app.domain.model.Script
import com.smaranam.app.domain.repository.ContentRepository
import com.smaranam.app.domain.repository.RecitationRepository
import com.smaranam.app.domain.repository.UserSessionRepository
import java.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DeityListItem(
    val deity: Deity,
    val practiceBadge: String,
    val isIshta: Boolean,
)

data class NaamavaliListItem(
    val naamavali: Naamavali,
    val badge: String,
)

data class NaamavaliDetailState(
    val naamavali: Naamavali? = null,
    val deityName: String = "",
    val naamas: List<com.smaranam.app.domain.model.Naama> = emptyList(),
    val script: Script = Script.TELUGU,
    val badge: String = "",
    val readLogged: Boolean = false,
)

class LibraryViewModel(
    private val contentRepository: ContentRepository,
    private val recitationRepository: RecitationRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    private val recitations = recitationRepository.observeRecitations()
    private val profile = userSessionRepository.observeProfile()

    val deityList = combine(contentRepository.observeDeities(), recitations, profile) { deities, logs, user ->
        deities.map { deity ->
            val count = logs.count { it.deityId == deity.id }
            DeityListItem(
                deity = deity,
                practiceBadge = if (count > 0) "chanted ${count}×" else "Not yet practiced",
                isIshta = user.ishtaDevataId == deity.id,
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _detailState = MutableStateFlow(NaamavaliDetailState())
    val detailState: StateFlow<NaamavaliDetailState> = _detailState.asStateFlow()

    private val _naamavaliItems = MutableStateFlow<List<NaamavaliListItem>>(emptyList())
    val naamavaliItems: StateFlow<List<NaamavaliListItem>> = _naamavaliItems.asStateFlow()

    fun loadDeity(deityId: String) {
        viewModelScope.launch {
            val naamavalis = contentRepository.getNaamavalisForDeity(deityId)
            _naamavaliItems.value = naamavalis.map { naamavali ->
                val stats = recitationRepository.getNaamavaliStats(naamavali.id)
                NaamavaliListItem(
                    naamavali = naamavali,
                    badge = formatBadge(stats),
                )
            }
        }
    }

    fun loadNaamavali(naamavaliId: String) {
        viewModelScope.launch {
            val naamavali = contentRepository.getNaamavali(naamavaliId) ?: return@launch
            val deity = contentRepository.getDeity(naamavali.deityId)
            val naamas = contentRepository.getNaamasForNaamavali(naamavaliId)
            val stats = recitationRepository.getNaamavaliStats(naamavaliId)
            val script = userSessionRepository.currentProfile().defaultScript
            _detailState.value = NaamavaliDetailState(
                naamavali = naamavali,
                deityName = deity?.name ?: "",
                naamas = naamas,
                script = script,
                badge = formatBadge(stats),
            )
        }
    }

    fun setScript(script: Script) {
        _detailState.value = _detailState.value.copy(script = script)
    }

    fun logRead(userId: String) {
        val detail = _detailState.value
        val naamavali = detail.naamavali ?: return
        viewModelScope.launch {
            val now = Instant.now()
            recitationRepository.logRecitation(
                Recitation(
                    userId = userId,
                    deityId = naamavali.deityId,
                    naamavaliId = naamavali.id,
                    mode = RecitationMode.READ,
                    count = 1,
                    startedAt = now,
                    completedAt = now,
                    source = RecitationSource.READER,
                ),
            )
            val stats = recitationRepository.getNaamavaliStats(naamavali.id)
            _detailState.value = detail.copy(
                badge = formatBadge(stats),
                readLogged = true,
            )
        }
    }

    private fun formatBadge(stats: NaamavaliRecitationStats): String {
        val parts = mutableListOf<String>()
        if (stats.readCount > 0) parts += "read ${stats.readCount}×"
        if (stats.listenCount > 0) parts += "listened ${stats.listenCount}×"
        return parts.joinToString(" · ").ifEmpty { "Not yet practiced" }
    }
}

class LibraryViewModelFactory(
    private val contentRepository: ContentRepository,
    private val recitationRepository: RecitationRepository,
    private val userSessionRepository: UserSessionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LibraryViewModel::class.java)) {
            return LibraryViewModel(contentRepository, recitationRepository, userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
