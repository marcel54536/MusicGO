package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Audio;
import musicgo.modelo.Playlist;
import musicgo.modelo.Usuario;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Gestion de playlists: lista de listas del usuario, detalle de la
 * seleccionada (con quitar/compartir) y seccion de playlists que otros
 * usuarios compartieron. Delega todas las acciones en el controlador.
 *
 * @author Equipo MusicGO
 */
public class PlaylistsVista implements Vista {

    private final VBox raiz = new VBox(Tema.ESPACIO);
    private final ListView<Playlist> listaPlaylists = new ListView<>();
    private final VBox detalle = new VBox(Tema.ESPACIO_S);
    private final VBox compartidas = new VBox(Tema.ESPACIO_S);
    private final TextField nombreNueva = Componentes.campo("Nombre de la nueva playlist");
    private final Button crear = Componentes.botonPrimario("Crear");

    private Consumer<String> onCrear = n -> { };
    private Consumer<Playlist> onEliminar = p -> { };
    private Consumer<Playlist> onCompartir = p -> { };
    private BiConsumer<Playlist, String> onQuitar = (p, id) -> { };

    public PlaylistsVista() {
        raiz.setPadding(new Insets(Tema.ESPACIO_L));
        crear.setOnAction(e -> { onCrear.accept(nombreNueva.getText()); nombreNueva.clear(); });
        listaPlaylists.setPrefWidth(260);
        listaPlaylists.setCellFactory(lv -> celdaPlaylist());
        listaPlaylists.getSelectionModel().selectedItemProperty()
                .addListener((o, a, b) -> mostrarDetalle(b));

        HBox cuerpo = Componentes.hbox(Tema.ESPACIO, listaPlaylists, detalle);
        HBox.setHgrow(detalle, Priority.ALWAYS);
        VBox.setVgrow(cuerpo, Priority.ALWAYS);

        raiz.getChildren().addAll(
                Componentes.titulo("Mis playlists"),
                Componentes.hbox(Tema.ESPACIO_S, nombreNueva, crear),
                cuerpo,
                Componentes.subtitulo("Compartidas conmigo"),
                compartidas);
    }

    /** Reconstruye las listas a partir del usuario. */
    public void render(Usuario u) {
        listaPlaylists.getItems().setAll(u.getBiblioteca().getPlaylists());
        detalle.getChildren().setAll(Componentes.textoSuave("Selecciona una playlist para ver su contenido."));
        compartidas.getChildren().clear();
        if (u.getCompartidasConmigo().isEmpty()) {
            compartidas.getChildren().add(Componentes.textoSuave("Nadie te ha compartido playlists todavia."));
        }
        for (Playlist p : u.getCompartidasConmigo()) {
            compartidas.getChildren().add(filaCompartida(p));
        }
    }

    private void mostrarDetalle(Playlist p) {
        detalle.getChildren().clear();
        if (p == null) return;
        Button compartir = Componentes.botonSecundario("Compartir");
        Button eliminar = Componentes.botonGhost("Eliminar playlist");
        compartir.setOnAction(e -> onCompartir.accept(p));
        eliminar.setOnAction(e -> onEliminar.accept(p));
        detalle.getChildren().addAll(
                Componentes.subtitulo(p.getNombre()),
                Componentes.textoSuave(p.cantidadAudios() + " audios · " + p.getId()),
                Componentes.hbox(Tema.ESPACIO_S, compartir, eliminar));
        for (Audio a : p.getContenido()) {
            Button quitar = Componentes.botonGhost("Quitar");
            quitar.setOnAction(e -> onQuitar.accept(p, a.getId()));
            HBox fila = Componentes.hbox(Tema.ESPACIO,
                    Componentes.caratula(a.getTitulo(), 36),
                    Componentes.vbox(2, Componentes.texto(a.getTitulo()),
                            Componentes.textoSuave(a.creditos())),
                    Componentes.espaciador(), quitar);
            fila.getStyleClass().add("card");
            fila.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
            detalle.getChildren().add(fila);
        }
    }

    private HBox filaCompartida(Playlist p) {
        HBox fila = Componentes.hbox(Tema.ESPACIO,
                Componentes.caratula(p.getNombre(), 36),
                Componentes.vbox(2, Componentes.texto(p.getNombre()),
                        Componentes.textoSuave("de @" + p.getAliasPropietario() + " · " + p.cantidadAudios() + " audios")));
        fila.getStyleClass().add("card");
        fila.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        return fila;
    }

    private javafx.scene.control.ListCell<Playlist> celdaPlaylist() {
        return new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Playlist p, boolean vacio) {
                super.updateItem(p, vacio);
                setText(vacio || p == null ? null : p.getNombre() + "  (" + p.cantidadAudios() + ")");
            }
        };
    }

    public void setOnCrear(Consumer<String> c)              { this.onCrear = c; }
    public void setOnEliminar(Consumer<Playlist> c)         { this.onEliminar = c; }
    public void setOnCompartir(Consumer<Playlist> c)        { this.onCompartir = c; }
    public void setOnQuitar(BiConsumer<Playlist, String> c) { this.onQuitar = c; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
