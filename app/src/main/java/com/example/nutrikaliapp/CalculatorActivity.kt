package com.example.nutrikaliapp

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrikaliapp.databinding.ActivityCalculatorBinding
import com.example.nutrikaliapp.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class CalculatorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalculatorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokenManager.init(applicationContext)

        // Cargar datos del perfil si existen
        loadUserData()

        // Configurar spinners
        setupSpinners()

        // Botón calcular
        binding.calculateButton.setOnClickListener {
            calcularYMostrar()
        }

        // Navegación
        binding.backButton.setOnClickListener { finish() }
        binding.userButton.setOnClickListener {
            startActivity(android.content.Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupSpinners() {
        // Género
        val genderAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.gender_options,
            android.R.layout.simple_spinner_item
        )
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = genderAdapter

        // Actividad
        val activityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.activity_options,
            android.R.layout.simple_spinner_item
        )
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.activitySpinner.adapter = activityAdapter

        // Objetivo
        val goalAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.goal_options,
            android.R.layout.simple_spinner_item
        )
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.goalSpinner.adapter = goalAdapter
    }

    private fun loadUserData() {
        val user = TokenManager.getCurrentUser()
        if (user != null) {
            binding.weightEditText.setText(user.peso.toString())
            binding.heightEditText.setText(user.estatura.toString())
            binding.ageEditText.setText(user.edad.toString())
            // Mapear objetivo del perfil al spinner
            val goalPosition = when (user.objetivo?.lowercase()) {
                "bajar peso", "perder peso" -> 0
                "mantener peso" -> 1
                "subir peso", "ganar peso", "aumentar peso" -> 2
                else -> 1
            }
            binding.goalSpinner.setSelection(goalPosition)
        }
    }

    private fun calcularYMostrar() {
        // Obtener valores
        val pesoStr = binding.weightEditText.text.toString()
        val alturaStr = binding.heightEditText.text.toString()
        val edadStr = binding.ageEditText.text.toString()

        if (pesoStr.isEmpty() || alturaStr.isEmpty() || edadStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val peso = pesoStr.toDoubleOrNull()
        val alturaCm = alturaStr.toDoubleOrNull()
        val edad = edadStr.toIntOrNull()

        if (peso == null || alturaCm == null || edad == null) {
            Toast.makeText(this, "Valores no válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val genero = binding.genderSpinner.selectedItemPosition // 0 = Hombre, 1 = Mujer
        val activityIndex = binding.activitySpinner.selectedItemPosition
        val goalIndex = binding.goalSpinner.selectedItemPosition

        val alturaM = alturaCm / 100.0

        // 1. IMC
        val imc = peso / (alturaM * alturaM)
        val estadoFisico = when {
            imc < 18.5 -> "Bajo peso"
            imc < 25 -> "Normal"
            imc < 30 -> "Sobrepeso"
            else -> "Obesidad"
        }

        // 2. TMB (Mifflin-St Jeor)
        val tmb = if (genero == 0) {
            10 * peso + 6.25 * alturaCm - 5 * edad + 5
        } else {
            10 * peso + 6.25 * alturaCm - 5 * edad - 161
        }

        // 3. Factor de actividad
        val factorActividad = when (activityIndex) {
            0 -> 1.2   // Sedentario
            1 -> 1.375 // Ligero
            2 -> 1.55  // Moderado
            3 -> 1.725 // Activo
            else -> 1.9 // Muy activo
        }

        var caloriasDiarias = tmb * factorActividad

        val caloriasAjuste = when (goalIndex) {
            0 -> -500.0   // Double
            1 -> 0.0      // Double
            else -> 500.0 // Double
        }
        caloriasDiarias += caloriasAjuste
        if (caloriasDiarias < 1200.0) caloriasDiarias = 1200.0  // Comparación Double con Double

        // 5. Agua diaria (35 ml/kg)
        val aguaLitros = peso * 0.035

        // 6. Peso ideal (fórmula de Devine)
        val alturaPulgadas = alturaCm / 2.54
        val pesoIdeal = if (genero == 0) {
            50.0 + 2.3 * (alturaPulgadas - 60)
        } else {
            45.5 + 2.3 * (alturaPulgadas - 60)
        }

        // Mostrar resultados
        val resultado = buildString {
            appendLine("📊 IMC: ${String.format(Locale.US, "%.1f", imc)} ($estadoFisico)")
            appendLine("🔥 Calorías diarias: ${String.format(Locale.US, "%.0f", caloriasDiarias)} kcal")
            appendLine("🎯 Objetivo: ${binding.goalSpinner.selectedItem}")
            appendLine("💧 Agua diaria: ${String.format(Locale.US, "%.1f", aguaLitros)} L")
            appendLine("⚖️ Peso ideal: ${String.format(Locale.US, "%.1f", pesoIdeal)} kg")
        }
        binding.resultTextView.text = resultado
    }
}