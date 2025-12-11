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
@Database(entities = [User::class], version = 2, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Paso 2: Crear el objeto de Migración
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Paso 3: Definir la operación a realizar en la migración.
                // En este caso, añadir una nueva columna 'email' a la tabla 'user'.
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
                // Paso 4: Añadir la migración al constructor de la base de datos.
                .addMigrations(MIGRATION_1_2)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
