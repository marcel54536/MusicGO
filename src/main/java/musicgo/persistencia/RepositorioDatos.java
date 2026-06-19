package musicgo.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Lectura/escritura de archivos JSON desde disco.
 *
 * Centraliza el acceso a archivos para que el resto del codigo no
 * tenga que andar lidiando con IOException ni paths concretos.
 *
 * @author Equipo MusicGO
 */
public class RepositorioDatos {

    private final String rutaBase;

    /**
     * @param rutaBase carpeta que contiene los .json del proyecto
     *                 (por defecto suele ser "data/")
     */
    public RepositorioDatos(String rutaBase) {
        this.rutaBase = rutaBase;
    }

    /**
     * Lee el archivo y devuelve la raiz parseada.
     *
     * @param nombreArchivo nombre del archivo dentro de la carpeta base
     * @return objeto raiz del JSON, o null si el archivo no existe
     */
    public Object leer(String nombreArchivo) {
        Path p = Paths.get(rutaBase, nombreArchivo);
        if (!Files.exists(p)) return null;
        try {
            String contenido = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
            if (contenido.isBlank()) return null;
            return JsonParser.parse(contenido);
        } catch (IOException e) {
            System.err.println("[Persistencia] Error leyendo " + nombreArchivo + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Escribe un objeto Java como JSON en el archivo indicado.
     *
     * @param nombreArchivo destino
     * @param contenido objeto a serializar
     * @return true si se grabo, false si hubo error de IO
     */
    public boolean escribir(String nombreArchivo, Object contenido) {
        Path p = Paths.get(rutaBase, nombreArchivo);
        try {
            Files.createDirectories(p.getParent() == null ? Paths.get(".") : p.getParent());
            String json = JsonWriter.toJson(contenido);
            Files.write(p, json.getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            System.err.println("[Persistencia] Error escribiendo " + nombreArchivo + ": " + e.getMessage());
            return false;
        }
    }

    public String getRutaBase() {
        return rutaBase;
    }
}
