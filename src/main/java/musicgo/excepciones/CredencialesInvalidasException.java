package musicgo.excepciones;

/**
 * Se lanza cuando un intento de inicio de sesion (usuario o administrador)
 * presenta credenciales que no coinciden con ningun registro valido.
 *
 * @author Equipo MusicGO
 */
public class CredencialesInvalidasException extends Exception {

    public CredencialesInvalidasException() {
        super("Credenciales invalidas. Verifica tus datos e intenta de nuevo.");
    }

    public CredencialesInvalidasException(String detalle) {
        super(detalle);
    }
}
