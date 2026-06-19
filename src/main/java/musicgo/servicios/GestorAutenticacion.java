package musicgo.servicios;

import musicgo.excepciones.CredencialesInvalidasException;
import musicgo.modelo.Administrador;
import musicgo.modelo.Usuario;

/**
 * Punto unico de autenticacion. Distingue los dos perfiles de la Fase 2:
 * el usuario final (entra por alias) y el administrador (entra validando
 * usuario + clave contra su archivo exclusivo).
 *
 * @author Equipo MusicGO
 */
public class GestorAutenticacion {

    private final GestorUsuarios gestorUsuarios;
    private final GestorAdministradores gestorAdmins;

    public GestorAutenticacion(GestorUsuarios gestorUsuarios, GestorAdministradores gestorAdmins) {
        this.gestorUsuarios = gestorUsuarios;
        this.gestorAdmins = gestorAdmins;
    }

    /**
     * Inicia sesion como usuario final.
     *
     * @param alias alias registrado
     * @return el usuario autenticado
     * @throws CredencialesInvalidasException si el alias no existe
     */
    public Usuario iniciarSesionUsuario(String alias) throws CredencialesInvalidasException {
        Usuario u = gestorUsuarios.buscar(alias);
        if (u == null) {
            throw new CredencialesInvalidasException(
                    "No existe ningun usuario con el alias '" + alias + "'.");
        }
        return u;
    }

    /**
     * Inicia sesion como administrador validando credenciales.
     *
     * @param usuario alias del administrador
     * @param clave clave del administrador
     * @return el administrador autenticado
     * @throws CredencialesInvalidasException si las credenciales no coinciden
     */
    public Administrador iniciarSesionAdmin(String usuario, String clave)
            throws CredencialesInvalidasException {
        Administrador a = gestorAdmins.validar(usuario, clave);
        if (a == null) {
            throw new CredencialesInvalidasException();
        }
        return a;
    }
}
