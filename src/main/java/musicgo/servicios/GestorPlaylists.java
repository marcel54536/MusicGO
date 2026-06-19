package musicgo.servicios;

import musicgo.excepciones.ContenidoNoEncontradoException;
import musicgo.excepciones.UsuarioNoEncontradoException;
import musicgo.modelo.Audio;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Playlist;
import musicgo.modelo.Usuario;

/**
 * Gestiona las playlists de un usuario: creacion, alta/baja de audios y
 * la funcion "Compartir Playlist" de la Fase 2. Persiste en caliente a
 * traves de {@link GestorUsuarios}.
 *
 * @author Equipo MusicGO
 */
public class GestorPlaylists {

    private final Catalogo catalogo;
    private final GestorUsuarios gestorUsuarios;

    public GestorPlaylists(Catalogo catalogo, GestorUsuarios gestorUsuarios) {
        this.catalogo = catalogo;
        this.gestorUsuarios = gestorUsuarios;
    }

    public Playlist crearPlaylist(Usuario usuario, String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la playlist no puede estar vacio.");
        }
        String id;
        int n = 1;
        do {
            id = String.format("PL%03d", n++);
        } while (usuario.getBiblioteca().buscarPorId(id) != null);

        Playlist p = new Playlist(id, nombre.trim(), usuario.getAlias());
        usuario.getBiblioteca().agregarPlaylist(p);
        gestorUsuarios.persistir();
        return p;
    }

    public boolean agregarAudio(Usuario usuario, String idPlaylist, String idAudio)
            throws ContenidoNoEncontradoException {
        Playlist p = playlistDe(usuario, idPlaylist);
        Audio a = catalogo.buscarAudio(idAudio);
        if (a == null) throw new ContenidoNoEncontradoException(idAudio);
        boolean ok = p.agregarAudio(a);
        usuario.refrescarConteoBiblioteca();
        gestorUsuarios.persistir();
        return ok;
    }

    public boolean removerAudio(Usuario usuario, String idPlaylist, String idAudio) {
        Playlist p = playlistDe(usuario, idPlaylist);
        boolean ok = p.removerAudio(idAudio);
        usuario.refrescarConteoBiblioteca();
        gestorUsuarios.persistir();
        return ok;
    }

    public boolean eliminarPlaylist(Usuario usuario, String idPlaylist) {
        boolean ok = usuario.getBiblioteca().eliminarPlaylist(idPlaylist);
        usuario.refrescarConteoBiblioteca();
        gestorUsuarios.persistir();
        return ok;
    }

    /**
     * Comparte una playlist del usuario origen con otro usuario (por alias).
     * El receptor recibe una copia que conserva al propietario original.
     *
     * @throws UsuarioNoEncontradoException si el alias destino no existe
     */
    public void compartir(Usuario origen, String aliasDestino, String idPlaylist)
            throws UsuarioNoEncontradoException {
        Usuario destino = gestorUsuarios.buscar(aliasDestino);
        if (destino == null) throw new UsuarioNoEncontradoException(aliasDestino);
        if (destino.getAlias().equalsIgnoreCase(origen.getAlias())) {
            throw new IllegalArgumentException("No puedes compartir una playlist contigo mismo.");
        }
        Playlist original = playlistDe(origen, idPlaylist);

        Playlist copia = new Playlist(original.getId(), original.getNombre(), origen.getAlias());
        for (Audio a : original.getContenido()) copia.agregarAudio(a);
        destino.recibirPlaylist(copia);
        gestorUsuarios.persistir();
    }

    private Playlist playlistDe(Usuario usuario, String idPlaylist) {
        Playlist p = usuario.getBiblioteca().buscarPorId(idPlaylist);
        if (p == null) {
            throw new IllegalArgumentException("No existe la playlist " + idPlaylist);
        }
        return p;
    }
}
