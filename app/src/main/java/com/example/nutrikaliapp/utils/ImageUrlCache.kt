package com.example.nutrikaliapp.utils

import com.example.nutrikaliapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageUrlCache {
    private val cache = mutableMapOf<String, String>()

    suspend fun getImageUrl(imageKey: String): String? {
        cache[imageKey]?.let { return it }

        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.apiService.getImageUrl(imageKey)
            }
            if (response.success) {
                cache[imageKey] = response.imageUrl
                response.imageUrl
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}