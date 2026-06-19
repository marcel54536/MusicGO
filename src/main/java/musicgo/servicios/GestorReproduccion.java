package musicgo.servicios;

import musicgo.excepciones.ContenidoNoEncontradoException;
import musicgo.excepciones.ContenidoRestringidoException;
import musicgo.modelo.Audio;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Usuario;

/**
 * Simula la reproduccion de un contenido. Aplica el control parental,
 * actualiza el tiempo de escucha del usuario y suma la reproduccion al
 * contador global de la plataforma.
 *
 * <p>Fase 2: no imprime nada; devuelve el {@link Audio} para que la vista
 * arme el reproductor grafico (MVC).</p>
 *
 * @author Equipo MusicGO
 */
public class GestorReproduccion {

    private final Catalogo catalogo;
    private final GestorEstadisticas estadisticas;

    public GestorReproduccion(Catalogo catalogo, GestorEstadisticas estadisticas) {
        this.catalogo = catalogo;
        this.estadisticas = estadisticas;
    }

    /**
     * Valida y "reproduce" el audio indicado.
     *
     * @param usuario usuario que escucha
     * @param idAudio id del contenido
     * @return el audio reproducido (para mostrarlo en el reproductor)
     * @throws ContenidoNoEncontradoException si el audio no esta en el catalogo
     * @throws ContenidoRestringidoException si el perfil bloquea contenido explicito
     */
    public Audio reproducir(Usuario usuario, String idAudio)
            throws ContenidoNoEncontradoException, ContenidoRestringidoException {

        Audio a = catalogo.buscarAudio(idAudio);
        if (a == null) throw new ContenidoNoEncontradoException(idAudio);

        if (usuario.tieneControlParental() && a.getClasificacion().esRestringido()) {
            throw new ContenidoRestringidoException(a.getTitulo());
        }

        usuario.getEstadisticas().sumarTiempoEscucha(a.getDuracionSegundos());
        estadisticas.registrarReproduccion(idAudio);
        return a;
    }
}
