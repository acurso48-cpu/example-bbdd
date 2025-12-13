package com.kuvuni.examplesqlite

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kuvuni.examplesqlite.databinding.ActivityMainBinding
import com.kuvuni.examplesqlite.db.ContactoDatabase
import com.kuvuni.examplesqlite.db.entity.User
import io.github.serpro69.kfaker.Faker
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.random.Random


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
        User(
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



