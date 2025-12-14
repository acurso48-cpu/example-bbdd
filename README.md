# ExampleSQLite — Guía completa para el curso (Julián González)

Este repositorio es un proyecto didáctico Android que demuestra el uso de SQLite a través de Room. Está pensado para que los alumnos analicen, ejecuten y extiendan la aplicación mientras aprenden conceptos de persistencia local, arquitectura simple y buenas prácticas.

Autor: Julián González

🦆 Patito de goma (por si quieres poner una sonrisa en clase)

ASCII:

          _
        >(.)__
         (___/ \

Imagen (pública):

![Patito de goma](https://upload.wikimedia.org/wikipedia/commons/5/57/Rubber_duck.jpg)

Índice
- Resumen rápido
- Qué contiene el código (recorrido por los ficheros clave)
  - Entidad: `User`
  - DAO: `UserDao`
  - Database: `ContactoDatabase`
  - Repositorio: `UserRepository`
  - UI: `MainActivity` y `activity_main.xml`
- Cómo compilar y ejecutar (Windows / PowerShell)
- Notas didácticas y actividades sugeridas
- Errores y puntos a vigilar (kapt / generación de código / migraciones)
- Mejoras recomendadas para el curso
- Recursos y lectura adicional

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

Puntos importantes que remarcar con los alumnos
- Flujo de datos: Room + Flow hace que la UI reciba cambios reactivos.
- Uso de `suspend` y `lifecycleScope` para operaciones de escritura.
- `fallbackToDestructiveMigration()` está bien para demos, pero explicar riesgos (pérdida de datos) y cómo implementar migraciones reales.
- `ViewBinding` simplifica la manipulación de vistas y evita castings manuales.

Cómo compilar y ejecutar (Windows / PowerShell)

1) Abrir PowerShell en la raíz del repo: `D:\CursoAndroid25\ExampleSQLite`

2) Limpiar y compilar (debug):

```powershell
# Limpiar
.\gradlew clean
# Compilar la variante debug (genera APK sin ofuscación)
.\gradlew assembleDebug
```

3) Instalar en dispositivo/emulador conectado:

```powershell
.\gradlew installDebug
```

4) Compilar y ver salida detallada (útil para depuración de KAPT/Room):

```powershell
.\gradlew :app:assembleDebug --info
```

Notas sobre `assembleDebug` y `kapt` (resumen para los alumnos)
- `assembleDebug` es la tarea de Gradle que construye la variante `debug`: compila código, procesa recursos, ejecuta annotation processors y genera el APK en `app\build\outputs\apk\debug\`.
- `kapt` es la herramienta de Kotlin para ejecutar procesadores de anotaciones (Room genera código). Asegúrate de aplicar `kotlin("kapt")` y declarar `kapt("androidx.room:room-compiler:VERSION")` en `build.gradle.kts`.

Errores comunes y cómo enseñarlos en clase
- Olvidar `kapt` -> errores sobre clases generadas no encontradas.
- Cambiar una entidad sin migración y tener `fallbackToDestructiveMigration()` -> pérdida de datos (explicar migraciones).
- Llamar a DAO en hilo principal (evitar, Room y Flow ayudan pero las escrituras deben ser `suspend`).

Actividades sugeridas (sesiones de clase)
- Sesión 1: Identificar las capas (Entity, DAO, Repo, UI) y explicar responsabilidades.
  - Tarea: Dibujar el esquema de la tabla `user` y explicar cada columna.

- Sesión 2: Añadir un campo a `User` (p.ej. `phone`) y crear la migración `1 -> 2`.
  - Tarea: Implementar `MIGRATION_1_2` y cambiar `version` a 2 en `ContactoDatabase`.

- Sesión 3: Testeo de DAOs
  - Tarea: Escribir tests unitarios usando `Room.inMemoryDatabaseBuilder` y las utilidades de `room-testing`.

- Sesión 4: Inyección de dependencias
  - Tarea avanzada: Refactorizar para usar Hilt y proporcionar `ContactoDatabase` y `UserRepository` mediante DI.

Mejoras sugeridas (para proyectos/PR de alumnos)
- Sustituir `fallbackToDestructiveMigration()` por migraciones controladas y tests de migración.
- Añadir validaciones (p.ej. email) y manejo de errores en la UI.
- Extraer la capa de datos a un módulo independiente para facilitar pruebas y reuso.
- Añadir pruebas instrumentadas e integradas.
- Implementar una arquitectura más limpia (ViewModel + StateFlow/LiveData + Repository) si se desea más separación.

Checklist de calidad rápida (para revisar en un PR)
- [ ] El proyecto compila (`.\gradlew assembleDebug`).
- [ ] No hay llamadas a la BD en el hilo principal.
- [ ] Se han añadido migraciones cuando se cambia una entidad.
- [ ] Se han añadido tests mínimos para DAO.

Estructura relevantes del repo
- `app/src/main/java/com/kuvuni/examplesqlite/db/entity/User.kt`
- `app/src/main/java/com/kuvuni/examplesqlite/db/dao/UserDao.kt`
- `app/src/main/java/com/kuvuni/examplesqlite/db/ContactoDatabase.kt`
- `app/src/main/java/com/kuvuni/examplesqlite/db/repo/UserRepository.kt`
- `app/src/main/java/com/kuvuni/examplesqlite/MainActivity.kt`
- `app/src/main/res/layout/activity_main.xml`
- `docs/` — materiales de apoyo ya incluidos en el repositorio (útiles para tus lecciones)

Recursos y enlaces
- Room: https://developer.android.com/training/data-storage/room
- KAPT: https://kotlinlang.org/docs/kapt.html
- Gradle Tasks: https://docs.gradle.org/current/userguide/intro_tasks.html

¿Quieres que:
- Genere un `docs/README-teaching.md` más orientado a las diapositivas y ejercicios paso a paso?
- Cree `docs/paso1V2.md` integrando y ampliando `Paso1.md` con todo el código y comentarios como profesor?
- O que prepare una PR modelo con las mejoras (migración de ejemplo, tests de DAO y Hilt)?

Dime qué prefieres y lo implemento (puedo crear archivos en `docs/` o modificar `app/` según la opción).
