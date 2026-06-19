package musicgo.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Biblioteca personal del usuario. Agrupa todas sus playlists.
 *
 * <p>Relacion con {@link Usuario}: composicion. La biblioteca solo
 * tiene sentido dentro de un usuario; si se elimina al usuario, su
 * biblioteca tambien desaparece.</p>
 *
 * @author Equipo MusicGO
 */
public class Biblioteca {

    private List<Playlist> playlists;

    public Biblioteca() {
        this.playlists = new ArrayList<>();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public int cantidadPlaylists() {
        return playlists.size();
    }

    /**
     * Cuenta el total de canciones presentes en todas las playlists
     * (suma simple, puede incluir repetidas entre playlists distintas).
     * @return total de audios contados
     */
    public int totalCanciones() {
        int total = 0;
        for (Playlist p : playlists) {
            for (Audio a : p.getContenido()) {
                if (a instanceof Cancion) {
                    total++;
                }
            }
        }
        return total;
    }

    /**
     * Agrega una playlist nueva. No permite ids duplicados.
     */
    public boolean agregarPlaylist(Playlist p) {
        if (p == null) return false;
        for (Playlist existente : playlists) {
            if (existente.getId().equals(p.getId())) {
                return false;
            }
        }
        playlists.add(p);
        return true;
    }

    /**
     * Busca una playlist por su id.
     * @return la playlist si existe, null si no
     */
    public Playlist buscarPorId(String id) {
        if (id == null) return null;
        for (Playlist p : playlists) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public Playlist buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Playlist p : playlists) {
            if (p.getNombre().equalsIgnoreCase(nombre)) return p;
        }
        return null;
    }

    public boolean eliminarPlaylist(String id) {
        if (id == null) return false;
        for (int i = 0; i < playlists.size(); i++) {
            if (playlists.get(i).getId().equals(id)) {
                playlists.remove(i);
                return true;
            }
        }
        return false;
    }
}
