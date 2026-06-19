package musicgo.vista.usuario;

import javafx.geometry.Insets;
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
 * Armazon de la app del usuario final: barra superior (saldo, control
 * parental, sesion), barra lateral de navegacion, area de contenido
 * intercambiable y el mini reproductor fijo abajo.
 *
 * @author Equipo MusicGO
 */
public class AppUsuarioVista implements Vista {

    private final BorderPane raiz = new BorderPane();
    private final StackPane contenido = new StackPane();
    private final ReproductorBarra reproductor = new ReproductorBarra();

    private final Button navCatalogo = navItem("Catalogo");
    private final Button navPlaylists = navItem("Mis playlists");
    private final Button navEstadisticas = navItem("Estadisticas");
    private final Button logout = Componentes.botonGhost("Cerrar sesion");
    private final CheckBox controlParental = new CheckBox("Control parental");
    private final Label saldo = Componentes.chip("Saldo: $0.00");

    public AppUsuarioVista(Usuario usuario) {
        raiz.getStyleClass().add("root");
        controlParental.setSelected(usuario.tieneControlParental());
        raiz.setTop(barraSuperior(usuario));
        raiz.setLeft(barraLateral());
        raiz.setCenter(contenido);
        raiz.setBottom(reproductor.getRaiz());
        actualizarSaldo(usuario.getSaldo());
    }

    private HBox barraSuperior(Usuario u) {
        HBox barra = Componentes.hbox(Tema.ESPACIO,
                Componentes.marca("MusicGO"),
                Componentes.espaciador(),
                saldo, controlParental,
                Componentes.textoSuave("@" + u.getAlias()), logout);
        barra.getStyleClass().add("topbar");
        barra.setPadding(new Insets(Tema.ESPACIO));
        return barra;
    }

    private VBox barraLateral() {
        VBox lateral = Componentes.vbox(Tema.ESPACIO_S, navCatalogo, navPlaylists, navEstadisticas);
        lateral.getStyleClass().add("sidebar");
        lateral.setPadding(new Insets(Tema.ESPACIO));
        lateral.setPrefWidth(210);
        return lateral;
    }

    private static Button navItem(String texto) {
        Button b = new Button(texto);
        b.getStyleClass().add("nav-item");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    /** Coloca un panel en el area central. */
    public void mostrarContenido(Parent panel) {
        contenido.getChildren().setAll(panel);
    }

    /** Resalta el item de navegacion activo. */
    public void marcarNav(Button activo) {
        for (Button b : new Button[]{navCatalogo, navPlaylists, navEstadisticas}) {
            b.getStyleClass().remove("nav-activo");
        }
        activo.getStyleClass().add("nav-activo");
    }

    public void actualizarSaldo(double monto) {
        saldo.setText(String.format("Saldo: $%.2f", monto));
    }

    public ReproductorBarra getReproductor() { return reproductor; }
    public Button getNavCatalogo()    { return navCatalogo; }
    public Button getNavPlaylists()   { return navPlaylists; }
    public Button getNavEstadisticas() { return navEstadisticas; }
    public Button getLogout()         { return logout; }
    public CheckBox getControlParental() { return controlParental; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
