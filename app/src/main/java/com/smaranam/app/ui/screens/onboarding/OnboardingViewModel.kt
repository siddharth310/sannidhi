package com.smaranam.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smaranam.app.domain.model.Script
import com.smaranam.app.domain.model.UserProfile
import com.smaranam.app.domain.repository.UserSessionRepository
import com.smaranam.app.ui.navigation.OnboardingRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val phone: String = "",
    val password: String = "",
    val isSignUp: Boolean = true,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
)

data class IshtaDevataUiState(
    val selectedDeityId: String? = null,
)

data class PreferencesUiState(
    val selectedScript: Script = Script.TELUGU,
    val prefersDarkTheme: Boolean = false,
)

class OnboardingViewModel(
    private val userSessionRepository: UserSessionRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile> = userSessionRepository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserProfile.Guest)

    private val _authState = MutableStateFlow(AuthUiState())
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    private val _ishtaDevataState = MutableStateFlow(IshtaDevataUiState())
    val ishtaDevataState: StateFlow<IshtaDevataUiState> = _ishtaDevataState.asStateFlow()

    private val _preferencesState = MutableStateFlow(PreferencesUiState())
    val preferencesState: StateFlow<PreferencesUiState> = _preferencesState.asStateFlow()

    fun resolveSplashDestination(): String {
        val current = profile.value
        return when {
            current.isLoggedIn && current.isOnboardingComplete -> OnboardingRoute.Main
            current.isLoggedIn -> OnboardingRoute.IshtaDevata
            else -> OnboardingRoute.Auth
        }
    }

    fun onPhoneChange(value: String) {
        _authState.update { it.copy(phone = value.filter { char -> char.isDigit() }, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _authState.update { it.copy(password = value, errorMessage = null) }
    }

    fun toggleAuthMode() {
        _authState.update { it.copy(isSignUp = !it.isSignUp, errorMessage = null) }
    }

    fun submitAuth(onSuccess: (destination: String) -> Unit) {
        val state = _authState.value
        val phone = state.phone.trim()
        val password = state.password

        when {
            phone.length < 10 -> {
                _authState.update { it.copy(errorMessage = "Enter a valid 10-digit phone number.") }
                return
            }
            password.length < 6 -> {
                _authState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
                return
            }
        }

        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                if (state.isSignUp) {
                    userSessionRepository.signUp(phone, password)
                    _authState.update { it.copy(isLoading = false) }
                    onSuccess(OnboardingRoute.IshtaDevata)
                } else {
                    val success = userSessionRepository.logIn(phone, password)
                    if (!success) {
                        _authState.update {
                            it.copy(isLoading = false, errorMessage = "Phone or password is incorrect.")
                        }
                        return@launch
                    }
                    _authState.update { it.copy(isLoading = false) }
                    val destination = if (userSessionRepository.currentProfile().isOnboardingComplete) {
                        OnboardingRoute.Main
                    } else {
                        OnboardingRoute.IshtaDevata
                    }
                    onSuccess(destination)
                }
            } catch (_: Exception) {
                _authState.update {
                    it.copy(isLoading = false, errorMessage = "Something went wrong. Please try again.")
                }
            }
        }
    }

    fun selectDeity(deityId: String) {
        _ishtaDevataState.update {
            it.copy(selectedDeityId = if (it.selectedDeityId == deityId) null else deityId)
        }
    }

    fun confirmDeity(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userSessionRepository.setIshtaDevata(_ishtaDevataState.value.selectedDeityId)
            onSuccess()
        }
    }

    fun skipDeity(onSuccess: () -> Unit) {
        viewModelScope.launch {
            userSessionRepository.setIshtaDevata(null)
            onSuccess()
        }
    }

    fun selectScript(script: Script) {
        _preferencesState.update { it.copy(selectedScript = script) }
    }

    fun toggleTheme() {
        _preferencesState.update { it.copy(prefersDarkTheme = !it.prefersDarkTheme) }
    }

    fun savePreferences(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val prefs = _preferencesState.value
            userSessionRepository.setPreferences(prefs.selectedScript, prefs.prefersDarkTheme)
            userSessionRepository.completeOnboarding()
            onSuccess()
        }
    }

    fun skipPreferences(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val prefs = _preferencesState.value
            userSessionRepository.setPreferences(prefs.selectedScript, prefs.prefersDarkTheme)
            userSessionRepository.completeOnboarding()
            onSuccess()
        }
    }

    fun dismissFirstRunNudge() {
        viewModelScope.launch {
            userSessionRepository.dismissFirstRunNudge()
        }
    }
}

class OnboardingViewModelFactory(
    private val userSessionRepository: UserSessionRepository,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            return OnboardingViewModel(userSessionRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
