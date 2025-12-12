# Paso 2: Creando una Interfaz de Usuario (UI) para el CRUD

En este paso, nos enfocaremos en construir la interfaz de usuario que permitirá a los usuarios interactuar con la base de datos. Crearemos los elementos visuales para realizar operaciones CRUD.

## ¿Qué es un CRUD?

CRUD es un acrónimo que representa las cuatro operaciones básicas que se pueden realizar en una base de datos:

*   **C**reate (Crear): Añadir nuevos registros a la base de datos.
*   **R**ead (Leer): Recuperar y mostrar registros de la base de datos.
*   **U**pdate (Actualizar): Modificar registros existentes en la base de datos.
*   **D**elete (Eliminar): Borrar registros de la base de datos.

Nuestra interfaz de usuario facilitará estas cuatro operaciones en la tabla `User` que definimos anteriormente.

## 1. Diseñando la Interfaz de Usuario en `activity_main.xml`

Vamos a modificar el archivo `app/src/main/res/layout/activity_main.xml` para añadir los campos de texto (EditText) para introducir los datos del usuario, y botones (Button) para disparar las acciones de CRUD. Para poder actualizar y eliminar, añadiremos un campo para el ID del usuario.

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <EditText
        android:id="@+id/etUserId"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:hint="ID de Usuario (para Actualizar/Eliminar)"
        android:inputType="number"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <EditText
        android:id="@+id/etFirstName"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="16dp"
        android:hint="Nombre"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etUserId" />

    <EditText
        android:id="@+id/etLastName"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="16dp"
        android:hint="Apellido"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etFirstName" />

    <EditText
        android:id="@+id/etEmail"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="16dp"
        android:hint="Email"
        android:inputType="textEmailAddress"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etLastName" />

    <Button
        android:id="@+id/btnCreate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:text="Crear"
        app:layout_constraintEnd_toStartOf="@+id/btnRead"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etEmail" />

    <Button
        android:id="@+id/btnRead"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Leer"
        app:layout_constraintEnd_toStartOf="@+id/btnUpdate"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/btnCreate"
        app:layout_constraintTop_toTopOf="@+id/btnCreate" />

    <Button
        android:id="@+id/btnUpdate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Actualizar"
        app:layout_constraintEnd_toStartOf="@+id/btnDelete"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/btnRead"
        app:layout_constraintTop_toTopOf="@+id/btnRead" />

    <Button
        android:id="@+id/btnDelete"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Eliminar"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toEndOf="@+id/btnUpdate"
        app:layout_constraintTop_toTopOf="@+id/btnUpdate" />

    <TextView
        android:id="@+id/tvResults"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:layout_marginBottom="16dp"
        android:text="Resultados:"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/btnCreate" />

</androidx.constraintlayout.widget.ConstraintLayout>
```

**Explicación de los componentes:**

*   **`EditText`**: Hemos añadido campos de texto para el ID, nombre, apellido y email. El ID es crucial para saber qué registro actualizar o eliminar.
*   **`Button`**: Cuatro botones para las operaciones CRUD.
*   **`TextView`**: Un campo de texto (`tvResults`) para mostrar los resultados.

## 2. Conectando la UI con el Código en `MainActivity.kt`

Necesitamos conectar estos elementos de la UI con nuestro código. Habilitaremos *View Binding* para ello.

### Habilitar View Binding

Abre tu archivo `build.gradle.kts (Module :app)` y añade lo siguiente dentro del bloque `android`:

```kotlin
android {
    // ...
    buildFeatures {
        viewBinding = true
    }
}
```

Sincroniza tu proyecto con los archivos de Gradle.

## 3. Implementando la Lógica del CRUD

Ahora, actualicemos `MainActivity.kt` para implementar la lógica de cada botón. Usaremos `lifecycleScope` para lanzar corutinas y realizar las operaciones de base de datos en un hilo secundario, evitando bloquear el hilo principal de la UI.

```kotlin
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
                val usersText = users.joinToString(separator = "
") {
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
```

### Explicación de la Lógica:

1.  **Inicialización `lazy`**: `db` y `userDao` se inicializan de forma diferida (`by lazy`), lo que significa que se crearán la primera vez que se accedan, no al crear la `Activity`.
2.  **`btnCreate`**:
    *   Recoge el texto de los `EditText`.
    *   Crea una nueva instancia de `User` (el ID se autogenera).
    *   Lanza una corutina para llamar a `userDao.insert()`.
    *   Limpia los campos y muestra un mensaje.
3.  **`btnRead`**:
    *   Lanza una corutina para obtener todos los usuarios con `userDao.getAll()`.
    *   Formatea la lista de usuarios en un `String`.
    *   Muestra el resultado en el `TextView`.
4.  **`btnUpdate`**:
    *   Recoge el ID y los demás datos. El ID es crucial.
    *   Crea un objeto `User`, **incluyendo el ID** del usuario que se va a actualizar.
    *   Llama a `userDao.update()` dentro de una corutina.
5.  **`btnDelete`**:
    *   Recoge el ID del usuario a eliminar.
    *   Crea un objeto `User` donde solo nos importa el `uid`. Room usará este `uid` para encontrar y eliminar el registro.
    *   Llama a `userDao.delete()` dentro de una corutina.
6.  **Funciones de Ayuda**: `clearInputFields` y `showToast` nos ayudan a reutilizar código y mantener `setupClickListeners` más limpio.

¡Felicidades! Has implementado una aplicación completa con operaciones CRUD. Ahora tus alumnos pueden probar a crear, leer, actualizar y eliminar usuarios en la base de datos.
