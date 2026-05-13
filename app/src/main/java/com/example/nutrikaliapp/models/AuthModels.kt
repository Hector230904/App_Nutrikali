package com.example.nutrikaliapp.models

import com.example.nutrikaliapp.UserData

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String? = null
)

data class AuthResponse(
    val message: String,
    val token: String,
    val user: UserData
)

data class UserData(
    val id: Int,
    val email: String,
    val name: String?
)

data class AuthError(
    val error: String
)

