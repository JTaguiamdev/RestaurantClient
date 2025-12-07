package com.orderly.data

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(private val prefs: SharedPreferences) {

    fun saveToken(token: String) {
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun deleteToken() {
        prefs.edit().remove(TOKEN_KEY).apply()
    }

    companion object {
        private const val TOKEN_KEY = "auth_token"
    }
}