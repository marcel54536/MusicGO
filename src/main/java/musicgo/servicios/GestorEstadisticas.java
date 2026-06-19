package musicgo.servicios;

import musicgo.modelo.Audio;
import musicgo.modelo.Catalogo;
import musicgo.persistencia.JsonParser;
import musicgo.persistencia.ServicioPersistencia;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Estadisticas globales de la plataforma. Mantiene un contador de
 * reproducciones por contenido (persistido en {@code reproducciones.json})
 * para alimentar el "Top de Contenidos Mas Reproducidos" del dashboard.
 *
 * @author Equipo MusicGO
 */
public class GestorEstadisticas {

    private static final String ARCHIVO = "reproducciones.json";

    private final ServicioPersistencia persistencia;
    private final Map<String, Integer> conteo = new LinkedHashMap<>();

    /** Par (audio, reproducciones) para el ranking del dashboard. */
    public record Top(Audio audio, int reproducciones) { }

    public GestorEstadisticas(ServicioPersistencia persistencia) {
        this.persistencia = persistencia;
        cargar();
    }

    private void cargar() {
        Object raiz = persistencia.getRepositorio().leer(ARCHIVO);
        if (raiz == null) return;
        Map<String, Object> m = JsonParser.asMap(raiz);
        for (Map.Entry<String, Object> e : m.entrySet()) {
            conteo.put(e.getKey(), JsonParser.asInt(e.getValue()));
        }
    }

    /** Suma una reproduccion al contador del contenido y persiste en caliente. */
    public void registrarReproduccion(String idAudio) {
        conteo.merge(idAudio, 1, Integer::sum);
        persistencia.guardarAsync(ARCHIVO, new LinkedHashMap<>(conteo));
    }

    public int reproduccionesDe(String idAudio) {
        return conteo.getOrDefault(idAudio, 0);
    }

    /**
     * Ranking de los contenidos mas reproducidos de la plataforma.
     *
     * @param catalogo catalogo para resolver ids a objetos Audio
     * @param limite cantidad maxima de elementos del top
     * @return lista ordenada de mayor a menor por reproducciones
     */
    public List<Top> topReproducidos(Catalogo catalogo, int limite) {
        List<Top> tops = new ArrayList<>();
        for (Map.Entry<String, Integer> e : conteo.entrySet()) {
            Audio a = catalogo.buscarAudio(e.getKey());
            if (a != null) tops.add(new Top(a, e.getValue()));
        }
        tops.sort(Comparator.comparingInt(Top::reproducciones).reversed());
        return tops.size() > limite ? tops.subList(0, limite) : tops;
    }
}
