package musicgo.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Producto especial tipo paquete "Top Ten": un combo de canciones
 * destacadas. Agrega los IDs de las canciones (agregacion por id) para
 * mantenerse desacoplado del catalogo al serializar a JSON.
 *
 * @author Equipo MusicGO
 */
public class PaqueteTopTen extends Producto {

    private String tematica;          // ej: "Pop 2025", "Rock Clasico"
    private final List<String> idsCanciones;

    public PaqueteTopTen(String id, String nombre, double precio,
                         String descripcion, String tematica) {
        super(id, nombre, precio, descripcion);
        this.tematica = tematica;
        this.idsCanciones = new ArrayList<>();
    }

    public String getTematica() {
        return tematica;
    }

    public void setTematica(String tematica) {
        this.tematica = tematica;
    }

    public List<String> getIdsCanciones() {
        return idsCanciones;
    }

    /** Agrega un id de cancion al paquete, sin duplicados. */
    public void agregarCancion(String idCancion) {
        if (idCancion != null && !idCancion.isBlank() && !idsCanciones.contains(idCancion)) {
            idsCanciones.add(idCancion);
        }
    }

    public int cantidadCanciones() {
        return idsCanciones.size();
    }

    @Override
    public String getTipo() {
        return "paquete_top_ten";
    }

    @Override
    public String detalle() {
        return "Paquete Top Ten\n"
                + "Tematica: " + tematica + "\n"
                + "Canciones incluidas: " + idsCanciones.size() + " (" + String.join(", ", idsCanciones) + ")\n"
                + "Precio: $" + String.format("%.2f", getPrecio());
    }

    @Override
    public String toString() {
        return "[TopTen] " + getId() + " - " + getNombre()
                + " (" + tematica + ") " + idsCanciones.size() + " canciones $" + getPrecio();
    }
}
