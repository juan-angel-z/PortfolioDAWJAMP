# Generación automática de documentación de código

## Comparativa de herramientas y aplicación práctica con Javadoc

**Memoria**

Noviembre de 2026

---

# Generación automática de documentación de código

## Resumen

Esta memoria compara varias herramientas de generación de documentación (Javadoc, JSDoc, Sphinx, Doxygen, MkDocs y Swagger/OpenAPI) según su facilidad de uso, su integración con proyectos, sus formatos de salida, su comunidad y su curva de aprendizaje. Después se aplica Javadoc, la herramienta estándar de Java, a un programa de gestión de tareas: se añaden comentarios con el formato Javadoc a todas las clases y se genera la documentación en HTML. Además se obtiene la misma documentación en Markdown, mediante un doclet propio, y en PDF. Se explican los avisos que dio Javadoc, la estructura de la carpeta de entrega y una reflexión final sobre la experiencia.

## Palabras clave

Documentación, Javadoc, comentarios de código, HTML, Markdown, PDF, doclet

## Índice

1. [Introducción](#1-introducción)
2. [Ficha comparativa de herramientas](#2-ficha-comparativa-de-herramientas)
   1. [Resumen de cada herramienta](#21-resumen-de-cada-herramienta)
   2. [Tabla comparativa](#22-tabla-comparativa)
   3. [Herramienta elegida](#23-herramienta-elegida)
3. [Aplicación práctica](#3-aplicación-práctica)
   1. [El programa documentado](#31-el-programa-documentado)
   2. [Comentarios añadidos al código](#32-comentarios-añadidos-al-código)
   3. [Generación de la documentación en HTML](#33-generación-de-la-documentación-en-html)
   4. [Generación en Markdown y en PDF](#34-generación-en-markdown-y-en-pdf)
   5. [Carpeta de entrega](#35-carpeta-de-entrega)
4. [Reflexión](#4-reflexión)
5. [Bibliografía](#5-bibliografía)

---

## 1. Introducción

Documentar el código a mano en un archivo aparte tiene un problema: en cuanto el código cambia, la documentación se queda vieja. Las herramientas de generación de documentación resuelven esto leyendo comentarios escritos dentro del propio código, junto a cada clase o función, y construyendo a partir de ellos páginas de documentación. Así, cuando se cambia el código, basta con actualizar el comentario de al lado y volver a generar.

El objetivo de este trabajo es conocer las herramientas más habituales, compararlas y utilizar una de ellas sobre un programa real.

## 2. Ficha comparativa de herramientas

### 2.1 Resumen de cada herramienta

| Herramienta | Qué es |
| --- | --- |
| **Javadoc** | Generador oficial de documentación para Java. Viene incluido en el JDK y lee los comentarios con el formato `/** ... */`. |
| **JSDoc** | Generador para JavaScript. Lee comentarios `/** ... */` con etiquetas similares a las de Javadoc y se instala con npm. |
| **Sphinx** | Generador de documentación muy usado en Python. Con su extensión *autodoc* extrae las *docstrings* del código y las combina con textos escritos a mano. |
| **Doxygen** | Generador para muchos lenguajes (C, C++, Java, Python, PHP, C#...). Se configura con un archivo `Doxyfile` y puede dibujar diagramas de clases y de llamadas. |
| **MkDocs** | Generador de sitios de documentación a partir de archivos Markdown. No extrae la documentación del código por sí mismo, sino que se escriben las páginas a mano, aunque existen plugins para ello. |
| **Swagger / OpenAPI** | Estándar para describir APIs REST. A partir de una especificación en YAML o JSON genera una página interactiva en la que se pueden probar los endpoints. |

### 2.2 Tabla comparativa

| | Facilidad de uso | Integración con proyectos | Formatos de salida | Comunidad | Curva de aprendizaje |
| --- | --- | --- | --- | --- | --- |
| **Javadoc** | Alta. Ya está en el JDK y se ejecuta con un solo comando | Muy buena: plugins de Maven y Gradle, y soporte en los IDE (IntelliJ, Eclipse, NetBeans) | HTML. Otros formatos requieren doclets de terceros o conversión | Muy grande y con documentación oficial | Baja |
| **JSDoc** | Alta. Se instala con npm | Buena: se lanza desde scripts de `package.json` | HTML. Markdown mediante plugins | Grande | Baja |
| **Sphinx** | Media. Hay que configurar el proyecto (`conf.py`) | Muy buena en Python, con alojamiento gratuito en Read the Docs | HTML, PDF (vía LaTeX), ePub y páginas de manual | Muy grande | Media |
| **Doxygen** | Media. Tiene muchas opciones de configuración | Buena: funciona con muchos lenguajes y hay asistente gráfico | HTML, LaTeX/PDF, RTF, páginas de manual y XML | Grande y veterana | Media |
| **MkDocs** | Alta. Basta con escribir Markdown y un archivo `mkdocs.yml` | Buena para documentación de usuario y de proyecto | Sitio web estático en HTML. PDF mediante plugins | Grande, con muchos temas | Baja |
| **Swagger / OpenAPI** | Media. Hay que describir la API o usar una librería que la genere | Muy buena con frameworks web (Spring, FastAPI, Express...) | Página HTML interactiva y especificación en JSON o YAML | Muy grande | Media |

### 2.3 Herramienta elegida

Se elige **Javadoc** por varias razones:

- El programa que se documenta está escrito en Java y Javadoc es el estándar de documentación propio de ese lenguaje.
- No hay que instalar nada adicional, porque viene con el JDK.
- El formato de comentarios (`/** ... */` con etiquetas como `@param` o `@return`) es sencillo y los IDE lo entienden: muestran la documentación al pasar el ratón sobre un método.
- Incluye una comprobación de errores (`-Xdoclint`) que avisa de comentarios que faltan o están mal escritos.

Las otras herramientas encajan mejor en otros casos: JSDoc para JavaScript, Sphinx para Python, Doxygen cuando se mezclan lenguajes, MkDocs para manuales escritos a mano y Swagger para describir APIs REST.

---

## 3. Aplicación práctica

### 3.1 El programa documentado

El programa es un gestor de tareas sencillo que funciona por consola. Permite crear tareas con una prioridad, marcarlas como completadas, eliminarlas y consultarlas. Está formado por seis archivos dentro del paquete `gestortareas`:

| Archivo | Función |
| --- | --- |
| `Tarea.java` | Representa una tarea: identificador, título, prioridad y estado. |
| `Prioridad.java` | Enumeración con los tres niveles de prioridad: baja, media y alta. |
| `GestorTareas.java` | Guarda la lista de tareas y ofrece las operaciones para gestionarla. |
| `TareaNoEncontradaException.java` | Excepción que se lanza al buscar una tarea que no existe. |
| `Main.java` | Programa de demostración que usa las clases anteriores. |
| `package-info.java` | Descripción general del paquete, que Javadoc usa en su página principal. |

El programa compila sin avisos y se ejecuta correctamente. Su salida es:

```text
Tareas pendientes al empezar:
[ ] #1 Entregar la memoria de Apache (Alta)
[ ] #2 Revisar los apuntes de Docker (Media)
[ ] #3 Ordenar la carpeta de capturas (Baja)
[ ] #4 Probar Javadoc (Alta)

Error controlado: No existe ninguna tarea con el id 99

Tareas de prioridad alta:
[x] #1 Entregar la memoria de Apache (Alta)
[ ] #4 Probar Javadoc (Alta)

Completadas: 1 de 3
```

### 3.2 Comentarios añadidos al código

Todas las clases, constructores, métodos y constantes públicos llevan un comentario Javadoc. Un comentario Javadoc empieza con `/**`, se coloca justo antes del elemento que describe y sigue esta estructura: una primera frase que resume lo que hace y, después, etiquetas para los detalles. Por ejemplo, el método `agregar` de `GestorTareas` está documentado así:

```java
/**
 * Crea una tarea nueva y la añade a la lista.
 *
 * @param titulo    título de la tarea; no puede ser nulo ni estar vacío
 * @param prioridad prioridad de la tarea; no puede ser nula
 * @return la tarea creada, con su identificador ya asignado
 * @throws IllegalArgumentException si el título es nulo o está vacío
 * @throws NullPointerException     si la prioridad es nula
 */
public Tarea agregar(String titulo, Prioridad prioridad) {
    Tarea tarea = new Tarea(siguienteId++, titulo, prioridad);
    tareas.add(tarea);
    return tarea;
}
```

Las etiquetas que se han utilizado son:

| Etiqueta | Para qué sirve |
| --- | --- |
| `@param` | Describe un parámetro del método o constructor. |
| `@return` | Describe el valor que devuelve el método. |
| `@throws` | Indica qué excepciones puede lanzar y en qué casos. |
| `@version` y `@since` | Indican la versión de la clase y desde cuándo existe. |
| `@see` | Enlaza con otra clase relacionada. |
| `{@link Clase}` | Inserta un enlace a otra clase o método dentro del texto. |
| `{@code texto}` | Muestra un fragmento con formato de código. |
| `<p>` y `<pre>` | Etiquetas HTML para separar párrafos e incluir bloques de ejemplo. |

Además, el archivo `package-info.java` describe el paquete completo, y en `GestorTareas` se incluye un pequeño ejemplo de uso dentro del propio comentario.

### 3.3 Generación de la documentación en HTML

El HTML se genera con la herramienta `javadoc`, que ya viene con el JDK:

```bash
javadoc -Xdoclint:all -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 -locale es \
  -author -version -windowtitle "Gestor de tareas - API" -doctitle "Gestor de tareas" \
  -d documentacion/html -sourcepath codigo/src -subpackages gestortareas
```

| Opción | Función |
| --- | --- |
| `-Xdoclint:all` | Activa todas las comprobaciones: avisa de comentarios que faltan o tienen errores. |
| `-encoding`, `-docencoding`, `-charset` | Usan UTF-8 para que los acentos y la `ñ` se vean bien. |
| `-locale es` | Indica el idioma de la documentación. |
| `-author`, `-version` | Incluyen las etiquetas `@author` y `@version` en el resultado. |
| `-windowtitle`, `-doctitle` | Título de la pestaña del navegador y de la página principal. |
| `-d` | Carpeta donde se escribe el resultado. |
| `-sourcepath`, `-subpackages` | Dónde está el código y qué paquete se documenta. |

**Aviso encontrado.** En la primera ejecución Javadoc terminó bien, pero avisó de un problema:

```text
TareaNoEncontradaException.java:14: warning: no comment
    private final int id;
```

El campo `id` de la excepción es privado, pero aparece en la página `serialized-form` porque la excepción es serializable, y por eso Javadoc espera que tenga comentario. Se resolvió añadiendo un comentario `/** Identificador de la tarea que no se encontró. */`. Tras volver a generar, la documentación se obtuvo con cero avisos.

**Resultado.** La documentación queda en `documentacion/html/`. Se abre con el archivo [`index.html`](documentacion/html/index.html). Contiene:

- Una página principal con la descripción del paquete.
- Una página por cada clase, enumeración y excepción, con sus constructores, métodos, parámetros, valores devueltos y excepciones.
- Un índice de todas las clases, un índice alfabético y un árbol de herencia.
- Un buscador que funciona sin conexión.

Aunque se indicó `-locale es`, las etiquetas fijas de la interfaz (como *Method Summary* o *Parameters*) siguen en inglés, porque el JDK solo trae esa traducción para unos pocos idiomas. Los textos propios del programa, que son los comentarios, salen en español.

### 3.4 Generación en Markdown y en PDF

Javadoc genera HTML y no tiene salida en Markdown ni en PDF. Para obtener también esos formatos se ha hecho lo siguiente:

- **Markdown.** Javadoc permite sustituir su generador de HTML por un *doclet*, que es un programa en Java que recibe los comentarios ya analizados. Se ha escrito uno, [`MarkdownDoclet.java`](herramientas/MarkdownDoclet.java), que recorre las clases y escribe un único archivo Markdown con las firmas, descripciones, parámetros y excepciones. Se ejecuta con:

  ```bash
  javadoc -doclet MarkdownDoclet -docletpath herramientas/build -encoding UTF-8 \
    -sourcepath codigo/src -subpackages gestortareas -salida documentacion/markdown/api.md
  ```

  El resultado es [`documentacion/markdown/api.md`](documentacion/markdown/api.md), que se ve bien directamente en GitHub.

- **PDF.** Se convierte el Markdown anterior a HTML con `pandoc`, con una hoja de estilos propia (`herramientas/estilo.css`), y ese HTML se pasa a PDF con `wkhtmltopdf`. El resultado es [`documentacion/pdf/api.pdf`](documentacion/pdf/api.pdf), de 6 páginas en tamaño A4.

Los tres formatos salen de los mismos comentarios del código, así que son coherentes entre sí.

### 3.5 Carpeta de entrega

```text
entrega_documentacion/
├── 05_documentacion_codigo.md        Este documento
├── generar_documentacion.sh          Script que regenera toda la documentación
├── codigo/
│   └── src/gestortareas/             Código fuente con los comentarios Javadoc
│       ├── GestorTareas.java
│       ├── Main.java
│       ├── Prioridad.java
│       ├── Tarea.java
│       ├── TareaNoEncontradaException.java
│       └── package-info.java
├── documentacion/
│   ├── html/                         Documentación de Javadoc (abrir index.html)
│   ├── markdown/api.md               Documentación en Markdown
│   └── pdf/api.pdf                   Documentación en PDF
└── herramientas/
    ├── MarkdownDoclet.java           Doclet que genera el Markdown
    └── estilo.css                    Estilos usados para el PDF
```

Para volver a generar todo basta con ejecutar `./generar_documentacion.sh`, que compila el código, genera los tres formatos y muestra cualquier aviso de Javadoc. Necesita un JDK, y `pandoc` y `wkhtmltopdf` para el PDF.

---

## 4. Reflexión

Usar Javadoc ha sido fácil. No hubo que instalar nada porque viene con el JDK, y con un solo comando se genera una web completa de documentación, con buscador, índices y enlaces entre clases. Lo que más trabajo llevó fue escribir los comentarios de cada método, no la herramienta. La opción `-Xdoclint` resultó útil, porque avisó de un campo privado sin comentario que se me había pasado, y se corrigió en un minuto.

Entre las ventajas, la documentación vive junto al código, así que es más fácil mantenerla al día, y los IDE enseñan esos comentarios al usar un método. Es un estándar muy conocido en Java, por lo que cualquiera que entre al proyecto sabe leerla. Entre las desventajas, solo genera HTML: para sacar Markdown tuve que escribir un doclet y para el PDF convertir el resultado con otras herramientas. Además, las etiquetas de la interfaz salen en inglés aunque los comentarios estén en español, y los comentarios hay que mantenerlos, porque la herramienta no detecta que la descripción haya dejado de ser cierta.

La recomendaría para un proyecto colaborativo, con la condición de acordar unas normas de comentarios y de generar la documentación de forma automática, por ejemplo con Maven o Gradle, para que los avisos se revisen con cada cambio.

---

## 5. Bibliografía

- Oracle. *Javadoc Tool Guide (JDK 21).* https://docs.oracle.com/en/java/javase/21/javadoc/javadoc.html
- Oracle. *How to Write Doc Comments for the Javadoc Tool.* https://www.oracle.com/technical-resources/articles/java/javadoc-tool.html
- Oracle. *Paquete jdk.javadoc.doclet (API de doclets).* https://docs.oracle.com/en/java/javase/21/docs/api/jdk.javadoc/jdk/javadoc/doclet/package-summary.html
- JSDoc. *Documentación de JSDoc.* https://jsdoc.app/
- Sphinx. *Sphinx documentation.* https://www.sphinx-doc.org/
- Doxygen. *Doxygen: generate documentation from source code.* https://www.doxygen.nl/
- MkDocs. *MkDocs: project documentation with Markdown.* https://www.mkdocs.org/
- SmartBear. *OpenAPI Specification y Swagger.* https://swagger.io/specification/
- Pandoc. *Pandoc: a universal document converter.* https://pandoc.org/
- wkhtmltopdf. *wkhtmltopdf.* https://wkhtmltopdf.org/
