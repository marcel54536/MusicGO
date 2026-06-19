package musicgo.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Catalogo global de la plataforma: agrupa todos los {@link Audio}
 * (canciones y episodios) y todos los {@link Producto} a la venta.
 *
 * <p>Es un contenedor con buscadores y altas/bajas; la carga y el guardado
 * a JSON viven en la capa de persistencia (SRP).</p>
 *
 * @author Equipo MusicGO
 */
public class Catalogo {

    private final List<Audio> audios = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();

    public List<Audio> getAudios() {
        return audios;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void agregarAudio(Audio a) {
        if (a != null && buscarAudio(a.getId()) == null) audios.add(a);
    }

    public void agregarProducto(Producto p) {
        if (p != null && buscarProducto(p.getId()) == null) productos.add(p);
    }

    public boolean eliminarAudio(String id) {
        return audios.removeIf(a -> a.getId().equals(id));
    }

    public boolean eliminarProducto(String id) {
        return productos.removeIf(p -> p.getId().equals(id));
    }

    public Audio buscarAudio(String id) {
        if (id == null) return null;
        for (Audio a : audios) {
            if (a.getId().equals(id)) return a;
        }
        return null;
    }

    public Producto buscarProducto(String id) {
        if (id == null) return null;
        for (Producto p : productos) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    /** @return solo las canciones del catalogo */
    public List<Cancion> soloCanciones() {
        return filtrar(Cancion.class);
    }

    /** @return solo los episodios de podcast del catalogo */
    public List<EpisodioPodcast> soloPodcasts() {
        return filtrar(EpisodioPodcast.class);
    }

    /** Filtro generico por subtipo de audio, para no repetir bucles (DRY). */
    private <T extends Audio> List<T> filtrar(Class<T> tipo) {
        List<T> res = new ArrayList<>();
        for (Audio a : audios) {
            if (tipo.isInstance(a)) res.add(tipo.cast(a));
        }
        return res;
    }

    /** @return total de elementos (audios + productos) */
    public int total() {
        return audios.size() + productos.size();
    }
}
