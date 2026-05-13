package com.example.nutrikaliapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrikaliapp.databinding.ActivityCalendarBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

    // Lista de alimentos disponibles (simulando BD)
    private val foodOptions = listOf(
        "Seleccionar", "Manzana", "Plátano", "Pechuga de pollo", "Arroz blanco",
        "Pan integral", "Huevo", "Aguacate", "Brócoli", "Salmón", "Quinoa",
        "Avena", "Yogur", "Queso fresco", "Frutos secos"
    )

    // Días de la semana
    private val daysOfWeek = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")

    companion object {
        private const val PREFS_NAME = "meal_plans"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        setupSpinners()
        loadCurrentDayPlan()

        // Botón guardar
        binding.saveButton.setOnClickListener {
            saveCurrentDayPlan()
        }

        // Botón ver plan
        binding.viewPlanButton.setOnClickListener {
            viewCurrentDayPlan()
        }

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

        // Listener para cuando cambia el día seleccionado
        binding.daySpinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                loadCurrentDayPlan()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun setupSpinners() {
        // Configurar adaptadores para todos los spinners
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val breakfastAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodOptions)
        breakfastAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val lunchAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodOptions)
        lunchAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val dinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodOptions)
        dinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val snacksAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, foodOptions)
        snacksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Configurar spinner de días
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, daysOfWeek)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.daySpinner.adapter = dayAdapter

        binding.breakfastSpinner.adapter = breakfastAdapter
        binding.lunchSpinner.adapter = lunchAdapter
        binding.dinnerSpinner.adapter = dinnerAdapter
        binding.snacksSpinner.adapter = snacksAdapter
    }

    private fun getCurrentDay(): String {
        return daysOfWeek[binding.daySpinner.selectedItemPosition]
    }

    private fun loadCurrentDayPlan() {
        val day = getCurrentDay()
        val json = sharedPrefs.getString(day, null)

        if (json != null) {
            val type = object : TypeToken<MealPlan>() {}.type
            val plan: MealPlan = gson.fromJson(json, type)

            // Establecer selecciones
            setSpinnerSelection(binding.breakfastSpinner, plan.breakfast)
            setSpinnerSelection(binding.lunchSpinner, plan.lunch)
            setSpinnerSelection(binding.dinnerSpinner, plan.dinner)
            setSpinnerSelection(binding.snacksSpinner, plan.snacks)
        } else {
            // Resetear selecciones
            binding.breakfastSpinner.setSelection(0)
            binding.lunchSpinner.setSelection(0)
            binding.dinnerSpinner.setSelection(0)
            binding.snacksSpinner.setSelection(0)
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        if (value.isNotEmpty()) {
            val position = foodOptions.indexOf(value)
            if (position >= 0) {
                spinner.setSelection(position)
            } else {
                spinner.setSelection(0)
            }
        } else {
            spinner.setSelection(0)
        }
    }

    private fun saveCurrentDayPlan() {
        val day = getCurrentDay()
        val breakfast = binding.breakfastSpinner.selectedItem.toString()
        val lunch = binding.lunchSpinner.selectedItem.toString()
        val dinner = binding.dinnerSpinner.selectedItem.toString()
        val snacks = binding.snacksSpinner.selectedItem.toString()

        // Validar que al menos haya una selección
        if (breakfast == "Seleccionar" && lunch == "Seleccionar" && dinner == "Seleccionar" && snacks == "Seleccionar") {
            Toast.makeText(this, "Selecciona al menos una comida", Toast.LENGTH_SHORT).show()
            return
        }

        val plan = MealPlan(
            breakfast = if (breakfast != "Seleccionar") breakfast else "",
            lunch = if (lunch != "Seleccionar") lunch else "",
            dinner = if (dinner != "Seleccionar") dinner else "",
            snacks = if (snacks != "Seleccionar") snacks else ""
        )

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
