package com.example.nutrikaliapp

import com.google.gson.annotations.SerializedName

data class Food(
    @SerializedName("id_alimento") val id: Int,
    @SerializedName("nombre") val name: String,
    @SerializedName("calorias") val calories: Double,
    @SerializedName("proteinas") val protein: Double,
    @SerializedName("carbohidratos") val carbs: Double,
    @SerializedName("grasas") val fat: Double,
    @SerializedName("imagen_key") val imageKey: String? = null,
    var imageUrl: String? = null
)


// Wrappers de respuesta (paginada y búsqueda)
data class FoodsResponse(
    val success: Boolean,
    val data: List<Food>,
    val pagination: Pagination?
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

data class FoodSearchResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Food>
)

data class FoodResponse(
    val id: Int,
    val name: String,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double
)

data class ImageUrlResponse(
    val success: Boolean,
    val imageUrl: String,
    val fileKey: String
)

