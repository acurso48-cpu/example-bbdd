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

Vamos a modificar el archivo `app/src/main/res/layout/activity_main.xml` para añadir los campos de texto (EditText) para introducir los datos del usuario, y botones (Button) para disparar las acciones de CRUD.

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

*   **`EditText`**: Hemos añadido tres campos de texto para que el usuario pueda introducir el nombre (`etFirstName`), el apellido (`etLastName`) y el email (`etEmail`) de un usuario.
*   **`Button`**: Cuatro botones para las operaciones CRUD: `btnCreate`, `btnRead`, `btnUpdate`, y `btnDelete`.
*   **`TextView`**: Un campo de texto (`tvResults`) para mostrar los resultados de las operaciones, como la lista de usuarios recuperados de la base de datos.

## 2. Conectando la UI con el Código en `MainActivity.kt`

Ahora, necesitamos conectar estos elementos de la UI con nuestro código en `MainActivity.kt`. Usaremos `findViewById` o, preferiblemente, *View Binding* para acceder a ellos. Para este ejemplo, vamos a habilitar View Binding.

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

Sincroniza tu proyecto con los archivos de Gradle. Ahora podemos usar View Binding en `MainActivity.kt`.

### Actualizar `MainActivity.kt`

```kotlin
package com.kuvuni.examplesqlite

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kuvuni.examplesqlite.databinding.ActivityMainBinding
import com.kuvuni.examplesqlite.db.AppDatabase
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)

        binding.btnCreate.setOnClickListener {
            // Implementar la lógica para CREAR un usuario
        }

        binding.btnRead.setOnClickListener {
            // Implementar la lógica para LEER los usuarios
        }

        binding.btnUpdate.setOnClickListener {
            // Implementar la lógica para ACTUALIZAR un usuario
        }

        binding.btnDelete.setOnClickListener {
            // Implementar la lógica para ELIMINAR un usuario
        }
    }
}
```

**Explicación de los cambios:**

1.  Hemos declarado una variable `binding` de tipo `ActivityMainBinding`. Esta clase es generada automáticamente por View Binding y contiene referencias a todas las vistas con un ID en nuestro layout.
2.  En `onCreate`, inflamos el layout usando `ActivityMainBinding.inflate(layoutInflater)` y establecemos la vista de contenido con `setContentView(binding.root)`.
3.  Ahora podemos acceder a las vistas directamente desde el objeto `binding`, por ejemplo: `binding.btnCreate`.
4.  Hemos añadido `setOnClickListener` a cada botón. Dentro de estos listeners, implementaremos la lógica para interactuar con la base de datos en los siguientes pasos.

¡Felicidades! Has creado la interfaz de usuario básica para tu aplicación de base de datos. En el siguiente paso, implementaremos la lógica para cada uno de los botones del CRUD.
