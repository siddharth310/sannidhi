package com.smaranam.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.smaranam.app.domain.model.Script
import com.smaranam.app.domain.model.UserProfile
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

class UserSessionStore(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun observeProfile(): Flow<UserProfile> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(readProfile())
        }
        trySend(readProfile())
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }.distinctUntilChanged()

    fun readProfile(): UserProfile {
        val scriptName = prefs.getString(KEY_DEFAULT_SCRIPT, Script.TELUGU.name) ?: Script.TELUGU.name
        return UserProfile(
            userId = prefs.getString(KEY_USER_ID, null),
            phone = prefs.getString(KEY_PHONE, null),
            isLoggedIn = prefs.getBoolean(KEY_LOGGED_IN, false),
            ishtaDevataId = prefs.getString(KEY_ISHTA_DEVATA_ID, null),
            defaultScript = runCatching { Script.valueOf(scriptName) }.getOrDefault(Script.TELUGU),
            prefersDarkTheme = prefs.getBoolean(KEY_DARK_THEME, false),
            isOnboardingComplete = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false),
            showFirstRunNudge = prefs.getBoolean(KEY_SHOW_FIRST_RUN_NUDGE, false),
        )
    }

    fun signUp(phone: String, password: String) {
        val salt = generateSalt()
        val userId = UUID.randomUUID().toString()
        prefs.edit {
            putBoolean(KEY_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_PHONE, phone)
            putString(KEY_PASSWORD_HASH, hashPassword(password, salt))
            putString(KEY_PASSWORD_SALT, salt)
            putBoolean(KEY_ONBOARDING_COMPLETE, false)
            putBoolean(KEY_SHOW_FIRST_RUN_NUDGE, false)
            remove(KEY_ISHTA_DEVATA_ID)
        }
    }

    fun logIn(phone: String, password: String): Boolean {
        val storedPhone = prefs.getString(KEY_PHONE, null) ?: return false
        val storedHash = prefs.getString(KEY_PASSWORD_HASH, null) ?: return false
        val salt = prefs.getString(KEY_PASSWORD_SALT, null) ?: return false
        if (storedPhone != phone) return false
        if (hashPassword(password, salt) != storedHash) return false
        prefs.edit { putBoolean(KEY_LOGGED_IN, true) }
        return true
    }

    fun setIshtaDevata(deityId: String?) {
        prefs.edit {
            if (deityId == null) {
                remove(KEY_ISHTA_DEVATA_ID)
            } else {
                putString(KEY_ISHTA_DEVATA_ID, deityId)
            }
        }
    }

    fun setPreferences(defaultScript: Script, prefersDarkTheme: Boolean) {
        prefs.edit {
            putString(KEY_DEFAULT_SCRIPT, defaultScript.name)
            putBoolean(KEY_DARK_THEME, prefersDarkTheme)
        }
    }

    fun completeOnboarding() {
        prefs.edit {
            putBoolean(KEY_ONBOARDING_COMPLETE, true)
            putBoolean(KEY_SHOW_FIRST_RUN_NUDGE, true)
        }
    }

    fun dismissFirstRunNudge() {
        prefs.edit { putBoolean(KEY_SHOW_FIRST_RUN_NUDGE, false) }
    }

    fun logOut() {
        prefs.edit { clear() }
    }

    private fun hashPassword(password: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest("$salt$password".toByteArray())
        return hash.joinToString("") { byte -> "%02x".format(byte) }
    }

    private fun generateSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)
        return bytes.joinToString("") { byte -> "%02x".format(byte) }
    }

    companion object {
        private const val PREFS_NAME = "smaranam_user_session"
        private const val KEY_LOGGED_IN = "logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_PHONE = "phone"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_PASSWORD_SALT = "password_salt"
        private const val KEY_ISHTA_DEVATA_ID = "ishta_devata_id"
        private const val KEY_DEFAULT_SCRIPT = "default_script"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
        private const val KEY_SHOW_FIRST_RUN_NUDGE = "show_first_run_nudge"
    }
}
