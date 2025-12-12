# Guía Detallada para Practicar SQL Online

La teoría sobre SQL es fundamental, pero la única forma real de dominar este lenguaje es escribiendo consultas. Afortunadamente, no necesitas instalar un sistema complejo en tu ordenador para empezar. ¡Puedes usar herramientas online gratuitas que simulan un entorno de base de datos real!

## La Herramienta: DB Fiddle

Vamos a usar una web llamada **[DB Fiddle](https://www.db-fiddle.com/)**. Es una herramienta fantástica para aprender y experimentar por varias razones:

*   Es **gratis** y no requiere registrarse.
*   Permite elegir entre diferentes motores de base de datos. Usaremos **SQLite**, que es la tecnología subyacente en las bases de datos de Android.
*   La interfaz es muy clara y está dividida en dos partes principales, lo que facilita el aprendizaje.

## Paso 1: Entendiendo el Entorno de DB Fiddle

1.  Abre la web [https://www.db-fiddle.com/](https://www.db-fiddle.com/).
2.  En la esquina superior izquierda, asegúrate de que el desplegable esté en **SQLite**.

La interfaz se divide en dos paneles clave:

*   **Panel Izquierdo (Schema SQL):** Piensa en este panel como el lugar donde diseñas la **arquitectura** de tu base de datos. Aquí defines la estructura de tus tablas (`CREATE TABLE`) y puedes insertar los datos iniciales (`INSERT`). Es el plano de tu edificio.

*   **Panel Derecho (Query SQL):** Este es el panel de **interacción**. Una vez que la estructura está creada, aquí es donde haces "preguntas" a tus datos usando consultas (`SELECT`), para obtener la información que necesitas. Es donde exploras el edificio ya construido.

## Paso 2: Creando Nuestra Base de Datos (El Schema)

Vamos a crear dos tablas, `usuarios` y `pedidos`, para simular una pequeña tienda online. Copia y pega TODO el siguiente código en el panel de la **izquierda** (Schema SQL).

```sql
-- Borramos las tablas si ya existen, para empezar de cero.
-- Es una buena práctica para asegurar que nuestro script funcione siempre.
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS usuarios;

-- Creamos la tabla de usuarios
-- Aquí guardaremos la información de las personas registradas.
CREATE TABLE usuarios (
  -- INTEGER PRIMARY KEY AUTOINCREMENT: Define una columna de números enteros que será
  -- el identificador único (PRIMARY KEY) de cada usuario. Se incrementará solo.
  id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
  -- TEXT NOT NULL: Una columna de texto que no puede estar vacía.
  nombre TEXT NOT NULL,
  -- TEXT NOT NULL UNIQUE: Una columna de texto obligatoria y, además, única.
  -- No puede haber dos usuarios con el mismo email.
  email TEXT NOT NULL UNIQUE,
  -- INTEGER NOT NULL: Una columna de números enteros obligatoria.
  edad INTEGER NOT NULL
);

-- Creamos la tabla de pedidos con una clave foránea
-- Esta tabla almacenará los productos que ha comprado cada usuario.
CREATE TABLE pedidos (
  id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
  producto TEXT NOT NULL,
  cantidad INTEGER DEFAULT 1, -- Si no se especifica, la cantidad será 1 por defecto.
  id_del_usuario INTEGER, -- Esta columna conectará el pedido con un usuario.

  -- FOREIGN KEY: Este es el "enlace" entre las tablas.
  -- Le dice a la base de datos que cada 'id_del_usuario' en esta tabla
  -- DEBE corresponder a un 'id_usuario' que ya existe en la tabla 'usuarios'.
  -- Esto mantiene la integridad de nuestros datos.
  FOREIGN KEY (id_del_usuario) REFERENCES usuarios(id_usuario)
);

-- Insertamos algunos datos de ejemplo en las tablas para poder practicar
INSERT INTO usuarios (nombre, email, edad) VALUES
('Ana García', 'ana.garcia@email.com', 28),
('Luis Rivas', 'l.rivas@email.com', 35),
('Sara Cruz', 'sarac@email.com', 42),
('Carlos Paz', 'c.paz@email.com', 22),
('Beatriz Soler', 'bea.s@email.com', 28);

INSERT INTO pedidos (producto, cantidad, id_del_usuario) VALUES
('Libro de Cocina', 1, 1), -- El usuario con id 1 (Ana) compró un libro
('Ratón USB', 1, 2), -- El usuario con id 2 (Luis) compró un ratón
('Teclado Mecánico', 1, 1), -- Ana también compró un teclado
('Monitor 24 pulgadas', 2, 3), -- Sara compró 2 monitores
('Alfombrilla XL', 1, 2),
('Webcam HD', 1, 1),
('Auriculares Bluetooth', 1, 4);
```

Ahora, haz clic en el botón **"Run"** (arriba a la izquierda). Si todo ha ido bien, verás un mensaje de `Schema Ready` en verde. ¡Ya tienes tu base de datos lista para ser consultada!

## Paso 3: ¡A Practicar! Ejercicios Resueltos y Explicados

Es tu turno. Escribe cada una de las siguientes consultas en el panel de la **derecha** (Query SQL) y pulsa "Run" para ver el resultado. Intenta resolverlo por tu cuenta primero y luego comprueba la solución.

--- 

### **Ejercicios Básicos**

**1. Selecciona todos los datos de la tabla `pedidos`.**
*Solución:*
```sql
SELECT * FROM pedidos;
```
*Explicación:*
`SELECT *` es el comodín para "seleccionar todas las columnas". `FROM pedidos` especifica de qué tabla queremos obtener la información.

---

**2. Selecciona solo el `nombre` y la `edad` de todos los usuarios.**
*Solución:*
```sql
SELECT nombre, edad FROM usuarios;
```
*Explicación:*
En lugar de usar `*`, especificamos los nombres exactos de las columnas que nos interesan, separados por comas.

---

**3. Selecciona los usuarios que tengan exactamente 28 años.**
*Solución:*
```sql
SELECT * FROM usuarios WHERE edad = 28;
```
*Explicación:*
La cláusula `WHERE` nos permite filtrar los registros. Solo nos devolverá las filas donde la columna `edad` cumpla la condición de ser igual a `28`.

---

**4. Selecciona los productos de la tabla `pedidos` cuya cantidad sea mayor que 1.**
*Solución:*
```sql
SELECT * FROM pedidos WHERE cantidad > 1;
```
*Explicación:*
De nuevo, usamos `WHERE` para filtrar, pero esta vez con el operador `>` (mayor que) para encontrar las filas deseadas.

---

**5. Selecciona los usuarios ordenados por edad, del más joven al más mayor.**
*Solución:*
```sql
SELECT * FROM usuarios ORDER BY edad ASC;
```
*Explicación:*
`ORDER BY` nos permite ordenar los resultados basándonos en una columna. `ASC` indica un orden ascendente (de menor a mayor). Para un orden descendente (de mayor a menor), usaríamos `DESC`.

--- 

### **Ejercicios Intermedios**

**6. Cuenta cuántos usuarios hay en total en la tabla `usuarios`.**
*Solución:*
```sql
SELECT COUNT(*) FROM usuarios;
```
*Explicación:*
`COUNT(*)` es una función de agregación que cuenta el número total de filas en la tabla especificada.

---

**7. Encuentra la edad del usuario de mayor edad.**
*Solución:*
```sql
SELECT MAX(edad) FROM usuarios;
```
*Explicación:*
`MAX()` es otra función de agregación que devuelve el valor máximo de una columna específica.

---

**8. Selecciona todos los usuarios cuyo nombre empiece por la letra 'A'.**
*Solución:*
```sql
SELECT * FROM usuarios WHERE nombre LIKE 'A%';
```
*Explicación:*
El operador `LIKE` se usa para buscar patrones en texto. El símbolo `%` es un comodín que significa "cualquier secuencia de caracteres". Por lo tanto, `'A%'` busca cualquier texto que empiece con 'A'.

---

**9. Calcula la cantidad total de productos que se han vendido.**
*Solución:*
```sql
SELECT SUM(cantidad) FROM pedidos;
```
*Explicación:*
La función de agregación `SUM()` calcula la suma de todos los valores de una columna numérica.

--- 

### **Ejercicios Avanzados (¡Dominando el JOIN!)**

El `JOIN` es una de las operaciones más potentes de SQL. Nos permite combinar filas de dos o más tablas basándonos en una columna relacionada entre ellas (nuestra clave primaria y foránea).

**10. Queremos ver una lista de productos y el nombre del usuario que los compró.**
*Solución:*
```sql
SELECT
  p.producto,
  u.nombre
FROM pedidos AS p
JOIN usuarios AS u ON p.id_del_usuario = u.id_usuario;
```
*Explicación:*
*   `FROM pedidos AS p JOIN usuarios AS u`: Estamos diciendo que queremos combinar (`JOIN`) la tabla `pedidos` (a la que le damos el alias `p`) con la tabla `usuarios` (con alias `u`). Los alias hacen la consulta más corta y legible.
*   `ON p.id_del_usuario = u.id_usuario`: Esta es la condición de unión. Se crea la conexión entre las filas donde el `id_del_usuario` del pedido coincide con el `id_usuario` del usuario.

---

**11. Muestra solo los pedidos realizados por 'Ana García'.**
*Solución:*
```sql
SELECT p.producto, p.cantidad
FROM pedidos AS p
JOIN usuarios AS u ON p.id_del_usuario = u.id_usuario
WHERE u.nombre = 'Ana García';
```
*Explicación:*
Esta consulta es igual a la anterior, pero añadimos una cláusula `WHERE` al final. El `WHERE` se aplica sobre el resultado de la unión, permitiéndonos filtrar los pedidos para que solo muestre los del usuario cuyo nombre es 'Ana García'.

---

**12. (Reto) Cuenta cuántos pedidos ha realizado cada usuario.**
*Solución:*
```sql
SELECT
  u.nombre,
  COUNT(p.id_pedido) AS numero_de_pedidos
FROM usuarios AS u
LEFT JOIN pedidos AS p ON u.id_usuario = p.id_del_usuario
GROUP BY u.nombre
ORDER BY numero_de_pedidos DESC;
```
*Explicación:*
Esta es la consulta más compleja y combina varios conceptos:
1.  `LEFT JOIN`: Usamos `LEFT JOIN` para incluir a **todos** los usuarios, incluso a aquellos que no han hecho pedidos. Si un usuario no tiene pedidos, su conteo será 0.
2.  `COUNT(p.id_pedido)`: Contamos los pedidos de cada usuario.
3.  `GROUP BY u.nombre`: ¡Esta es la clave! Le dice a la función `COUNT` que no cuente todos los pedidos juntos, sino que agrupe las filas por `nombre` de usuario y cuente los pedidos para cada grupo (cada usuario).
4.  `AS numero_de_pedidos`: Le damos un nombre descriptivo a nuestra columna calculada.
5.  `ORDER BY`: Ordenamos los resultados para ver quién ha comprado más.

---

### **Ejercicios Adicionales para Practicar (Sin Solución)**

Ahora te toca a ti poner a prueba todo lo que has aprendido. Intenta resolver las siguientes consultas. ¡No te preocupes si no salen a la primera! La práctica es la clave.

**13. Encuentra a todos los usuarios que tengan más de 30 años y ordénalos por nombre de la A a la Z.**

**14. Muestra todos los productos que ha comprado el usuario con el email 'l.rivas@email.com'.**

**15. Calcula la cantidad total de productos (la suma de `cantidad`) comprados por 'Ana García'.**

**16. (Reto Avanzado) Encuentra si hay algún usuario que no haya realizado ningún pedido.** (Pista: necesitarás un `LEFT JOIN` y una cláusula `WHERE` para buscar valores nulos o `NULL`).

**17. Muestra el email de los usuarios que han comprado un 'Teclado Mecánico'.**

**18. Calcula la edad media de todos los usuarios registrados.** (Pista: busca la función de agregación para calcular promedios o averages).

**19. Lista todos los pedidos (`producto` y `cantidad`) que fueron realizados por usuarios menores de 30 años.**

---

Esta es la mejor forma de que los conceptos de SQL se queden grabados. ¡A practicar!
