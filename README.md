# ExampleSQLite — Guía completa para el curso (Julián González)

Este repositorio es un proyecto didáctico Android que demuestra el uso de SQLite a través de Room. Está pensado para que los alumnos analicen, ejecuten y extiendan la aplicación mientras aprenden conceptos de persistencia local, arquitectura simple y buenas prácticas.

Autor: Julián González

          _
        >(.)__
         (___/ \



Índice
- Resumen rápido
- Qué contiene el código (recorrido por los ficheros clave)
    - Entidad: `User`
    - DAO: `UserDao`
    - Database: `ContactoDatabase`
    - Repositorio: `UserRepository`
    - UI: `MainActivity` y `activity_main.xml`

---

Resumen rápido

Esta app implementa un CRUD sencillo sobre una entidad `User` usando Room. La UI ofrece botones para crear, leer, actualizar y borrar usuarios. La persistencia está organizada en capas mínimas: Entities → DAO → Repository → UI.

Recorrido por el código (qué encontré y por qué es relevante)

1) Entidad — `app/src/main/java/com/kuvuni/examplesqlite/db/entity/User.kt`
    - Clase `User` anotada con `@Entity(tableName = "user")`.
    - Campos: `uid` (PrimaryKey autogenerado), `firstName`, `lastName`, `age`, `email`, `date`, `image`.
    - Uso de `@ColumnInfo` para nombres y valores por defecto.
    - Campo `fullName` marcado con `@Ignore` (no persistente).
    - Comentario del autor que explica buenas prácticas (no almacenar blobs grandes).

2) DAO — `app/src/main/java/com/kuvuni/examplesqlite/db/dao/UserDao.kt`
    - Interfaz `UserDao` con `@Dao`.
    - Métodos: `insert`, `update`, `delete` (suspend) y varias consultas con `@Query` que devuelven `Flow` (p.ej. `getAllUsers`, `getUserById`, `getAdultUsers`).
    - Uso de `Flow` está bien explicado: Room emite cambios automáticamente y ejecuta la consulta en background.

3) Database — `app/src/main/java/com/kuvuni/examplesqlite/db/ContactoDatabase.kt`
    - Clase `ContactoDatabase` anotada `@Database(entities = [User::class], version = 1, exportSchema = true)`.
    - Singleton `getDatabase(context)` con `@Volatile` y `synchronized` para inicialización segura.
    - Uso de `.fallbackToDestructiveMigration()` por simplicidad (comentarios indicando cómo añadir migraciones: `MIGRATION_1_2`).
    - Incluye ejemplo de `Migration` que ejecuta `ALTER TABLE`.

4) Repositorio — `app/src/main/java/com/kuvuni/examplesqlite/db/repo/UserRepository.kt`
    - `UserRepository` encapsula llamadas al `UserDao`.
    - expone `Flow` para listas ordenadas y métodos `suspend` para `insert`, `update`, `delete`.
    - Buen patrón para desacoplar la UI del DAO.

5) UI — `app/src/main/java/com/kuvuni/examplesqlite/MainActivity.kt` y `app/src/main/res/layout/activity_main.xml`
    - `MainActivity` usa ViewBinding (`ActivityMainBinding`) y `lifecycleScope` para lanzar coroutines.
    - Inicializa `repository` a partir de `ContactoDatabase.getDatabase(this).userDao()`.
    - Implementa: `createUser`, `readUsers`, `updateUser`, `deleteUser`.
    - `readUsers()` usa `collect` en un Flow y actualiza un `TextView` con resultados.
    - `deleteUser()` muestra `AlertDialog` de confirmación.
    - Layout contiene EditTexts para `userId`, `firstName`, `lastName`, `age`, botones para CRUD y un `TextView` para resultados.

