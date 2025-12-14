# Paso 3: Implementando la Lógica CRUD y Coroutines

En este paso, daremos vida a nuestra aplicación. Conectaremos los botones de la UI a las operaciones de la base de datos (Crear, Leer, Actualizar, Eliminar) utilizando el `UserRepository`. Para manejar estas operaciones, que no pueden ejecutarse en el hilo principal, usaremos **Coroutines**.

## ¿Qué son las Coroutines y por qué las necesitamos?

Imagina que estás en la caja de un supermercado (el hilo principal de tu app o *UI Thread*). Este hilo es el único que puede modificar la interfaz de usuario.

Si de repente te pones a hacer una tarea muy larga, como contar todos los granos de arroz de un paquete (una operación de base de datos o de red), toda la cola de clientes (la UI) se quedará bloqueada y enfadada. ¡La app no responderá!

Las **Coroutines** son como contratar a un ayudante súper eficiente. Le das la tarea pesada (contar el arroz) y él la hace en segundo plano, sin bloquear la caja. Cuando termina, te entrega el resultado.

En Android, usamos coroutines para realizar tareas largas (como acceder a la BBDD) fuera del hilo principal, manteniendo la UI fluida y receptiva. `suspend fun` es la palabra clave que nos indica que una función es "amiga de las coroutines" y puede ser pausada y reanudada.

## 1. Un pequeño ajuste en la UI para Update y Delete

Para poder actualizar o eliminar un usuario específico, necesitamos una forma de identificarlo. La forma más sencilla es por su `id`. Vamos a añadir un campo para el `id` en `activity_main.xml`.

Modifica `app/src/main/res/layout/activity_main.xml` y añade el `EditText` para el `userId` y un `guideline` para organizar mejor los botones:

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">

    <!-- Guideline para alinear los botones -->
    <androidx.constraintlayout.widget.Guideline
        android:id="@+id/guideline"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layout_constraintGuide_begin="250dp" />

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
        android:id="@+id/etAge"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="8dp"
        android:layout_marginEnd="16dp"
        android:hint="Edad"
        android:inputType="number"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etLastName" />

    <Button
        android:id="@+id/btnCreate"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Crear"
        app:layout_constraintEnd_toStartOf="@+id/btnRead"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="@+id/guideline" />

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

## 2. Implementando la lógica en `MainActivity.kt`

Ahora vamos a completar el código de `MainActivity.kt`. Usaremos `lifecycleScope` de los KTX de `Activity` para lanzar las coroutines. Este `scope` está ligado al ciclo de vida de la `Activity`, por lo que las coroutines se cancelarán automáticamente cuando la `Activity` se destruya, evitando fugas de memoria.

Añade la dependencia de `activity-ktx` si aún no la tienes en `build.gradle.kts (Module :app)`:
```kotlin
dependencies {
    // ...
    implementation("androidx.activity:activity-ktx:1.9.0")
}
```
Sincroniza el proyecto.

### Código final de `MainActivity.kt`

RETO: Modifica la "entity" User.kt para que este código sea totalmente válido. No modificar nada en él.

```kotlin
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
```

### Explicación del Código

1.  **`createUser()`**:
    *   Recoge los datos de los `EditText`.
    *   Valida que no estén vacíos.
    *   Crea un objeto `User`. **Nota**: no pasamos el `uid` porque Room se encargará de generarlo al ser `autoGenerate = true`.
    *   Usa `lifecycleScope.launch` para iniciar una coroutine.
    *   Llama a `repository.insert(user)`, que es una `suspend fun`.
    *   Muestra un `Snackbar` de confirmación y limpia los campos.

2.  **`readUsers()`**:
    *   Inicia una coroutine.
    *   Usa `repository.allUsers.collect`. `allUsers` es un `Flow`. `collect` es un operador que "escucha" los cambios en el `Flow`.
    *   Cada vez que los datos en la tabla `User` cambian, el `Flow` emite la nueva lista de usuarios.
    *   Dentro de `collect`, formateamos la lista de usuarios y la mostramos en el `TextView`. ¡Nuestra UI ahora es **reactiva**!

3.  **`updateUser()`**:
    *   Similar a `create`, pero esta vez **sí** incluimos el `uid` que obtenemos del `etUserId`.
    *   Room usará este `uid` (que es la clave primaria) para encontrar al usuario y actualizar sus campos.

4.  **`deleteUser()`**:
    *   Para eliminar un usuario, Room solo necesita la clave primaria.
    *   Creamos un objeto `User` pasándole solo el `uid`. Los otros campos no importan.
    *   Llamamos a `repository.delete(user)`.

¡Felicidades! Ahora tienes una aplicación Android completamente funcional con una base de datos local que realiza las operaciones CRUD.

### EJERCICIOS PROPUESTOS:
Ahora que tienes una aplicación CRUD funcional, ¡es hora de poner a prueba tus habilidades! Intenta implementar las siguientes mejoras y funcionalidades.

1. **`Ordenar los Resultados`**:
   *  Objetivo: Aprender a modificar las consultas de Room para ordenar los datos. 
   *  Tarea: Crear una nueva consulta en UserDao que obtiene todos los usuarios para que los resultados se muestren ordenados por nombre (firstName) de forma ascendente (A-Z).
   *  Tarea: Crear una nueva consulta en UserDao que obtiene todos los usuarios para que los resultados se muestren ordenados por id (uid) de descendente (Z-A).

2. **`Ocultar el Teclado al Pulsar un Botón`**:
   *  Objetivo: Mejorar la experiencia de usuario (UX).
   *  Tarea: Actualmente, después de pulsar "Crear", "Actualizar" o "Eliminar", el teclado se queda visible, tapando el Snackbar que aparece en la parte inferior. Crea una función de utilidad en MainActivity para ocultar el teclado y llámala justo antes de mostrar cada Snackbar.

3. **`Confirmación antes de Borrar`**
   *  Objetivo: Prevenir acciones destructivas accidentales mostrando un diálogo de confirmación. 
   *  Tarea: Modifica la función deleteUser. Antes de llamar al repositorio para eliminar el usuario, muestra un AlertDialog que pregunte "¿Estás seguro de que quieres eliminar a este usuario?". La eliminación solo debe proceder si el usuario pulsa el botón "Aceptar".


