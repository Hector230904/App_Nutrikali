package com.example.nutrikaliapp.network

import com.example.nutrikaliapp.Food
import com.example.nutrikaliapp.models.AuthResponse
import com.example.nutrikaliapp.models.LoginRequest
import com.example.nutrikaliapp.models.RegisterRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ==================== AUTH ====================

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @GET("api/auth/me")
    suspend fun getProfile(): AuthResponse  // Token se agrega automáticamente por el interceptor

    // ==================== ALIMENTOS ====================

    @GET("api/alimentos")
    suspend fun getFoods(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): FoodsResponse

    @GET("api/alimentos/buscar")
    suspend fun searchFoods(@Query("q") query: String): FoodSearchResponse

    @GET("api/alimentos/{id}")
    suspend fun getFoodById(@Path("id") id: Int): FoodDetailResponse

    // ==================== RECETAS ====================

    @GET("api/recetas")
    suspend fun getRecetas(): RecetasResponse

    @GET("api/recetas/{id}")
    suspend fun getRecetaById(@Path("id") id: Int): RecetaDetailResponse

    @POST("api/recetas")
    suspend fun createReceta(@Body receta: CreateRecetaRequest): RecetaResponse

    // ==================== REGISTROS DIARIOS ====================

    @GET("api/registros/hoy")
    suspend fun getRegistroHoy(): RegistroHoyResponse

    @POST("api/registros")
    suspend fun createRegistro(@Body registro: CreateRegistroRequest): RegistroResponse

    @GET("api/registros/semana")
    suspend fun getRegistrosSemana(): RegistrosResponse

    // ==================== PLANES ====================

    @GET("api/planes/activo")
    suspend fun getPlanActivo(): PlanActivoResponse

    @POST("api/planes")
    suspend fun createPlan(@Body plan: CreatePlanRequest): PlanResponse

    // ==================== USUARIOS ====================

    @PUT("api/usuarios/{id}")
    suspend fun updateProfile(
        @Path("id") userId: Int,
        @Body user: Map<String, Any>
    ): AuthResponse

    // ==================== HEALTH ====================

    @GET("health")
    suspend fun getHealth(): Response<Map<String, Any>>
}

// ==================== MODELOS DE RESPUESTA ====================

// Alimentos
data class FoodsResponse(
    val success: Boolean,
    val data: List<Food>,
    val pagination: PaginationInfo?
)

data class FoodSearchResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Food>
)

data class FoodDetailResponse(
    val success: Boolean,
    val data: Food
)

data class PaginationInfo(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int
)

// Recetas
data class RecetasResponse(
    val success: Boolean,
    val count: Int,
    val data: List<Receta>
)

data class RecetaDetailResponse(
    val success: Boolean,
    val data: Receta
)

data class RecetaResponse(
    val success: Boolean,
    val message: String,
    val data: Receta
)

data class CreateRecetaRequest(
    val nombre: String,
    val descripcion: String? = null,
    val tiempo_preparacion: Int? = null,
    val imagen_url: String? = null,
    val ingredientes: List<IngredienteRequest>? = null
)

data class IngredienteRequest(
    val id_alimento: Int,
    val cantidad: Double
)

// Registros Diarios
data class RegistroHoyResponse(
    val success: Boolean,
    val data: RegistroHoyData?
)

data class RegistroHoyData(
    val registro: RegistroDiario?,
    val meta_calorias: Int?,
    val porcentaje: Int?
)

data class RegistroResponse(
    val success: Boolean,
    val message: String,
    val data: RegistroDiario
)

data class RegistrosResponse(
    val success: Boolean,
    val data: RegistrosData
)

data class RegistrosData(
    val registros: List<RegistroDiario>,
    val resumen: ResumenSemanal
)

data class CreateRegistroRequest(
    val fecha: String,  // Formato: "2024-01-15"
    val calorias_consumidas: Int
)

// Planes
data class PlanActivoResponse(
    val success: Boolean,
    val data: PlanActivo?
)

data class PlanActivo(
    val id_plan: Int,
    val calorias_diarias: Int,
    val fecha_inicio: String,
    val fecha_fin: String
)

data class PlanResponse(
    val success: Boolean,
    val message: String,
    val data: PlanActivo
)

data class CreatePlanRequest(
    val calorias_diarias: Int,
    val fecha_inicio: String,
    val fecha_fin: String
)

// ==================== MODELOS DE DATOS ====================

data class Receta(
    val id_receta: Int,
    val nombre: String,
    val descripcion: String?,
    val tiempo_preparacion: Int?,
    val imagen_url: String?,
    val calorias_totales: Int?,
    val recetaAlimentos: List<RecetaIngrediente>?
)

data class RecetaIngrediente(
    val id_alimento: Int,
    val cantidad: Double,
    val alimento: Food
)

data class RegistroDiario(
    val id_registro: Int,
    val fecha: String,
    val calorias_consumidas: Int
)

data class ResumenSemanal(
    val total_dias: Int,
    val total_calorias: Int,
    val promedio_diario: Int,
    val desde: String,
    val hasta: String
)