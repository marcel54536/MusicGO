package musicgo.controlador;

import musicgo.app.AppContext;
import musicgo.app.Router;
import musicgo.excepciones.CredencialesInvalidasException;
import musicgo.excepciones.UsuarioYaExisteException;
import musicgo.modelo.Administrador;
import musicgo.modelo.Usuario;
import musicgo.vista.LoginVista;
import musicgo.vista.ui.Alertas;

/**
 * Controla la pantalla de autenticacion: valida credenciales contra los
 * servicios, registra usuarios nuevos y navega al panel correspondiente
 * (back-office o app de usuario) segun el perfil.
 *
 * @author Equipo MusicGO
 */
public class LoginControlador {

    private final AppContext ctx;
    private final Router router;
    private final LoginVista vista = new LoginVista();

    public LoginControlador(AppContext ctx, Router router) {
        this.ctx = ctx;
        this.router = router;
    }

    public void iniciar() {
        vista.getTabUsuario().setOnAction(e -> vista.seleccionarTab(true));
        vista.getTabAdmin().setOnAction(e -> vista.seleccionarTab(false));
        vista.getIrRegistro().setOnAction(e -> vista.mostrarRegistro());
        vista.getVolverLogin().setOnAction(e -> vista.seleccionarTab(true));

        vista.getEntrarUsuario().setOnAction(e -> entrarUsuario());
        vista.getEntrarAdmin().setOnAction(e -> entrarAdmin());
        vista.getCrearCuenta().setOnAction(e -> registrar());
        validacionEnVivo();

        router.mostrar(vista.getRaiz());
    }

    private void entrarUsuario() {
        try {
            Usuario u = ctx.autenticacion().iniciarSesionUsuario(vista.getAliasUsuario().getText());
            new UsuarioControlador(ctx, router, u).iniciar();
        } catch (CredencialesInvalidasException ex) {
            Alertas.error(ex.getMessage());
        }
    }

    private void entrarAdmin() {
        try {
            Administrador a = ctx.autenticacion().iniciarSesionAdmin(
                    vista.getUsuarioAdmin().getText(), vista.getClaveAdmin().getText());
            new AdminControlador(ctx, router, a).iniciar();
        } catch (CredencialesInvalidasException ex) {
            Alertas.error(ex.getMessage());
        }
    }

    private void registrar() {
        try {
            Usuario u = ctx.usuarios().registrar(
                    vista.getAliasRegistro().getText(), vista.getCorreoRegistro().getText());
            Alertas.exito("Cuenta '" + u.getAlias() + "' creada. Ya puedes iniciar sesion.");
            vista.getAliasUsuario().setText(u.getAlias());
            vista.seleccionarTab(true);
        } catch (UsuarioYaExisteException | IllegalArgumentException ex) {
            Alertas.error(ex.getMessage());
        }
    }

    /** Avisa en vivo si el alias de registro ya esta tomado (unicidad). */
    private void validacionEnVivo() {
        vista.getAliasRegistro().textProperty().addListener((obs, viejo, nuevo) -> {
            boolean tomado = !nuevo.isBlank() && ctx.usuarios().existe(nuevo);
            vista.getCrearCuenta().setDisable(tomado);
            vista.getCrearCuenta().setText(tomado ? "Alias no disponible" : "Crear cuenta");
        });
    }
}
