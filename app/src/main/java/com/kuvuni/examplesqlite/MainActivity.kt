package com.kuvuni.examplesqlite

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kuvuni.examplesqlite.databinding.ActivityMainBinding
import com.kuvuni.examplesqlite.db.AppDatabase
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val db by lazy { AppDatabase.getDatabase(this) }
    private val userDao by lazy { db.userDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnCreate.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val email = binding.etEmail.text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                val user = User(firstName = firstName, lastName = lastName, email = email)
                lifecycleScope.launch {
                    userDao.insert(user)
                    clearInputFields()
                    showToast("Usuario Creado")
                }
            } else {
                showToast("Por favor, rellena todos los campos")
            }
        }

        binding.btnRead.setOnClickListener {
            lifecycleScope.launch {
                val users = userDao.getAll()
                val usersText = users.joinToString(separator = "\n") {
                    "ID: ${it.uid}, Nombre: ${it.firstName}, Apellido: ${it.lastName}, Email: ${it.email}"
                }
                binding.tvResults.text = if (users.isEmpty()) "No hay usuarios" else usersText
            }
        }

        binding.btnUpdate.setOnClickListener {
            val userId = binding.etUserId.text.toString().toIntOrNull()
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val email = binding.etEmail.text.toString()

            if (userId != null && firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty()) {
                val user = User(uid = userId, firstName = firstName, lastName = lastName, email = email)
                lifecycleScope.launch {
                    userDao.update(user)
                    showToast("Usuario Actualizado")
                }
            } else {
                showToast("Por favor, introduce un ID válido y rellena todos los campos")
            }
        }

        binding.btnDelete.setOnClickListener {
            val userId = binding.etUserId.text.toString().toIntOrNull()

            if (userId != null) {
                lifecycleScope.launch {
                    val user = User(uid = userId, firstName = "", lastName = "", email = "") // Solo el ID es necesario para el borrado
                    userDao.delete(user)
                    showToast("Usuario Eliminado")
                }
            } else {
                showToast("Por favor, introduce un ID de usuario")
            }
        }
    }
    
    private fun clearInputFields() {
        binding.etUserId.text.clear()
        binding.etFirstName.text.clear()
        binding.etLastName.text.clear()
        binding.etEmail.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
