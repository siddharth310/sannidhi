package com.naamalu.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.naamalu.app.domain.model.Script
import com.naamalu.app.domain.model.UserProfile
import com.naamalu.app.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile> = userSessionRepository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile.Guest)

    fun setScript(script: Script) {
        viewModelScope.launch {
            val current = userSessionRepository.currentProfile()
            userSessionRepository.setPreferences(script, current.prefersDarkTheme)
        }
    }

    fun setDarkTheme(prefersDark: Boolean) {
        viewModelScope.launch {
            val current = userSessionRepository.currentProfile()
            userSessionRepository.setPreferences(current.defaultScript, prefersDark)
        }
    }

    fun logOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userSessionRepository.logOut()
            onComplete()
        }
    }
}

class SettingsViewModelFactory(
    private val userSessionRepository: UserSessionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
