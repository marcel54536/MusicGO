package musicgo.persistencia;

import musicgo.modelo.ArteVisualAlbum;
import musicgo.modelo.Audio;
import musicgo.modelo.Cancion;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Clasificacion;
import musicgo.modelo.EpisodioPodcast;
import musicgo.modelo.PaqueteTopTen;
import musicgo.modelo.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Traduce el catalogo entre su forma JSON y los objetos del dominio en
 * ambos sentidos. Es la unica clase que conoce la "forma" del JSON del
 * catalogo (SRP): asi GestorCatalogo se ocupa solo de la logica de negocio.
 *
 * @author Equipo MusicGO
 */
public final class MapeadorCatalogo {

    private MapeadorCatalogo() { }

    // ---------------- JSON -> dominio ----------------

    /** Llena el catalogo con lo que haya en la raiz JSON ya parseada. */
    public static void poblar(Catalogo catalogo, Object raiz) {
        if (raiz == null) return;
        Map<String, Object> mapa = JsonParser.asMap(raiz);
        for (Object o : JsonParser.asList(mapa.get("audios"))) {
            Audio a = aAudio(JsonParser.asMap(o));
            if (a != null) catalogo.agregarAudio(a);
        }
        for (Object o : JsonParser.asList(mapa.get("productos"))) {
            Producto p = aProducto(JsonParser.asMap(o));
            if (p != null) catalogo.agregarProducto(p);
        }
    }

    private static Audio aAudio(Map<String, Object> o) {
        String tipo = JsonParser.asString(o.get("tipo"));
        Audio a;
        if ("cancion".equalsIgnoreCase(tipo)) {
            a = new Cancion(s(o, "id"), s(o, "titulo"), i(o, "duracion"),
                    s(o, "artista"), s(o, "album"), s(o, "genero"));
        } else if ("episodio".equalsIgnoreCase(tipo)) {
            a = new EpisodioPodcast(s(o, "id"), s(o, "titulo"), i(o, "duracion"),
                    s(o, "anfitrion"), s(o, "podcast"), s(o, "descripcion"), i(o, "episodio"));
        } else {
            return null;
        }
        a.setClasificacion(Clasificacion.desde(s(o, "clasificacion")));
        return a;
    }

    private static Producto aProducto(Map<String, Object> o) {
        String tipo = JsonParser.asString(o.get("tipo"));
        if ("arte_visual".equalsIgnoreCase(tipo)) {
            return new ArteVisualAlbum(s(o, "id"), s(o, "nombre"), d(o, "precio"),
                    s(o, "descripcion"), s(o, "album"), s(o, "artista"), s(o, "formato"));
        }
        if ("paquete_top_ten".equalsIgnoreCase(tipo)) {
            PaqueteTopTen p = new PaqueteTopTen(s(o, "id"), s(o, "nombre"), d(o, "precio"),
                    s(o, "descripcion"), s(o, "tematica"));
            for (Object id : JsonParser.asList(o.get("idsCanciones"))) {
                p.agregarCancion(JsonParser.asString(id));
            }
            return p;
        }
        return null;
    }

    // ---------------- dominio -> JSON ----------------

    /** Serializa el catalogo completo a un mapa listo para escribir. */
    public static Map<String, Object> aMapa(Catalogo catalogo) {
        Map<String, Object> raiz = new LinkedHashMap<>();
        List<Object> audios = new ArrayList<>();
        for (Audio a : catalogo.getAudios()) audios.add(deAudio(a));
        List<Object> productos = new ArrayList<>();
        for (Producto p : catalogo.getProductos()) productos.add(deProducto(p));
        raiz.put("audios", audios);
        raiz.put("productos", productos);
        return raiz;
    }

    private static Map<String, Object> deAudio(Audio a) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tipo", a.getTipo());
        m.put("id", a.getId());
        m.put("titulo", a.getTitulo());
        m.put("duracion", a.getDuracionSegundos());
        if (a instanceof Cancion c) {
            m.put("artista", c.getArtista());
            m.put("album", c.getAlbum());
            m.put("genero", c.getGenero());
        } else if (a instanceof EpisodioPodcast e) {
            m.put("anfitrion", e.getAnfitrion());
            m.put("podcast", e.getNombrePodcast());
            m.put("descripcion", e.getDescripcion());
            m.put("episodio", e.getNumeroEpisodio());
        }
        m.put("clasificacion", a.getClasificacion().name());
        return m;
    }

    private static Map<String, Object> deProducto(Producto p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("tipo", p.getTipo());
        m.put("id", p.getId());
        m.put("nombre", p.getNombre());
        m.put("precio", p.getPrecio());
        m.put("descripcion", p.getDescripcion());
        if (p instanceof ArteVisualAlbum av) {
            m.put("album", av.getAlbumAsociado());
            m.put("artista", av.getArtista());
            m.put("formato", av.getFormato());
        } else if (p instanceof PaqueteTopTen pt) {
            m.put("tematica", pt.getTematica());
            m.put("idsCanciones", new ArrayList<Object>(pt.getIdsCanciones()));
        }
        return m;
    }

    // ---------------- helpers cortos ----------------

    private static String s(Map<String, Object> o, String k) { return JsonParser.asString(o.get(k)); }
    private static int i(Map<String, Object> o, String k) { return JsonParser.asInt(o.get(k)); }
    private static double d(Map<String, Object> o, String k) { return JsonParser.asDouble(o.get(k)); }
}
