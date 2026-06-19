package musicgo.modelo;

import musicgo.interfaces.Identificable;

import java.util.ArrayList;
import java.util.List;

/**
 * Usuario final de la plataforma MusicGO.
 *
 * <p>Su alias (unico) actua como identificador. Fase 2 agrega: saldo
 * virtual MusicGo, bandera de control parental y las playlists que otros
 * usuarios le compartieron.</p>
 *
 * <p>Relaciones: composicion con {@link Biblioteca} y {@link Estadisticas};
 * agregacion con {@link Compra} y con las playlists compartidas.</p>
 *
 * @author Equipo MusicGO
 */
public class Usuario implements Identificable {

    private String alias;
    private String correo;
    private double saldo;
    private boolean controlParental;
    private final Biblioteca biblioteca;
    private final Estadisticas estadisticas;
    private final List<Compra> historialCompras;
    private final List<Playlist> compartidasConmigo;

    public Usuario(String alias, String correo) {
        this(alias, correo, new Biblioteca(), new Estadisticas(), new ArrayList<>());
    }

    /** Constructor para reconstruir un usuario desde JSON con sus datos completos. */
    public Usuario(String alias, String correo, Biblioteca biblioteca,
                   Estadisticas estadisticas, List<Compra> historialCompras) {
        this.alias = alias;
        this.correo = correo;
        this.biblioteca = (biblioteca != null) ? biblioteca : new Biblioteca();
        this.estadisticas = (estadisticas != null) ? estadisticas : new Estadisticas();
        this.historialCompras = (historialCompras != null) ? historialCompras : new ArrayList<>();
        this.compartidasConmigo = new ArrayList<>();
        this.saldo = 0.0;
        this.controlParental = false;
    }

    /** El alias funciona como id en este sistema. */
    @Override
    public String getId() {
        return alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getCorreo() {
        return correo;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean tieneControlParental() {
        return controlParental;
    }

    public Biblioteca getBiblioteca() {
        return biblioteca;
    }

    public Estadisticas getEstadisticas() {
        return estadisticas;
    }

    public List<Compra> getHistorialCompras() {
        return historialCompras;
    }

    public List<Playlist> getCompartidasConmigo() {
        return compartidasConmigo;
    }

    public void setAlias(String alias) {
        if (alias != null && !alias.isBlank()) this.alias = alias.trim();
    }

    public void setCorreo(String correo) {
        if (correo != null && !correo.isBlank()) this.correo = correo.trim();
    }

    public void setControlParental(boolean controlParental) {
        this.controlParental = controlParental;
    }

    /** Acredita saldo virtual (recarga). Ignora montos no positivos. */
    public void recargarSaldo(double monto) {
        if (monto > 0) this.saldo += monto;
    }

    /**
     * Intenta descontar saldo virtual.
     * @return true si habia fondos y se descontó; false si no alcanzaba
     */
    public boolean descontarSaldo(double monto) {
        if (monto <= 0 || monto > saldo) return false;
        this.saldo -= monto;
        return true;
    }

    /** Carga directa del saldo (uso de persistencia). */
    public void setSaldo(double saldo) {
        if (saldo >= 0) this.saldo = saldo;
    }

    /** Agrega una compra al historial y actualiza las estadisticas. */
    public void registrarCompra(Compra c) {
        if (c == null) return;
        historialCompras.add(c);
        estadisticas.registrarCompra();
    }

    /** Recibe una playlist compartida por otro usuario (sin duplicar). */
    public void recibirPlaylist(Playlist p) {
        if (p == null) return;
        for (Playlist existente : compartidasConmigo) {
            if (existente.getId().equals(p.getId())
                    && existente.getAliasPropietario().equals(p.getAliasPropietario())) {
                return;
            }
        }
        compartidasConmigo.add(p);
    }

    /** Recalcula la cantidad de canciones en biblioteca a partir de las playlists. */
    public void refrescarConteoBiblioteca() {
        estadisticas.setCancionesEnBiblioteca(biblioteca.totalCanciones());
    }

    @Override
    public String toString() {
        return "Usuario{alias='" + alias + "', correo='" + correo
                + "', saldo=" + saldo + ", playlists=" + biblioteca.cantidadPlaylists()
                + ", compras=" + historialCompras.size() + "}";
    }
}
