package com.example.nutrikaliapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nutrikaliapp.adapters.FoodAdapter
import com.example.nutrikaliapp.databinding.ActivityDietsBinding
import com.example.nutrikaliapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DietsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDietsBinding
    private lateinit var foodAdapter: FoodAdapter
    private var searchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDietsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener texto de búsqueda desde el Intent (enviado por HomeActivity)
        searchQuery = intent.getStringExtra("search_query") ?: ""

        // Configurar título según si hay búsqueda
        if (searchQuery.isNotEmpty()) {
            binding.titleTextView.text = getString(R.string.search_results_title, searchQuery)
        } else {
            binding.titleTextView.text = getString(R.string.diets_title)
        }

        // Configurar RecyclerView
        setupRecyclerView()

        // Cargar alimentos desde la API
        loadFoods()

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
        // Inicialmente vacío, luego se actualizará con los datos de la API
        foodAdapter = FoodAdapter(emptyList()) { food ->
            mostrarDetalleAlimento(food)
        }

        binding.foodsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.foodsRecyclerView.adapter = foodAdapter
        binding.foodsRecyclerView.setHasFixedSize(true)
    }

    private fun loadFoods() {
        // Mostrar progreso
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Elegir endpoint según si hay búsqueda
                if (searchQuery.isNotEmpty()) {
                    val response = RetrofitClient.apiService.searchFoods(searchQuery)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        if (response.success) {
                            foodAdapter.updateData(response.data)
                            if (response.data.isEmpty()) {
                                Toast.makeText(
                                    this@DietsActivity,
                                    getString(R.string.no_search_results, searchQuery),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            Toast.makeText(this@DietsActivity, R.string.error_loading_foods, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Carga paginada (página 1, 50 elementos)
                    val response = RetrofitClient.apiService.getFoods(page = 1, limit = 50)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.GONE
                        if (response.success) {
                            foodAdapter.updateData(response.data)
                        } else {
                            Toast.makeText(this@DietsActivity, R.string.error_loading_foods, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@DietsActivity,
                        "${getString(R.string.error_network)}: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
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