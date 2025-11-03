package com.example.dtrs

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ResourceActivity : AppCompatActivity() {

    private lateinit var adapter: ResourceAdapter
    private lateinit var api: ResourceApi
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var btnFilter: ImageButton
    private var resourcesList: List<Resource> = listOf()

    private lateinit var auth: FirebaseAuth

    private lateinit var addResourceLauncher: androidx.activity.result.ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resource)

        auth = FirebaseAuth.getInstance()

        val toolbar: MaterialToolbar = findViewById(R.id.topAppBar)
        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    true
                }

                else -> false
            }
        }
        addResourceLauncher = registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                getResources(api)
            }
        }



        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        searchView = findViewById(R.id.searchView)
        btnFilter = findViewById(R.id.btnFilter)

        val fabAgregar: FloatingActionButton = findViewById(R.id.fab_agregar)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://6907a856b1879c890eda64fb.mockapi.io/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(ResourceApi::class.java)

        getResources(api)

        fabAgregar.setOnClickListener {
            val intent = Intent(this, AddResourceActivity::class.java)
            addResourceLauncher.launch(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.trim() ?: ""
                if (query.isEmpty()) {
                    adapter.updateList(resourcesList)
                    return true
                }

                val filtered = resourcesList.filter { resource ->
                    var matches = false

                    // Buscar por ID si la query es un número
                    query.toIntOrNull()?.let { idQuery ->
                        matches = matches || resource.id == idQuery
                    }

                    // Buscar por título o tipo
                    matches = matches ||
                            resource.title.contains(query, ignoreCase = true) ||
                            resource.type.contains(query, ignoreCase = true)

                    matches
                }

                adapter.updateList(filtered)
                return true
            }
        })


        btnFilter.setOnClickListener {
            val options = arrayOf("Filtrar por tipo", "Filtrar por título", "Ordenar A-Z", "Ordenar Z-A", "Mostrar todos")

            AlertDialog.Builder(this)
                .setTitle("Opciones de filtro y orden")
                .setItems(options) { _, index ->
                    when (options[index]) {

                        "Filtrar por tipo" -> {
                            val tipos = resourcesList.map { it.type }.distinct().toTypedArray()
                            AlertDialog.Builder(this)
                                .setTitle("Filtrar por tipo")
                                .setItems(tipos) { _, tipoIndex ->
                                    val selected = tipos[tipoIndex]
                                    val filtered = resourcesList.filter { it.type == selected }
                                    adapter.updateList(filtered)
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }

                        "Filtrar por título" -> {
                            val titulos = resourcesList.map { it.title }.distinct().toTypedArray()
                            AlertDialog.Builder(this)
                                .setTitle("Filtrar por título")
                                .setItems(titulos) { _, titleIndex ->
                                    val selected = titulos[titleIndex]
                                    val filtered = resourcesList.filter { it.title == selected }
                                    adapter.updateList(filtered)
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }

                        "Ordenar A-Z" -> {
                            val sorted = resourcesList.sortedBy { it.title.lowercase() }
                            adapter.updateList(sorted)
                        }

                        "Ordenar Z-A" -> {
                            val sorted = resourcesList.sortedByDescending { it.title.lowercase() }
                            adapter.updateList(sorted)
                        }

                        "Mostrar todos" -> {
                            adapter.updateList(resourcesList)
                        }
                    }
                }
                .show()
        }
    }

    override fun onResume() {
        super.onResume()
        getResources(api)
    }

    private fun getResources(api: ResourceApi) {
        val call = api.getResources()
        call.enqueue(object : Callback<List<Resource>> {
            override fun onResponse(
                call: Call<List<Resource>>,
                response: Response<List<Resource>>
            ) {
                if (response.isSuccessful) {
                    resourcesList = response.body() ?: listOf()
                    val ordered = resourcesList.sortedByDescending { it.id }
                    adapter = ResourceAdapter(ordered) { resourceToDelete ->

                        AlertDialog.Builder(this@ResourceActivity)
                            .setTitle("Eliminar recurso")
                            .setMessage("¿Seguro que deseas eliminar '${resourceToDelete.title}'?")
                            .setPositiveButton("Sí") { _, _ ->
                                deleteResource(resourceToDelete, api)
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }

                    recyclerView.adapter = adapter
                } else {
                    Toast.makeText(
                        this@ResourceActivity,
                        "Error al obtener los recursos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Resource>>, t: Throwable) {
                Toast.makeText(this@ResourceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun deleteResource(resource: Resource, api: ResourceApi) {
        Log.e("API", "id : $resource")
        val llamada = api.deleteResource(resource.id)
        llamada.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ResourceActivity, "Recurso eliminado", Toast.LENGTH_SHORT).show()
                    getResources(api)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e("API", "Error al eliminar recurso : $error")
                    Toast.makeText(
                        this@ResourceActivity,
                        "Error al eliminar recurso",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("API", "Error al eliminar recurso : $t")
                Toast.makeText(this@ResourceActivity, "Error al eliminar recurso", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}
