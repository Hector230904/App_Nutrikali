package com.example.nutrikaliapp.utils

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREFS_NAME = "nutrikali_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Token JWT
    var token: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_TOKEN, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_TOKEN, value).apply()
            }
        }

    // Email del usuario
    var userEmail: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_USER_EMAIL, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_USER_EMAIL, value).apply()
            }
        }

    // Nombre del usuario
    var userName: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_USER_NAME, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_USER_NAME, value).apply()
            }
        }

    // Verificar si hay sesión iniciada
    fun isLoggedIn(): Boolean = !token.isNullOrEmpty()

    // Limpiar todo (logout)
    fun clear() {
        prefs.edit().clear().apply()
    }
}

