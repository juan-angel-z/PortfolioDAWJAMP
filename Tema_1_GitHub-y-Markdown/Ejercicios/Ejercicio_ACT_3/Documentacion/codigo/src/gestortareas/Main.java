package gestortareas;

/**
 * Programa de demostración del gestor de tareas.
 * <p>
 * Crea algunas tareas, completa una, elimina otra y muestra el resultado
 * por consola. También comprueba el error que se produce al buscar una
 * tarea que no existe.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
public class Main {

    /**
     * Constructor por defecto. La clase solo contiene el método {@link #main(String[])}.
     */
    public Main() {
        // Clase de demostración, no necesita inicialización
    }

    /**
     * Punto de entrada del programa.
     *
     * @param args argumentos de línea de comandos; no se utilizan
     */
    public static void main(String[] args) {
        GestorTareas gestor = new GestorTareas();

        gestor.agregar("Entregar la memoria de Apache", Prioridad.ALTA);
        gestor.agregar("Revisar los apuntes de Docker", Prioridad.MEDIA);
        gestor.agregar("Ordenar la carpeta de capturas", Prioridad.BAJA);
        gestor.agregar("Probar Javadoc", Prioridad.ALTA);

        System.out.println("Tareas pendientes al empezar:");
        gestor.listarPendientes().forEach(System.out::println);

        try {
            gestor.completar(1);
            gestor.eliminar(3);
            gestor.completar(99);
        } catch (TareaNoEncontradaException e) {
            System.out.println("\nError controlado: " + e.getMessage());
        }

        System.out.println("\nTareas de prioridad alta:");
        gestor.listarPorPrioridad(Prioridad.ALTA).forEach(System.out::println);

        System.out.println("\nCompletadas: " + gestor.contarCompletadas()
                + " de " + gestor.getTotal());
    }
}
