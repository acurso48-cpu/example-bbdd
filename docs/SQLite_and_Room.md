# SQLite y Room en Android

Esta es una explicación de lo que son SQLite y Room y cómo se usan en el desarrollo de Android.

## Parte 3: El Camino Moderno - Room

Room es una capa de abstracción sobre SQLite que facilita el trabajo con bases de datos en Android. Proporciona una API más sencilla y potente, al mismo tiempo que aprovecha toda la potencia de SQLite.

### 3.1. Configuración del Proyecto con Room

(Se explica cómo añadir las dependencias de Room y KSP al proyecto en el fichero `build.gradle.kts`)

### 3.2. Definiendo la Estructura de Datos: La Entidad (`@Entity`)

(Se explica cómo crear la clase `User` con la anotación `@Entity` y sus propiedades)

### 3.3. Accediendo a los Datos: El DAO (`@Dao`)

(Se explica cómo crear la interfaz `UserDao` con las anotaciones `@Insert`, `@Query`, etc.)

### 3.4. Uniendo las Piezas: La Clase `Database` (`@Database`)

(Se explica cómo crear la clase `AppDatabase` que hereda de `RoomDatabase` y une las entidades y DAOs)

### 3.5. Poniéndolo todo en Marcha: Usando la Base de Datos

(Se muestra un ejemplo de cómo obtener la base de datos y usar el DAO desde una `Activity`)

### 3.6. Evolucionando la Base de Datos: Migraciones

Las aplicaciones nunca son estáticas; siempre están evolucionando. Imagina que publicamos nuestra aplicación, y miles de usuarios ya han guardado sus datos. Ahora, en la versión 2.0 de la app, nos damos cuenta de que necesitamos guardar también el **email** de cada usuario.

¿Qué hacemos? Simplemente vamos a nuestra clase `User.kt` y añadimos el campo `email`.

**El Problema: `IllegalStateException`**

Si solo hacemos eso, la próxima vez que un usuario actualice la app, esta se romperá con un error `IllegalStateException`. ¿Por qué? Porque Room es muy listo: 

1.  Ve que la base de datos en el dispositivo del usuario es de la `version 1`.
2.  Pero ve que el código de la app ahora espera una tabla `user` con una columna `email` (un esquema que corresponde a una `version 2`). 
3.  Como no le hemos dicho **cómo pasar de la versión 1 a la 2**, Room no sabe qué hacer y, para prevenir la corrupción de datos, falla estrepitosamente.

**La Solución Profesional: `Migration`**

Una migración es una clase donde le damos a Room las instrucciones SQL exactas para transformar el esquema de una versión a otra sin perder los datos de los usuarios. 

Vamos a ver los pasos que hemos seguido en nuestro proyecto para añadir la columna `email`.

**Paso 1: Modificar la Entidad**

Primero, actualizamos nuestra clase `User.kt` para que incluya el nuevo campo.

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/entity/User.kt
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0,

    // ... otros campos ...
    val age: Int,

    // Nuevo campo añadido
    @ColumnInfo(name = "email", defaultValue = "NULL")
    val email: String?
)
```

**Paso 2, 3 y 4: Implementar la Migración en `AppDatabase.kt`**

Ahora viene la parte central. Modificamos nuestra clase `AppDatabase` para enseñarle a Room cómo manejar el cambio.

```kotlin
// app/src/main/java/com/kuvuni/examplesqlite/db/AppDatabase.kt

// 1. Incrementamos la versión de la BBDD a 2
@Database(entities = [User::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // 2. Creamos un objeto Migration para ir de la versión 1 a la 2.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 3. Le decimos a SQLite qué hacer: añadir la columna 'email'.
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
                // 4. Añadimos nuestra migración al constructor de Room.
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

Analicemos los puntos clave de este código:

1.  **`version = 2`**: Hemos incrementado el número de versión en la anotación `@Database`. Esto es lo que le sirve a Room como "disparador" para buscar una migración.
2.  **`Migration(1, 2)`**: Creamos un objeto `Migration`, especificando la versión de inicio y la versión de destino. Le estamos diciendo a Room: "Estas son las instrucciones para actualizar desde la v1 a la v2".
3.  **`database.execSQL(...)`**: Dentro del método `migrate`, escribimos la sentencia SQL necesaria. `ALTER TABLE user ADD COLUMN email TEXT` es el comando estándar de SQL para añadir una nueva columna a una tabla existente.
4.  **`.addMigrations(MIGRATION_1_2)`**: Finalmente, registramos nuestro objeto de migración en el constructor de la base de datos. Ahora, cuando Room se inicie y vea que la base de datos del dispositivo es v1 pero el código espera v2, buscará en la lista de migraciones, encontrará `MIGRATION_1_2` y la ejecutará.

Gracias a este proceso, la app se actualizará de forma transparente para el usuario, su base de datos se modificará para incluir la nueva columna `email` y, lo más importante, **no perderá ninguno de los datos que ya tenía guardados**.

## Conclusión

Entender y saber implementar migraciones es una habilidad no negociable para un desarrollador Android profesional. Separa las aplicaciones de juguete de las aplicaciones robustas y mantenibles a largo plazo. Room convierte este proceso, que antes era complejo y propenso a errores, en una tarea ordenada y segura.
