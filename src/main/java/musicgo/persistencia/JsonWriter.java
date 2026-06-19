package musicgo.persistencia;

import java.util.List;
import java.util.Map;

/**
 * Helper para construir texto JSON con sangria legible.
 *
 * Hecho a mano para no tener que usar Gson/Jackson y mantener el
 * proyecto sin dependencias externas. Soporta los mismos tipos que
 * {@link JsonParser}: Map, List, String, Number, Boolean y null.
 *
 * @author Equipo MusicGO
 */
public class JsonWriter {

    private static final String INDENT = "  ";

    /**
     * Convierte un objeto Java al texto JSON correspondiente.
     *
     * @param obj objeto a serializar
     * @return texto JSON con sangria de 2 espacios
     */
    public static String toJson(Object obj) {
        StringBuilder sb = new StringBuilder();
        escribirValor(obj, sb, 0);
        return sb.toString();
    }

    private static void escribirValor(Object o, StringBuilder sb, int nivel) {
        if (o == null) {
            sb.append("null");
        } else if (o instanceof String) {
            escribirString((String) o, sb);
        } else if (o instanceof Boolean) {
            sb.append(o.toString());
        } else if (o instanceof Number) {
            sb.append(o.toString());
        } else if (o instanceof Map) {
            escribirObjeto((Map<?, ?>) o, sb, nivel);
        } else if (o instanceof List) {
            escribirArreglo((List<?>) o, sb, nivel);
        } else {
            // fallback: lo convertimos a string
            escribirString(o.toString(), sb);
        }
    }

    private static void escribirObjeto(Map<?, ?> mapa, StringBuilder sb, int nivel) {
        if (mapa.isEmpty()) {
            sb.append("{}");
            return;
        }
        sb.append("{\n");
        int i = 0;
        int total = mapa.size();
        for (Map.Entry<?, ?> e : mapa.entrySet()) {
            indentar(sb, nivel + 1);
            escribirString(e.getKey().toString(), sb);
            sb.append(": ");
            escribirValor(e.getValue(), sb, nivel + 1);
            if (i < total - 1) sb.append(",");
            sb.append("\n");
            i++;
        }
        indentar(sb, nivel);
        sb.append("}");
    }

    private static void escribirArreglo(List<?> lista, StringBuilder sb, int nivel) {
        if (lista.isEmpty()) {
            sb.append("[]");
            return;
        }
        sb.append("[\n");
        for (int i = 0; i < lista.size(); i++) {
            indentar(sb, nivel + 1);
            escribirValor(lista.get(i), sb, nivel + 1);
            if (i < lista.size() - 1) sb.append(",");
            sb.append("\n");
        }
        indentar(sb, nivel);
        sb.append("]");
    }

    private static void escribirString(String s, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\t': sb.append("\\t");  break;
                case '\r': sb.append("\\r");  break;
                case '\b': sb.append("\\b");  break;
                case '\f': sb.append("\\f");  break;
                default:
                    sb.append(c);
            }
        }
        sb.append('"');
    }

    private static void indentar(StringBuilder sb, int nivel) {
        for (int i = 0; i < nivel; i++) {
            sb.append(INDENT);
        }
    }
}
