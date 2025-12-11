package com.kuvuni.examplesqlite.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

/**
 * Esta es una clase de entidad que representa la tabla 'user' en la base de datos.
 *
 * @param uid La clave primaria autoincremental.
 * @param firstName El nombre del usuario.
 * @param lastName El apellido del usuario.
 * @param age La edad del usuario. Tiene un valor por defecto de 0.
 * @param email El correo electrónico del usuario. Añadido en la versión 2 de la BBDD.
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0, // Es importante que sea var si es autoincremental y quieres que Room lo asigne

    @ColumnInfo(name = "first_name")
    val firstName: String?,

    @ColumnInfo(name = "last_name")
    val lastName: String?,

    @ColumnInfo(defaultValue = "0")
    val age: Int,

    @ColumnInfo(name = "email", defaultValue = "NULL")
    val email: String?
) {
    // Room no persistirá este campo porque no está en el constructor primario
    // y está anotado con @Ignore
    @Ignore
    val fullName: String = "$firstName $lastName"
}
