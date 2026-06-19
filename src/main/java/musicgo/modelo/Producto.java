package musicgo.modelo;

import musicgo.interfaces.Comprable;
import musicgo.interfaces.Identificable;

/**
 * Clase abstracta de cualquier producto especial a la venta. No todos
 * los productos son audio (artes visuales, paquetes Top Ten): por eso
 * se separa de {@link Audio} aunque ambos sean {@link Identificable}.
 *
 * <p>Fase 2: {@code mostrarDetalle()} (que imprimia en consola) se
 * reemplazo por {@link #detalle()}, que <em>devuelve</em> el texto y deja
 * que la vista decida como mostrarlo (MVC).</p>
 *
 * @author Equipo MusicGO
 */
public abstract class Producto implements Comprable, Identificable {

    private final String id;
    private String nombre;
    private double precio;
    private String descripcion;

    protected Producto(String id, String nombre, double precio, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    @Override
    public String getDescripcion() {
        return descripcion;
    }

    public void setNombre(String nombre) {
        if (nombre != null && !nombre.isBlank()) this.nombre = nombre.trim();
    }

    public void setPrecio(double precio) {
        if (precio >= 0) this.precio = precio;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** @return cadena identificadora del tipo de producto (para JSON) */
    public abstract String getTipo();

    /**
     * Detalle legible del producto (polimorfico). La vista lo muestra
     * tal cual; el modelo no conoce la interfaz.
     * @return texto multilinea con los datos del producto
     */
    public abstract String detalle();
}
