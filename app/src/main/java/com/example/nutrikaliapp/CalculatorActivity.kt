package com.example.nutrikaliapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrikaliapp.databinding.ActivityCalculatorBinding

data class FoodItem(
    val name: String,
    val calories: Double,      // kcal por 100g
    val protein: Double,       // proteínas por 100g
    val carbs: Double,         // carbohidratos por 100g
    val fat: Double,           // grasas por 100g
    val unit: String = "100g"
)

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding
    private lateinit var foodList: List<FoodItem>
    private var selectedFood: FoodItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Datos simulados (más completos, como si vinieran de BD)
        foodList = listOf(
            FoodItem("Manzana", 52.0, 0.3, 14.0, 0.2),
            FoodItem("Plátano", 89.0, 1.1, 23.0, 0.3),
            FoodItem("Pechuga de pollo", 165.0, 31.0, 0.0, 3.6),
            FoodItem("Arroz blanco", 130.0, 2.7, 28.0, 0.3),
            FoodItem("Pan integral", 265.0, 9.0, 49.0, 4.0),
            FoodItem("Huevo", 155.0, 13.0, 1.1, 11.0),
            FoodItem("Aguacate", 160.0, 2.0, 8.5, 14.7),
            FoodItem("Brócoli", 34.0, 2.8, 6.6, 0.4),
            FoodItem("Salmón", 208.0, 20.0, 0.0, 13.0),
            FoodItem("Quinoa", 120.0, 4.4, 21.3, 1.9)
        )

        // Configurar Spinner
        val foodNames = foodList.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.foodSpinner.adapter = adapter

        // Seleccionar primer elemento por defecto
        selectedFood = foodList.firstOrNull()
        if (selectedFood != null) {
            updateNutrientDisplay(selectedFood!!, 100.0)
        }

        // Listener del Spinner
        binding.foodSpinner.setOnItemClickListener { _, _, position, _ ->
            selectedFood = foodList[position]
            val quantity = binding.quantityEditText.text.toString().toDoubleOrNull() ?: 100.0
            updateNutrientDisplay(selectedFood!!, quantity)
        }

        // Botón calcular
        binding.calculateButton.setOnClickListener {
            val quantityText = binding.quantityEditText.text.toString()
            if (quantityText.isEmpty()) {
                Toast.makeText(this, "Ingresa la cantidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val quantity = quantityText.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Cantidad no válida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val food = selectedFood
            if (food == null) {
                Toast.makeText(this, "Selecciona un alimento", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateNutrientDisplay(food, quantity)
        }

        // Botón de retroceso
        binding.backButton.setOnClickListener {
            finish()
        }

        // Botón usuario
        binding.userButton.setOnClickListener {
            Toast.makeText(this, "Perfil (próximamente)", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNutrientDisplay(food: FoodItem, quantityGrams: Double) {
        val factor = quantityGrams / 100.0
        val calories = food.calories * factor
        val protein = food.protein * factor
        val carbs = food.carbs * factor
        val fat = food.fat * factor

        val resultText = buildString {
            appendLine("🍽️ ${food.name}")
            appendLine("──────────────")
            appendLine("🔥 Calorías: ${String.format("%.1f", calories)} kcal")
            appendLine("🥩 Proteínas: ${String.format("%.1f", protein)} g")
            appendLine("🍚 Carbohidratos: ${String.format("%.1f", carbs)} g")
            appendLine("🧈 Grasas: ${String.format("%.1f", fat)} g")
            appendLine("──────────────")
            appendLine("📊 Porción: $quantityGrams g")
        }
        binding.resultTextView.setText(resultText)
    }
}