package com.example.nutrikaliapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nutrikaliapp.databinding.ActivitySettingsBinding
import com.google.android.material.button.MaterialButton

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_REMINDERS = "reminders"
        private const val KEY_SOUNDS = "sounds"

        // Constantes para temas
        const val THEME_GREEN = "green"
        const val THEME_BLUE = "blue"
        const val THEME_ORANGE = "orange"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Cargar preferencias guardadas
        loadSettings()

        // Vista previa del tamaño de fuente
        binding.fontSizeSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                val fontSize = (progress + 10).toFloat()
                binding.fontSizePreview.textSize = fontSize
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        // Botón guardar
        binding.saveButton.setOnClickListener {
            saveSettings()
        }

        // Botón de retroceso
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSettings() {
        // Cargar tema guardado
        val savedTheme = sharedPrefs.getString(KEY_THEME, THEME_GREEN)
        when (savedTheme) {
            THEME_GREEN -> binding.themeGreen.isChecked = true
            THEME_BLUE -> binding.themeBlue.isChecked = true
            THEME_ORANGE -> binding.themeOrange.isChecked = true
        }

        // Cargar tamaño de fuente
        val fontSize = sharedPrefs.getInt(KEY_FONT_SIZE, 16)
        binding.fontSizeSeekBar.progress = fontSize - 10
        binding.fontSizePreview.textSize = fontSize.toFloat()

        // Cargar switches
        binding.reminderSwitch.isChecked = sharedPrefs.getBoolean(KEY_REMINDERS, true)
        binding.soundSwitch.isChecked = sharedPrefs.getBoolean(KEY_SOUNDS, true)
    }

    private fun saveSettings() {
        val editor = sharedPrefs.edit()

        // Guardar tema seleccionado
        val selectedThemeId = binding.themeRadioGroup.checkedRadioButtonId
        val theme = when (selectedThemeId) {
            R.id.themeGreen -> THEME_GREEN
            R.id.themeBlue -> THEME_BLUE
            R.id.themeOrange -> THEME_ORANGE
            else -> THEME_GREEN
        }
        editor.putString(KEY_THEME, theme)

        // Aplicar tema (cambia colores de la barra superior y botones)
        applyTheme(theme)

        // Guardar tamaño de fuente
        val fontSize = binding.fontSizeSeekBar.progress + 10
        editor.putInt(KEY_FONT_SIZE, fontSize)

        // Aplicar tamaño de fuente global
        applyFontSize(fontSize)

        // Guardar estado de switches
        editor.putBoolean(KEY_REMINDERS, binding.reminderSwitch.isChecked)
        editor.putBoolean(KEY_SOUNDS, binding.soundSwitch.isChecked)

        editor.apply()

        Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show()
    }

    private fun applyTheme(theme: String) {
        val topBarColor = when (theme) {
            THEME_GREEN -> "#38C958"
            THEME_BLUE -> "#2196F3"
            THEME_ORANGE -> "#FF9800"
            else -> "#38C958"
        }

        val buttonColor = when (theme) {
            THEME_GREEN -> "#AEE637"
            THEME_BLUE -> "#64B5F6"
            THEME_ORANGE -> "#FFB74D"
            else -> "#AEE637"
        }

        // Cambiar color de la barra superior
        binding.topBar.setBackgroundColor(Color.parseColor(topBarColor))

        // Cambiar color del botón guardar
        binding.saveButton.backgroundTintList = ContextCompat.getColorStateList(this, android.R.color.transparent)
        binding.saveButton.setBackgroundColor(Color.parseColor(buttonColor))
    }

    private fun applyFontSize(size: Int) {
        // Esto afectaría a toda la app, pero por ahora solo cambiamos la vista previa
        // Idealmente, usarías un tema base en Application
        binding.fontSizePreview.textSize = size.toFloat()
    }
}
