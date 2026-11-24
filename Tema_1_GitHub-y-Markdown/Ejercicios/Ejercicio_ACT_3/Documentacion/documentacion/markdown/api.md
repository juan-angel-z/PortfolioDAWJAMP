# Documentación de la API: `gestortareas`

Gestor de tareas sencillo para consola.

El paquete contiene las clases necesarias para crear tareas con prioridad, guardarlas en una lista, marcarlas como completadas y consultarlas. La clase `gestortareas.Main` muestra un ejemplo de uso.

## Índice de clases

- [GestorTareas](#clase-gestortareas) — Gestiona una lista de `Tarea`: permite añadirlas, buscarlas, completarlas, eliminarlas y consultarlas según su estado o prioridad.
- [Main](#clase-main) — Programa de demostración del gestor de tareas.
- [Prioridad](#enumeración-prioridad) — Niveles de prioridad que puede tener una `Tarea`.
- [Tarea](#clase-tarea) — Representa una tarea de una lista de tareas.
- [TareaNoEncontradaException](#excepción-tareanoencontradaexception) — Excepción que se lanza cuando se busca una `Tarea` cuyo identificador no existe en el `GestorTareas`.

---

## Clase `GestorTareas`

```java
public class GestorTareas
```

Gestiona una lista de `Tarea`: permite añadirlas, buscarlas, completarlas, eliminarlas y consultarlas según su estado o prioridad.

Los identificadores se asignan automáticamente empezando en 1.

Ejemplo de uso:

```java
GestorTareas gestor = new GestorTareas();
Tarea t = gestor.agregar("Estudiar Javadoc", Prioridad.ALTA);
gestor.completar(t.getId());
```

**Versión:** 1.0  
**Desde:** 1.0  

### Constructores

#### `GestorTareas`

```java
public GestorTareas()
```

Crea un gestor vacío, sin ninguna tarea.

### Métodos

#### `agregar`

```java
public Tarea agregar(String titulo, Prioridad prioridad)
```

Crea una tarea nueva y la añade a la lista.

**Parámetros:**

- `titulo`: título de la tarea; no puede ser nulo ni estar vacío
- `prioridad`: prioridad de la tarea; no puede ser nula

**Devuelve:** la tarea creada, con su identificador ya asignado

**Lanza:**

- `IllegalArgumentException`: si el título es nulo o está vacío
- `NullPointerException`: si la prioridad es nula

#### `buscarPorId`

```java
public Tarea buscarPorId(int id) throws TareaNoEncontradaException
```

Busca una tarea por su identificador.

**Parámetros:**

- `id`: identificador de la tarea buscada

**Devuelve:** la tarea que tiene ese identificador

**Lanza:**

- `TareaNoEncontradaException`: si no existe ninguna tarea con ese identificador

#### `completar`

```java
public void completar(int id) throws TareaNoEncontradaException
```

Marca como completada la tarea con el identificador indicado.

**Parámetros:**

- `id`: identificador de la tarea que se quiere completar

**Lanza:**

- `TareaNoEncontradaException`: si no existe ninguna tarea con ese identificador

#### `eliminar`

```java
public void eliminar(int id) throws TareaNoEncontradaException
```

Elimina de la lista la tarea con el identificador indicado.

**Parámetros:**

- `id`: identificador de la tarea que se quiere eliminar

**Lanza:**

- `TareaNoEncontradaException`: si no existe ninguna tarea con ese identificador

#### `listarPendientes`

```java
public List<Tarea> listarPendientes()
```

Devuelve las tareas que todavía no se han completado.

**Devuelve:** una lista nueva con las tareas pendientes, en el orden en que se crearon

#### `listarPorPrioridad`

```java
public List<Tarea> listarPorPrioridad(Prioridad prioridad)
```

Devuelve las tareas que tienen una prioridad concreta, estén o no completadas.

**Parámetros:**

- `prioridad`: prioridad que se quiere filtrar

**Devuelve:** una lista nueva con las tareas de esa prioridad

#### `contarCompletadas`

```java
public int contarCompletadas()
```

Cuenta cuántas tareas están completadas.

**Devuelve:** el número de tareas completadas

#### `getTotal`

```java
public int getTotal()
```

Devuelve el número total de tareas de la lista.

**Devuelve:** el número de tareas, completadas o no

---

## Clase `Main`

```java
public class Main
```

Programa de demostración del gestor de tareas.

Crea algunas tareas, completa una, elimina otra y muestra el resultado por consola. También comprueba el error que se produce al buscar una tarea que no existe.

**Versión:** 1.0  
**Desde:** 1.0  

### Constructores

#### `Main`

```java
public Main()
```

Constructor por defecto. La clase solo contiene el método `main(String[])`.

### Métodos

#### `main`

```java
public static void main(String[] args)
```

Punto de entrada del programa.

**Parámetros:**

- `args`: argumentos de línea de comandos; no se utilizan

---

## Enumeración `Prioridad`

```java
public enum Prioridad
```

Niveles de prioridad que puede tener una `Tarea`.

Los valores están ordenados de menor a mayor importancia, por lo que `ordinal()` puede usarse para comparar prioridades.

**Versión:** 1.0  
**Desde:** 1.0  

### Constantes

| Constante | Descripción |
| --- | --- |
| `BAJA` | Tarea que puede esperar sin problema. |
| `MEDIA` | Tarea normal, sin urgencia especial. |
| `ALTA` | Tarea urgente que conviene resolver cuanto antes. |

### Métodos

#### `values`

```java
public static Prioridad[] values()
```

Devuelve un array con todas las constantes de la enumeración, en el orden en que están declaradas.

#### `valueOf`

```java
public static Prioridad valueOf(String name)
```

Devuelve la constante de la enumeración con el nombre indicado.

**Parámetros:**

- `name`: nombre exacto de la constante

**Lanza:**

- `IllegalArgumentException`: si no existe ninguna constante con ese nombre

#### `getEtiqueta`

```java
public String getEtiqueta()
```

Devuelve el texto legible de la prioridad.

**Devuelve:** la etiqueta de la prioridad, por ejemplo `"Alta"`

---

## Clase `Tarea`

```java
public class Tarea
```

Representa una tarea de una lista de tareas.

Cada tarea tiene un identificador único, un título, una `Prioridad` y un estado que indica si ya se ha completado.

**Versión:** 1.0  
**Desde:** 1.0  
**Véase también:** `GestorTareas`  

### Constructores

#### `Tarea`

```java
public Tarea(int id, String titulo, Prioridad prioridad)
```

Crea una tarea nueva, que empieza sin completar.

**Parámetros:**

- `id`: identificador único de la tarea
- `titulo`: texto que describe la tarea; no puede ser nulo ni estar vacío
- `prioridad`: prioridad asignada a la tarea; no puede ser nula

**Lanza:**

- `IllegalArgumentException`: si el título es nulo o está vacío
- `NullPointerException`: si la prioridad es nula

### Métodos

#### `getId`

```java
public int getId()
```

Devuelve el identificador de la tarea.

**Devuelve:** el identificador único

#### `getTitulo`

```java
public String getTitulo()
```

Devuelve el título de la tarea.

**Devuelve:** el título, sin espacios al principio ni al final

#### `getPrioridad`

```java
public Prioridad getPrioridad()
```

Devuelve la prioridad de la tarea.

**Devuelve:** la prioridad asignada

#### `isCompletada`

```java
public boolean isCompletada()
```

Indica si la tarea ya está completada.

**Devuelve:** `true` si está completada, `false` en caso contrario

#### `completar`

```java
public void completar()
```

Marca la tarea como completada.

Si la tarea ya estaba completada, el método no tiene ningún efecto.

#### `toString`

```java
public String toString()
```

Devuelve una representación en texto de la tarea con el formato `[x] #1 Título (Prioridad)`, donde la `x` indica que está completada.

**Devuelve:** la tarea en formato de texto

---

## Excepción `TareaNoEncontradaException`

```java
public class TareaNoEncontradaException extends Exception
```

Excepción que se lanza cuando se busca una `Tarea` cuyo identificador no existe en el `GestorTareas`.

**Versión:** 1.0  
**Desde:** 1.0  

### Constructores

#### `TareaNoEncontradaException`

```java
public TareaNoEncontradaException(int id)
```

Crea la excepción indicando el identificador que no se ha encontrado.

**Parámetros:**

- `id`: identificador de la tarea buscada

### Métodos

#### `getId`

```java
public int getId()
```

Devuelve el identificador de la tarea que no se encontró.

**Devuelve:** el identificador buscado

---

