# Usando Faker para Generar Datos de Prueba

Cuando desarrollamos nuestra aplicación, es útil tener una forma de generar datos realistas y variados para poblar nuestra base de datos. La librería **Faker** es excelente para esto. Nos permite crear nombres, direcciones, correos electrónicos y mucho más, de forma aleatoria.

## 1. Añadir la Dependencia de Faker

Para usar Faker en nuestro proyecto, necesitamos añadir la dependencia al archivo `build.gradle.kts` de nuestro módulo `app`. Como lo usaremos en el código principal de la app (y no solo en tests), usaremos `implementation`.

Abre `app/build.gradle.kts` y añade la siguiente línea en la sección `dependencies`:

```kotlin
dependencies {
    // ... otras dependencias
    implementation("io.github.serpro69:kotlin-faker:1.12.0") // <-- Añade esta línea
}
```

Después de añadir la dependencia, no olvides sincronizar el proyecto con Gradle.

## 2. Usando Faker en MainActivity

En lugar de usar Faker para pruebas, vamos a usarlo para generar datos de ejemplo directamente desde nuestra `MainActivity`. Esto puede ser útil para tener datos en la base de datos desde el primer momento que ejecutamos la aplicación.

Dentro de `MainActivity.kt`, podemos inicializar Faker y usarlo para generar un usuario y guardarlo en la base de datos.

```kotlin
package com.kuvuni.examplesqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.kuvuni.examplesqlite.db.ContactoDatabase
import com.kuvuni.examplesqlite.db.entity.User
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ContactoDatabase.getDatabase(this)
        val userDao = db.userDao()
        val faker = Faker()

        // Lanzamos una corutina para no bloquear el hilo principal
        GlobalScope.launch {
            // Generamos un usuario de ejemplo
            val user = User(
                uid = 0, //Room se encargará de generar el id
                firstName = faker.name.firstName(),
                lastName = faker.name.lastName(),
                age = faker.number.numberBetween(18, 99),
                email = faker.internet.email(),
                date = System.currentTimeMillis(),
                image = null
            )
            
            // Lo insertamos en la base de datos
            userDao.insert(user)
            Log.d("MainActivity", "Usuario insertado: $user")

            // Leemos todos los usuarios y los mostramos en el log
            val users = userDao.getAll()
            Log.d("MainActivity", "Usuarios en la BD: ${users.joinToString()}")
        }
    }
}
```

### Explicación del Código:

1.  **`val faker = Faker()`**: Creamos una instancia de `Faker` que usaremos para generar los datos.
2.  **`GlobalScope.launch`**: Las operaciones de base de datos no pueden correr en el hilo principal. Usamos una corutina con `GlobalScope` para realizar la inserción y lectura de forma asíncrona. Para una aplicación real, sería mejor inyectar un `CoroutineScope`.
3.  **Generación de `User`**: Usamos `faker` para generar un nombre, apellido, edad y email.
4.  **`userDao.insert(user)`**: Insertamos el nuevo usuario en la base de datos.
5.  **`Log.d(...)`**: Usamos el Log para verificar que el usuario se ha insertado y para ver todos los usuarios actuales en la base de datos.

Con Faker, puedes poblar tu base de datos con datos de aspecto realista para probar tu UI y la lógica de tu aplicación de una manera más efectiva.
