package gestortareas;

/**
 * Niveles de prioridad que puede tener una {@link Tarea}.
 * <p>
 * Los valores están ordenados de menor a mayor importancia, por lo que
 * {@link #ordinal()} puede usarse para comparar prioridades.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public enum Prioridad {

    /** Tarea que puede esperar sin problema. */
    BAJA("Baja"),

    /** Tarea normal, sin urgencia especial. */
    MEDIA("Media"),

    /** Tarea urgente que conviene resolver cuanto antes. */
    ALTA("Alta");

    private final String etiqueta;

    /**
     * Crea un nivel de prioridad con el texto que se muestra al usuario.
     *
     * @param etiqueta texto legible asociado a la prioridad
     */
    Prioridad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /**
     * Devuelve el texto legible de la prioridad.
     *
     * @return la etiqueta de la prioridad, por ejemplo {@code "Alta"}
     */
    public String getEtiqueta() {
        return etiqueta;
    }
}
