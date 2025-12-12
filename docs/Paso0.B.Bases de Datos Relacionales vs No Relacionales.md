# Bases de Datos: Relacionales (SQL) vs. No Relacionales (NoSQL)

En el mundo de la programación, no todas las bases de datos son iguales. Se dividen en dos grandes familias con filosofías muy diferentes: las bases de datos **Relacionales (SQL)** y las **No Relacionales (NoSQL)**.

Imagina que tienes que organizar tu colección de música.

*   **Enfoque Relacional (SQL)**: Sería como usar una hoja de cálculo de Excel muy estricta. Creas una tabla `canciones` con columnas fijas: `ID`, `Titulo`, `Artista`, `Album`, `Duracion`. Cada nueva canción **debe** rellenar estas columnas, y los datos deben tener el formato correcto (la duración es un número, el título es texto, etc.).

*   **Enfoque No Relacional (NoSQL)**: Sería como tener una caja de fichas o documentos. En una ficha apuntas una canción con su título y artista. En otra, apuntas otra canción, pero además del título y artista, añades el `año_lanzamiento` y una lista de `productores`. En otra, solo apuntas el título y un enlace a Spotify. Cada ficha tiene la estructura que necesite.

Vamos a profundizar en cada una.

---

## 1. Bases de Datos Relacionales (SQL)

Son el tipo de base de datos más tradicional y extendido. Llevan con nosotros desde los años 70.

*   **Modelo**: Organizan los datos en **tablas** predefinidas que se componen de **filas** y **columnas**.
*   **Esquema**: Tienen un **esquema estricto y fijo** (*schema-on-write*). Esto significa que **antes** de poder guardar cualquier dato, debes definir la estructura de tus tablas y las columnas que contendrán. Si intentas guardar un dato que no encaja, la base de datos lo rechazará.
*   **Lenguaje**: Usan **SQL (Structured Query Language)** como lenguaje estándar para definir, manipular y consultar los datos.
*   **Consistencia**: Su principal fortaleza es la **consistencia** de los datos. Garantizan las propiedades **ACID** (Atomicidad, Consistencia, Aislamiento, Durabilidad), lo que asegura que las transacciones (como una transferencia bancaria) se completen correctamente o no se hagan en absoluto, pero nunca se queden a medias. Son extremadamente fiables.
*   **Relaciones**: Como su nombre indica, son excelentes para crear relaciones entre tablas usando claves primarias y foráneas.

#### Tipos y Ejemplos de BBDD Relacionales:

*   **SQLite**: ¡La que usamos en Android! Es una versión súper ligera y auto-contenida que se guarda en un único fichero. Perfecta para dispositivos móviles.
*   **MySQL**: Probablemente la base de datos de código abierto más popular del mundo. Usada masivamente en desarrollo web.
*   **PostgreSQL**: Considerada por muchos como la base de datos relacional de código abierto más avanzada. Es conocida por su robustez y su gran cantidad de funcionalidades.
*   **Oracle, SQL Server**: Soluciones comerciales muy potentes, orientadas a grandes empresas.

**Ideal para**: Aplicaciones financieras, sistemas de contabilidad, sistemas de reservas, y en general, cualquier aplicación donde la estructura de los datos es predecible y la integridad de los mismos es la máxima prioridad.

---

## 2. Bases de Datos No Relacionales (NoSQL)

Surgieron como una alternativa para manejar los enormes volúmenes de datos y la flexibilidad que requerían las aplicaciones web modernas (Big Data, redes sociales, etc.). NoSQL no significa "No a SQL", sino más bien "**Not Only SQL**" (No solo SQL).

*   **Modelo**: No tienen un único modelo. Existen varios, todos ellos mucho más flexibles que las tablas.
*   **Esquema**: Tienen un **esquema dinámico o flexible** (*schema-on-read*). No necesitas definir toda la estructura al principio. Puedes guardar datos con diferentes formatos y la estructura se interpreta al leerlos. 
*   **Escalabilidad**: Están diseñadas para escalar **horizontalmente**, lo que significa que puedes añadir más servidores para manejar más tráfico, haciéndolas ideales para sistemas masivos.
*   **Consistencia**: Generalmente, relajan las estrictas propiedades ACID en favor del rendimiento y la disponibilidad (siguen un modelo llamado **BASE**).

#### Tipos y Ejemplos de BBDD No Relacionales:

1.  **Bases de Datos Documentales**
    *   **Cómo funcionan**: Almacenan los datos en documentos, normalmente en formatos como **JSON** o BSON. Cada documento es auto-contenido y puede tener una estructura diferente.
    *   **Analogía**: Un archivador donde cada documento es una ficha (un objeto JSON).
    *   **Ejemplos**: **MongoDB**, **Firebase Firestore** (muy usado en Android y desarrollo web), CouchDB.
    *   **Ideal para**: Contenido de blogs, catálogos de productos donde cada producto tiene atributos diferentes, perfiles de usuario, etc.

2.  **Bases de Datos de Clave-Valor (Key-Value)**
    *   **Cómo funcionan**: Son el tipo más simple. Guardan los datos como un diccionario: una clave única que apunta a un valor. Son extremadamente rápidas para obtener un dato si conoces su clave.
    *   **Analogía**: Un diccionario de palabras y sus definiciones.
    *   **Ejemplos**: **Redis**, Amazon DynamoDB.
    *   **Ideal para**: Almacenamiento de cachés, sesiones de usuario, configuraciones de aplicación.

3.  **Bases de Datos de Columnas Anchas (Wide-Column)**
    *   **Cómo funcionan**: Parecen tablas, pero las filas no tienen por qué tener las mismas columnas. Permiten un número masivo de columnas que pueden variar de una fila a otra.
    *   **Analogía**: Una super-tabla donde cada fila puede tener sus propias columnas personalizadas.
    *   **Ejemplos**: **Cassandra**, Google Bigtable.
    *   **Ideal para**: Big Data, analíticas, sistemas que escriben cantidades masivas de datos como logs o datos de sensores (IoT).

4.  **Bases de Datos de Grafos**
    *   **Cómo funcionan**: Están diseñadas específicamente para almacenar información sobre relaciones. Se centran en los nodos (las "cosas") y las aristas (las conexiones entre las "cosas").
    *   **Analogía**: Un mapa de una red social.
    *   **Ejemplos**: **Neo4j**, Amazon Neptune.
    *   **Ideal para**: Redes sociales (amigo de, sigue a...), sistemas de recomendación ("clientes que compraron esto también compraron..."), detección de fraude.

## Tabla Comparativa Rápida

| Característica | SQL (Relacional) | NoSQL (No Relacional) |
|:---|:---|:---|
| **Modelo** | Tablas con filas y columnas | Documentos, Clave-Valor, Grafos, etc. |
| **Esquema** | Estricto y predefinido | Flexible y dinámico |
| **Lenguaje** | SQL | Múltiples APIs y lenguajes (varía por BBDD) |
| **Escalabilidad** | Vertical (más potencia a un servidor) | Horizontal (más servidores) |
| **Consistencia** | Alta (ACID) | Flexible (BASE), prioriza disponibilidad |
| **Ejemplo en Android** | **SQLite (Room)** | **Firebase Firestore** |

## Conclusión

No hay una "mejor" que otra; son herramientas diferentes para problemas diferentes. En el desarrollo nativo de Android, la opción por defecto y la más integrada es **SQLite** (una base de datos relacional) gestionada a través de **Room**, porque la mayoría de las apps necesitan guardar datos estructurados de forma fiable en el dispositivo.

Sin embargo, cuando una app necesita sincronizar datos con la nube en tiempo real o trabajar con datos muy flexibles, es muy común usar una solución NoSQL como **Firebase Firestore**, que actúa como un servicio en la nube y una base de datos local al mismo tiempo.
