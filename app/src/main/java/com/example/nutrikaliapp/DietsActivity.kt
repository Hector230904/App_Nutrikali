package com.example.nutrikaliapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutrikaliapp.adapters.FoodAdapter
import com.example.nutrikaliapp.databinding.ActivityDietsBinding

class DietsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDietsBinding
    private lateinit var foodAdapter: FoodAdapter
    private var searchQuery: String = ""

    // Datos completos (simulando BD)
    private val allFoods = listOf(
        Food(1, "Pollo a la plancha", 165.0, 31.0, 0.0, 3.6),
        Food(2, "Arroz blanco", 130.0, 2.7, 28.0, 0.3),
        Food(3, "Manzana", 52.0, 0.3, 14.0, 0.2),
        Food(4, "Plátano", 89.0, 1.1, 23.0, 0.3),
        Food(5, "Salmón", 208.0, 20.0, 0.0, 13.0),
        Food(6, "Aguacate", 160.0, 2.0, 8.5, 14.7),
        Food(7, "Brócoli", 34.0, 2.8, 6.6, 0.4),
        Food(8, "Quinoa", 120.0, 4.4, 21.3, 1.9),
        Food(9, "Huevo", 155.0, 13.0, 1.1, 11.0),
        Food(10, "Pan integral", 265.0, 9.0, 49.0, 4.0),
        Food(11, "Avena", 389.0, 16.9, 66.3, 6.9),
        Food(12, "Yogur griego", 150.0, 4.5, 23.0, 4.0),
        Food(13, "Pechuga de pavo", 135.0, 29.0, 0.0, 2.0),
        Food(14, "Lentejas", 116.0, 9.0, 20.0, 0.4),
        Food(15, "Nueces", 654.0, 15.0, 14.0, 65.0),
        Food(16, "Queso fresco", 98.0, 11.0, 2.0, 5.0),
        Food(17, "Pizza", 266.0, 11.0, 33.0, 10.0),
        Food(18, "Hamburguesa", 295.0, 17.0, 26.0, 15.0),
        Food(19, "Ensalada", 180.0, 8.0, 12.0, 12.0),
        Food(20, "Sopa de verduras", 75.0, 2.0, 15.0, 1.0),
        Food(21, "Batido de proteínas", 120.0, 24.0, 4.0, 1.5),
        Food(22, "Tofu", 76.0, 8.0, 2.0, 4.8),
        Food(23, "Garbanzos", 139.0, 8.0, 23.0, 2.5),
        Food(24, "Pasta integral", 124.0, 5.0, 26.0, 1.0)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener texto de búsqueda desde el Intent
        searchQuery = intent.getStringExtra("search_query") ?: ""

        // Si hay búsqueda, mostrar título de búsqueda en la barra superior
        if (searchQuery.isNotEmpty()) {
            binding.titleTextView.text = getString(R.string.search_results_title, searchQuery)
        }

        setupRecyclerView()

        // Botones de navegación
        binding.backButton.setOnClickListener {
            finish()
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.userButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        // Filtrar alimentos según la búsqueda (si existe)
        val filteredFoods = if (searchQuery.isNotEmpty()) {
            allFoods.filter { food ->
                food.name.contains(searchQuery, ignoreCase = true)
            }
        } else {
            allFoods
        }

        foodAdapter = FoodAdapter(filteredFoods) { food ->
            mostrarDetalleAlimento(food)
        }

        binding.foodsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.foodsRecyclerView.adapter = foodAdapter
        binding.foodsRecyclerView.setHasFixedSize(true)

        // Mostrar mensaje si no hay resultados
        if (filteredFoods.isEmpty()) {
            android.widget.Toast.makeText(
                this,
                getString(R.string.no_search_results, searchQuery),
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun mostrarDetalleAlimento(food: Food) {
        val mensaje = buildString {
            appendLine("🍽️ ${food.name}")
            appendLine("─────────────")
            appendLine("🔥 Calorías: ${food.calories} kcal")
            appendLine("🥩 Proteínas: ${food.protein} g")
            appendLine("🍚 Carbohidratos: ${food.carbs} g")
            appendLine("🧈 Grasas: ${food.fat} g")
            appendLine("─────────────")
            appendLine(getString(R.string.nutrition_info))
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.food_details_title)
            .setMessage(mensaje)
            .setPositiveButton(R.string.accept, null)
            .show()
    }
}