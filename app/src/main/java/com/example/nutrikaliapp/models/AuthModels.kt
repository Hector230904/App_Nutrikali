package com.example.nutrikaliapp.models

data class LoginRequest(
    val correo: String,
    val contraseña: String
)

data class RegisterRequest(
    val nombre: String,
    val correo: String,
    val contraseña: String,
    val edad: Int,
    val peso: Double,
    val estatura: Double,
    val objetivo: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val usuario: User?
)

data class User(
    val id_usuario: Int,
    val nombre: String,
    val correo: String,
    val edad: Int,
    val peso: Double,
    val estatura: Double,
    val objetivo: String?
)
