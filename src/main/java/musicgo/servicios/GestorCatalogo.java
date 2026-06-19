package musicgo.servicios;

import musicgo.modelo.ArteVisualAlbum;
import musicgo.modelo.Audio;
import musicgo.modelo.Cancion;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Clasificacion;
import musicgo.modelo.EpisodioPodcast;
import musicgo.modelo.PaqueteTopTen;
import musicgo.modelo.Producto;
import musicgo.persistencia.MapeadorCatalogo;
import musicgo.persistencia.ServicioPersistencia;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Servicio del catalogo: carga, alta/baja/edicion y persistencia en
 * caliente. Genera los ids ({@code C###}, {@code E###}, {@code P###}) y
 * notifica a sus observadores cuando algo cambia, para que la vista del
 * usuario refleje en tiempo real lo que toca el administrador.
 *
 * <p>El patron observador se implementa con {@code Runnable} simples para
 * NO acoplar el modelo a JavaFX (se mantiene la independencia de capas).</p>
 *
 * @author Equipo MusicGO
 */
public class GestorCatalogo {

    private static final String ARCHIVO = "catalogo.json";

    private final ServicioPersistencia persistencia;
    private final Catalogo catalogo = new Catalogo();
    private final List<Runnable> observadores = new ArrayList<>();

    public GestorCatalogo(ServicioPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    /** Carga el catalogo desde el JSON. @return total de elementos cargados */
    public int cargar() {
        MapeadorCatalogo.poblar(catalogo, persistencia.getRepositorio().leer(ARCHIVO));
        return catalogo.total();
    }

    // ---------------- altas (generan id) ----------------

    public Cancion crearCancion(String titulo, int duracion, String artista,
                                String album, String genero, Clasificacion clas) {
        Cancion c = new Cancion(nuevoId("C", catalogo.getAudios(), Audio::getId),
                titulo, duracion, artista, album, genero);
        c.setClasificacion(clas);
        catalogo.agregarAudio(c);
        persistirYNotificar();
        return c;
    }

    public EpisodioPodcast crearEpisodio(String titulo, int duracion, String anfitrion,
                                         String podcast, String descripcion, int numero,
                                         Clasificacion clas) {
        EpisodioPodcast e = new EpisodioPodcast(nuevoId("E", catalogo.getAudios(), Audio::getId),
                titulo, duracion, anfitrion, podcast, descripcion, numero);
        e.setClasificacion(clas);
        catalogo.agregarAudio(e);
        persistirYNotificar();
        return e;
    }

    public ArteVisualAlbum crearArteVisual(String nombre, double precio, String descripcion,
                                           String album, String artista, String formato) {
        ArteVisualAlbum p = new ArteVisualAlbum(nuevoId("P", catalogo.getProductos(), Producto::getId),
                nombre, precio, descripcion, album, artista, formato);
        catalogo.agregarProducto(p);
        persistirYNotificar();
        return p;
    }

    public PaqueteTopTen crearPaquete(String nombre, double precio, String descripcion,
                                      String tematica, List<String> idsCanciones) {
        PaqueteTopTen p = new PaqueteTopTen(nuevoId("P", catalogo.getProductos(), Producto::getId),
                nombre, precio, descripcion, tematica);
        if (idsCanciones != null) idsCanciones.forEach(p::agregarCancion);
        catalogo.agregarProducto(p);
        persistirYNotificar();
        return p;
    }

    // ---------------- baja / edicion ----------------

    /** Elimina un elemento (audio o producto) por id. */
    public boolean eliminar(String id) {
        boolean ok = catalogo.eliminarAudio(id) || catalogo.eliminarProducto(id);
        if (ok) persistirYNotificar();
        return ok;
    }

    /** Persiste y avisa a las vistas tras editar un elemento existente. */
    public void persistirYNotificar() {
        persistencia.guardarAsync(ARCHIVO, MapeadorCatalogo.aMapa(catalogo));
        observadores.forEach(Runnable::run);
    }

    // ---------------- observadores ----------------

    public void agregarObservador(Runnable obs) {
        if (obs != null) observadores.add(obs);
    }

    public void quitarObservador(Runnable obs) {
        observadores.remove(obs);
    }

    // ---------------- generacion de id ----------------

    /** Genera el siguiente id con prefijo dado, evitando colisiones. */
    private <T> String nuevoId(String prefijo, List<T> existentes, Function<T, String> idDe) {
        int max = 0;
        for (T t : existentes) {
            String id = idDe.apply(t);
            if (id != null && id.startsWith(prefijo)) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(prefijo.length())));
                } catch (NumberFormatException ignored) {
                    // ids con otro formato simplemente no cuentan
                }
            }
        }
        return String.format("%s%03d", prefijo, max + 1);
    }
}
