# SQLite y Room en Android

(Contenido de las secciones 3.1 a 3.5 omitido por brevedad)

### 3.6. Evolucionando la Base de Datos: Migraciones

(Sección sobre cómo realizar migraciones en Room, explicando el proceso de añadir la columna `email`)

### 3.7. Depurando nuestra Base de Datos: El Database Inspector

Una de las tareas más comunes (y a veces frustrantes) al trabajar con bases de datos es comprobar si los datos se están guardando, actualizando o borrando correctamente. ¿Cómo podemos "ver" lo que hay dentro de la base de datos de nuestra app mientras se ejecuta?

Aquí es donde brilla una de las herramientas más útiles de Android Studio: el **Database Inspector**.

El Database Inspector te permite inspeccionar, consultar y modificar las bases de datos de tu aplicación en tiempo real mientras está en ejecución en un emulador o dispositivo.

#### ¿Cómo lo usamos?

**Paso 1: Ejecutar la aplicación**

Primero, asegúrate de que tu aplicación se está ejecutando en un emulador o un dispositivo conectado con un **nivel de API 26 o superior**.

**Paso 2: Abrir el Database Inspector**

En el menú inferior de Android Studio, busca y haz clic en la pestaña **App Inspection**. Si no la ves, puedes abrirla desde el menú principal: **View > Tool Windows > App Inspection**.

![Abrir App Inspection](https://i.imgur.com/vjE7q6p.png)

**Paso 3: Seleccionar el proceso**

Dentro de la ventana de App Inspection, asegúrate de que el proceso de tu aplicación esté seleccionado. Por lo general, se selecciona automáticamente.

En el panel que aparece, haz clic en la pestaña **Database Inspector**.

**Paso 4: Explorar la Base de Datos**

¡Ya estás dentro! El Database Inspector te mostrará las bases de datos de tu app. En el panel izquierdo, verás las tablas que contiene (en nuestro caso, la tabla `user`).

*   Haz doble clic sobre la tabla `user`.
*   En el panel derecho, verás todos los datos de la tabla, presentados en un formato de filas y columnas, igual que en una hoja de cálculo.

![Vista del Database Inspector](https://i.imgur.com/PZc3XkQ.png)

#### Funcionalidades Clave

1.  **Live Updates (Actualizaciones en Vivo)**

    En la parte superior del panel derecho, hay un botón de `Live updates`. Si lo activas, el inspector actualizará la vista de la tabla automáticamente cada vez que los datos cambien en la aplicación. 

    *   **Pruébalo**: Con `Live updates` activado, si tu app tiene un botón para añadir un nuevo usuario, púlsalo. Verás cómo la nueva fila aparece instantáneamente en el inspector. ¡Es casi mágico!

2.  **Ejecutar Consultas Personalizadas**

    Esta es una funcionalidad extremadamente potente para depurar. El inspector te permite ejecutar consultas SQL directamente sobre la base de datos en ejecución.

    *   Haz clic en el botón **Open New Query**.
    *   Se abrirá una nueva pestaña donde podrás escribir cualquier consulta SQL. Por ejemplo, prueba a escribir:
        ```sql
        SELECT * FROM user WHERE age > 30 ORDER BY age DESC;
        ```
    *   Pulsa el botón "Run" y verás el resultado de tu consulta.

    Esto es increíblemente útil para probar consultas complejas antes de ponerlas en tu `@Dao`, o para buscar datos específicos mientras depuras un problema.

3.  **Modificar Datos (¡con cuidado!)**

    Incluso puedes modificar los datos directamente desde el inspector. Haz doble clic en una celda (por ejemplo, en el nombre de un usuario), escribe un nuevo valor y pulsa Intro. El cambio se guardará en la base de datos del dispositivo.

    > **Advertencia**: Esta función es muy útil para hacer pruebas rápidas, pero úsala con precaución. Estás modificando los datos en vivo y esto puede causar comportamientos inesperados en tu app si no lo tienes en cuenta.

## Conclusión de la Sección

El Database Inspector es una herramienta que todo desarrollador de Android debe dominar. Convierte la base de datos de una "caja negra" a una "caja de cristal", permitiéndote ver exactamente qué está pasando con los datos de tu aplicación en todo momento. Te ahorrará incontables horas de depuración.
