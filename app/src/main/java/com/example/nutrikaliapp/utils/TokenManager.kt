package com.example.nutrikaliapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.nutrikaliapp.models.User

object TokenManager {
    private const val PREFS_NAME = "nutrikali_prefs"
    private const val KEY_TOKEN = "jwt_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_AGE = "user_age"
    private const val KEY_USER_WEIGHT = "user_weight"
    private const val KEY_USER_HEIGHT = "user_height"
    private const val KEY_USER_GOAL = "user_goal"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var token: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_TOKEN, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_TOKEN, value).apply()
            }
        }

    var userEmail: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_USER_EMAIL, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_USER_EMAIL, value).apply()
            }
        }

    var userName: String?
        get() = if (::prefs.isInitialized) prefs.getString(KEY_USER_NAME, null) else null
        set(value) {
            if (::prefs.isInitialized) {
                prefs.edit().putString(KEY_USER_NAME, value).apply()
            }
        }

    fun saveUser(user: User) {
        if (!::prefs.isInitialized) return
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, user.id_usuario)
        editor.putString(KEY_USER_NAME, user.nombre)
        editor.putString(KEY_USER_EMAIL, user.correo)
        editor.putInt(KEY_USER_AGE, user.edad)
        editor.putFloat(KEY_USER_WEIGHT, user.peso.toFloat())
        editor.putFloat(KEY_USER_HEIGHT, user.estatura.toFloat())
        editor.putString(KEY_USER_GOAL, user.objetivo)
        editor.apply()
        userEmail = user.correo
        userName = user.nombre
    }

    fun getCurrentUser(): User? {
        if (!::prefs.isInitialized) return null
        val id = prefs.getInt(KEY_USER_ID, -1)
        if (id == -1) return null
        return User(
            id_usuario = id,
            nombre = prefs.getString(KEY_USER_NAME, "") ?: "",
            correo = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            edad = prefs.getInt(KEY_USER_AGE, 0),
            peso = prefs.getFloat(KEY_USER_WEIGHT, 0.0f).toDouble(),   // ← 0.0f
            estatura = prefs.getFloat(KEY_USER_HEIGHT, 0.0f).toDouble(), // ← 0.0f
            objetivo = prefs.getString(KEY_USER_GOAL, null)
        )
    }

    fun isLoggedIn(): Boolean = !token.isNullOrEmpty()

    fun clear() {
        if (::prefs.isInitialized) {
            prefs.edit().clear().apply()
        }
    }
}