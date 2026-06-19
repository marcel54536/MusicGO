package musicgo.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

/**
 * Pantalla de autenticacion: alterna entre acceso de usuario final,
 * acceso de administrador y registro de cuenta. Solo arma la interfaz y
 * expone sus controles; el {@code LoginControlador} les pone la logica.
 *
 * @author Equipo MusicGO
 */
public class LoginVista implements Vista {

    private final StackPane raiz = new StackPane();
    private final StackPane cuerpo = new StackPane();

    private final Button tabUsuario = Componentes.botonGhost("Usuario");
    private final Button tabAdmin = Componentes.botonGhost("Administrador");

    private final TextField aliasUsuario = Componentes.campo("Tu alias");
    private final Button entrarUsuario = Componentes.botonPrimario("Entrar");
    private final Button irRegistro = Componentes.botonGhost("Crear una");

    private final TextField usuarioAdmin = Componentes.campo("Usuario administrador");
    private final PasswordField claveAdmin = Componentes.clave("Clave");
    private final Button entrarAdmin = Componentes.botonPrimario("Entrar al panel");

    private final TextField aliasRegistro = Componentes.campo("Elige un alias");
    private final TextField correoRegistro = Componentes.campo("correo@ejemplo.com");
    private final Button crearCuenta = Componentes.botonPrimario("Crear cuenta");
    private final Button volverLogin = Componentes.botonGhost("Volver");

    public LoginVista() {
        raiz.getStyleClass().add("root");
        VBox tarjeta = Componentes.card(
                Componentes.marca("MusicGO"),
                Componentes.textoSuave("Tu musica y tus podcasts, sin limites."),
                tabs(),
                cuerpo);
        tarjeta.setMaxWidth(420);
        tarjeta.setPadding(new Insets(Tema.ESPACIO_L));
        tarjeta.setSpacing(Tema.ESPACIO);
        anchoCompleto(entrarUsuario, entrarAdmin, crearCuenta);
        raiz.getChildren().add(tarjeta);
        seleccionarTab(true);
    }

    private HBox tabs() {
        HBox h = Componentes.hbox(Tema.ESPACIO_S, tabUsuario, tabAdmin);
        h.setAlignment(Pos.CENTER);
        return h;
    }

    private VBox panelUsuario() {
        return Componentes.vbox(Tema.ESPACIO_S,
                Componentes.subtitulo("Iniciar sesion"),
                aliasUsuario, entrarUsuario,
                Componentes.hbox(Tema.ESPACIO_S,
                        Componentes.textoSuave("No tienes cuenta?"), irRegistro));
    }

    private VBox panelAdmin() {
        return Componentes.vbox(Tema.ESPACIO_S,
                Componentes.subtitulo("Acceso administrador"),
                usuarioAdmin, claveAdmin, entrarAdmin);
    }

    private VBox panelRegistro() {
        return Componentes.vbox(Tema.ESPACIO_S,
                Componentes.subtitulo("Crear cuenta"),
                aliasRegistro, correoRegistro, crearCuenta, volverLogin);
    }

    /** Cambia entre la pestania de usuario y la de administrador. */
    public void seleccionarTab(boolean usuario) {
        tabUsuario.getStyleClass().remove("nav-activo");
        tabAdmin.getStyleClass().remove("nav-activo");
        tabUsuario.getStyleClass().add(usuario ? "nav-activo" : "btn-ghost");
        tabAdmin.getStyleClass().add(usuario ? "btn-ghost" : "nav-activo");
        cuerpo.getChildren().setAll(usuario ? panelUsuario() : panelAdmin());
    }

    public void mostrarRegistro() {
        cuerpo.getChildren().setAll(panelRegistro());
    }

    private void anchoCompleto(Button... botones) {
        for (Button b : botones) b.setMaxWidth(Double.MAX_VALUE);
    }

    // --- getters para el controlador ---
    public Button getTabUsuario()     { return tabUsuario; }
    public Button getTabAdmin()       { return tabAdmin; }
    public Button getIrRegistro()     { return irRegistro; }
    public Button getVolverLogin()    { return volverLogin; }
    public Button getEntrarUsuario()  { return entrarUsuario; }
    public Button getEntrarAdmin()    { return entrarAdmin; }
    public Button getCrearCuenta()    { return crearCuenta; }
    public TextField getAliasUsuario()    { return aliasUsuario; }
    public TextField getUsuarioAdmin()    { return usuarioAdmin; }
    public PasswordField getClaveAdmin()  { return claveAdmin; }
    public TextField getAliasRegistro()   { return aliasRegistro; }
    public TextField getCorreoRegistro()  { return correoRegistro; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
