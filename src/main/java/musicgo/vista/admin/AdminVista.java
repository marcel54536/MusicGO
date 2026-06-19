package musicgo.vista.admin;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Audio;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Producto;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

import java.util.function.Consumer;

/**
 * Vista del back-office: barra superior, botones de alta y un listado del
 * catalogo con acciones de editar/eliminar por fila. Solo arma la UI y
 * expone controles y callbacks; la logica vive en el {@code AdminControlador}.
 *
 * @author Equipo MusicGO
 */
public class AdminVista implements Vista {

    private final BorderPane raiz = new BorderPane();
    private final VBox lista = new VBox(Tema.ESPACIO_S);

    private final Button nuevaCancion = Componentes.botonPrimario("+ Cancion");
    private final Button nuevoPodcast = Componentes.botonPrimario("+ Podcast");
    private final Button nuevoProducto = Componentes.botonPrimario("+ Producto");
    private final Button logout = Componentes.botonGhost("Cerrar sesion");

    private Consumer<Audio> onEditarAudio = a -> { };
    private Consumer<Producto> onEditarProducto = p -> { };
    private Consumer<String> onEliminar = id -> { };

    public AdminVista(String nombreAdmin) {
        raiz.getStyleClass().add("root");
        raiz.setTop(barraSuperior(nombreAdmin));
        raiz.setCenter(centro());
    }

    private HBox barraSuperior(String nombreAdmin) {
        HBox barra = Componentes.hbox(Tema.ESPACIO,
                Componentes.marca("MusicGO"), Componentes.chip("Back-Office"),
                Componentes.espaciador(),
                Componentes.textoSuave("Admin: " + nombreAdmin), logout);
        barra.getStyleClass().add("topbar");
        barra.setPadding(new Insets(Tema.ESPACIO));
        return barra;
    }

    private VBox centro() {
        HBox acciones = Componentes.hbox(Tema.ESPACIO_S,
                nuevaCancion, nuevoPodcast, nuevoProducto);
        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);

        VBox v = Componentes.vbox(Tema.ESPACIO,
                Componentes.titulo("Gestion del catalogo"),
                Componentes.textoSuave("Los cambios se reflejan al instante en la app de los usuarios."),
                acciones, scroll);
        v.setPadding(new Insets(Tema.ESPACIO_L));
        return v;
    }

    /** Vuelve a dibujar el listado completo a partir del catalogo. */
    public void render(Catalogo catalogo) {
        lista.getChildren().clear();
        lista.getChildren().add(Componentes.subtitulo("Audios (" + catalogo.getAudios().size() + ")"));
        for (Audio a : catalogo.getAudios()) {
            lista.getChildren().add(filaAudio(a));
        }
        lista.getChildren().add(Componentes.subtitulo("Productos (" + catalogo.getProductos().size() + ")"));
        for (Producto p : catalogo.getProductos()) {
            lista.getChildren().add(filaProducto(p));
        }
    }

    private HBox filaAudio(Audio a) {
        VBox info = Componentes.vbox(2,
                Componentes.texto(a.getTitulo()),
                Componentes.textoSuave(a.getTipo() + " · " + a.creditos()));
        Button editar = Componentes.botonSecundario("Editar");
        Button eliminar = Componentes.botonGhost("Eliminar");
        editar.setOnAction(e -> onEditarAudio.accept(a));
        eliminar.setOnAction(e -> onEliminar.accept(a.getId()));
        return fila(Componentes.caratula(a.getTitulo(), 44), info, a.getClasificacion().esRestringido(), editar, eliminar);
    }

    private HBox filaProducto(Producto p) {
        VBox info = Componentes.vbox(2,
                Componentes.texto(p.getNombre()),
                Componentes.textoSuave(p.getTipo() + " · $" + String.format("%.2f", p.getPrecio())));
        Button editar = Componentes.botonSecundario("Editar");
        Button eliminar = Componentes.botonGhost("Eliminar");
        editar.setOnAction(e -> onEditarProducto.accept(p));
        eliminar.setOnAction(e -> onEliminar.accept(p.getId()));
        return fila(Componentes.caratula(p.getNombre(), 44), info, false, editar, eliminar);
    }

    private HBox fila(javafx.scene.Node caratula, VBox info, boolean explicito, Button... acciones) {
        HBox h = Componentes.hbox(Tema.ESPACIO, caratula, info);
        if (explicito) h.getChildren().add(Componentes.etiquetaExplicito());
        h.getChildren().add(Componentes.espaciador());
        h.getChildren().addAll(acciones);
        h.getStyleClass().add("card");
        h.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    public void setOnEditarAudio(Consumer<Audio> c)       { this.onEditarAudio = c; }
    public void setOnEditarProducto(Consumer<Producto> c) { this.onEditarProducto = c; }
    public void setOnEliminar(Consumer<String> c)         { this.onEliminar = c; }

    public Button getNuevaCancion()  { return nuevaCancion; }
    public Button getNuevoPodcast()  { return nuevoPodcast; }
    public Button getNuevoProducto() { return nuevoProducto; }
    public Button getLogout()        { return logout; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
