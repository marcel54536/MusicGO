package musicgo.excepciones;

/**
 * Se lanza cuando un perfil con control parental activo intenta
 * reproducir un contenido clasificado como explicito.
 *
 * <p>El controlador la captura y bloquea visualmente la reproduccion
 * mostrando una advertencia (requisito "Filtro y Restriccion de
 * Contenido" de la Fase 2).</p>
 *
 * @author Equipo MusicGO
 */
public class ContenidoRestringidoException extends Exception {

    public ContenidoRestringidoException(String titulo) {
        super("'" + titulo + "' es contenido explicito y tu perfil tiene "
                + "el control parental activado.");
    }
}
