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

/**
 * Clase de test unitario para el UserDao.
 */
@RunWith(RobolectricTestRunner::class)
class UserDaoTest {

    private lateinit var userDao: UserDao
    private lateinit var db: AppDatabase

    /**
     * Este método se ejecuta ANTES de cada test. Se encarga de crear una base de datos
     * en memoria para que cada test esté aislado y no afecte a los demás.
     */
    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // Usamos una base de datos en memoria para que los datos no persistan entre tests.
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            // Permite que Room ejecute las consultas en el hilo principal (solo para tests).
            .allowMainThreadQueries()
            .build()
        userDao = db.userDao()
    }

    /**
     * Este método se ejecuta DESPUÉS de cada test. Se encarga de cerrar la conexión
     * a la base de datos para liberar recursos.
     */
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /**
     * Test para comprobar que la inserción y la lectura de un usuario funcionan correctamente.
     */
    @Test
    @Throws(Exception::class)
    fun insertAndGetUser() = runBlocking {
        // 1. Preparamos los datos
        val user = User(uid = 1, firstName = "John", lastName = "Doe", age = 30, email = "john.doe@example.com")
        
        // 2. Ejecutamos la acción a probar (insertar el usuario)
        userDao.insert(user)
        
        // 3. Leemos el resultado
        val userFromDb = userDao.getUserById(1)
        
        // 4. Comprobamos que el resultado es el esperado (Assert)
        assertEquals(user, userFromDb)
    }
}
