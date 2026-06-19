package musicgo.modelo;

import musicgo.interfaces.Identificable;

import java.util.ArrayList;
import java.util.List;

/**
 * Lista de reproduccion. Agrupa varios {@link Audio} (canciones y/o
 * episodios) bajo un nombre dado por el usuario.
 *
 * <p>Relacion con {@link Audio}: agregacion. Una playlist contiene
 * referencias a audios pero los audios existen por si solos en el
 * catalogo independientemente de las playlists.</p>
 *
 * @author Equipo MusicGO
 */
public class Playlist implements Identificable {

    private String id;
    private String nombre;
    private String aliasPropietario;
    private List<Audio> contenido;

    public Playlist(String id, String nombre, String aliasPropietario) {
        this.id = id;
        this.nombre = nombre;
        this.aliasPropietario = aliasPropietario;
        this.contenido = new ArrayList<>();
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAliasPropietario() {
        return aliasPropietario;
    }

    public List<Audio> getContenido() {
        return contenido;
    }

    public int cantidadAudios() {
        return contenido.size();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Agrega un audio a la playlist. No permite duplicados (mismo id).
     *
     * @param audio audio a agregar
     * @return true si se agrego, false si ya existia o era nulo
     */
    public boolean agregarAudio(Audio audio) {
        if (audio == null) return false;
        for (Audio a : contenido) {
            if (a.getId().equals(audio.getId())) {
                return false; // ya estaba
            }
        }
        contenido.add(audio);
        return true;
    }

    /**
     * Quita el audio cuyo id coincide con el indicado.
     *
     * @param idAudio id del audio a remover
     * @return true si se removio efectivamente, false si no estaba
     */
    public boolean removerAudio(String idAudio) {
        if (idAudio == null) return false;
        for (int i = 0; i < contenido.size(); i++) {
            if (contenido.get(i).getId().equals(idAudio)) {
                contenido.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Calcula la duracion total sumando las duraciones individuales.
     * @return duracion en segundos
     */
    public int duracionTotalSegundos() {
        int total = 0;
        for (Audio a : contenido) {
            total += a.getDuracionSegundos();
        }
        return total;
    }

    @Override
    public String toString() {
        return "Playlist '" + nombre + "' (" + id + ") - "
                + contenido.size() + " audios";
    }
}
