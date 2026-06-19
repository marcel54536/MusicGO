package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import musicgo.modelo.Audio;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

/**
 * Mini reproductor fijo en la parte inferior: caratula, metadatos,
 * controles (anterior / play-pausa / siguiente) y barra de progreso.
 * Solo presenta; la animacion y la cola las maneja su controlador.
 *
 * @author Equipo MusicGO
 */
public class ReproductorBarra {

    private final HBox raiz;
    private final StackPane caratula = new StackPane();
    private final Label titulo = Componentes.texto("Nada en reproduccion");
    private final Label creditos = Componentes.textoSuave("Elige algo del catalogo");
    private final Label tiempo = Componentes.textoSuave("0:00 / 0:00");
    private final ProgressBar barra = new ProgressBar(0);

    private final Button anterior = Componentes.botonGhost("⏮");
    private final Button playPausa = Componentes.botonPrimario("▶");
    private final Button siguiente = Componentes.botonGhost("⏭");

    public ReproductorBarra() {
        caratula.getChildren().add(Componentes.caratula("?", 48));
        VBox meta = Componentes.vbox(2, titulo, creditos);
        meta.setMinWidth(180);
        barra.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(barra, javafx.scene.layout.Priority.ALWAYS);
        HBox controles = Componentes.hbox(Tema.ESPACIO_S, anterior, playPausa, siguiente);
        controles.setAlignment(Pos.CENTER);
        HBox progreso = Componentes.hbox(Tema.ESPACIO_S, barra, tiempo);

        raiz = Componentes.hbox(Tema.ESPACIO, caratula, meta, controles, progreso);
        raiz.getStyleClass().add("miniplayer");
        raiz.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        HBox.setHgrow(progreso, javafx.scene.layout.Priority.ALWAYS);
    }

    /** Carga los metadatos de un audio en el reproductor. */
    public void cargar(Audio a) {
        titulo.setText(a.getTitulo());
        creditos.setText(a.creditos());
        caratula.getChildren().setAll(Componentes.caratula(a.getTitulo(), 48));
        barra.setProgress(0);
        tiempo.setText("0:00 / " + a.duracionFormateada());
    }

    public void marcarReproduciendo(boolean reproduciendo) {
        playPausa.setText(reproduciendo ? "⏸" : "▶");
    }

    public HBox getRaiz()        { return raiz; }
    public ProgressBar getBarra() { return barra; }
    public Label getTiempo()     { return tiempo; }
    public Button getAnterior()  { return anterior; }
    public Button getPlayPausa() { return playPausa; }
    public Button getSiguiente() { return siguiente; }
}
