package musicgo.interfaces;

import musicgo.modelo.Clasificacion;

/**
 * Contrato que cumple cualquier contenido reproducible dentro de MusicGO.
 *
 * <p>Fase 2: el contrato dejo de imprimir por consola (eso violaba MVC).
 * Ahora expone <em>datos</em> y delega la presentacion a la vista. El
 * metodo {@link #creditos()} es polimorfico: cada audio arma sus propios
 * creditos (artista/album para una cancion, anfitrion/podcast para un
 * episodio).</p>
 *
 * @author Equipo MusicGO
 */
public interface Reproducible {

    /** @return titulo legible del contenido */
    String getTitulo();

    /** @return duracion total en segundos */
    int getDuracionSegundos();

    /**
     * Linea de creditos lista para mostrar en el reproductor.
     * @return creditos formateados segun el tipo concreto de audio
     */
    String creditos();

    /**
     * Clasificacion del contenido (apto / explicito). La usa la capa de
     * control parental para bloquear visualmente la reproduccion.
     * @return clasificacion del contenido
     */
    Clasificacion getClasificacion();
}
