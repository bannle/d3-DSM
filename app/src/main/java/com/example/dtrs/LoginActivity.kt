package com.example.dtrs


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
private lateinit var etEmail: EditText
private lateinit var  etPass: EditText
private lateinit var btnLogin: Button
private lateinit var tvRedirectSignUp: TextView
private lateinit var auth: FirebaseAuth

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin = findViewById(R.id.button_login)
        etEmail = findViewById(R.id.editTextTextEmailAddress)
        etPass = findViewById(R.id.editTextTextPassword)
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)

        auth = FirebaseAuth.getInstance()


        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()

            btnLogin.setOnClickListener {
                login()
            }
        }
    }



    private fun login() {
        val email = etEmail.text.toString().trim()
        val pass = etPass.text.toString().trim()
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Debe llenar todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                Toast.makeText(
                    this, "Inicio de sesión exitoso",
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(this, ResourcesActivity::class.java)
                startActivity(intent)
            } else
                Toast.makeText(this, "No se pudo iniciar sesión", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, ResourcesActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}