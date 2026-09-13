package com.naamalu.app.domain.repository

import com.naamalu.app.domain.model.Script
import com.naamalu.app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserSessionRepository {
    fun observeProfile(): Flow<UserProfile>

    suspend fun signUp(phone: String, password: String)

    suspend fun logIn(phone: String, password: String): Boolean

    suspend fun setIshtaDevata(deityId: String?)

    suspend fun setPreferences(defaultScript: Script, prefersDarkTheme: Boolean)

    suspend fun completeOnboarding()

    suspend fun dismissFirstRunNudge()

    suspend fun logOut()

    fun currentProfile(): UserProfile
}
