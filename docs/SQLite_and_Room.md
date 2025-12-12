# Guía Completa de SQLite y Room en Android

## Parte 1: Introducción a la Persistencia

En la mayoría de las aplicaciones, no basta con que los datos existan mientras la app está abierta. Los usuarios esperan que su información (sus contactos, sus notas, el progreso de su juego) siga ahí cuando vuelvan a abrir la aplicación. A esta capacidad de guardar datos de forma permanente la llamamos **persistencia**.

En Android, la solución más robusta y estándar para la persistencia de datos estructurados es el uso de una base de datos. La base de datos que viene integrada en cada dispositivo Android se llama **SQLite**.

## Parte 2: El Camino Moderno - Room

Trabajar directamente con SQLite puede ser complicado y propenso a errores. Por ello, Google creó **Room**, una librería que actúa como una capa de abstracción sobre SQLite. Room nos facilita enormemente la vida, proporcionando una API más clara, más segura y que requiere escribir mucho menos código.

Room se encarga del trabajo pesado y nosotros nos centramos en lo importante: definir nuestros datos y cómo queremos interactuar con ellos.

### 3.1. Configuración del Proyecto con Room

Antes de poder usar la magia de Room, necesitamos configurar nuestro proyecto. Esto implica añadir las librerías necesarias a nuestros ficheros de Gradle.

#### ¿Qué es KSP y por qué lo necesitamos?

Cuando trabajas con Room, escribes anotaciones como `@Entity`, `@Dao` o `@Database`. Estas anotaciones son etiquetas que le dan un significado especial a tus clases e interfaces, pero no hacen nada por sí mismas.

Aquí es donde entra en juego el **procesamiento de anotaciones**. Es un proceso que ocurre durante la compilación de tu app. Una herramienta especial lee estas anotaciones y **genera código Kotlin o Java automáticamente** basándose en ellas.

**KSP (Kotlin Symbol Processing)** es la herramienta moderna de Google para el procesamiento de anotaciones en proyectos de Kotlin. Es el sucesor de una herramienta más antigua llamada `kapt`.

**¿Por qué lo necesita Room?**

Room usa KSP para:
*   Leer tu interfaz `@Dao` y generar la clase completa que implementa cada una de tus consultas SQL.
*   Leer tu clase `@Database` y generar todo el código "caldera" (boilerplate) para construir y gestionar la base de datos.
*   Validar tus consultas SQL en tiempo de compilación para que no fallen en el teléfono del usuario.

En resumen, gracias a KSP, nosotros solo escribimos la parte "declarativa" (qué queremos) y Room escribe la implementación (el cómo) por nosotros. Por eso es obligatorio añadir el plugin `ksp` a nuestro proyecto. KSP es significativamente más rápido que su predecesor `kapt` y está diseñado desde cero para Kotlin, lo que lo convierte en la opción recomendada.

**1. El Catálogo de Versiones (`gradle/libs.versions.toml`)**

Una buena práctica es centralizar las versiones y las dependencias en el catálogo de versiones. Así hemos configurado el nuestro para incluir Room y KSP (el procesador de anotaciones de Kotlin).

```toml
[versions]
agp = "8.13.1"
kotlin = "2.0.0"
ksp = "2.0.0-1.0.21"
room = "2.6.1"
# ... otras versiones

[libraries]
# Room
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
# ... otras librerías

[plugins]
# ...
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

**2. El Fichero de Build del Módulo (`app/build.gradle.kts`)**

Con el catálogo definido, aplicar los plugins y añadir las dependencias es muy limpio:

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Plugin de KSP
}

// ...

dependencies {
    // ...

    // Room
    implementation(libs.androidx.room.runtime) // API principal de Room
    implementation(libs.androidx.room.ktx)     // Extensiones de Kotlin (Corrutinas, Flow)
    ksp(libs.androidx.room.compiler)            // Procesador de anotaciones

    // ...
}
```

Después de estos cambios, es crucial sincronizar el proyecto con Gradle para que se descarguen y configuren las nuevas librerías.

### 3.2. Definiendo la Estructura de Datos: La Entidad (`@Entity`)

El primer paso es definir nuestras tablas. En Room, cada tabla se representa con una clase de datos (una `data class`) anotada con `@Entity`.

Hemos creado la clase `User.kt`:

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/entity/User.kt
package com.kuvuni.examplesqlite.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(defaultValue = "0")
    val age: Int,

    @ColumnInfo(name = "email", defaultValue = "NULL")
    val email: String?
) {
    @Ignore
    val fullName: String = "$firstName $lastName"
}
```
*   `@Entity`: Marca la clase como una tabla. Con `tableName` le damos un nombre explícito.
*   `@PrimaryKey`: Define la clave primaria. `autoGenerate = true` hace que SQLite le asigne un ID único y creciente automáticamente.
*   `@ColumnInfo`: Permite personalizar la columna, como su nombre (`name`) o un valor por defecto (`defaultValue`).
*   `@Ignore`: Le dice a Room que ignore un campo para que no intente guardarlo en la base de datos.

### 3.3. Accediendo a los Datos: El DAO (`@Dao`)

Una vez definida la tabla, necesitamos una forma de acceder a ella. Esto se hace a través de una interfaz anotada con `@Dao` (Data Access Object).

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/dao/UserDao.kt
package com.kuvuni.examplesqlite.db.dao

import androidx.room.*
import com.kuvuni.examplesqlite.db.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user ORDER BY first_name ASC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserById(uid: Int): User?
}
```
*   `@Dao`: Identifica la interfaz como un DAO para Room.
*   `@Insert`, `@Update`, `@Delete`: Anotaciones de conveniencia para las operaciones más comunes. Room genera el código SQL por nosotros.
    *   **Profundizando en `onConflict`**: El parámetro `onConflict` de `@Insert` es muy importante. Define qué hacer si intentas insertar un dato que viola una regla de la base de datos. El conflicto más común es intentar insertar una fila con una **clave primaria que ya existe**.
        *   `OnConflictStrategy.IGNORE`: Esta estrategia le dice a Room: "Si intentas insertar un usuario con un `uid` que ya está en la tabla, simplemente **ignora** esta nueva inserción y no hagas nada. No lances un error, solo sigue adelante". Es muy útil para escenarios de "insertar si no existe".
        *   Otras estrategias comunes son `REPLACE` (borra el dato antiguo y lo reemplaza por el nuevo) y `ABORT` (la estrategia por defecto, que cancela la operación y lanza un error).
*   `suspend`: Marcar una función como `suspend` es crucial. Le indica a Room que la operación puede ser larga y debe ejecutarse en un hilo de fondo para no bloquear la interfaz de usuario.
*   `@Query`: La anotación más potente. Nos permite escribir cualquier consulta SQL. Room las valida en tiempo de compilación, lo que nos ahorra errores en tiempo de ejecución.


### 3.4. Uniendo las Piezas: La Clase `Database` (`@Database`)

Ya tenemos las `Entity` (las tablas) y los `DAO` (las consultas). La pieza final es la clase que une todo: la base de datos principal. Es una clase abstracta que hereda de `RoomDatabase` y actúa como el punto de acceso central a toda la base de datos de la app.

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/AppDatabase.kt
@Database(entities = [User::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user ADD COLUMN email TEXT")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

Desglosemos esta clase en profundidad:

*   **`@Database`**: Esta es la anotación principal que le dice a Room: "Esta clase es una base de datos".
    *   `entities = [User::class]`: Aquí debemos listar **todas** las clases de entidad que pertenecen a esta base de datos. Si tuviéramos más tablas (ej: `Product`, `Order`), las añadiríamos al array: `[User::class, Product::class, Order::class]`. Room necesita saber esto para crear las tablas correspondientes.
    *   `version = 2`: Este número es **crucial**. Representa la versión actual del esquema de tu base de datos. Si en el futuro modificas una tabla (ej: añades una columna), deberás incrementar este número a `3` y proporcionar una `Migration` de la versión 2 a la 3. Es la forma que tiene Room de controlar los cambios.
    *   `exportSchema = true`: Esta opción le dice a Room que guarde el esquema de la base de datos (la estructura de las tablas, columnas, etc.) en un fichero JSON dentro de tu proyecto. Es una **muy buena práctica** mantenerlo en `true`, ya que estos ficheros de esquema son muy útiles para depurar migraciones y para tener un historial de versiones de tu base de datos.

*   **`abstract fun userDao()`**: Por cada DAO que tu base de datos utilice, debes declarar una función `abstract` que lo devuelva. No tienes que escribir el cuerpo de la función; Room, gracias al procesador de anotaciones KSP, generará la implementación por ti y te devolverá una instancia funcional de tu `UserDao`.

*   **El Patrón Singleton (`companion object`)**: Este es, conceptualmente, el bloque más importante de la clase. Su objetivo es asegurar que solo exista **una única instancia** de la base de datos en toda la aplicación. ¿Por qué?
    *   **Rendimiento**: Crear una instancia de una base de datos (`Room.databaseBuilder(...)`) es una operación "pesada". Implica acceder al sistema de ficheros del dispositivo, verificar la estructura, etc. Hacer esto repetidamente ralentizaría tu aplicación.
    *   **Consistencia**: Tener múltiples instancias de la misma base de datos abiertas al mismo tiempo es una receta para el desastre: puede llevar a que los datos estén corruptos o a errores de concurrencia (un hilo intenta leer mientras otro está escribiendo de forma descontrolada).

    Para lograr este patrón Singleton, usamos varias herramientas de Kotlin y Java:

    *   `@Volatile private var INSTANCE: AppDatabase? = null`: Declaramos una variable estática para guardar nuestra única instancia. La anotación `@Volatile` es muy importante en un entorno multihilo (como es Android). Asegura que el valor de la variable `INSTANCE` sea siempre el más actualizado y visible para todos los hilos de la aplicación, evitando que un hilo vea la instancia como `null` cuando otro hilo ya la ha creado.

    *   `synchronized(this)`: Este es el corazón de la seguridad del Singleton. Imagina que dos partes diferentes de tu app intentan obtener la base de datos por primera vez *exactamente al mismo tiempo*. Ambos hilos comprobarían que `INSTANCE` es `null` y ambos intentarían crear una nueva instancia. ¡Desastre! El bloque `synchronized` actúa como una cerradura: solo **un hilo** puede entrar en ese bloque de código a la vez. El primer hilo que llega, entra, crea la instancia y la asigna a `INSTANCE`. Cuando el segundo hilo intenta entrar, debe esperar a que el primero termine. Para cuando el segundo hilo puede entrar, `INSTANCE` ya no es `null`, por lo que simplemente devuelve la instancia ya creada sin volver a construirla.

    *   `INSTANCE ?: synchronized(this) { ... }`: Esta es una forma idiomática en Kotlin (usando el operador Elvis `?:`) de decir: "Si `INSTANCE` ya tiene un valor, devuélvelo. Si es `null`, ejecuta el bloque `synchronized` para crearlo". Esto se conoce como "Double-Checked Locking" y es muy eficiente, porque el costoso bloqueo `synchronized` solo se ejecuta la primera vez que se crea la instancia.

### 3.5. Poniéndolo todo en Marcha: Usando la Base de Datos

Ya tenemos todas las piezas del puzle. ¿Cómo las usamos desde la pantalla de nuestra app? Este es el paso que conecta todo.

Vamos a modificar nuestra `MainActivity.kt` para insertar un nuevo usuario cuando se cree la actividad.

```kotlin
// En MainActivity.kt
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kuvuni.examplesqlite.db.AppDatabase
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Obtenemos una instancia de la base de datos
        val db = AppDatabase.getDatabase(this)

        // 2. Usamos un lifecycleScope para lanzar una corrutina
        lifecycleScope.launch {
            // 3. Creamos un nuevo usuario
            val newUser = User(firstName = "Ada", lastName = "Lovelace", age = 36, email = "ada@lovelace.com")
            
            // 4. Usamos el DAO para insertarlo en un hilo de fondo
            db.userDao().insert(newUser)
        }
    }
}
```
*   **`AppDatabase.getDatabase(this)`**: Llamamos a nuestro método `static` del `companion object` para obtener la instancia Singleton de la base de datos. Le pasamos el `context` de la Activity.
*   **`lifecycleScope.launch`**: Como nuestro método `insert` en el DAO es una función `suspend`, debe ser llamado desde una Corrutina o desde otra función `suspend`. `lifecycleScope` es la forma recomendada en Android para lanzar operaciones asíncronas que están atadas al ciclo de vida de un componente (la corrutina se cancelará automáticamente si la `Activity` se destruye, evitando fugas de memoria).

Con este código, cada vez que la app se inicie, se creará un nuevo usuario "Ada Lovelace" y se insertará en la base de datos de forma segura.

### 3.6. Evolucionando la Base de Datos: Migraciones

Las aplicaciones nunca son estáticas; siempre están evolucionando. Imagina que publicamos nuestra aplicación, y miles de usuarios ya han guardado sus datos. Ahora, en la versión 2.0 de la app, nos damos cuenta de que necesitamos guardar también el **email** de cada usuario.

Si solo añadimos el campo a la entidad `User` y subimos la versión de la base de datos en `@Database` sin más, la app de los usuarios existentes se romperá con un error `IllegalStateException`. Room no sabrá cómo pasar del esquema antiguo (sin email) al nuevo.

La solución profesional es una **migración**.

1.  **Actualizamos la Entidad**: Añadimos el campo `email` a `User.kt` (ya lo hicimos).
2.  **Incrementamos la versión**: Cambiamos `version = 1` a `version = 2` en la anotación `@Database`.
3.  **Creamos la Migración**: Definimos un objeto `Migration` que especifica la consulta SQL para alterar la tabla.

    ```kotlin
    // Dentro del companion object de AppDatabase.kt
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE user ADD COLUMN email TEXT")
        }
    }
    ```
4.  **Añadimos la Migración**: La registramos en el constructor de la base de datos.

    ```kotlin
    // Dentro de getDatabase()
    Room.databaseBuilder(...)
        .addMigrations(MIGRATION_1_2)
        .build()
    ```
Este proceso garantiza que los usuarios que actualicen la app no pierdan los datos que ya tenían guardados.


**Ejemplo 2: Añadir una columna `password` (Migración de 2 a 3)**

Siguiendo el mismo principio, vamos a añadir un campo para una contraseña.

**1. Actualizar la Entidad `User`:**

Añadimos `password` a la `data class`.

```kotlin
@Entity(tableName = "user")
data class User(
    // ... campos existentes
    @ColumnInfo(name = "password")
    val password: String
)
```

**2. Incrementar la versión y crear la nueva `Migration`:**

En `AppDatabase.kt`, subimos la versión a 3 y definimos la migración de 2 a 3.

```kotlin
@Database(entities = [User::class], version = 3, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    // ...

    companion object {
        // ... MIGRATION_1_2

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE user ADD COLUMN password TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "user_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3) // Añadir la nueva migración
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

*   **`ALTER TABLE user ADD COLUMN password TEXT NOT NULL DEFAULT ''`**: De nuevo, es crucial proporcionar un `DEFAULT` para las filas existentes, ya que la nueva columna es `NOT NULL`. La lógica para hashear la contraseña (ej. con MD5 o, mejor aún, con algoritmos modernos como Argon2 o Scrypt) se debe implementar en el código de tu aplicación antes de guardar el dato, no en la base de datos directamente.


### 3.7. Depurando nuestra Base de Datos: El Database Inspector

Android Studio incluye una herramienta increíblemente útil, el **App Inspector**, que contiene el **Database Inspector**. Para usarlo:

1.  Ejecuta la app en un emulador o dispositivo (API 26+).
2.  Ve a **View > Tool Windows > App Inspection**.
3.  Selecciona la pestaña **Database Inspector**.

Desde ahí, podrás ver las tablas, los datos en tiempo real, e incluso ejecutar consultas SQL directamente sobre la base de datos de la app para depurar.

### 3.8. Probando nuestra Base de Datos: Testing de DAOs

NOTA: Este apartado es opcional y no te va a dar errores. ¡Buscaté la vida para que funcione! 

Para garantizar que nuestras consultas funcionan, escribimos tests unitarios para los DAOs. Estos tests se ejecutan en un entorno controlado y no necesitan un dispositivo.

Hemos creado la clase `UserDaoTest.kt` en `src/test/java/...`:

```kotlin
@RunWith(RobolectricTestRunner::class)
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        // Usa una base de datos en memoria para los tests
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetUser() = runBlocking {
        val user = User(uid = 1, ...)
        userDao.insert(user)
        val userFromDb = userDao.getUserById(1)
        assertEquals(user, userFromDb)
    }
}
```
*   `Room.inMemoryDatabaseBuilder`: Crea una base de datos temporal en RAM. Es rápida y asegura que cada test esté aislado.
*   `@Before` y `@After`: Se usan para configurar el entorno antes de cada test y para limpiarlo después.
*   `@Test`: Marca una función como un test que comprueba una funcionalidad concreta, siguiendo el patrón Preparar-Ejecutar-Comprobar (Given-When-Then).

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
