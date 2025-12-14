package com.kuvuni.examplesqlite.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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
    val uid: Int = 0,

    @ColumnInfo(name = "nombre")
    var firstName: String?,

    @ColumnInfo(name = "apellidos")
    var lastName: String?,

    //defaultValue debe ser una constante
    @ColumnInfo(name = "edad", defaultValue = "0")
    var age: Int,

    @ColumnInfo(defaultValue = "NULL")
    var email: String?,

    //@ColumnInfo()
    // val email: String? = null,

    // @ColumnInfo(defaultValue = "'DESCONOCIDO'")
    // val email: String,

    @ColumnInfo(name = "fecha_creacion")
    var date: Long,

    @ColumnInfo(name = "avatar")
    var image: ByteArray?, //No almacenar Blob muy grandes, mejor usar un enlace a las imágenes o comprimir las imágenes.

) {

    // Room no persistirá este campo porque no está en el constructor primario
    // y está anotado con @Ignore
    @Ignore
    val fullName: String = "$firstName $lastName"

    // Constructor secundario
    constructor(firstName: String?, lastName: String?, age: Int, date: Long) :
            this(0, firstName, lastName, age, null, date, null)

}
