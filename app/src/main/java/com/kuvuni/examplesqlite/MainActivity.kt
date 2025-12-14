package com.kuvuni.examplesqlite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.kuvuni.examplesqlite.databinding.ActivityMainBinding
import com.kuvuni.examplesqlite.db.ContactoDatabase
import com.kuvuni.examplesqlite.db.entity.User
import com.kuvuni.examplesqlite.db.repo.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userDao = ContactoDatabase.getDatabase(this).userDao()
        repository = UserRepository(userDao)

        binding.btnCreate.setOnClickListener {
            createUser()
        }

        binding.btnRead.setOnClickListener {
            readUsers()
        }

        binding.btnUpdate.setOnClickListener {
            updateUser()
        }

        binding.btnDelete.setOnClickListener {
            deleteUser()
        }
    }

    private fun createUser() {
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString()

        if (firstName.isNotBlank() && lastName.isNotBlank() && age.isNotBlank()) {
            val user = User(firstName = firstName, lastName = lastName, age = age.toInt(), date = System.currentTimeMillis())
            lifecycleScope.launch {
                repository.insert(user)
                Snackbar.make(binding.root, "Usuario Creado", Snackbar.LENGTH_SHORT).show()
                clearFields()
            }
        } else {
            Snackbar.make(binding.root, "Por favor, rellena todos los campos", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun readUsers() {
        lifecycleScope.launch {
            repository.allUsers.collect { users ->  //collect siempre está escuchando.
                val usersText = users.joinToString(separator = "\n") {
                    "ID: ${it.uid}, Nombre: ${it.firstName} ${it.lastName}, Edad: ${it.age}"
                }
                binding.tvResults.text = "Resultados:\n$usersText"
            }
        }
    }

    private fun updateUser() {
        val userId = binding.etUserId.text.toString()
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val age = binding.etAge.text.toString()

        if (userId.isNotBlank() && firstName.isNotBlank() && lastName.isNotBlank() && age.isNotBlank()) {
            lifecycleScope.launch {
                val user = repository.getUserById(userId.toInt()).firstOrNull()
                user?.let {
                    user.firstName = firstName
                    user.lastName = lastName
                    user.age = age.toInt()
                    repository.update(user)
                    Snackbar.make(binding.root, "Usuario Actualizado", Snackbar.LENGTH_SHORT).show()
                    clearFields()
                }
            }
        } else {
            Snackbar.make(binding.root, "Rellena todos los campos (incluido ID)", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun deleteUser() {
        val userId = binding.etUserId.text.toString()
        if (userId.isNotBlank()) {
            lifecycleScope.launch {
                val user = repository.getUserById(userId.toInt()).firstOrNull() // Solo necesitamos el ID para eliminar
                user?.let {
                    repository.delete(user)
                    Snackbar.make(binding.root, "Usuario eliminado", Snackbar.LENGTH_SHORT).show()
                    clearFields()
                } ?: run {
                    Snackbar.make(binding.root, "Usuario no encontrado", Snackbar.LENGTH_SHORT).show()
                }
            }
        } else {
            Snackbar.make(binding.root, "Por favor, introduce un ID de usuario", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun clearFields() {
        binding.etUserId.text.clear()
        binding.etFirstName.text.clear()
        binding.etLastName.text.clear()
        binding.etAge.text.clear()
    }
}