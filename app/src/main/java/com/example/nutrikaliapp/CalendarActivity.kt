package com.example.nutrikaliapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.nutrikaliapp.databinding.ActivityCalendarBinding
import com.example.nutrikaliapp.network.RetrofitClient
import com.example.nutrikaliapp.utils.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class MealPlan(
    val breakfast: String = "",
    val lunch: String = "",
    val dinner: String = "",
    val snacks: String = ""
)

class CalendarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var sharedPrefs: SharedPreferences
    private val gson = Gson()

    // Lista de alimentos disponibles (se cargará desde la API)
    private var foodNames: List<String> = listOf()
    // Item por defecto en los spinners
    private val defaultItem = "Seleccionar"

    // Días de la semana
    private val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    companion object {
        private const val PREFS_NAME = "meal_plans"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        TokenManager.init(applicationContext)
        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Verificar sesión antes de cargar alimentos (opcional)
        if (!TokenManager.isLoggedIn()) {
            Toast.makeText(this, "Sesión no válida. Inicia sesión nuevamente.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Mostrar progressBar mientras carga (si existe en el layout)
        binding.progressBar?.visibility = View.VISIBLE

        // Cargar alimentos desde la API
        loadFoodsFromApi()

        // Configurar spinners y listeners después de que se carguen los alimentos
        setupListeners()
    }

    private fun loadFoodsFromApi() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getFoods(page = 1, limit = 200) // traemos suficientes
                }
                withContext(Dispatchers.Main) {
                    binding.progressBar?.visibility = View.GONE
                    if (response.success && response.data.isNotEmpty()) {
                        // Extraer los nombres de los alimentos
                        foodNames = response.data.map { it.name }.distinct().sorted()
                        // Configurar spinners con los nombres obtenidos
                        setupSpinners()
                        // Cargar el plan del día actual después de tener spinners listos
                        loadCurrentDayPlan()
                    } else {
                        Toast.makeText(this@CalendarActivity, "No se pudieron cargar los alimentos. Usando lista por defecto.", Toast.LENGTH_LONG).show()
                        // Fallback a lista manual si la API falla
                        foodNames = listOf("Manzana", "Plátano", "Pechuga de pollo", "Arroz blanco", "Pan integral", "Huevo", "Aguacate", "Brócoli", "Salmón", "Quinoa", "Avena", "Yogur", "Queso fresco", "Frutos secos")
                        setupSpinners()
                        loadCurrentDayPlan()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar?.visibility = View.GONE
                    Toast.makeText(this@CalendarActivity, "Error al cargar alimentos: ${e.message}", Toast.LENGTH_SHORT).show()
                    // Fallback
                    foodNames = listOf("Manzana", "Plátano", "Pechuga de pollo", "Arroz blanco", "Pan integral", "Huevo", "Aguacate", "Brócoli", "Salmón", "Quinoa", "Avena", "Yogur", "Queso fresco", "Frutos secos")
                    setupSpinners()
                    loadCurrentDayPlan()
                }
            }
        }
    }

    private fun setupSpinners() {
        // Combinar el item por defecto con la lista de alimentos
        val spinnerOptions = listOf(defaultItem) + foodNames

        // Adaptador para los spinners de comidas
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.breakfastSpinner.adapter = adapter
        binding.lunchSpinner.adapter = adapter
        binding.dinnerSpinner.adapter = adapter
        binding.snacksSpinner.adapter = adapter

        // Configurar spinner de días
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.daySpinner.adapter = dayAdapter
    }

    private fun setupListeners() {
        // Listener para cuando cambia el día seleccionado
        binding.daySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadCurrentDayPlan()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }

        // Botón guardar
        binding.saveButton.setOnClickListener {
            saveCurrentDayPlan()
        }

        // Botón ver plan
        binding.viewPlanButton.setOnClickListener {
            viewCurrentDayPlan()
        }

        // Botones de navegación
        binding.backButton.setOnClickListener { finish() }
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        binding.userButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun getCurrentDay(): String = daysOfWeek[binding.daySpinner.selectedItemPosition]

    private fun loadCurrentDayPlan() {
        // Esperar a que los spinners tengan adaptador (puede que aún no estén configurados)
        if (binding.breakfastSpinner.adapter == null) return

        val day = getCurrentDay()
        val json = sharedPrefs.getString(day, null)

        if (json != null) {
            val type = object : TypeToken<MealPlan>() {}.type
            val plan: MealPlan = gson.fromJson(json, type)

            setSpinnerSelection(binding.breakfastSpinner, plan.breakfast)
            setSpinnerSelection(binding.lunchSpinner, plan.lunch)
            setSpinnerSelection(binding.dinnerSpinner, plan.dinner)
            setSpinnerSelection(binding.snacksSpinner, plan.snacks)
        } else {
            // Resetear a la opción por defecto
            setSpinnerSelection(binding.breakfastSpinner, "")
            setSpinnerSelection(binding.lunchSpinner, "")
            setSpinnerSelection(binding.dinnerSpinner, "")
            setSpinnerSelection(binding.snacksSpinner, "")
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as? ArrayAdapter<String> ?: return
        val position = if (value.isNotEmpty()) {
            adapter.getPosition(value).takeIf { it >= 0 } ?: adapter.getPosition(defaultItem)
        } else {
            adapter.getPosition(defaultItem)
        }
        spinner.setSelection(if (position >= 0) position else 0)
    }

    private fun saveCurrentDayPlan() {
        val day = getCurrentDay()

        val breakfast = if (binding.breakfastSpinner.selectedItem.toString() != defaultItem)
            binding.breakfastSpinner.selectedItem.toString() else ""
        val lunch = if (binding.lunchSpinner.selectedItem.toString() != defaultItem)
            binding.lunchSpinner.selectedItem.toString() else ""
        val dinner = if (binding.dinnerSpinner.selectedItem.toString() != defaultItem)
            binding.dinnerSpinner.selectedItem.toString() else ""
        val snacks = if (binding.snacksSpinner.selectedItem.toString() != defaultItem)
            binding.snacksSpinner.selectedItem.toString() else ""

        if (breakfast.isEmpty() && lunch.isEmpty() && dinner.isEmpty() && snacks.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos una comida", Toast.LENGTH_SHORT).show()
            return
        }

        val plan = MealPlan(breakfast, lunch, dinner, snacks)
        val json = gson.toJson(plan)
        sharedPrefs.edit().putString(day, json).apply()

        Toast.makeText(this, "Plan guardado para $day", Toast.LENGTH_SHORT).show()
    }

    private fun viewCurrentDayPlan() {
        val day = getCurrentDay()
        val json = sharedPrefs.getString(day, null)

        if (json == null) {
            Toast.makeText(this, "No hay plan guardado para $day", Toast.LENGTH_SHORT).show()
            return
        }

        val type = object : TypeToken<MealPlan>() {}.type
        val plan: MealPlan = gson.fromJson(json, type)

        val message = buildString {
            appendLine("📅 $day")
            appendLine("─────────────")
            if (plan.breakfast.isNotEmpty()) appendLine("🍳 Desayuno: ${plan.breakfast}")
            if (plan.lunch.isNotEmpty()) appendLine("🍽️ Almuerzo: ${plan.lunch}")
            if (plan.dinner.isNotEmpty()) appendLine("🍲 Comida: ${plan.dinner}")
            if (plan.snacks.isNotEmpty()) appendLine("🍎 Snacks: ${plan.snacks}")
            if (plan.breakfast.isEmpty() && plan.lunch.isEmpty() && plan.dinner.isEmpty() && plan.snacks.isEmpty()) {
                appendLine("No hay comidas registradas")
            }
        }

        android.app.AlertDialog.Builder(this)
            .setTitle("Plan del día")
            .setMessage(message)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
