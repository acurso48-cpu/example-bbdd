package com.kuvuni.examplesqlite.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.kuvuni.examplesqlite.db.entity.User

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
     */
    @Query("SELECT * FROM user ORDER BY first_name ASC")
    fun getAllUsers(): List<User>

    /**
     * Obtiene un usuario por su ID.
     * El ":uid" en la consulta se corresponde con el parámetro uid del método.
     */
    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserById(uid: Int): User?
}
