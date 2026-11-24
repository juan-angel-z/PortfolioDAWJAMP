package gestortareas;

import java.util.Objects;

/**
 * Representa una tarea de una lista de tareas.
 * <p>
 * Cada tarea tiene un identificador único, un título, una {@link Prioridad}
 * y un estado que indica si ya se ha completado.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 * @see GestorTareas
 */
public class Tarea {

    private final int id;
    private final String titulo;
    private final Prioridad prioridad;
    private boolean completada;

    /**
     * Crea una tarea nueva, que empieza sin completar.
     *
     * @param id        identificador único de la tarea
     * @param titulo    texto que describe la tarea; no puede ser nulo ni estar vacío
     * @param prioridad prioridad asignada a la tarea; no puede ser nula
     * @throws IllegalArgumentException si el título es nulo o está vacío
     * @throws NullPointerException     si la prioridad es nula
     */
    public Tarea(int id, String titulo, Prioridad prioridad) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.prioridad = Objects.requireNonNull(prioridad, "La prioridad no puede ser nula");
        this.completada = false;
    }

    /**
     * Devuelve el identificador de la tarea.
     *
     * @return el identificador único
     */
    public int getId() {
        return id;
    }

    /**
     * Devuelve el título de la tarea.
     *
     * @return el título, sin espacios al principio ni al final
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Devuelve la prioridad de la tarea.
     *
     * @return la prioridad asignada
     */
    public Prioridad getPrioridad() {
        return prioridad;
    }

    /**
     * Indica si la tarea ya está completada.
     *
     * @return {@code true} si está completada, {@code false} en caso contrario
     */
    public boolean isCompletada() {
        return completada;
    }

    /**
     * Marca la tarea como completada.
     * <p>
     * Si la tarea ya estaba completada, el método no tiene ningún efecto.
     * </p>
     */
    public void completar() {
        this.completada = true;
    }

    /**
     * Devuelve una representación en texto de la tarea con el formato
     * {@code [x] #1 Título (Prioridad)}, donde la {@code x} indica que está completada.
     *
     * @return la tarea en formato de texto
     */
    @Override
    public String toString() {
        return String.format("[%s] #%d %s (%s)",
                completada ? "x" : " ", id, titulo, prioridad.getEtiqueta());
    }
}
