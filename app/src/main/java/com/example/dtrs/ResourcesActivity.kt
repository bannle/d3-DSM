package com.example.dtrs

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

//librerias para implementar el cierre de sesión
import com.google.firebase.auth.FirebaseAuth
private lateinit var btnLogOut: Button
private lateinit var auth: FirebaseAuth


class ResourcesActivity : AppCompatActivity() {

    //redirige a cada pantalla
    private fun redirectTo(view: View, destination: Class<*>) {
        view.setOnClickListener {
            val intent = Intent(this, destination)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resources)
        auth = FirebaseAuth.getInstance()


        btnLogOut = findViewById(R.id.btnLogOut)

        btnLogOut.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}