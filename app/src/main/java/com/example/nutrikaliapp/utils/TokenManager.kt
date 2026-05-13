package com.example.nutrikaliapp.utils

import android.content.Context
import android.content.SharedPreferences

class TokenManager(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("nutrikali_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "jwt_token"
        private const val USER_EMAIL_KEY = "user_email"
        private const val USER_NAME_KEY = "user_name"
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun saveUserInfo(email: String, name: String?) {
        sharedPreferences.edit()
            .putString(USER_EMAIL_KEY, email)
            .putString(USER_NAME_KEY, name ?: "")
            .apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString(USER_EMAIL_KEY, null)
    }

    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}

