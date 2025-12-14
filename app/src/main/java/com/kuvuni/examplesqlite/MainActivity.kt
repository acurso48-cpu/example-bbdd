package com.kuvuni.examplesqlite

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
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
        hideKeyboard()
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
        hideKeyboard()
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
            // 1. Construir el AlertDialog
            AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación") // Título del diálogo
                .setMessage("¿Estás seguro de que quieres eliminar al usuario con ID $userId?") // Mensaje principal

                // 2. Botón de Acción Positiva ("Sí, eliminar")
                .setPositiveButton("Aceptar") { _, _ ->
                    // Este bloque se ejecuta SOLO si el usuario pulsa "Aceptar"
                    lifecycleScope.launch {
                        val user = repository.getUserById(userId.toInt()).firstOrNull()
                        user?.let { userToDelete ->
                            repository.delete(userToDelete)
                            Snackbar.make(
                                binding.root,
                                "Usuario con ID ${userToDelete.uid} Eliminado",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            clearFields()
                            hideKeyboard() // ¡Buena idea llamar a la función para ocultar el teclado aquí!
                        } ?: run {
                            Snackbar.make(
                                binding.root,
                                "No se encontró un usuario con el ID $userId",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                // 3. Botón de Acción Negativa ("No, cancelar")
                .setNegativeButton("Cancelar") { dialog, _ ->
                    // Este bloque se ejecuta si el usuario pulsa "Cancelar"
                    dialog.dismiss() // Simplemente cierra el diálogo
                    clearFields()
                    hideKeyboard()
                }

                // 4. Crear y Mostrar el diálogo
                .create()
                .show()
        }
    }

    private fun clearFields() {
        binding.etUserId.text.clear()
        binding.etFirstName.text.clear()
        binding.etLastName.text.clear()
        binding.etAge.text.clear()
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

}