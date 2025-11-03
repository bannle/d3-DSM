package com.example.dtrs

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.widget.ArrayAdapter
import android.widget.Spinner

class AddResourceActivity : AppCompatActivity() {

    private lateinit var api: ResourceApi
    private lateinit var spinnerType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_resource)

        val etTitle: EditText = findViewById(R.id.etTitle)
        val etDescription: EditText = findViewById(R.id.etDescription)
        val etLink: EditText = findViewById(R.id.etLink)
        val etImage: EditText = findViewById(R.id.etImage)
        val btnSave: Button = findViewById(R.id.btnSave)
        spinnerType = findViewById(R.id.spinnerType)

        // Configurar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://6907a856b1879c890eda64fb.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ResourceApi::class.java)

        // Cargar tipos desde la API
        api.getResources().enqueue(object : Callback<List<Resource>> {
            override fun onResponse(call: Call<List<Resource>>, response: Response<List<Resource>>) {
                if (response.isSuccessful) {
                    val tipos = response.body()?.map { it.type }?.distinct() ?: listOf()
                    val adapter = ArrayAdapter(
                        this@AddResourceActivity,
                        android.R.layout.simple_spinner_item,
                        tipos
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerType.adapter = adapter
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(this@AddResourceActivity, "Error al cargar tipos", Toast.LENGTH_SHORT).show()
            }
        })

        // Guardar recurso
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val type = spinnerType.selectedItem?.toString() ?: ""
            val link = etLink.text.toString().trim()
            val image = etImage.text.toString().trim()

            // Validaciones
            if (title.isEmpty() || title.length > 50) {
                Toast.makeText(this, "Título vacío o demasiado largo (máx 50)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (description.isEmpty() || description.length > 200) {
                Toast.makeText(this, "Descripción vacía o demasiado larga (máx 200)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (type.isEmpty() || type.length > 30) {
                Toast.makeText(this, "Tipo vacío o demasiado largo (máx 30)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.WEB_URL.matcher(link).matches()) {
                Toast.makeText(this, "Link inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.WEB_URL.matcher(image).matches()) {
                Toast.makeText(this, "URL de imagen inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val newResource = Resource(
                id = 0,
                title = title,
                description = description,
                type = type,
                link = link,
                image = image
            )

            api.addResource(newResource).enqueue(object : Callback<Resource> {
                override fun onResponse(call: Call<Resource>, response: Response<Resource>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddResourceActivity, "Recurso agregado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this@AddResourceActivity, "Error al agregar recurso", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Resource>, t: Throwable) {
                    Toast.makeText(this@AddResourceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
