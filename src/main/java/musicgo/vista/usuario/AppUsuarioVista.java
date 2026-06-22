package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Usuario;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

/**
 * Armazón de la app del usuario final: barra superior (perfil, saldo,
 * control parental, sesión), barra lateral con TODOS los módulos
 * (catálogo, playlists, compras, estadísticas), área de contenido
 * intercambiable y el mini reproductor fijo abajo.
 *
 * @author Equipo MusicGO
 */
public class AppUsuarioVista implements Vista {

    private final BorderPane raiz = new BorderPane();
    private final StackPane contenido = new StackPane();
    private final ReproductorBarra reproductor = new ReproductorBarra();

    private final Button navCatalogo = navItem("🎵  Catálogo");
    private final Button navPlaylists = navItem("📚  Mis playlists");
    private final Button navCompras = navItem("🛒  Mis compras");
    private final Button navEstadisticas = navItem("📊  Estadísticas");
    private final Button logout = Componentes.botonGhost("Cerrar sesión");
    private final CheckBox controlParental = new CheckBox("Control parental");
    private final Label saldo = Componentes.chip("Saldo: $0.00");

    public AppUsuarioVista(Usuario usuario) {
        raiz.getStyleClass().add("root");
        controlParental.setSelected(usuario.tieneControlParental());
        raiz.setTop(barraSuperior(usuario));
        raiz.setLeft(barraLateral(usuario));
        raiz.setCenter(contenido);
        raiz.setBottom(reproductor.getRaiz());
        actualizarSaldo(usuario.getSaldo());
    }

    private HBox barraSuperior(Usuario u) {
        saldo.getStyleClass().add("chip-saldo");
        HBox barra = Componentes.hbox(Tema.ESPACIO,
                Componentes.marca("MusicGO"),
                Componentes.espaciador(),
                saldo, controlParental, logout);
        barra.getStyleClass().add("topbar");
        barra.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        return barra;
    }

    private VBox barraLateral(Usuario u) {
        HBox perfil = Componentes.hbox(Tema.ESPACIO_S,
                Componentes.caratula(u.getAlias(), 40),
                Componentes.vbox(0,
                        Componentes.texto(u.getAlias()),
                        Componentes.textoSuave(u.getCorreo())));
        VBox lateral = Componentes.vbox(Tema.ESPACIO_S,
                perfil,
                separador(),
                Componentes.textoSuave("MENÚ"),
                navCatalogo, navPlaylists, navCompras, navEstadisticas);
        lateral.getStyleClass().add("sidebar");
        lateral.setPadding(new Insets(Tema.ESPACIO));
        lateral.setPrefWidth(230);
        return lateral;
    }

    private static Label separador() {
        Label l = new Label();
        l.setMaxWidth(Double.MAX_VALUE);
        l.setStyle("-fx-border-color: #e3e3e8; -fx-border-width: 0 0 1 0;");
        return l;
    }

    private static Button navItem(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("nav-item");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        return b;
    }

    /** Coloca un panel en el área central. */
    public void mostrarContenido(Parent panel) {
        contenido.getChildren().setAll(panel);
    }

    /** Resalta el ítem de navegación activo. */
    public void marcarNav(Button activo) {
        for (Button b : new Button[]{navCatalogo, navPlaylists, navCompras, navEstadisticas}) {
            b.getStyleClass().remove("nav-activo");
        }
        activo.getStyleClass().add("nav-activo");
    }

    public void actualizarSaldo(double monto) {
        saldo.setText(String.format("💳  Saldo: $%.2f", monto));
    }

    public ReproductorBarra getReproductor() { return reproductor; }
    public Button getNavCatalogo()    { return navCatalogo; }
    public Button getNavPlaylists()   { return navPlaylists; }
    public Button getNavCompras()     { return navCompras; }
    public Button getNavEstadisticas() { return navEstadisticas; }
    public Button getLogout()         { return logout; }
    public CheckBox getControlParental() { return controlParental; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
