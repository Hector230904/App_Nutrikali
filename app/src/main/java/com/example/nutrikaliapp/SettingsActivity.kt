package com.example.nutrikaliapp

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.nutrikaliapp.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPrefs: SharedPreferences

    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_REMINDERS = "reminders"
        private const val KEY_SOUNDS = "sounds"

        const val THEME_GREEN = "green"
        const val THEME_BLUE = "blue"
        const val THEME_ORANGE = "orange"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadSettings()
        setupListeners()
    }

    private fun setupListeners() {
        binding.fontSizeSeekBar.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                val fontSize = (progress + 10).toFloat()
                binding.fontSizePreview.textSize = fontSize
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.saveButton.setOnClickListener { saveSettings() }
        binding.backButton.setOnClickListener { finish() }
    }

    private fun loadSettings() {
        val savedTheme = sharedPrefs.getString(KEY_THEME, THEME_GREEN)
        when (savedTheme) {
            THEME_GREEN -> binding.themeGreen.isChecked = true
            THEME_BLUE -> binding.themeBlue.isChecked = true
            THEME_ORANGE -> binding.themeOrange.isChecked = true
        }

        val fontSize = sharedPrefs.getInt(KEY_FONT_SIZE, 16)
        binding.fontSizeSeekBar.progress = fontSize - 10
        binding.fontSizePreview.textSize = fontSize.toFloat()

        binding.reminderSwitch.isChecked = sharedPrefs.getBoolean(KEY_REMINDERS, true)
        binding.soundSwitch.isChecked = sharedPrefs.getBoolean(KEY_SOUNDS, true)
    }

    private fun saveSettings() {
        val editor = sharedPrefs.edit()

        val selectedThemeId = binding.themeRadioGroup.checkedRadioButtonId
        val theme = when (selectedThemeId) {
            R.id.themeGreen -> THEME_GREEN
            R.id.themeBlue -> THEME_BLUE
            R.id.themeOrange -> THEME_ORANGE
            else -> THEME_GREEN
        }
        editor.putString(KEY_THEME, theme)
        applyTheme(theme)

        val fontSize = binding.fontSizeSeekBar.progress + 10
        editor.putInt(KEY_FONT_SIZE, fontSize)
        applyFontSize(fontSize)

        editor.putBoolean(KEY_REMINDERS, binding.reminderSwitch.isChecked)
        editor.putBoolean(KEY_SOUNDS, binding.soundSwitch.isChecked)
        editor.apply()

        android.widget.Toast.makeText(this, getString(R.string.configuracion_guardada), android.widget.Toast.LENGTH_SHORT).show()
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
        binding.topBar.setBackgroundColor(Color.parseColor(topBarColor))
        binding.saveButton.setBackgroundColor(Color.parseColor(buttonColor))
    }

    private fun applyFontSize(size: Int) {
        binding.fontSizePreview.textSize = size.toFloat()
    }
}