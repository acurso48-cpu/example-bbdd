# Conceptos Básicos de Bases de Datos (BBDD) - Guía Ampliada

¡Hola! Antes de aprender a crear bases de datos en nuestras apps de Android, es muy importante entender qué son y por qué son tan útiles. En esta guía, vamos a profundizar en los conceptos más importantes.

## 1. ¿Qué es una Base de Datos? La Gran Biblioteca Digital

Imagina una biblioteca gigante. No es solo un montón de libros apilados sin orden. Al contrario, los libros están organizados en estanterías (por género, por autor...), cada libro tiene una ficha única para identificarlo y puedes pedirle al bibliotecario que te busque un libro concreto.

Una **base de datos** es exactamente eso, pero en el mundo digital: **un contenedor organizado para almacenar, gestionar y recuperar información (datos) de forma eficiente y segura.**

## 2. Los Bloques de Construcción: Tablas, Columnas y Filas

Las bases de datos que usamos en Android (SQLite) se llaman **relacionales**. Esto significa que organizan los datos en **tablas**, que son como hojas de cálculo de Excel.

*   **Tablas**: Guardan información sobre un tipo de "cosa". Ej: una tabla `usuarios`, una tabla `productos`, una tabla `pedidos`.
*   **Columnas (o Campos)**: Definen qué datos guardamos de cada "cosa". Ej: `nombre`, `email`, `precio`.
*   **Filas (o Registros)**: Cada "cosa" concreta que guardamos. Ej: el usuario "Ana García", el producto "Portátil XYZ".

**Tabla: `usuarios`**

| id  | nombre     | email                  | edad |
|:----|:-----------|:-----------------------|:-----|
| 1   | Ana García | ana.garcia@email.com   | 28   |
| 2   | Luis Rivas | l.rivas@email.com      | 35   |
| 3   | Sara Cruz  | sarac@email.com        | 42   |

## 3. La Pieza Clave: La Clave Primaria (Primary Key)

Fíjate bien en la columna `id`. Es la columna más importante de la tabla. La llamamos **Clave Primaria** (o *Primary Key*).

Una clave primaria es una columna (o un conjunto de columnas) que cumple dos reglas de oro:

1.  **Valor ÚNICO**: No puede haber dos filas en la misma tabla con el mismo valor en su clave primaria. Es como el DNI de una persona o la matrícula de un coche. No hay dos iguales.
2.  **NUNCA NULO**: La clave primaria siempre debe tener un valor.

### ¿Por qué es tan importante?

*   **Identificación sin ambigüedades**: Nos permite señalar una fila exacta sin miedo a equivocarnos. Si decimos "dame el usuario con `id = 2`", sabemos que nos referimos inequívocamente a Luis Rivas.
*   **Rapidez extrema**: Las bases de datos están súper optimizadas para buscar filas por su clave primaria. Es la forma más rápida de encontrar un dato.
*   **Relaciones**: Como veremos más adelante, las claves primarias son la base para conectar unas tablas con otras.

### La Magia del `AUTOINCREMENT`

Casi siempre, no queremos pensar qué número de `id` le toca al siguiente usuario. ¡Queremos que la base de datos lo haga por nosotros! A esto se le llama **autoincremento**. Cuando creamos la tabla, le decimos que la columna `id` es una clave primaria autoincremental. Así, cuando insertamos un nuevo usuario, la base de datos automáticamente le asigna el siguiente número disponible (4, 5, 6...).

En Room, esto se hace con la anotación `@PrimaryKey(autoGenerate = true)`.

## 4. El Idioma de los Datos: Un Paseo por SQL

Para comunicarnos con la base de datos (pedirle datos, insertar nuevos, etc.), usamos un lenguaje especial llamado **SQL (Structured Query Language)**, que se pronuncia "ese-cu-ele". Es un lenguaje estándar y muy potente.

Vamos a ver las 4 operaciones básicas (conocidas como **CRUD**: **C**reate, **R**ead, **U**pdate, **D**elete).

### a) Leer / Consultar Datos (`SELECT`)

Es la operación más común. La usamos para hacerle preguntas a la base de datos.

*   **Para verlo TODO**: `SELECT * FROM usuarios;`
    *   El asterisco `*` es un comodín que significa "todas las columnas". Esta consulta devuelve la tabla `usuarios` completa.

*   **Para ver solo algunas columnas**: `SELECT nombre, email FROM usuarios;`
    *   Esto devolverá solo los nombres y emails de todos los usuarios.

*   **¡Los Filtros! La Cláusula `WHERE`**: Aquí empieza la potencia. `WHERE` nos permite poner condiciones.
    *   `SELECT * FROM usuarios WHERE edad > 30;` (Devuelve a Luis y a Sara).
    *   `SELECT * FROM usuarios WHERE email = 'ana.garcia@email.com';` (Devuelve solo a Ana).
    *   `SELECT * FROM usuarios WHERE nombre LIKE 'A%';` (Devuelve a los usuarios cuyo nombre empieza por 'A'. El `%` es un comodín).

*   **Ordenando los resultados (`ORDER BY`)**: Podemos ordenar la salida.
    *   `SELECT * FROM usuarios ORDER BY edad DESC;` (Devuelve los usuarios ordenados del más mayor al más joven).
    *   `SELECT * FROM usuarios ORDER BY nombre ASC;` (Ordena alfabéticamente por nombre).

### b) Crear / Insertar Datos (`INSERT`)

Para añadir una nueva fila a una tabla.

*   `INSERT INTO usuarios (nombre, email, edad) VALUES ('Carlos Paz', 'c.paz@email.com', 22);`
    *   Fíjate que no especificamos el `id`. Como es autoincremental, la base de datos se encargará de asignarle el `id = 4`.

### c) Actualizar Datos (`UPDATE`)

Para modificar una o más filas que ya existen.

*   `UPDATE usuarios SET email = 'luis.rivas.new@email.com' WHERE id = 2;`
    *   **¡CUIDADO!** La cláusula `WHERE` aquí es **VITAL**. Si te olvidas del `WHERE`, la consulta `UPDATE usuarios SET email = ...` ¡¡cambiaría el email de **TODOS** los usuarios de la tabla!!

### d) Borrar Datos (`DELETE`)

Para eliminar una o más filas.

*   `DELETE FROM usuarios WHERE id = 3;`
    *   Igual que con `UPDATE`, la cláusula `WHERE` es **CRUCIAL**. Si ejecutas `DELETE FROM usuarios;` sin un `WHERE`, ¡¡borrarás **TODAS LAS FILAS** de la tabla para siempre!!

## 5. Conectando Mundos: Claves Foráneas y Relaciones

Aquí es donde las bases de datos relacionales realmente brillan. Las tablas no viven aisladas.

Imagina que ahora, además de usuarios, queremos guardar los "pedidos" que hace cada usuario. Podríamos tener dos tablas:

**Tabla: `usuarios`**

| id_usuario (PK) | nombre     |
|:----------------|:-----------|
| 1               | Ana García |
| 2               | Luis Rivas |

**Tabla: `pedidos`**

| id_pedido (PK) | producto       | cantidad | id_del_usuario (FK) |
|:---------------|:---------------|:---------|:--------------------|
| 101            | 'Libro'        | 2        | 1                   |
| 102            | 'Ratón USB'    | 1        | 2                   |
| 103            | 'Teclado'      | 1        | 1                   |

Fíjate en la columna `id_del_usuario` de la tabla `pedidos`. Los valores de esta columna (1, 2, 1) se corresponden con los `id_usuario` de la tabla `usuarios`. A esta columna `id_del_usuario` la llamamos **Clave Foránea** (o *Foreign Key*).

Una **clave foránea** es una columna en una tabla que apunta a la **clave primaria** de otra tabla. Así es como creamos una **relación**. En este caso, hemos creado una relación de "un usuario puede tener muchos pedidos".

### ¿Para qué sirve esto? Consultas `JOIN`

Las claves foráneas nos permiten combinar tablas al hacer consultas. Por ejemplo, si queremos ver el nombre del usuario que hizo cada pedido, podemos usar una consulta `JOIN`.

*   `SELECT usuarios.nombre, pedidos.producto FROM pedidos JOIN usuarios ON pedidos.id_del_usuario = usuarios.id_usuario;`

Esta consulta le dice a la base de datos:
1.  "Combina la tabla `pedidos` y la tabla `usuarios`.
2.  "El punto de unión (`ON`) es donde el `id_del_usuario` de un pedido coincida con el `id_usuario` de un usuario".
3.  "Y de esa combinación, solo quiero que me muestres el nombre del usuario y el nombre del producto".

El resultado sería algo así:

| nombre     | producto       |
|:-----------|:---------------|
| Ana García | 'Libro'        |
| Luis Rivas | 'Ratón USB'    |
| Ana García | 'Teclado'      |

## Resumen Final para Android

Cuando usas **Room**, no siempre tienes que escribir estas sentencias SQL a mano. Room te da anotaciones como `@Insert`, `@Delete` o `@Update` que generan el SQL por ti. Pero para las consultas `SELECT`, a menudo escribirás el SQL dentro de la anotación `@Query("...")`.

Entender bien cómo funciona SQL, las claves primarias y las foráneas te convertirá en un desarrollador mucho más completo y te permitirá sacarle todo el partido a Room y a cualquier base de datos que uses en tu carrera.
