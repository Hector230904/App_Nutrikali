package com.example.nutrikaliapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nutrikaliapp.databinding.ActivityHomeBinding
import com.example.nutrikaliapp.utils.TokenManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)

        if (!tokenManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Saludo personalizado
        val userEmail = tokenManager.getUserEmail()
        val userName = userEmail?.split("@")?.first() ?: "Usuario"
        binding.greetingTextView.text = getString(R.string.greeting_message, userName)

        //  Búsqueda: al hacer clic en el ícono, abre DietsActivity con el texto de búsqueda
        binding.searchIcon.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isEmpty()) {
                Toast.makeText(this, "Escribe algo para buscar", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, DietsActivity::class.java)
                intent.putExtra("search_query", query)
                startActivity(intent)
            }
        }

        // También permitir buscar al presionar "Enter" en el teclado
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    val intent = Intent(this, DietsActivity::class.java)
                    intent.putExtra("search_query", query)
                    startActivity(intent)
                }
                true
            } else {
                false
            }
        }

        // Botones superiores
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        binding.userButton.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Botones de acceso rápido
        binding.btnCalculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }
        binding.btnDiets.setOnClickListener {
            startActivity(Intent(this, DietsActivity::class.java))
        }
        binding.btnCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        // Manejo del botón de retroceso (cierra sesión y sale)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.exit_title)
            .setMessage(R.string.exit_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                tokenManager.clearAll()
                finishAffinity()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun redirectToLogin() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}