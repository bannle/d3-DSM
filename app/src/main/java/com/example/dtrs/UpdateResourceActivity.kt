package com.example.dtrs

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateResourceActivity : AppCompatActivity() {

    private lateinit var api: ResourceApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_resource)

        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val etType: EditText = findViewById(R.id.etType)
        val etLink: EditText = findViewById(R.id.etLink)
        val etImage: EditText = findViewById(R.id.etImage)
        val ivImage: ImageView = findViewById(R.id.ivImage)
        val btnSave: Button = findViewById(R.id.btnSave)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://6907a856b1879c890eda64fb.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ResourceApi::class.java)

        // Recibir datos del recurso
        val resourceId = intent.getIntExtra("id", -1)
        if (resourceId == -1) {
            Toast.makeText(this, "Error: id inválido", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val type = intent.getStringExtra("type")
        val link = intent.getStringExtra("link")
        val image = intent.getStringExtra("image")

        // Mostrar datos
        etTitle.setText(title)
        etDescription.setText(description)
        etType.setText(type)
        etLink.setText(link)
        etImage.setText(image)

        Glide.with(this).load(image).placeholder(android.R.drawable.ic_menu_report_image).into(ivImage)

        // Botón Guardar
        btnSave.setOnClickListener {
            val newTitle = etTitle.text.toString().trim()
            val newDescription = etDescription.text.toString().trim()
            val newType = etType.text.toString().trim()
            val newLink = etLink.text.toString().trim()
            val newImage = etImage.text.toString().trim()

            // Validaciones
            if (newTitle.isEmpty() || newTitle.length > 50) {
                Toast.makeText(this, "Título vacío o demasiado largo (máx 50)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newDescription.isEmpty() || newDescription.length > 200) {
                Toast.makeText(this, "Descripción vacía o demasiado larga (máx 200)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newType.isEmpty() || newType.length > 30) {
                Toast.makeText(this, "Tipo vacío o demasiado largo (máx 30)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.WEB_URL.matcher(newLink).matches()) {
                Toast.makeText(this, "Link inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.WEB_URL.matcher(newImage).matches()) {
                Toast.makeText(this, "URL de imagen inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear objeto actualizado
            val updatedResource = Resource(
                id = resourceId,
                title = newTitle,
                description = newDescription,
                type = newType,
                link = newLink,
                image = newImage
            )

            val call = api.updateResource(resourceId, updatedResource)
            call.enqueue(object : Callback<Resource> {
                override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@UpdateResourceActivity, "Recurso actualizado", Toast.LENGTH_SHORT).show()
                        val intent = Intent()
                        setResult(RESULT_OK, intent)
                        finish()
                    } else {
                        Toast.makeText(this@UpdateResourceActivity, "Error al actualizar recurso", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Resource>, t: Throwable) {
                    Toast.makeText(this@UpdateResourceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
