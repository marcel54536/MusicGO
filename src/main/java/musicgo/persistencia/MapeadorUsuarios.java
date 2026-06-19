package musicgo.persistencia;

import musicgo.modelo.Audio;
import musicgo.modelo.Biblioteca;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Compra;
import musicgo.modelo.Estadisticas;
import musicgo.modelo.Playlist;
import musicgo.modelo.Usuario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Traduce la lista de usuarios entre JSON y dominio. Conoce la forma del
 * {@code usuarios.json} (SRP) e incluye los campos nuevos de la Fase 2:
 * saldo virtual, control parental y playlists compartidas.
 *
 * @author Equipo MusicGO
 */
public final class MapeadorUsuarios {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private MapeadorUsuarios() { }

    // ---------------- JSON -> dominio ----------------

    /** Reconstruye los usuarios resolviendo los audios contra el catalogo. */
    public static List<Usuario> leer(Object raiz, Catalogo catalogo) {
        List<Usuario> usuarios = new ArrayList<>();
        if (raiz == null) return usuarios;
        for (Object o : JsonParser.asList(raiz)) {
            usuarios.add(aUsuario(JsonParser.asMap(o), catalogo));
        }
        return usuarios;
    }

    private static Usuario aUsuario(Map<String, Object> m, Catalogo catalogo) {
        String alias = JsonParser.asString(m.get("alias"));
        Estadisticas est = aEstadisticas(m.get("estadisticas"));

        Biblioteca bib = new Biblioteca();
        for (Object o : JsonParser.asList(m.get("playlists"))) {
            bib.agregarPlaylist(aPlaylist(JsonParser.asMap(o), alias, catalogo));
        }

        List<Compra> compras = new ArrayList<>();
        for (Object o : JsonParser.asList(m.get("compras"))) {
            compras.add(aCompra(JsonParser.asMap(o), alias));
        }

        Usuario u = new Usuario(alias, JsonParser.asString(m.get("correo")), bib, est, compras);
        u.setSaldo(JsonParser.asDouble(m.get("saldo")));
        Object cp = m.get("controlParental");
        u.setControlParental(cp instanceof Boolean b && b);
        for (Object o : JsonParser.asList(m.get("compartidas"))) {
            Map<String, Object> mp = JsonParser.asMap(o);
            String duenio = JsonParser.asString(mp.get("propietario"));
            u.recibirPlaylist(aPlaylist(mp, duenio, catalogo));
        }
        return u;
    }

    private static Estadisticas aEstadisticas(Object o) {
        if (o == null) return new Estadisticas();
        Map<String, Object> m = JsonParser.asMap(o);
        return new Estadisticas(
                JsonParser.asLong(m.get("tiempoEscucha")),
                JsonParser.asInt(m.get("cancionesEnBiblioteca")),
                JsonParser.asInt(m.get("compras")),
                JsonParser.asInt(m.get("reproducciones")));
    }

    private static Playlist aPlaylist(Map<String, Object> m, String duenio, Catalogo catalogo) {
        Playlist pl = new Playlist(
                JsonParser.asString(m.get("id")), JsonParser.asString(m.get("nombre")), duenio);
        for (Object id : JsonParser.asList(m.get("audios"))) {
            Audio a = catalogo.buscarAudio(JsonParser.asString(id));
            if (a != null) pl.agregarAudio(a);
        }
        return pl;
    }

    private static Compra aCompra(Map<String, Object> m, String alias) {
        LocalDateTime fecha = LocalDateTime.now();
        String f = JsonParser.asString(m.get("fecha"));
        try {
            if (f != null) fecha = LocalDateTime.parse(f, FMT);
        } catch (Exception ignored) {
            // si la fecha no parsea, dejamos la actual
        }
        return new Compra(JsonParser.asString(m.get("idProducto")), alias,
                JsonParser.asDouble(m.get("monto")), fecha);
    }

    // ---------------- dominio -> JSON ----------------

    public static List<Object> aLista(List<Usuario> usuarios) {
        List<Object> arr = new ArrayList<>();
        for (Usuario u : usuarios) arr.add(deUsuario(u));
        return arr;
    }

    private static Map<String, Object> deUsuario(Usuario u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("alias", u.getAlias());
        m.put("correo", u.getCorreo());
        m.put("saldo", u.getSaldo());
        m.put("controlParental", u.tieneControlParental());
        m.put("estadisticas", deEstadisticas(u.getEstadisticas()));

        List<Object> pls = new ArrayList<>();
        for (Playlist p : u.getBiblioteca().getPlaylists()) pls.add(dePlaylist(p, false));
        m.put("playlists", pls);

        List<Object> comp = new ArrayList<>();
        for (Playlist p : u.getCompartidasConmigo()) comp.add(dePlaylist(p, true));
        m.put("compartidas", comp);

        List<Object> compras = new ArrayList<>();
        for (Compra c : u.getHistorialCompras()) compras.add(deCompra(c));
        m.put("compras", compras);
        return m;
    }

    private static Map<String, Object> deEstadisticas(Estadisticas e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tiempoEscucha", e.getTiempoEscuchaSegundos());
        m.put("cancionesEnBiblioteca", e.getCancionesEnBiblioteca());
        m.put("compras", e.getComprasRealizadas());
        m.put("reproducciones", e.getReproduccionesTotales());
        return m;
    }

    private static Map<String, Object> dePlaylist(Playlist p, boolean incluirPropietario) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", p.getId());
        m.put("nombre", p.getNombre());
        if (incluirPropietario) m.put("propietario", p.getAliasPropietario());
        List<Object> ids = new ArrayList<>();
        for (Audio a : p.getContenido()) ids.add(a.getId());
        m.put("audios", ids);
        return m;
    }

    private static Map<String, Object> deCompra(Compra c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("idProducto", c.getIdProducto());
        m.put("monto", c.getMontoPagado());
        m.put("fecha", c.getFecha().format(FMT));
        return m;
    }
}
