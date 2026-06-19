package musicgo.modelo;

/**
 * Clasificacion de un contenido de audio para el control parental
 * (requerimiento "Filtro y Restriccion de Contenido" de la Fase 2).
 *
 * <p>Es un enum y no un boolean porque modela un concepto del dominio
 * con etiqueta legible y deja la puerta abierta a mas niveles sin tocar
 * el resto del codigo (OCP).</p>
 *
 * @author Equipo MusicGO
 */
public enum Clasificacion {

    /** Apto para todo publico. */
    APTO_TODO_PUBLICO("Apto", false),

    /** Contenido explicito: bloqueado si el perfil tiene control parental. */
    EXPLICITO("Explicito", true);

    private final String etiqueta;
    private final boolean restringido;

    Clasificacion(String etiqueta, boolean restringido) {
        this.etiqueta = etiqueta;
        this.restringido = restringido;
    }

    /** @return texto corto para mostrar en la interfaz */
    public String etiqueta() {
        return etiqueta;
    }

    /** @return true si requiere que el perfil no tenga restricciones activas */
    public boolean esRestringido() {
        return restringido;
    }

    /**
     * Convierte el texto del JSON en una constante, tolerando ausencia.
     * @param texto valor leido del JSON (puede ser null)
     * @return la clasificacion correspondiente o APTO_TODO_PUBLICO por defecto
     */
    public static Clasificacion desde(String texto) {
        if (texto == null) return APTO_TODO_PUBLICO;
        try {
            return Clasificacion.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return APTO_TODO_PUBLICO;
        }
    }
}
