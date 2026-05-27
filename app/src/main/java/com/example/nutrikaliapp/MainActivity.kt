package com.example.nutrikaliapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nutrikaliapp.models.LoginRequest
import com.example.nutrikaliapp.models.RegisterRequest
import com.example.nutrikaliapp.network.RetrofitClient
import com.example.nutrikaliapp.utils.TokenManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        TokenManager.init(applicationContext)

        if (!TokenManager.token.isNullOrEmpty()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val emailEdit = findViewById<TextInputEditText>(R.id.emailEditText)
        val passwordEdit = findViewById<TextInputEditText>(R.id.passwordEditText)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val registerText = findViewById<TextView>(R.id.registerTextView)
        val forgotText = findViewById<TextView>(R.id.forgotPasswordTextView)
        progressBar = findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            val email = emailEdit.text?.toString()?.trim().orEmpty()
            val password = passwordEdit.text?.toString().orEmpty()

            if (email.isEmpty()) {
                Toast.makeText(this, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            doLogin(email, password)
        }

        registerText.setOnClickListener {
            showRegisterDialog()
        }

        forgotText.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun doLogin(email: String, password: String) {
        showLoading(true)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(correo = email, contraseña = password))
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    TokenManager.token = response.token
                    TokenManager.userEmail = response.usuario?.correo
                    TokenManager.userName = response.usuario?.nombre

                    // Guardar el objeto usuario completo
                    response.usuario?.let { user ->
                        TokenManager.saveUser(user)
                    }

                    val nombre = response.usuario?.nombre ?: "Usuario"
                    Toast.makeText(this@MainActivity, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                    finish()
                }
            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMsg = if (errorBody?.contains("Credenciales inválidas") == true)
                        "Correo o contraseña incorrectos"
                    else "Error de autenticación: ${e.message()}"
                    Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity, "Error de red. Verifica tu conexión.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(this@MainActivity, "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showRegisterDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_register, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.cancelButton)
        val regBtn = dialogView.findViewById<MaterialButton>(R.id.regButton)
        val regEmail = dialogView.findViewById<TextInputEditText>(R.id.regEmailEditText)
        val regPassword = dialogView.findViewById<TextInputEditText>(R.id.regPasswordEditText)
        val regName = dialogView.findViewById<TextInputEditText>(R.id.regNameEditText)

        cancelBtn.setOnClickListener { dialog.dismiss() }

        regBtn.setOnClickListener {
            val email = regEmail.text?.toString()?.trim().orEmpty()
            val password = regPassword.text?.toString().orEmpty()
            val name = regName.text?.toString()?.trim()?.takeIf { it.isNotEmpty() } ?: ""

            if (email.isEmpty() || password.length < 6) {
                Toast.makeText(this, "Correo y contraseña (mínimo 6 caracteres) requeridos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Valores por defecto (el usuario los actualizará después en su perfil)
            val edad = 25
            val peso = 70.0
            val estatura = 1.70
            val objetivo = "Mantener peso"

            showLoading(true)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.apiService.register(
                        RegisterRequest(
                            nombre = name,
                            correo = email,
                            contraseña = password,
                            edad = edad,
                            peso = peso,
                            estatura = estatura,
                            objetivo = objetivo
                        )
                    )
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        TokenManager.token = response.token
                        TokenManager.userEmail = response.usuario?.correo
                        TokenManager.userName = response.usuario?.nombre

                        // Guardar el objeto usuario completo
                        response.usuario?.let { user ->
                            TokenManager.saveUser(user)
                        }

                        val nombre = response.usuario?.nombre ?: "Usuario"
                        Toast.makeText(this@MainActivity, "Registro exitoso. ¡Bienvenido $nombre!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                        finish()
                    }
                } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        val errorMsg = if (e.code() == 400 || e.code() == 409) "El correo ya está registrado o faltan datos"
                        else "Error en el registro"
                        Toast.makeText(this@MainActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(this@MainActivity, "Error de red", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        showLoading(false)
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }

    private fun showForgotPasswordDialog() {
        val input = EditText(this)
        input.hint = "Correo electrónico"
        AlertDialog.Builder(this)
            .setTitle("Recuperar contraseña")
            .setView(input)
            .setPositiveButton("Enviar") { _, _ ->
                val email = input.text?.toString()?.trim().orEmpty()
                if (email.isEmpty()) {
                    Toast.makeText(this, "Ingrese un correo", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Si el correo existe, recibirás instrucciones", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        findViewById<MaterialButton>(R.id.loginButton).isEnabled = !show
        findViewById<TextView>(R.id.registerTextView).isEnabled = !show
    }
}