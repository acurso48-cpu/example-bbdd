# Guía Maestra del Database Inspector: Depura tus Bases de Datos como un Profesional

## 1. ¿Qué es el Database Inspector y por qué es tan importante?

El **Database Inspector** es una herramienta integrada en Android Studio que te proporciona una ventana directa y en tiempo real a las bases de datos SQLite de tu aplicación. 

Antes de esta herramienta, depurar bases de datos era un proceso tedioso que a menudo requería usar `adb` para copiar el archivo de la base de datos del dispositivo a tu ordenador y luego usar un cliente de SQLite externo para inspeccionarlo. Era un proceso lento y solo te daba una "foto" estática de los datos en un momento concreto.

El Database Inspector lo cambia todo, permitiéndote:

*   **Ver** el esquema de la base de datos (tablas, columnas, tipos de datos).
*   **Observar** los datos en tiempo real mientras la aplicación se ejecuta.
*   **Modificar** los datos al vuelo.
*   **Ejecutar** consultas SQL personalizadas directamente en la base de datos del dispositivo o emulador.

Es una herramienta indispensable para verificar que tus operaciones de Room (`@Insert`, `@Update`, `@Query`, etc.) funcionan exactamente como esperas.

## 2. Abriendo el Database Inspector

1.  **Ejecuta tu aplicación** en un emulador o un dispositivo físico con **API 26 o superior**.
2.  En la barra de herramientas inferior de Android Studio, haz clic en **App Inspection**.
3.  En la ventana de App Inspection, asegúrate de que el proceso de tu aplicación esté seleccionado.
4.  Haz clic en la pestaña **Database Inspector**.

Verás una lista de las bases de datos activas a la izquierda. En nuestro proyecto, verás `user_database`.

## 3. La Interfaz del Inspector: Un Vistazo Rápido

La interfaz se divide en tres partes principales:

1.  **Panel de Bases de Datos y Tablas (Izquierda)**: Un árbol jerárquico que muestra las bases de datos y las tablas que contienen. Aquí seleccionas qué tabla quieres inspeccionar.
2.  **Panel de Datos (Centro)**: Muestra el contenido de la tabla seleccionada en un formato de filas y columnas. 
3.  **Panel de Consultas (Derecha, o como nueva pestaña)**: Un editor de texto donde puedes escribir y ejecutar tus propias consultas SQL.

## 4. Ejemplos Prácticos de Consultas

Aquí es donde reside el verdadero poder del Database Inspector. Haz clic en el botón **Open New Query** para abrir un editor y probar estos ejemplos.

*Supongamos que nuestra tabla `user` tiene las columnas: `uid`, `first_name`, `last_name`, `age`, `email` y `password`.*

### Consultas de Selección (`SELECT`)

**1. Ver todos los datos de la tabla**

```sql
SELECT * FROM user;
```

**2. Seleccionar solo columnas específicas**

Para ver únicamente el nombre y el correo de todos los usuarios:

```sql
SELECT first_name, email FROM user;
```

**3. Filtrar datos con `WHERE`**

*   Encontrar un usuario por su `uid`:
    ```sql
    SELECT * FROM user WHERE uid = 5;
    ```
*   Encontrar todos los usuarios mayores de 30 años:
    ```sql
    SELECT * FROM user WHERE age > 30;
    ```
*   Encontrar usuarios con un nombre específico (las cadenas de texto van entre comillas simples):
    ```sql
    SELECT * FROM user WHERE first_name = 'Ada';
    ```

**4. Combinar múltiples condiciones (`AND` / `OR`)**

*   Encontrar usuarios llamados "Ada" que además tengan más de 30 años:
    ```sql
    SELECT * FROM user WHERE first_name = 'Ada' AND age > 30;
    ```
*   Encontrar usuarios que se llamen "Ada" o se apelliden "Lovelace":
    ```sql
    SELECT * FROM user WHERE first_name = 'Ada' OR last_name = 'Lovelace';
    ```

**5. Ordenar los resultados con `ORDER BY`**

*   Ordenar todos los usuarios por edad, de menor a mayor (`ASC` es el orden por defecto):
    ```sql
    SELECT * FROM user ORDER BY age ASC;
    ```
*   Ordenar todos los usuarios por nombre, en orden alfabético inverso (`DESC`):
    ```sql
    SELECT * FROM user ORDER BY first_name DESC;
    ```

**6. Limitar el número de resultados con `LIMIT`**

Para obtener solo los 5 primeros usuarios de la tabla:

```sql
SELECT * FROM user LIMIT 5;
```

**7. Búsqueda de patrones con `LIKE`**

El operador `LIKE` es muy útil para buscar texto. El símbolo `%` actúa como un comodín.

*   Encontrar todos los usuarios cuyo correo electrónico termine en `@lovelace.com`:
    ```sql
    SELECT * FROM user WHERE email LIKE '%@lovelace.com';
    ```
*   Encontrar todos los usuarios cuyo nombre empiece por la letra 'A':
    ```sql
    SELECT * FROM user WHERE first_name LIKE 'A%';
    ```
*   Encontrar todos los usuarios que contengan la letra 'a' en su apellido:
    ```sql
    SELECT * FROM user WHERE last_name LIKE '%a%';
    ```

### Consultas de Agregación

Estas consultas no devuelven filas de datos, sino un único valor calculado a partir de las filas.

**1. Contar el número total de usuarios (`COUNT`)**

```sql
SELECT COUNT(*) FROM user;
```

**2. Calcular la edad media de todos los usuarios (`AVG`)**

```sql
SELECT AVG(age) FROM user;
```

**3. Encontrar la edad máxima y mínima (`MAX` / `MIN`)**

```sql
-- Edad del usuario más viejo
SELECT MAX(age) FROM user;

-- Edad del usuario más joven
SELECT MIN(age) FROM user;
```

### Consultas de Modificación de Datos

**¡CUIDADO!** Estas consultas modifican los datos de tu base de datos de depuración. Son muy útiles para probar escenarios, pero úsalas con precaución.

**1. Actualizar datos (`UPDATE`)**

*   Cambiar el apellido de un usuario específico.
    ```sql
    UPDATE user SET last_name = 'Smith' WHERE uid = 1;
    ```
*   Incrementar la edad de todos los usuarios en 1 año.
    ```sql
    UPDATE user SET age = age + 1;
    ```

**2. Eliminar datos (`DELETE`)**

*   Eliminar un usuario específico por su `uid`.
    ```sql
    DELETE FROM user WHERE uid = 3;
    ```
*   Eliminar todos los usuarios que no tengan un correo electrónico definido.
    ```sql
    DELETE FROM user WHERE email IS NULL;
    ```
*   **¡PELIGRO!** Eliminar todas las filas de la tabla (úsalo solo si sabes lo que haces).
    ```sql
    DELETE FROM user;
    ```

## 5. El Modo "Live Updates"

Una de las características más potentes es la casilla **Live updates**. Si la marcas, la tabla de datos se refrescará automáticamente cada vez que tu aplicación realice una operación de escritura en la base de datos. 

Esto te permite, por ejemplo, pulsar un botón en tu app que guarda un nuevo usuario y ver al instante cómo aparece la nueva fila en el Database Inspector, confirmando que tu lógica de inserción funciona correctamente.

Esta guía te proporciona una base sólida para empezar a usar el Database Inspector. La mejor manera de aprender es experimentar: ¡lanza tu app y empieza a jugar con las consultas!
