package com.example.nutrikaliapp.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.nutrikaliapp.utils.TokenManager
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Cambia esta URL según corresponda:
    // - Emulador: "http://10.0.2.2:3000/"
    // - Dispositivo físico: "http://TU_IP_LOCAL:3000/"
    // - Producción: "https://tudominio.com/"
    private const val BASE_URL = "http://192.168.100.92:3000/"  // Para emulador Android Studio

    // Interceptor para agregar el token a cada petición
    private val authInterceptor = Interceptor { chain ->
        val token = TokenManager.token
        val request = chain.request().newBuilder()
            .addHeader("Content-Type", "application/json")
            .apply {
                if (!token.isNullOrEmpty()) {
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()
        chain.proceed(request)
    }

    // Interceptor para logging (debug)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Cliente OkHttp con interceptores
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Instancia de Retrofit
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}