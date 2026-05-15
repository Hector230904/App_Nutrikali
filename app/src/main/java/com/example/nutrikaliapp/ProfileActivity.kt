package com.example.nutrikaliapp

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrikaliapp.databinding.ActivityProfileBinding
import com.example.nutrikaliapp.utils.TokenManager

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "user_measurements"
        private const val KEY_WEIGHT = "weight"
        private const val KEY_HEIGHT = "height"
        private const val KEY_NOTES = "notes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ya no se instancia TokenManager, usamos el objeto directamente
        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Cargar datos del usuario logueado
        loadUserData()

        // Cargar datos guardados localmente
        loadLocalData()

        // Botón de retroceso
        binding.backButton.setOnClickListener {
            finish()
        }

        // Guardar medidas corporales
        binding.saveMeasurementsButton.setOnClickListener {
            saveMeasurements()
        }

        // Guardar notas
        binding.saveNotesButton.setOnClickListener {
            saveNotes()
        }
    }

    private fun loadUserData() {
        // Obtenemos los datos directamente del TokenManager
        val userEmail = TokenManager.userEmail ?: "correo@ejemplo.com"
        val userName = TokenManager.userName ?: userEmail.split("@").first()

        binding.userNameTextView.text = userName
        binding.emailEditText.setText(userEmail)

        // Contraseña oculta (no se guarda en TokenManager por seguridad)
        binding.passwordEditText.setText("********")
    }

    private fun loadLocalData() {
        val weight = sharedPrefs.getFloat(KEY_WEIGHT, 0f)
        val height = sharedPrefs.getFloat(KEY_HEIGHT, 0f)
        val notes = sharedPrefs.getString(KEY_NOTES, "") ?: ""

        if (weight > 0) binding.weightEditText.setText(weight.toString())
        if (height > 0) binding.heightEditText.setText(height.toString())
        binding.notesEditText.setText(notes)
    }

    private fun saveMeasurements() {
        val weightText = binding.weightEditText.text.toString()
        val heightText = binding.heightEditText.text.toString()

        if (weightText.isEmpty() && heightText.isEmpty()) {
            Toast.makeText(this, "Ingresa al menos un dato", Toast.LENGTH_SHORT).show()
            return
        }

        val editor = sharedPrefs.edit()

        if (weightText.isNotEmpty()) {
            val weight = weightText.toFloatOrNull()
            if (weight != null && weight > 0) {
                editor.putFloat(KEY_WEIGHT, weight)
            } else {
                Toast.makeText(this, "Peso no válido", Toast.LENGTH_SHORT).show()
                return
            }
        }

        if (heightText.isNotEmpty()) {
            val height = heightText.toFloatOrNull()
            if (height != null && height > 0) {
                editor.putFloat(KEY_HEIGHT, height)
            } else {
                Toast.makeText(this, "Estatura no válida", Toast.LENGTH_SHORT).show()
                return
            }
        }

        editor.apply()
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }

    private fun saveNotes() {
        val notes = binding.notesEditText.text.toString()
        sharedPrefs.edit().putString(KEY_NOTES, notes).apply()
        Toast.makeText(this, "Notas guardadas", Toast.LENGTH_SHORT).show()
    }
}