package com.example.nutrikaliapp.models

import com.google.gson.annotations.SerializedName

// ------ Auth ------
data class LoginRequest(
    @SerializedName("correo") val email: String,
    @SerializedName("contraseña") val password: String
)

data class RegisterRequest(
    @SerializedName("nombre") val name: String,
    @SerializedName("correo") val email: String,
    @SerializedName("contraseña") val password: String,
    @SerializedName("edad") val age: Int? = null,
    @SerializedName("peso") val weight: Double? = null,
    @SerializedName("estatura") val height: Double? = null,
    @SerializedName("objetivo") val goal: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val usuario: UserData?,
    // para errores
    val error: String? = null
)

data class UserData(
    @SerializedName("id_usuario") val id: Int,
    @SerializedName("nombre") val name: String,
    @SerializedName("correo") val email: String,
    val edad: Int?,
    val peso: Double?,
    val estatura: Double?,
    val objetivo: String?
)

