package musicgo.modelo;

import musicgo.interfaces.Identificable;

/**
 * Administrador de la plataforma (perfil Back-Office de la Fase 2).
 *
 * <p>A diferencia del {@link Usuario}, el administrador NO se registra
 * desde la interfaz: sus credenciales viven en un archivo persistente
 * exclusivo ({@code administradores.json}) y se validan al iniciar sesion
 * (abstraccion del registro publico).</p>
 *
 * @author Equipo MusicGO
 */
public class Administrador implements Identificable {

    private final String usuario;
    private final String clave;
    private final String nombre;

    public Administrador(String usuario, String clave, String nombre) {
        this.usuario = usuario;
        this.clave = clave;
        this.nombre = nombre;
    }

    /** El nombre de usuario actua como id del administrador. */
    @Override
    public String getId() {
        return usuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return nombre;
    }

    /**
     * Verifica credenciales. La comparacion vive en el modelo para no
     * exponer la clave fuera de la clase (encapsulamiento).
     *
     * @param usuarioIngresado alias tecleado en el login
     * @param claveIngresada clave tecleada en el login
     * @return true si ambas coinciden
     */
    public boolean credencialesValidas(String usuarioIngresado, String claveIngresada) {
        return usuario.equals(usuarioIngresado) && clave.equals(claveIngresada);
    }

    @Override
    public String toString() {
        return "Administrador{usuario='" + usuario + "', nombre='" + nombre + "'}";
    }
}
