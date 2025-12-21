package com.example.rumahistimewa.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserSession {
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    var currentUserRole: String? = null
    var currentUserId: String? = null
    private const val PREF_NAME = "user_session"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_ROLE = "user_role"
    private const val KEY_ID = "user_id"

    private lateinit var preferences: android.content.SharedPreferences

    fun init(context: android.content.Context) {
        preferences = context.getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
        val token = preferences.getString(KEY_TOKEN, null)
        val role = preferences.getString(KEY_ROLE, null)
        val id = preferences.getString(KEY_ID, null)

        if (token != null && role != null && id != null) {
            _isLoggedIn.value = true
            currentUserRole = role
            currentUserId = id
        }
    }

    var token: String?
        get() = preferences.getString(KEY_TOKEN, null)
        private set(value) {
            preferences.edit().putString(KEY_TOKEN, value).apply()
        }

    var afterLoginDestination: String? = null

    fun login(role: String, userId: String, authToken: String) {
        _isLoggedIn.value = true
        currentUserRole = role
        currentUserId = userId
        
        preferences.edit()
            .putString(KEY_TOKEN, authToken)
            .putString(KEY_ROLE, role)
            .putString(KEY_ID, userId)
            .apply()
    }

    fun logout() {
        _isLoggedIn.value = false
        currentUserRole = null
        currentUserId = null
        afterLoginDestination = null
        
        preferences.edit().clear().apply()
    }
}
