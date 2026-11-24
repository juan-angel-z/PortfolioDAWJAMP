package gestortareas;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona una lista de {@link Tarea}: permite añadirlas, buscarlas,
 * completarlas, eliminarlas y consultarlas según su estado o prioridad.
 * <p>
 * Los identificadores se asignan automáticamente empezando en 1.
 * </p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * GestorTareas gestor = new GestorTareas();
 * Tarea t = gestor.agregar("Estudiar Javadoc", Prioridad.ALTA);
 * gestor.completar(t.getId());
 * }</pre>
 *
 * @version 1.0
 * @since 1.0
 */
public class GestorTareas {

    private final List<Tarea> tareas = new ArrayList<>();
    private int siguienteId = 1;

    /**
     * Crea un gestor vacío, sin ninguna tarea.
     */
    public GestorTareas() {
        // No hace falta inicializar nada más
    }

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

    /**
     * Busca una tarea por su identificador.
     *
     * @param id identificador de la tarea buscada
     * @return la tarea que tiene ese identificador
     * @throws TareaNoEncontradaException si no existe ninguna tarea con ese identificador
     */
    public Tarea buscarPorId(int id) throws TareaNoEncontradaException {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return tarea;
            }
        }
        throw new TareaNoEncontradaException(id);
    }

    /**
     * Marca como completada la tarea con el identificador indicado.
     *
     * @param id identificador de la tarea que se quiere completar
     * @throws TareaNoEncontradaException si no existe ninguna tarea con ese identificador
     */
    public void completar(int id) throws TareaNoEncontradaException {
        buscarPorId(id).completar();
    }

    /**
     * Elimina de la lista la tarea con el identificador indicado.
     *
     * @param id identificador de la tarea que se quiere eliminar
     * @throws TareaNoEncontradaException si no existe ninguna tarea con ese identificador
     */
    public void eliminar(int id) throws TareaNoEncontradaException {
        tareas.remove(buscarPorId(id));
    }

    /**
     * Devuelve las tareas que todavía no se han completado.
     *
     * @return una lista nueva con las tareas pendientes, en el orden en que se crearon
     */
    public List<Tarea> listarPendientes() {
        List<Tarea> pendientes = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (!tarea.isCompletada()) {
                pendientes.add(tarea);
            }
        }
        return pendientes;
    }

    /**
     * Devuelve las tareas que tienen una prioridad concreta, estén o no completadas.
     *
     * @param prioridad prioridad que se quiere filtrar
     * @return una lista nueva con las tareas de esa prioridad
     */
    public List<Tarea> listarPorPrioridad(Prioridad prioridad) {
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (tarea.getPrioridad() == prioridad) {
                resultado.add(tarea);
            }
        }
        return resultado;
    }

    /**
     * Cuenta cuántas tareas están completadas.
     *
     * @return el número de tareas completadas
     */
    public int contarCompletadas() {
        int total = 0;
        for (Tarea tarea : tareas) {
            if (tarea.isCompletada()) {
                total++;
            }
        }
        return total;
    }

    /**
     * Devuelve el número total de tareas de la lista.
     *
     * @return el número de tareas, completadas o no
     */
    public int getTotal() {
        return tareas.size();
    }
}
