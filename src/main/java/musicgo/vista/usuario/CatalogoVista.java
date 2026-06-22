package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Audio;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Producto;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

import java.util.function.Consumer;

/**
 * Exploracion visual del catalogo en cuadricula. Diferencia audios de
 * productos y permite filtrar por tipo. Cada tarjeta delega sus acciones
 * (reproducir, agregar a playlist, comprar) en callbacks del controlador.
 *
 * @author Equipo MusicGO
 */
public class CatalogoVista implements Vista {

    private final VBox raiz = new VBox(Tema.ESPACIO);
    private final FlowPane grid = new FlowPane(Tema.ESPACIO, Tema.ESPACIO);
    private final HBoxFiltros filtros = new HBoxFiltros();
    private final TextField busqueda = Componentes.campo("Buscar por título o artista...");

    private Catalogo catalogo;
    private String filtro = "todo";
    private String texto = "";

    private Consumer<Audio> onReproducir = a -> { };
    private Consumer<Audio> onAgregarPlaylist = a -> { };
    private Consumer<Producto> onComprar = p -> { };

    public CatalogoVista() {
        raiz.setPadding(new Insets(Tema.ESPACIO_L));
        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scroll, javafx.scene.layout.Priority.ALWAYS);
        filtros.alFiltrar(f -> { filtro = f; pintar(); });
        busqueda.textProperty().addListener((o, a, b) -> { texto = b.trim().toLowerCase(); pintar(); });
        busqueda.setPrefWidth(280);
        var cabecera = Componentes.hbox(Tema.ESPACIO,
                Componentes.titulo("Explorar"), Componentes.espaciador(), busqueda);
        raiz.getChildren().addAll(cabecera, filtros, scroll);
    }

    public void render(Catalogo catalogo) {
        this.catalogo = catalogo;
        pintar();
    }

    private void pintar() {
        grid.getChildren().clear();
        if (catalogo == null) return;
        boolean audios = filtro.equals("todo") || filtro.equals("canciones") || filtro.equals("podcasts");
        if (audios) {
            for (Audio a : catalogo.getAudios()) {
                if (filtro.equals("canciones") && !a.getTipo().equals("cancion")) continue;
                if (filtro.equals("podcasts") && !a.getTipo().equals("episodio")) continue;
                if (!coincide(a.getTitulo(), a.creditos())) continue;
                grid.getChildren().add(tarjetaAudio(a));
            }
        }
        if (filtro.equals("todo") || filtro.equals("productos")) {
            for (Producto p : catalogo.getProductos()) {
                if (!coincide(p.getNombre(), p.getDescripcion())) continue;
                grid.getChildren().add(tarjetaProducto(p));
            }
        }
        if (grid.getChildren().isEmpty()) {
            grid.getChildren().add(Componentes.textoSuave("Sin resultados para tu búsqueda."));
        }
    }

    /** True si algún campo contiene el texto buscado (o si no hay búsqueda). */
    private boolean coincide(String... campos) {
        if (texto.isEmpty()) return true;
        for (String c : campos) {
            if (c != null && c.toLowerCase().contains(texto)) return true;
        }
        return false;
    }

    private VBox tarjetaAudio(Audio a) {
        Button play = Componentes.botonPrimario("▶ Reproducir");
        Button add = Componentes.botonSecundario("+ Playlist");
        play.setOnAction(e -> onReproducir.accept(a));
        add.setOnAction(e -> onAgregarPlaylist.accept(a));
        VBox cabecera = Componentes.vbox(Tema.ESPACIO_S, Componentes.caratula(a.getTitulo(), 120));
        Label titulo = Componentes.texto(a.getTitulo());
        Label cred = Componentes.textoSuave(a.creditos());
        VBox card = Componentes.card(cabecera, titulo, cred, Componentes.hbox(Tema.ESPACIO_S, play, add));
        if (a.getClasificacion().esRestringido()) card.getChildren().add(Componentes.etiquetaExplicito());
        card.setPrefWidth(210);
        card.getStyleClass().add("card-hover");
        return card;
    }

    private VBox tarjetaProducto(Producto p) {
        Button comprar = Componentes.botonPrimario("Comprar $" + String.format("%.2f", p.getPrecio()));
        comprar.setOnAction(e -> onComprar.accept(p));
        VBox card = Componentes.card(
                Componentes.caratula(p.getNombre(), 120),
                Componentes.texto(p.getNombre()),
                Componentes.textoSuave(p.getDescripcion()),
                comprar);
        card.setPrefWidth(210);
        card.getStyleClass().add("card-hover");
        return card;
    }

    public void setOnReproducir(Consumer<Audio> c)      { this.onReproducir = c; }
    public void setOnAgregarPlaylist(Consumer<Audio> c) { this.onAgregarPlaylist = c; }
    public void setOnComprar(Consumer<Producto> c)      { this.onComprar = c; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }

    /** Fila de chips de filtro reutilizable, separada para no inflar la vista. */
    private static final class HBoxFiltros extends javafx.scene.layout.HBox {
        private Consumer<String> alFiltrar = f -> { };
        HBoxFiltros() {
            super(Tema.ESPACIO_S);
            agregar("Todo", "todo");
            agregar("Canciones", "canciones");
            agregar("Podcasts", "podcasts");
            agregar("Productos", "productos");
        }
        void alFiltrar(Consumer<String> c) { this.alFiltrar = c; }
        private void agregar(String texto, String clave) {
            Label chip = Componentes.chip(texto);
            if (clave.equals("todo")) chip.getStyleClass().add("chip-activo");
            chip.setOnMouseClicked(e -> {
                getChildren().forEach(n -> n.getStyleClass().remove("chip-activo"));
                chip.getStyleClass().add("chip-activo");
                alFiltrar.accept(clave);
            });
            getChildren().add(chip);
        }
    }
}
