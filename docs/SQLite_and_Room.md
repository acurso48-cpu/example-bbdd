# SQLite y Room en Android

(Contenido de las secciones 3.1 a 3.5 omitido por brevedad)

### 3.6. Evolucionando la Base de Datos: Migraciones

(Sección sobre cómo realizar migraciones en Room, explicando el proceso de añadir la columna `email`)

### 3.7. Depurando nuestra Base de Datos: El Database Inspector

(Sección que explica cómo usar el Database Inspector para ver y depurar la base de datos en tiempo real)

### 3.8. Probando nuestra Base de Datos: Testing de DAOs

Escribir código es solo la mitad del trabajo. La otra mitad es asegurarnos de que funciona como esperamos. ¿Cómo podemos estar seguros de que nuestras consultas en el `UserDao` insertan, leen o borran los datos correctamente? La respuesta es con **tests unitarios**.

El objetivo de un test unitario para un DAO es probar cada consulta de forma aislada, sin necesidad de ejecutar la aplicación completa.

#### Paso 1: Añadir las Dependencias de Testing

Para poder probar Room y las corrutinas de forma efectiva, necesitamos añadir unas dependencias específicas de `testImplementation` a nuestro fichero `app/build.gradle.kts`.

```groovy
// app/build.gradle.kts
dependencies {
    // ... otras dependencias ...

    // Room testing
    testImplementation("androidx.room:room-testing:2.6.1")

    // Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
}
```
Ya hemos añadido estas dependencias a nuestro proyecto y sincronizado Gradle.

#### Paso 2: Crear la Clase de Test

Los tests unitarios en Android se colocan en la carpeta `src/test/java/com/paquetes/de/tu/app`. Hemos creado un nuevo fichero llamado `UserDaoTest.kt`.

```kotlin
// app/src/test/java/com/kuvuni/examplesqlite/UserDaoTest.kt
package com.kuvuni.examplesqlite

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.kuvuni.examplesqlite.db.AppDatabase
import com.kuvuni.examplesqlite.db.dao.UserDao
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() = runBlocking {
        val user = User(uid = 1, firstName = "John", lastName = "Doe", age = 30, email = "john.doe@example.com")
        userDao.insert(user)
        val userFromDb = userDao.getUserById(1)
        assertEquals(user, userFromDb)
    }
}
```

#### Analizando el Código del Test

*   **`@RunWith(RobolectricTestRunner::class)`**: Esta anotación nos permite usar APIs del framework de Android (como `Context`) en un test unitario que se ejecuta en nuestra máquina local, sin necesidad de un emulador.

*   **Base de Datos en Memoria**: 
    ```kotlin
    db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
    ```
    Esta es la clave del testing de Room. En lugar de crear un fichero de base de datos real en el disco, `inMemoryDatabaseBuilder` crea una base de datos temporal que solo existe en la memoria RAM mientras se ejecuta el test. Esto tiene dos ventajas enormes:
    1.  **Velocidad**: Es mucho más rápido.
    2.  **Aislamiento**: Cada test se ejecuta con una base de datos limpia. Los datos de un test no afectan a otro.

*   **`@Before` y `@After`**: Son anotaciones de JUnit.
    *   El método anotado con `@Before` (`createDb`) se ejecuta **antes** de cada uno de los tests. Es el lugar perfecto para inicializar la base de datos y el DAO.
    *   El método anotado con `@After` (`closeDb`) se ejecuta **después** de cada test. Se usa para limpiar recursos, como cerrar la conexión a la base de datos.

*   **`@Test`**: Esta anotación marca una función como un test. La función `insertAndGetUser` es nuestro caso de prueba.

*   **`runBlocking`**: Como nuestro método `insert` es una función `suspend`, necesitamos llamarla desde una corrutina. `runBlocking` es un constructor de corrutinas que se usa en los tests para ejecutar código `suspend` de forma síncrona.

*   **La Estructura de un Test: Given, When, Then**
    Nuestro test sigue un patrón clásico:
    1.  **Given (Preparar)**: Creamos el objeto `User` que vamos a insertar.
        ```kotlin
        val user = User(...)
        ```
    2.  **When (Ejecutar)**: Llamamos al método del DAO que queremos probar.
        ```kotlin
        userDao.insert(user)
        ```
    3.  **Then (Comprobar)**: Leemos los datos de la base de datos y usamos `assertEquals` para verificar que lo que hemos recuperado es exactamente lo que esperábamos.
        ```kotlin
        val userFromDb = userDao.getUserById(1)
        assertEquals(user, userFromDb)
        ```
    Si `user` y `userFromDb` son iguales, el test pasará (se pondrá en verde). Si no, fallará (se pondrá en rojo).

## Conclusión Final del Tutorial

¡Felicidades! Has completado un recorrido exhaustivo por el mundo de la persistencia de datos en Android. Has aprendido:

*   Los **fundamentos teóricos** de las bases de datos.
*   La diferencia entre BBDD **relacionales y no relacionales**.
*   Cómo **practicar SQL** de forma independiente.
*   A construir una base de datos completa con **Room**: Entidades, DAOs y la clase Database.
*   A **evolucionar tu base de datos** de forma segura con Migraciones.
*   A **depurar e inspeccionar** tus datos en tiempo real.
*   Y, finalmente, a **garantizar la calidad** de tus consultas con Tests Unitarios.

Con estas habilidades, estás más que preparado para manejar la persistencia de datos en cualquier aplicación Android de forma profesional, robusta y eficiente.
