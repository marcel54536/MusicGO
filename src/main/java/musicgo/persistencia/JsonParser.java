package musicgo.persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser JSON minimalista hecho a mano para no depender de librerias
 * externas (la idea es que el .jar del proyecto sea autosuficiente).
 *
 * <p>Soporta: objetos {@code {}}, arreglos {@code []}, strings,
 * numeros (devueltos como Double o Long), booleanos y null.</p>
 *
 * <p>NO soporta: comentarios, NaN/Infinity, escapes Unicode tipo backslash-u,
 * porque para los archivos del proyecto no nos hace falta.</p>
 *
 * <p>Uso tipico:</p>
 * <pre>
 *   Object raiz = JsonParser.parse(textoJson);
 *   if (raiz instanceof Map) { ... }
 * </pre>
 *
 * @author Equipo MusicGO
 */
public class JsonParser {

    private final String src;
    private int pos;

    private JsonParser(String src) {
        this.src = src;
        this.pos = 0;
    }

    /**
     * Parsea un texto JSON y devuelve la raiz como objeto Java
     * (Map, List, String, Double, Long, Boolean o null).
     *
     * @param json texto JSON
     * @return objeto raiz
     */
    public static Object parse(String json) {
        if (json == null) return null;
        JsonParser p = new JsonParser(json);
        p.saltarBlancos();
        Object res = p.leerValor();
        p.saltarBlancos();
        if (p.pos < p.src.length()) {
            throw new RuntimeException("JSON con basura al final, pos=" + p.pos);
        }
        return res;
    }

    private Object leerValor() {
        saltarBlancos();
        if (pos >= src.length()) {
            throw new RuntimeException("Fin de JSON inesperado");
        }
        char c = src.charAt(pos);
        if (c == '{') return leerObjeto();
        if (c == '[') return leerArreglo();
        if (c == '"') return leerString();
        if (c == 't' || c == 'f') return leerBooleano();
        if (c == 'n') return leerNull();
        // numero: arranca con digito o con '-'
        if (c == '-' || Character.isDigit(c)) return leerNumero();
        throw new RuntimeException("Caracter inesperado '" + c + "' en pos " + pos);
    }

    private Map<String, Object> leerObjeto() {
        Map<String, Object> mapa = new LinkedHashMap<>();
        consumir('{');
        saltarBlancos();
        if (mirar() == '}') {
            pos++;
            return mapa;
        }
        while (true) {
            saltarBlancos();
            String clave = leerString();
            saltarBlancos();
            consumir(':');
            Object valor = leerValor();
            mapa.put(clave, valor);
            saltarBlancos();
            char sig = mirar();
            if (sig == ',') {
                pos++;
                continue;
            }
            if (sig == '}') {
                pos++;
                break;
            }
            throw new RuntimeException("Se esperaba ',' o '}' en pos " + pos);
        }
        return mapa;
    }

    private List<Object> leerArreglo() {
        List<Object> lista = new ArrayList<>();
        consumir('[');
        saltarBlancos();
        if (mirar() == ']') {
            pos++;
            return lista;
        }
        while (true) {
            Object val = leerValor();
            lista.add(val);
            saltarBlancos();
            char sig = mirar();
            if (sig == ',') {
                pos++;
                continue;
            }
            if (sig == ']') {
                pos++;
                break;
            }
            throw new RuntimeException("Se esperaba ',' o ']' en pos " + pos);
        }
        return lista;
    }

    private String leerString() {
        consumir('"');
        StringBuilder sb = new StringBuilder();
        while (pos < src.length()) {
            char c = src.charAt(pos++);
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                if (pos >= src.length()) {
                    throw new RuntimeException("Escape JSON cortado");
                }
                char esc = src.charAt(pos++);
                switch (esc) {
                    case '"':  sb.append('"');  break;
                    case '\\': sb.append('\\'); break;
                    case '/':  sb.append('/');  break;
                    case 'n':  sb.append('\n'); break;
                    case 't':  sb.append('\t'); break;
                    case 'r':  sb.append('\r'); break;
                    case 'b':  sb.append('\b'); break;
                    case 'f':  sb.append('\f'); break;
                    default:
                        // no soportamos escapes unicode; lo dejamos pasar como tal
                        sb.append(esc);
                }
            } else {
                sb.append(c);
            }
        }
        throw new RuntimeException("String JSON sin cerrar");
    }

    private Object leerNumero() {
        int inicio = pos;
        if (mirar() == '-') pos++;
        while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        boolean esDecimal = false;
        if (pos < src.length() && src.charAt(pos) == '.') {
            esDecimal = true;
            pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        }
        if (pos < src.length() && (src.charAt(pos) == 'e' || src.charAt(pos) == 'E')) {
            esDecimal = true;
            pos++;
            if (pos < src.length() && (src.charAt(pos) == '+' || src.charAt(pos) == '-')) pos++;
            while (pos < src.length() && Character.isDigit(src.charAt(pos))) pos++;
        }
        String num = src.substring(inicio, pos);
        if (esDecimal) {
            return Double.parseDouble(num);
        } else {
            return Long.parseLong(num);
        }
    }

    private Boolean leerBooleano() {
        if (src.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        if (src.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw new RuntimeException("Booleano invalido en pos " + pos);
    }

    private Object leerNull() {
        if (src.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new RuntimeException("'null' invalido en pos " + pos);
    }

    private void saltarBlancos() {
        while (pos < src.length()
                && Character.isWhitespace(src.charAt(pos))) {
            pos++;
        }
    }

    private char mirar() {
        if (pos >= src.length()) {
            throw new RuntimeException("Fin de JSON inesperado");
        }
        return src.charAt(pos);
    }

    private void consumir(char esperado) {
        if (pos >= src.length() || src.charAt(pos) != esperado) {
            throw new RuntimeException("Se esperaba '" + esperado + "' en pos " + pos);
        }
        pos++;
    }

    // -------- helpers de acceso comodos --------

    /**
     * Cast seguro a Map. Si no es un objeto, lanza excepcion clara.
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> asMap(Object o) {
        if (!(o instanceof Map)) {
            throw new RuntimeException("Se esperaba un objeto JSON pero llego: " + o);
        }
        return (Map<String, Object>) o;
    }

    /**
     * Cast seguro a List.
     */
    @SuppressWarnings("unchecked")
    public static List<Object> asList(Object o) {
        if (o == null) return new ArrayList<>();
        if (!(o instanceof List)) {
            throw new RuntimeException("Se esperaba un arreglo JSON pero llego: " + o);
        }
        return (List<Object>) o;
    }

    public static String asString(Object o) {
        return (o == null) ? null : o.toString();
    }

    public static int asInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        return Integer.parseInt(o.toString());
    }

    public static long asLong(Object o) {
        if (o == null) return 0L;
        if (o instanceof Number) return ((Number) o).longValue();
        return Long.parseLong(o.toString());
    }

    public static double asDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number) return ((Number) o).doubleValue();
        return Double.parseDouble(o.toString());
    }
}
