package gestortareas;

/**
 * Excepción que se lanza cuando se busca una {@link Tarea} cuyo
 * identificador no existe en el {@link GestorTareas}.
 *
 * @version 1.0
 * @since 1.0
 */
public class TareaNoEncontradaException extends Exception {

    private static final long serialVersionUID = 1L;

    /** Identificador de la tarea que no se encontró. */
    private final int id;

    /**
     * Crea la excepción indicando el identificador que no se ha encontrado.
     *
     * @param id identificador de la tarea buscada
     */
    public TareaNoEncontradaException(int id) {
        super("No existe ninguna tarea con el id " + id);
        this.id = id;
    }

    /**
     * Devuelve el identificador de la tarea que no se encontró.
     *
     * @return el identificador buscado
     */
    public int getId() {
        return id;
    }
}
