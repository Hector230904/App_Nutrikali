package com.example.nutrikaliapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrikaliapp.databinding.ActivityCalculatorBinding
import com.example.nutrikaliapp.network.RetrofitClient
import com.example.nutrikaliapp.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private var foodList: List<Food> = emptyList()
    private var selectedFood: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokenManager.init(applicationContext)

        loadFoodsFromApi()

        binding.calculateButton.setOnClickListener {
            val quantityText = binding.quantityEditText.text.toString()
            val weightText = binding.weightEditText.text.toString()

            if (quantityText.isEmpty()) {
                Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val quantity = quantityText.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Cantidad no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val weight = weightText.toDoubleOrNull()
            if (weight == null || weight <= 0) {
                Toast.makeText(this, "Peso no válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val food = selectedFood
            if (food == null) {
                Toast.makeText(this, "Selecciona un alimento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateNutrientDisplay(food, quantity, weight)
        }

        binding.backButton.setOnClickListener { finish() }
        binding.userButton.setOnClickListener {
            startActivity(android.content.Intent(this, ProfileActivity::class.java))
        }
    }

    private fun loadFoodsFromApi() {
        if (!TokenManager.isLoggedIn()) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE

        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getFoods(page = 1, limit = 100)
                }
                if (response.success) {
                    foodList = response.data
                    withContext(Dispatchers.Main) {
                        setupSpinner()
                        binding.progressBar.visibility = android.view.View.GONE
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = android.view.View.GONE
                        Toast.makeText(this@CalculatorActivity, "Error al cargar alimentos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    val errorMsg = if (e.code() == 401) "No autorizado. Inicia sesión nuevamente."
                    else "Error al cargar alimentos: ${e.message()}"
                    Toast.makeText(this@CalculatorActivity, errorMsg, Toast.LENGTH_LONG).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@CalculatorActivity, "Error de red. Verifica tu conexión.", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@CalculatorActivity, "Error inesperado: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        if (foodList.isEmpty()) {
            Toast.makeText(this, "No hay alimentos disponibles", Toast.LENGTH_SHORT).show()
            return
        }

        val foodNames = foodList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.foodSpinner.adapter = adapter

        selectedFood = foodList.firstOrNull()
        if (selectedFood != null) {
            // Mostrar valores por defecto con cantidad 100g y peso 70kg
            updateNutrientDisplay(selectedFood!!, 100.0, 70.0)
        }

        binding.foodSpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedFood = foodList.getOrNull(position)
                val quantity = binding.quantityEditText.text.toString().toDoubleOrNull() ?: 100.0
                val weight = binding.weightEditText.text.toString().toDoubleOrNull() ?: 70.0
                if (selectedFood != null) {
                    updateNutrientDisplay(selectedFood!!, quantity, weight)
                }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedFood = null
            }
        }
    }

    private fun updateNutrientDisplay(food: Food, quantityGrams: Double, userWeightKg: Double) {
        val factor = quantityGrams / 100.0
        val calories = food.calories * factor
        val protein = food.protein * factor
        val carbs = food.carbs * factor
        val fat = food.fat * factor

        // Proteína por kg de peso corporal
        val proteinPerKg = protein / userWeightKg

        val resultText = buildString {
            appendLine("🍽️ ${food.name}")
            appendLine("──────────────")
            appendLine("🔥 Calorías: ${String.format(Locale.US, "%.1f", calories)} kcal")
            appendLine("🥩 Proteínas: ${String.format(Locale.US, "%.1f", protein)} g")
            appendLine("🍚 Carbohidratos: ${String.format(Locale.US, "%.1f", carbs)} g")
            appendLine("🧈 Grasas: ${String.format(Locale.US, "%.1f", fat)} g")
            appendLine("──────────────")
            appendLine("📊 Porción: $quantityGrams g")
            appendLine("⚖️ Peso: ${String.format(Locale.US, "%.1f", userWeightKg)} kg")
            appendLine("🥩 Proteína / kg: ${String.format(Locale.US, "%.2f", proteinPerKg)} g/kg")
        }
        binding.resultTextView.setText(resultText)
    }
}