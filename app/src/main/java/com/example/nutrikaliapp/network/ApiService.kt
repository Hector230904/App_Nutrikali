package com.example.nutrikaliapp.network

import com.example.nutrikaliapp.models.AuthResponse
import com.example.nutrikaliapp.models.LoginRequest
import com.example.nutrikaliapp.models.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
