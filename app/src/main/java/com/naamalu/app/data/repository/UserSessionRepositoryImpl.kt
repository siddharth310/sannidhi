package com.naamalu.app.data.repository

import com.naamalu.app.data.local.UserSessionStore
import com.naamalu.app.domain.model.Script
import com.naamalu.app.domain.model.UserProfile
import com.naamalu.app.domain.repository.UserSessionRepository
import kotlinx.coroutines.flow.Flow

class UserSessionRepositoryImpl(
    private val store: UserSessionStore,
) : UserSessionRepository {

    override fun observeProfile(): Flow<UserProfile> = store.observeProfile()

    override suspend fun signUp(phone: String, password: String) {
        store.signUp(phone, password)
    }

    override suspend fun logIn(phone: String, password: String): Boolean {
        return store.logIn(phone, password)
    }

    override suspend fun setIshtaDevata(deityId: String?) {
        store.setIshtaDevata(deityId)
    }

    override suspend fun setPreferences(defaultScript: Script, prefersDarkTheme: Boolean) {
        store.setPreferences(defaultScript, prefersDarkTheme)
    }

    override suspend fun completeOnboarding() {
        store.completeOnboarding()
    }

    override suspend fun dismissFirstRunNudge() {
        store.dismissFirstRunNudge()
    }

    override suspend fun logOut() {
        store.logOut()
    }

    override fun currentProfile(): UserProfile = store.readProfile()
}
