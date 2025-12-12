# Guía para Practicar SQL Online

La teoría sobre SQL es importante, pero la única forma real de aprender a "hablar" este idioma es escribiendo consultas. Afortunadamente, no necesitas instalar un sistema complejo en tu ordenador para empezar. ¡Puedes usar herramientas online gratuitas!

## La Herramienta: DB Fiddle

Vamos a usar una web llamada **[DB Fiddle](https://www.db-fiddle.com/)**. Es fantástica por varias razones:

*   Es **gratis** y no requiere registrarse.
*   Te permite elegir el motor de base de datos. Usaremos **SQLite**, que es el mismo que se usa en Android.
*   La interfaz es muy sencilla: a la izquierda pones la estructura de tu base de datos y a la derecha escribes las consultas para obtener datos.

## Paso 1: Abrir DB Fiddle y Preparar el Terreno

1.  Abre la web [https://www.db-fiddle.com/](https://www.db-fiddle.com/).
2.  En la esquina superior izquierda, asegúrate de que el desplegable esté en **SQLite**. (Suele ser la opción por defecto).

    ![DB Fiddle Interface](https://i.imgur.com/gO0y3uE.png)

3.  **El panel de la izquierda (Schema SQL)** es para definir la estructura de tus tablas (`CREATE TABLE`) y para insertar los datos iniciales (`INSERT`).

4.  **Copia y pega TODO el siguiente código** en el panel de la izquierda:

```sql
-- Borramos las tablas si ya existen, para empezar de cero
DROP TABLE IF EXISTS pedidos;
DROP TABLE IF EXISTS usuarios;

-- Creamos la tabla de usuarios
CREATE TABLE usuarios (
  id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
  nombre TEXT NOT NULL,
  email TEXT NOT NULL UNIQUE,
  edad INTEGER NOT NULL
);

-- Creamos la tabla de pedidos con una clave foránea
CREATE TABLE pedidos (
  id_pedido INTEGER PRIMARY KEY AUTOINCREMENT,
  producto TEXT NOT NULL,
  cantidad INTEGER DEFAULT 1,
  id_del_usuario INTEGER,
  FOREIGN KEY (id_del_usuario) REFERENCES usuarios(id_usuario)
);

-- Insertamos algunos datos de ejemplo en las tablas
INSERT INTO usuarios (nombre, email, edad) VALUES
('Ana García', 'ana.garcia@email.com', 28),
('Luis Rivas', 'l.rivas@email.com', 35),
('Sara Cruz', 'sarac@email.com', 42),
('Carlos Paz', 'c.paz@email.com', 22),
('Beatriz Soler', 'bea.s@email.com', 28);

INSERT INTO pedidos (producto, cantidad, id_del_usuario) VALUES
('Libro de Cocina', 1, 1),
('Ratón USB', 1, 2),
('Teclado Mecánico', 1, 1),
('Monitor 24 pulgadas', 2, 3),
('Alfombrilla XL', 1, 2),
('Webcam HD', 1, 1),
('Auriculares Bluetooth', 1, 4);

```

5.  Ahora, haz clic en el botón **"Run"** (arriba a la izquierda). Si todo ha ido bien, verás un mensaje de "Schema Ready" en verde. ¡Ya tienes tu base de datos lista!

## Paso 2: Hacer Preguntas a la Base de Datos (Consultas)

El **panel de la derecha (Query SQL)** es donde vas a escribir tus consultas `SELECT` para explorar los datos que acabas de crear.

1.  Escribe tu primera consulta en el panel derecho. Por ejemplo:

    ```sql
    SELECT * FROM usuarios;
    ```

2.  Haz clic en el botón **"Run"** de nuevo. ¡Verás el resultado de tu consulta en la parte inferior!

## Paso 3: ¡A Practicar! Ejercicios Propuestos

Ahora es tu turno. Intenta resolver las siguientes preguntas escribiendo una consulta SQL en el panel derecho por cada una. ¡No tengas miedo a experimentar!

**Ejercicios Básicos:**

1.  Selecciona todos los datos de la tabla `pedidos`.
2.  Selecciona solo el `nombre` y la `edad` de todos los usuarios.
3.  Selecciona los usuarios que tengan exactamente 28 años.
4.  Selecciona los productos de la tabla `pedidos` cuya cantidad sea mayor que 1.
5.  Selecciona los usuarios ordenados por edad, del más joven al más mayor (`ASC`).

**Ejercicios Intermedios:**

6.  Cuenta cuántos usuarios hay en total en la tabla `usuarios` (Pista: usa `COUNT(*)`).
7.  Encuentra al usuario de mayor edad (Pista: usa `MAX(edad)`).
8.  Selecciona todos los usuarios cuyo nombre empiece por la letra 'A' (Pista: usa `LIKE 'A%'`).
9.  Calcula la cantidad total de productos que se han vendido sumando la columna `cantidad` de la tabla `pedidos` (Pista: usa `SUM(cantidad)`).

**Ejercicios Avanzados (¡a por el JOIN!):**

10. Queremos ver una lista de productos y el nombre del usuario que los compró. Deberás combinar (`JOIN`) las tablas `usuarios` y `pedidos`.
11. Muestra solo los pedidos realizados por 'Ana García'.
12. (Reto) Cuenta cuántos pedidos ha realizado cada usuario. El resultado debería mostrar el nombre del usuario y su número de pedidos (Pista: necesitarás `JOIN`, `COUNT` y `GROUP BY`).

Esta es la mejor forma de que los conceptos de SQL se queden grabados. ¡A practicar!
