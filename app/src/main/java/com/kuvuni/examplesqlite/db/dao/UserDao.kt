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
     * Esta configuración es útil para actualizar la lista de inventario,
     * que implementarás en el siguiente codelab. Debido al tipo de datos que se
     * muestra para Flow, Room también ejecuta la búsqueda en el subproceso en segundo plano.
     * No necesitas convertirla de manera explícita en una función suspend ni
     * llamar dentro del alcance de la corrutina.
     */
    @Query("SELECT * FROM user ORDER BY apellidos ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user ORDER BY nombre ASC")
    fun getAllUsersByName(): Flow<List<User>>

    @Query("SELECT * FROM user ORDER BY uid DESC")
    fun getAllUsersByUid(): Flow<List<User>>


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
