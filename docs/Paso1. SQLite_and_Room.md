# Guía Completa de SQLite y Room en Android

## Parte 1: Introducción a la Persistencia

En la mayoría de las aplicaciones, no basta con que los datos existan mientras la app está abierta. Los usuarios esperan que su información (sus contactos, sus notas, el progreso de su juego) siga ahí cuando vuelvan a abrir la aplicación. A esta capacidad de guardar datos de forma permanente la llamamos **persistencia**.

En Android, la solución más robusta y estándar para la persistencia de datos estructurados es el uso de una base de datos. La base de datos que viene integrada en cada dispositivo Android se llama **SQLite**.

## Parte 2: El Camino Moderno - Room

Trabajar directamente con SQLite puede ser complicado y propenso a errores. Por ello, Google creó **Room**, una librería que actúa como una capa de abstracción sobre SQLite. Room nos facilita enormemente la vida, proporcionando una API más clara, más segura y que requiere escribir mucho menos código.

Room se encarga del trabajo pesado y nosotros nos centramos en lo importante: definir nuestros datos y cómo queremos interactuar con ellos.

### 3.1. Configuración del Proyecto con Room

Antes de poder usar la magia de Room, necesitamos configurar nuestro proyecto.

#### KSP (Kotlin Symbol Processing)

Cuando trabajas con Room, escribes anotaciones como `@Entity` y `@Dao`. KSP es una herramienta que se ejecuta durante la compilación, lee estas anotaciones y **genera código Kotlin automáticamente** basándose en ellas. Gracias a KSP, nosotros solo escribimos la parte "declarativa" (qué queremos) y Room escribe la implementación (el cómo) por nosotros.

**1. El Catálogo de Versiones (`gradle/libs.versions.toml`)**

Una buena práctica es centralizar las dependencias en el catálogo de versiones. Así hemos configurado el nuestro para incluir Room y KSP.

```toml
[versions]
agp = "8.13.1"
kotlin = "2.0.0"
ksp = "2.0.0-1.0.21"
room = "2.6.1"


[libraries]
# Room
androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }


[plugins]
# ...
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
```

**2. El Fichero de Build del Módulo (`app/build.gradle.kts`)**

Con el catálogo definido, aplicar los plugins y añadir las dependencias es muy limpio. Incluimos también las dependencias de testing y `Faker`.

```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp) // Plugin de KSP
}

// ...

dependencies {
    // Dependencias básicas...
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    //...

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Faker
    implementation("io.github.serpro69:kotlin-faker:1.12.0")

    // Room testing
    testImplementation("androidx.room:room-testing:2.6.1")

    // Librerías para Tests Unitarios
    testImplementation("androidx.test:core-ktx:1.6.1")
    testImplementation(libs.robolectric)

    // Coroutines testing
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")
}
```
Después de estos cambios, es crucial sincronizar el proyecto con Gradle.

### 3.2. Definiendo la Entidad (`@Entity`)

El primer paso es definir nuestras tablas. En Room, cada tabla se representa con una `data class` anotada con `@Entity`. Hemos actualizado la clase `User.kt` para reflejar la estructura final.

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/entity/User.kt
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Int = 0,

    @ColumnInfo(name = "nombre")
    val firstName: String?,

    @ColumnInfo(name = "apellidos")
    val lastName: String?,

    //defaultValue debe ser una constante
    @ColumnInfo(name = "edad", defaultValue = "0")
    val age: Int,

    @ColumnInfo(defaultValue = "NULL")
    val email: String?,

    //@ColumnInfo()
    // val email: String? = null,

    // @ColumnInfo(defaultValue = "'DESCONOCIDO'")
    // val email: String,

    @ColumnInfo(name = "fecha_creacion")
    val date: Long,

    @ColumnInfo(name = "avatar")
    val image: ByteArray?, //No almacenar Blob muy grandes, mejor usar un enlace a las imágenes o comprimir las imágenes.

) {
    // Room no persistirá este campo porque no está en el constructor primario
    // y está anotado con @Ignore
    @Ignore
    val fullName: String = "$firstName $lastName"
}

```
*   `@Entity`: Marca la clase como una tabla.
*   `@PrimaryKey(autoGenerate = true)`: Define la clave primaria autoincremental.
*   `@ColumnInfo`: Personaliza la columna (nombre, valor por defecto). Hemos añadido `creation_date` (un `Long` para la marca de tiempo) y `profile_image` (un `ByteArray?` para almacenar datos binarios como una imagen).

### 3.3. Accediendo a los Datos: El DAO (`@Dao`) y el Poder de `Flow`

El DAO (Data Access Object) es la interfaz donde definimos cómo interactuar con los datos. La hemos modificado para usar **Kotlin Flow**, lo que hace nuestra base de datos **reactiva**.

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/dao/UserDao.kt
package com.kuvuni.examplesqlite.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kuvuni.examplesqlite.db.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para la entidad User.
 * Aquí se definen todos los métodos para acceder a la tabla 'user'.
 */
@Dao
interface UserDao {

    /**
     * Inserta uno o más usuarios en la base de datos.
     * OnConflictStrategy.IGNORE: Si el usuario que se intenta insertar ya existe (misma clave primaria),
     * simplemente se ignora la operación de inserción.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    /**
     * Actualiza un usuario existente en la base de datos.
     * Room utiliza la clave primaria para encontrar el usuario a actualizar.
     */
    @Update
    suspend fun update(user: User)

    /**
     * Borra un usuario de la base de datos.
     * Room utiliza la clave primaria para encontrar el usuario a borrar.
     */
    @Delete
    suspend fun delete(user: User)

    /**
     * Obtiene todos los usuarios de la tabla, ordenados por nombre.
     * La anotación @Query permite escribir cualquier consulta SQL.
     * Room la valida en tiempo de compilación.
     * Se recomienda usar Flow en la capa de persistencia.
     * Con Flow como el tipo de datos que se muestra, recibirás una notificación
     * cada vez que cambien los datos de la base de datos.
     * Room mantiene este Flow actualizado por ti, lo que significa que solo
     * necesitas obtener los datos de forma explícita una vez.
     * Esta configuración es útil para actualizar la lista de contactos,
     * Debido al tipo de datos que se
     * muestra para Flow, Room también ejecuta la búsqueda en el subproceso en segundo plano.
     * No necesitas convertirla de manera explícita en una función suspend ni
     * llamar dentro del alcance de la corrutina.
     */
    @Query("SELECT * FROM user ORDER BY apellidos ASC")
    fun getAllUsers(): Flow<List<User>>

    /**
     * Obtiene un usuario por su ID.
     * El ":uid" en la consulta se corresponde con el parámetro uid del método.
     */
    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserById(uid: Int): Flow<User>

    /**
     * Obtiene todos los usuarios que son mayores de edad (18 años o más).
     * @return Un Flow que emite una lista de usuarios mayores de edad.
     */
    @Query("SELECT * FROM user WHERE edad >= 18")
    fun getAdultUsers(): Flow<List<User>>

}
```
*   **¿Por qué `Flow`?**: Al devolver un `Flow`, Room nos envía los datos actualizados **automáticamente** cada vez que cambian en la base de datos. Si insertas, actualizas o eliminas un usuario, cualquier colector activo del `Flow` recibirá la nueva lista de usuarios sin tener que volver a pedirla. Esto es fundamental para mantener la UI sincronizada con los datos.
*   `suspend`: Indica que la operación puede ser larga y debe ejecutarse en un hilo de fondo.

### 3.4. Uniendo las Piezas: La Clase `Database`

La clase `@Database` une las entidades y los DAOs. La hemos renombrado a `ContactoDatabase` y añadido las **migraciones** necesarias (si las hay) para los cambios en la entidad `User`.

```kotlin
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kuvuni.examplesqlite.db.dao.UserDao
import com.kuvuni.examplesqlite.db.entity.User

/**
 * La clase principal de la base de datos. Debe ser abstracta y extender RoomDatabase.
 * Anotada con @Database, lista todas las entidades y la versión de la base de datos.
 */
// Paso 1: Incrementar la versión de la BBDD a 2. Es buena práctica exportar el esquema.
@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class ContactoDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        /**
        El valor de una variable volátil nunca se almacena en caché,
        y todas las lecturas y escrituras son desde y hacia la memoria principal.
        Estas funciones ayudan a garantizar que el valor de Instance esté siempre actualizado
        y sea el mismo para todos los subprocesos de ejecución.
        Eso significa que los cambios realizados por un subproceso en Instance son
        visibles de inmediato para todos los demás subprocesos.
         */
        @Volatile
        private var Instance: ContactoDatabase? = null

        /**
         *
         */

        fun getDatabase(context: Context): ContactoDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ContactoDatabase::class.java, "user_database")
                    /**
                     * Como esta es una app de ejemplo, una alternativa simple es
                     * destruir y volver a compilar la base de datos, lo que significa que se
                     * pierden los datos de inventario. Por ejemplo, si cambias algo en la clase
                     * de entidad, como agregar un parámetro nuevo,
                     * puedes permitir que la app borre y vuelva a inicializar la base de datos.
                     */
                    .fallbackToDestructiveMigration()
                    // .addMigrations(MIGRATION_1_2) // Opción: añadir migraciones, cuando queremos mantener los datos y añadir algún campo.
                    .build()
                    /**
                     * also es una función de extensión en Kotlin que ejecuta un bloque de código sobre un objeto.
                     * Su principal característica es que, después de ejecutar el bloque,
                     * devuelve el objeto original sin modificarlo.
                     * Piensa en also como una forma de decir: "Haz esto con el objeto,
                     * y además (also), haz esta otra cosa con él".
                     */
                    .also { Instance = it } //
            }
        }

        // Opción: Crear el objeto de Migración, si cambiamos la estructura de la BBDD.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Definir la operación a realizar en la migración.
                // En este caso, añadir una nueva columna 'nif' a la tabla 'user'.
                database.execSQL("ALTER TABLE user ADD COLUMN nif TEXT")
            }
        }


    }
}
```
*   `version = 3`: Hemos incrementado la versión para reflejar los nuevos cambios en la estructura (schema).
*   `Migration`: Cada vez que cambias la estructura de una tabla, debes proporcionar una migración que le explique a Room cómo pasar de la versión antigua a la nueva sin perder datos.
*   **Patrón Singleton**: El `companion object` asegura que solo exista **una única instancia** de la base de datos en toda la app, lo cual es crucial para el rendimiento y la consistencia de los datos.

### 3.5. Operaciones CRUD en `MainActivity`

Hemos modificado `MainActivity.kt` para realizar todas las operaciones CRUD (Crear, Leer, Actualizar, Borrar), demostrando el uso de `Flow` y las corrutinas, e incluyendo tus excelentes comentarios.

#### Inicialización de Propiedades: `lateinit` vs. `by lazy`

En `MainActivity`, se usan dos estrategias de inicialización de variables que son clave para un código eficiente y seguro:

*   **`private lateinit var binding: ActivityMainBinding`**
    *   **¿Por qué `lateinit` aquí?**
        1.  **Dependencia del Framework**: No se puede inicializar en la declaración porque necesita el `layoutInflater` de la `Activity`, que solo está disponible a partir de `onCreate`.
        2.  **Certeza de Inicialización**: Tienes la garantía de que la variable se inicializará en `onCreate` antes de ser usada.

*   **`private val db by lazy { ContactoDatabase.getDatabase(this) }`**
    *   **¿Por qué `by lazy` aquí?**
        1.  **Creación Costosa**: Acceder a la base de datos es una operación pesada. `by lazy` la retrasa hasta su primer uso, evitando trabajo innecesario si la `Activity` se muestra pero no interactúa con la BD.
        2.  **Inicialización Única y Reutilizable (`val`)**: `by lazy` garantiza que `getDatabase(this)` se llame solo una vez. En usos posteriores, se devuelve la instancia ya creada, asegurando el patrón Singleton.

#### Código de Ejemplo en `MainActivity.kt`

```kotlin
class MainActivity : AppCompatActivity() {

    /**
     * lateinit var binding: ActivityMainBinding
     * •¿Por qué lateinit aquí?
     * i.     No puedes inicializarlo en la declaración.
     * ii.    Sabes que lo inicializarás pronto: Tienes la certeza de que justo al principio de onCreate.
     * iii.   Es una dependencia del Framework: El ciclo de vida de Android dicta cuándo puedes crear el binding.
     *
     * by lazy { ContactoDatabase.getDatabase(this) }
     * i.    Creación Costosa: Acceder a la base de datos es una operación que consume recursos.
     *       No tiene sentido hacerla si, por ejemplo, el usuario abre la pantalla y la cierra sin
     *       interactuar con nada que necesite la BD.
     * ii.  Inicialización Única y Reutilizable: Quieres una única instancia de la base de datos (val).
     *      by lazy garantiza que ContactoDatabase.getDatabase(this) se llame solo una vez.
     *      La primera vez que uses db, se creará; las siguientes veces, se te devolverá la instancia ya creada.
     * iii. Es Inmutable (val): Una vez que se crea la instancia de la base de datos,
     *      no quieres que cambie. by lazy solo funciona con val, lo que refuerza esta seguridad.
     */
    private lateinit var binding: ActivityMainBinding
    private val db by lazy { ContactoDatabase.getDatabase(this) }
    private val userDao by lazy { db.userDao() }

    //https://github.com/serpro69/kotlin-faker
    private val faker = Faker()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val user = User(
            firstName = "Patito",
            lastName = "Julián",
            age = 25,
            email = "acurso48Patito@gmail.com",
            date = System.currentTimeMillis(),
            image = null
        )
        lifecycleScope.launch {
            //userDao.insert(user)
            /*generateFakeUsers(20).forEach {
                userDao.insert(it)
            }*/

            // Leer todos los usuarios
            val TAG = "Lectura datos"
            val users = userDao.getAllUsers()

            users.firstOrNull { //Es un flow, por lo que usamos firstOrNull
                it.forEach {
                    Log.d(
                        TAG,
                        "Nombre: ${it.firstName}, Apellidos: ${it.lastName}, Edad: ${it.age} Email: ${it.email}")
                }
                true
            }

            // Actualizar usuario Opción 1
            // Define los nuevos datos para el usuario con uid = 1
            val updatedUser = User(
                uid = 1, // ¡Importante! Este es el ID del usuario que quieres actualizar
                firstName = "Patito",
                lastName = "Actualizado",
                age = 30,
                email = "patito.actualizado@gmail.com",
                date = System.currentTimeMillis(), // Opcional: actualizar la fecha
                image = null
            )
            // Llama al método update del DAO
            userDao.update(updatedUser)


            // Actualizar usuario Opción 2. Recomendado
            // 1. Obtenemos el Flow del usuario con uid = 1
            val userFlow = userDao.getUserById(4)

            /* 2. Coleccionamos el Flow y obtenemos el usuario
            * let te permite escribir de forma concisa y segura: "Si este objeto no es nulo, haz esto con él".
            * Es una alternativa a if (user != null) {
            *    // ... haz algo con 'user' ...
            *  }
            */
            userFlow.firstOrNull()?.let { user ->
                // 3. Creamos una copia con los cambios
                val updatedUser = user.copy(
                    firstName = "Patito",
                    lastName = "Modificado2"
                    //Otros campos modificados aquí.
                )
                // 4. Actualizamos en la base de datos
                userDao.update(updatedUser)
                // userDao.delete(user)

            }

            // Más conciso. Actualización (update)
            val userToUpdate = userDao.getUserById(3).firstOrNull()
            userToUpdate?.let {
                val updatedUser = it.copy(
                    firstName = "Pepito",
                    lastName = "Plaza de Toros"
                    //Otros campos modificados aquí.
                )
                userDao.update(updatedUser)
            } ?: run {
                //Código para ejecutar si userToUpdate es nulo
            }

            //Delete.

            val userToDelete = userDao.getUserById(8).firstOrNull()
            userToDelete?.let {
                userDao.delete(it)
            }
        }

        /**
         * Genera una lista de usuarios falsos utilizando la librería Faker.
         * @param count El número de usuarios a crear.
         * @return Una lista de objetos [User].
         */
        fun generateFakeUsers(count: Int): List<User> {
            val userList = mutableListOf<User>()
            repeat(count) {

                val user = User(
                    firstName = faker.name.firstName(),
                    lastName = faker.name.lastName(),
                    age = Random.nextInt(16, 80),
                    email = faker.internet.email(), // Genera un email,
                    date = System.currentTimeMillis(),
                    image = null // Dejamos la imagen como nula por ahora
                )
                userList.add(user)
            }
            return userList
        }
    }
}

```

**Análisis del Código:**

1.  **`lifecycleScope.launch`**: Todas las operaciones de base de datos (que son funciones `suspend`) se ejecutan dentro de una corrutina asociada al ciclo de vida de la `Activity` para no bloquear el hilo principal.
2.  **Create**: Usamos la librería `Faker` para generar una lista de usuarios con datos realistas para poblar la base de datos fácilmente durante el desarrollo.
3.  **Read**: Usamos `.firstOrNull()` sobre el `Flow` para obtener una única instantánea de los datos. Es una forma eficiente de leer el estado actual sin necesidad de observar cambios futuros.
4.  **Update/Delete**: El patrón más seguro y recomendado para estas operaciones es:
    *   Obtener el `Flow` del objeto con `getUserById(id)`.
    *   Usar `.firstOrNull()` para obtener el objeto actual del `Flow`.
    *   Usar `let` con una llamada segura (`?.`) para evitar `NullPointerExceptions` si el usuario no existe.
    *   Para actualizar, la función `.copy()` de las `data class` permite crear una instancia modificada de forma inmutable.
    *   Finalmente, se pasa el objeto al método `update()` o `delete()` del DAO.
