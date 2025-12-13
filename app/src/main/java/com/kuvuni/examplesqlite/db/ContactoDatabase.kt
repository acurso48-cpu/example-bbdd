package com.kuvuni.examplesqlite.db

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
