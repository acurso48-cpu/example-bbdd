# Inyección SQL: Qué es y por qué Room te protege

En la explicación sobre los parámetros de consulta como `:uid`, mencionamos que su principal propósito es prevenir la **inyección SQL**. Este es un tema de seguridad tan fundamental que merece su propio apartado.

## ¿Qué es la Inyección SQL?

La inyección SQL es una de las vulnerabilidades de seguridad más antiguas y peligrosas en el desarrollo de software. Ocurre cuando un atacante logra "inyectar" o insertar código SQL malicioso dentro de una consulta que tu aplicación no había previsto.

El objetivo del atacante es alterar la lógica de tu consulta SQL original para que la base de datos ejecute una acción diferente y no autorizada.

## El Peligro: ¿Qué puede hacer un atacante?

Si una aplicación es vulnerable a la inyección SQL, un atacante podría:

1.  **Leer datos sensibles**: Acceder a información de todos los usuarios, contraseñas, datos personales, etc.
2.  **Modificar datos**: Cambiar saldos de cuentas, actualizar roles de usuario para darse permisos de administrador, etc.
3.  **Eliminar datos**: Borrar tablas enteras o registros específicos, causando una pérdida de información irrecuperable.
4.  **Obtener control del servidor**: En algunos casos, se pueden ejecutar comandos a nivel del sistema operativo a través de la base de datos.

## Ejemplo de Código Vulnerable (Lo que NO se debe hacer)

Imaginemos que, en lugar de usar Room, estuviéramos construyendo nuestras consultas SQL a mano, concatenando cadenas. Podríamos tener una función como esta (pseudocódigo):

```kotlin
// ¡¡¡CÓDIGO VULNERABLE, NO USAR!!!
fun getUserById_INSEGURO(idUsuario: String): User {
    // Se construye la consulta concatenando el input del usuario directamente
    val consultaSQL = "SELECT * FROM user WHERE uid = " + idUsuario
    
    // Se ejecuta la consulta "cruda"
    val resultado = database.executeRawQuery(consultaSQL)
    return parsearResultado(resultado)
}
```

A primera vista, parece inofensivo. Si un usuario legítimo llama a `getUserById_INSEGURO("123")`, la consulta resultante será:
`SELECT * FROM user WHERE uid = 123`
Y todo funciona como se espera.

### El Ataque

Ahora, ¿qué pasa si un atacante, en lugar de un número, introduce el siguiente texto en el campo de `idUsuario`?

`"123; DROP TABLE user;"`

La consulta que se construiría y ejecutaría en la base de datos sería:

`SELECT * FROM user WHERE uid = 123; DROP TABLE user;`

La base de datos ve dos comandos SQL separados por un punto y coma:
1.  `SELECT * FROM user WHERE uid = 123;`: Un comando legítimo que se ejecuta sin problemas.
2.  `DROP TABLE user;`: ¡Un segundo comando, inyectado por el atacante, que **borra permanentemente la tabla de usuarios**!

Acabas de perder todos los datos de tus usuarios.

## La Solución: Parámetros de Enlace (La forma de Room)

Room nos protege de esto de manera automática y elegante usando **parámetros de enlace** (los que tienen la sintaxis de dos puntos, como `:uid`).

Veamos la forma correcta de hacerlo con Room, que es la que ya estamos usando:

```kotlin
@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserById(uid: Int): User?
}
```

Cuando llamas a `getUserById(miId)`, Room **no concatena cadenas**. En su lugar, hace algo mucho más inteligente:

1.  **Prepara la consulta**: Envía a la base de datos la plantilla de la consulta con un marcador de posición: `SELECT * FROM user WHERE uid = ?`.
2.  **Enlaza el parámetro**: Le dice a la base de datos: "Ahora, para el marcador de posición `?`, usa este valor que te estoy pasando".

Si un atacante intentara el mismo truco y lograra pasar la cadena `"123; DROP TABLE user;"` como parámetro `uid` (aunque en nuestro DAO el tipo es `Int` y fallaría, imaginemos que fuera `String`), la base de datos **no ejecutaría el `DROP TABLE`**.

La base de datos está programada para tratar cualquier cosa que se enlaza a un parámetro como un **valor literal**, no como código SQL. Buscaría un usuario cuyo `uid` fuera, literalmente, la cadena de texto `"123; DROP TABLE user;"`. No encontraría a nadie y devolvería un resultado vacío, sin causar ningún daño.

## Más Ejemplos de Ataques de Inyección SQL

El ejemplo de `DROP TABLE` es muy destructivo, pero los atacantes tienen muchos otros trucos. Veamos algunos escenarios comunes en una aplicación vulnerable.

### Escenario 1: Saltarse la Autenticación (Login Bypass)

Imagina una pantalla de login que comprueba el usuario y la contraseña.

**Código Vulnerable:**
```kotlin
// ¡¡¡CÓDIGO VULNERABLE!!!
fun autenticarUsuario(userName: String, userPass: String): Boolean {
    val consulta = "SELECT * FROM user WHERE first_name = '" + userName + "' AND password = '" + userPass + "'"
    val resultado = database.executeRawQuery(consulta)
    return resultado.hasRows() // Devuelve true si encuentra un usuario
}
```

Un usuario normal introduciría su nombre, por ejemplo, `admin`. La consulta buscaría al usuario `admin` con la contraseña proporcionada.

**El Ataque:**
Un atacante no necesita saber la contraseña. Puede introducir lo siguiente en el campo del nombre de usuario (`userName`):

`' OR 1=1 --`

Y deja la contraseña en blanco. La consulta SQL que se genera es:

`SELECT * FROM user WHERE first_name = '' OR 1=1 --' AND password = ''`

Analicemos esto:
*   `first_name = ''`: Esto es falso, pero no importa.
*   `OR 1=1`: `1=1` es **siempre verdadero**. Por lo tanto, la condición `WHERE` se convierte en `WHERE (algo falso) OR (algo verdadero)`, lo cual es siempre `TRUE`. La consulta seleccionará al primer usuario de la tabla (que suele ser el administrador).
*   `--`: Esto es un comentario en SQL. Todo lo que viene después (`' AND password = ''`) es ignorado por la base de datos.

¡El atacante acaba de iniciar sesión como el primer usuario de la base de datos sin tener la contraseña!

**Protección con Room:**
```kotlin
@Query("SELECT * FROM user WHERE first_name = :userName AND password = :userPass")
fun autenticar(userName: String, userPass: String): User?
```
Si el atacante introduce `' OR 1=1 --` como `userName`, Room lo tratará como una cadena de texto literal. Buscará un usuario cuyo nombre sea, literalmente, `' OR 1=1 --`. No lo encontrará, y el login fallará correctamente.

### Escenario 2: Extraer Datos Ocultos con `UNION`

A veces el atacante no quiere borrar datos, sino robarlos.

**Código Vulnerable:**
```kotlin
// ¡¡¡CÓDIGO VULNERABLE!!!
// Una función que busca productos por categoría y los muestra
fun buscarProductos(categoria: String): List<Producto> {
    val consulta = "SELECT nombre, descripcion FROM productos WHERE categoria = '" + categoria + "'"
    // ... ejecutar y mostrar
}
```

**El Ataque:**
El atacante introduce la siguiente cadena en el campo de `categoria`:

`' UNION SELECT first_name, email FROM user --`

La consulta resultante será:

`SELECT nombre, descripcion FROM productos WHERE categoria = '' UNION SELECT first_name, email FROM user --'`

Analicemos esto:
*   `SELECT nombre, descripcion FROM productos WHERE categoria = ''`: Esta parte probablemente no devuelva nada.
*   `UNION`: Es un comando SQL que combina los resultados de dos consultas `SELECT`.
*   `SELECT first_name, email FROM user`: Esta es la consulta inyectada. El atacante la ha diseñado para que devuelva dos columnas (`first_name`, `email`) que coinciden con las dos columnas de la consulta original (`nombre`, `descripcion`).

El resultado final es que la aplicación, que esperaba mostrar nombres y descripciones de productos, en su lugar mostrará una lista con los **nombres y correos electrónicos de todos los usuarios** de la base de datos.

**Protección con Room:**
```kotlin
@Query("SELECT nombre, descripcion FROM productos WHERE categoria = :categoria")
fun buscar(categoria: String): List<Producto>
```
De nuevo, Room buscaría una categoría de producto llamada, literalmente, `' UNION SELECT first_name, email FROM user --`. No la encontraría y no se filtraría ningún dato de usuario.

### Escenario 3: Inyección SQL a Ciegas (Blind SQL Injection)

Este es un tipo de ataque mucho más sutil. Ocurre cuando la aplicación no muestra directamente los resultados de la consulta ni errores explícitos de la base de datos. El atacante debe inferir la información poco a poco, como un juego de "frío o caliente".

**Código Vulnerable:**
```kotlin
// ¡¡¡CÓDIGO VULNERABLE!!!
// Una función que simplemente dice si un artículo existe o no, sin mostrar datos.
fun articuloExiste(idArticulo: String): Boolean {
    val consulta = "SELECT id FROM articulos WHERE id = '" + idArticulo + "'"
    val resultado = database.executeRawQuery(consulta)
    return resultado.hasRows() // Solo devuelve true/false
}
```
La aplicación no muestra ningún dato, solo un "Sí, existe" o "No, no existe". El atacante no puede usar `UNION` para ver datos de otras tablas.

**El Ataque (Basado en Tiempo):**
El atacante quiere averiguar si la tabla `user` existe. Puede inyectar una consulta que haga que la base de datos espere un tiempo determinado solo si una condición es verdadera.

Introduce esto como `idArticulo`:
`' AND (SELECT 1 FROM user LIMIT 1) = 1) AND 1=randomblob(900000000) --`
*(Esta es una técnica específica para SQLite para causar un retraso, ya que no tiene un comando `SLEEP` por defecto)*.

La consulta se convierte en algo complejo, pero la idea es:
`... WHERE id = '' AND (condición) AND (causar retraso si la condición es cierta) --`

El atacante mide el tiempo de respuesta:
*   **Si la página tarda varios segundos en cargar**: ¡Bingo! La condición fue verdadera. La tabla `user` existe.
*   **Si la página carga inmediatamente**: La condición fue falsa. La tabla `user` no existe.

Poco a poco, cambiando la condición, un atacante puede extraer nombres de tablas, nombres de columnas e incluso el contenido de las celdas, carácter por carácter (`... WHERE name LIKE 'a%'`, `... WHERE name LIKE 'b%'`, etc.). Es un proceso lento pero muy efectivo para robar datos de forma sigilosa.

**Protección con Room:**
```kotlin
@Query("SELECT EXISTS(SELECT 1 FROM articulos WHERE id = :idArticulo)")
fun articuloExiste(idArticulo: String): Boolean
```
De nuevo, si el atacante pasa la carga maliciosa como parámetro, Room buscará un `idArticulo` que sea literalmente esa cadena larguísima. No lo encontrará, la consulta devolverá `false` inmediatamente y el ataque se frustra sin que el atacante pueda inferir nada.

### Conclusión

La inyección SQL es una amenaza real y muy destructiva. Afortunadamente, al usar un ORM moderno como Room y seguir sus convenciones, estamos protegidos por defecto.

**Regla de oro**: Nunca construyas consultas SQL concatenando directamente la entrada del usuario. Usa siempre los parámetros de enlace (`:parametro`) que Room provee en su anotación `@Query`.
