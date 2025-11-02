package com.example.dtrs

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


private lateinit var etEmail: EditText
lateinit var etConfPass: EditText
private lateinit var etPass: EditText
private lateinit var btnSignUp: Button
lateinit var tvRedirectLogin: TextView

private lateinit var auth: FirebaseAuth


class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etEmail = findViewById(R.id.etSEmailAddress)
        etConfPass = findViewById(R.id.etSConfPassword)
        etPass = findViewById(R.id.etSPassword)
        btnSignUp = findViewById(R.id.btnSSigned)
        tvRedirectLogin = findViewById(R.id.tvRedirectLogin)

        auth = Firebase.auth

        btnSignUp.setOnClickListener{
            signUpUser()
        }
        tvRedirectLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signUpUser() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()
        val confirmPassword = etConfPass.text.toString().trim()

        // Validar campos vacíos
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que las contraseñas coincidan
        if (pass != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show()
            return
        }

        // validaciones
        val passwordRegex =
            Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#\$%^&*])[A-Za-z\\d!@#\$%^&*]{8,}\$")
        
        if (!passwordRegex.matches(pass)) {
            AlertDialog.Builder(this)
                .setTitle("Contraseña inválida")
                .setMessage(
                    "La contraseña debe tener al menos:\n" +
                            "• 8 caracteres\n" +
                            "• Una letra mayúscula\n" +
                            "• Una letra minúscula\n" +
                            "• Un número\n" +
                            "• Un carácter especial (!@#\$%^&*)"
                )
                .setPositiveButton("Entendido", null)
                .show()
            return
        }

        // Crear usuario
        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ResourcesActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun onStart(){
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, ResourcesActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}