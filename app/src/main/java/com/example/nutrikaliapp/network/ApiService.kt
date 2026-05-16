package com.example.nutrikaliapp.network

import com.example.nutrikaliapp.Food
import com.example.nutrikaliapp.FoodSearchResponse
import com.example.nutrikaliapp.FoodsResponse
import com.example.nutrikaliapp.models.AuthResponse
import com.example.nutrikaliapp.models.LoginRequest
import com.example.nutrikaliapp.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ----- Auth -----
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getProfile(): AuthResponse

    // ----- Alimentos -----

    suspend fun getFoods(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): FoodsResponse

    @GET("api/alimentos/buscar")
    suspend fun searchFoods(@Query("q") query: String): FoodSearchResponse

    @GET("api/alimentos/{id}")
    suspend fun getFoodById(@Path("id") id: Int): FoodDetailResponse

    // ----- Health check (opcional) -----
    @GET("health")
    suspend fun getHealth(): Response<Map<String, Any>>

    // ----- Actualizar perfil -----
    @PUT("api/usuarios/{id}")
    suspend fun updateProfile(
        @Path("id") userId: Int,
        @Body user: Map<String, Any>
    ): AuthResponse


}

// Respuesta para un solo alimento
data class FoodDetailResponse(
    val success: Boolean,
    val data: Food
)

