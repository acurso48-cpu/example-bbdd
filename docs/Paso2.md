# Paso 2: UI, Repositorio y Lógica Inicial

En este paso, nos enfocaremos en tres partes clave:
1.  Construir la interfaz de usuario (UI) para interactuar con la base de datos.
2.  Crear un **Repositorio** para abstraer el origen de los datos.
3.  Conectar la UI con la lógica de negocio en `MainActivity`.

NOTA MUY IMPORTANTE: Te vas a encontrar con errores en la compilación, es parte de la formación. Investiga y soluciona.

## ¿Qué es un CRUD?

CRUD es un acrónimo para las cuatro operaciones básicas de la gestión de datos:

*   **C**reate (Crear)
*   **R**ead (Leer)
*   **U**pdate (Actualizar)
*   **D**elete (Eliminar)

## 1. Diseñando la Interfaz de Usuario en `activity_main.xml`

Modifica `app/src/main/res/layout/activity_main.xml` para añadir los campos de texto y botones necesarios.

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
        android:id="@+id/etFirstName"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="16dp"
        android:layout_marginTop="16dp"
        android:layout_marginEnd="16dp"
        android:hint="Nombre"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

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
        android:layout_marginTop="16dp"
        android:text="Crear"
        app:layout_constraintEnd_toStartOf="@+id/btnRead"
        app:layout_constraintHorizontal_bias="0.5"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/etAge" />

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

## 2. Creando el Repositorio de Usuarios (`UserRepository`)

### ¿Por qué usar un Repositorio? (¡La analogía del restaurante!)

Imagina que tu aplicación es un restaurante 👨‍🍳.

*   La **UI** (`MainActivity`) es el **camarero** 🤵. Toma los pedidos del cliente (el usuario).
*   La **Base de Datos** (Room) es la **despensa gigante** 🥫. Tiene todos los ingredientes (datos), pero está en el sótano y es un poco caótica.
*   El **DAO** (`UserDao`) es el **jefe de almacén**. Sabe dónde está cada ingrediente, pero solo habla "lenguaje de almacén" (consultas SQL).

**¿Y el Repositorio? ¡Es tu Chef de Ingredientes personal!** 🧑‍🍳✨

El camarero (UI) no baja a la despensa a gritarle al jefe de almacén. ¡Sería un caos! En su lugar, le pasa el pedido al Chef de Ingredientes (el Repositorio).

Este Chef es un experto:
*   **Sabe a quién pedirle las cosas**: Habla perfectamente con el jefe de almacén (el DAO) para conseguir los ingredientes exactos.
*   **Puede usar otras fuentes**: Si la despensa no tiene algo, el Chef podría pedirlo a un proveedor externo (una API web). ¡Al camarero no le importa de dónde venga, solo quiere su pedido!
*   **Tiene una "nevera a mano" (Caché)**: Si le piden lo mismo una y otra vez, lo guarda cerca para que todo sea más rápido.
*   **Simplifica el trabajo**: El camarero solo dice "necesito una ensalada" y el Chef se encarga de reunir todos los ingredientes.

### ¿Aún no estás convencido? ¡La analogía del Asistente Personal! 🤖

Piensa en tu `ViewModel` o `Activity` como un **CEO ocupado** 💼. El CEO necesita información para tomar decisiones, pero no tiene tiempo para los detalles.

Sin un repositorio, el CEO tendría que hacer el trabajo de un becario: bajar a los archivos (la base de datos), buscar el archivador correcto (la tabla `User`), saber cómo abrirlo (SQL y DAO)... ¡Una locura!

**¡Entra el Repositorio, tu Asistente Personal de primera!** 🦸‍♀️

El CEO simplemente le dice a su asistente (el Repositorio): "Consígueme el informe de usuarios". El asistente se encarga de todo y le trae el informe listo. El CEO está feliz y es productivo.

### ¿La definitiva? ¡La analogía del Servicio de Entregas Universal! 🚚

Imagina que quieres un producto (tus datos). Vives en una ciudad donde hay:

*   Un almacén local (la base de datos Room).
*   Una tienda en otra ciudad (una API de red).
*   Un quiosco en la esquina (la caché en memoria).

**Sin un Repositorio**, tú (la UI/ViewModel) tendrías que saber la dirección de cada sitio, cómo es el transporte a cada uno (coche, tren, a pie) y decidir a dónde ir primero. ¡Qué pereza!

**Con un Repositorio (tu App de Entregas Definitiva)**, solo abres la app y dices: "Quiero el producto X".

La app (el Repositorio) hace toda la magia:
1.  Comprueba si lo tiene en su furgoneta de reparto (caché).
2.  Si no, va al almacén local (base de datos).
3.  Si tampoco está ahí, hace un pedido a la otra ciudad (API).

A ti te da igual todo ese proceso. ¡Tú solo recibes tu paquete! Así de simple.

**Moraleja**: El Repositorio es tu servicio de logística de datos. Tú pides, él entrega. Sin complicaciones.

### ¡A cocinar! Creando `UserRepository.kt`

Ahora, crea un nuevo paquete `repo` dentro de `db` (`com.kuvuni.examplesqlite.db.repo`) y, dentro de él, el archivo `UserRepository.kt`:

```kotlin
package com.kuvuni.examplesqlite.db.repo

import com.kuvuni.examplesqlite.db.dao.UserDao
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun insert(user: User) {
        userDao.insert(user)
    }

    suspend fun update(user: User) {
        userDao.update(user)
    }

    suspend fun delete(user: User) {
        userDao.delete(user)
    }

    fun getUserById(uid: Int): Flow<User> {
        return userDao.getUserById(uid)
    }
    
    fun getAdultUsers(): Flow<List<User>> {
        return userDao.getAdultUsers()
    }
}
```

## 3. Conectando la UI con el Código en `MainActivity.kt`

Ahora, conectaremos los elementos de la UI con la lógica en `MainActivity.kt`, utilizando el `UserRepository` que acabamos de crear.

### Habilitar View Binding

Asegúrate de tener View Binding habilitado en tu archivo `build.gradle.kts (Module :app)`:

```kotlin
android {
    // ...
    buildFeatures {
        viewBinding = true
    }
}
```
Sincroniza el proyecto si realizas cambios.

### Actualizar `MainActivity.kt`

```kotlin
package com.kuvuni.examplesqlite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kuvuni.examplesqlite.databinding.ActivityMainBinding
import com.kuvuni.examplesqlite.db.AppDatabase
import com.kuvuni.examplesqlite.db.repo.UserRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var repository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa el Repositorio
        val userDao = AppDatabase.getDatabase(this).userDao()
        repository = UserRepository(userDao)

        binding.btnCreate.setOnClickListener {
            // Lógica para CREAR un usuario usando el 'repository'
        }

        binding.btnRead.setOnClickListener {
            // Lógica para LEER usuarios usando el 'repository'
        }

        binding.btnUpdate.setOnClickListener {
            // Lógica para ACTUALIZAR un usuario usando el 'repository'
        }

        binding.btnDelete.setOnClickListener {
            // Lógica para ELIMINAR un usuario usando el 'repository'
        }
    }
}
```

**Explicación de los cambios:**

1.  **Inicialización del Repositorio**: En `onCreate`, obtenemos el `userDao` y lo usamos para crear nuestra instancia de `UserRepository`.
2.  **Abstracción**: `MainActivity` ya no habla con el "jefe de almacén" (DAO), solo con su "Chef" / "Asistente" / "Servicio de Entregas" (el Repositorio).
3.  **Listeners**: Los botones están listos para que la UI le pase las órdenes al Repositorio.

¡Excelente! Con el concepto del Repositorio más que claro, en el siguiente paso implementaremos la lógica CRUD completa. 
