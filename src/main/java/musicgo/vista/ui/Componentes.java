package musicgo.vista.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Fabrica de nodos JavaFX ya estilizados. Centraliza la creacion de
 * botones, campos, tarjetas y textos para que cada vista sea corta y
 * consistente (DRY) y para no repetir clases CSS por todos lados.
 *
 * @author Equipo MusicGO
 */
public final class Componentes {

    private Componentes() { }

    // ---------------- textos ----------------

    public static Label titulo(String t)      { return etiqueta(t, "titulo"); }
    public static Label subtitulo(String t)    { return etiqueta(t, "subtitulo"); }
    public static Label texto(String t)        { return etiqueta(t, "texto"); }
    public static Label textoSuave(String t)   { return etiqueta(t, "texto-suave"); }
    public static Label marca(String t)        { return etiqueta(t, "marca"); }

    private static Label etiqueta(String t, String clase) {
        Label l = new Label(t);
        l.getStyleClass().add(clase);
        l.setWrapText(true);
        return l;
    }

    // ---------------- botones ----------------

    public static Button botonPrimario(String t)   { return boton(t, "btn-primary"); }
    public static Button botonSecundario(String t)  { return boton(t, "btn-secondary"); }
    public static Button botonGhost(String t)       { return boton(t, "btn-ghost"); }

    private static Button boton(String t, String clase) {
        Button b = new Button(t);
        b.getStyleClass().add(clase);
        return b;
    }

    // ---------------- campos ----------------

    public static TextField campo(String prompt) {
        TextField c = new TextField();
        c.setPromptText(prompt);
        c.getStyleClass().add("campo");
        return c;
    }

    public static PasswordField clave(String prompt) {
        PasswordField c = new PasswordField();
        c.setPromptText(prompt);
        c.getStyleClass().add("campo");
        return c;
    }

    // ---------------- contenedores ----------------

    /** Tarjeta blanca con esquinas redondeadas y sombra suave. */
    public static VBox card(Node... hijos) {
        VBox v = new VBox(Tema.ESPACIO_S, hijos);
        v.getStyleClass().add("card");
        v.setPadding(new Insets(Tema.ESPACIO));
        return v;
    }

    public static VBox vbox(double sp, Node... hijos) {
        VBox v = new VBox(sp, hijos);
        v.setAlignment(Pos.TOP_LEFT);
        return v;
    }

    public static HBox hbox(double sp, Node... hijos) {
        HBox h = new HBox(sp, hijos);
        h.setAlignment(Pos.CENTER_LEFT);
        return h;
    }

    public static Label chip(String t) {
        Label l = new Label(t);
        l.getStyleClass().add("chip");
        return l;
    }

    /** Etiqueta pequena "EXPLICITO" para marcar contenido restringido. */
    public static Label etiquetaExplicito() {
        Label l = new Label("E");
        l.getStyleClass().add("chip-explicito");
        return l;
    }

    /** Region flexible que empuja el resto de nodos (separador elastico). */
    public static Region espaciador() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        VBox.setVgrow(r, Priority.ALWAYS);
        return r;
    }

    /**
     * "Caratula" circular de color con una inicial, como marcador de
     * portada cuando no hay imagen. Mantiene la estetica limpia.
     */
    public static Label caratula(String texto, double tam) {
        Label l = new Label(texto == null || texto.isBlank() ? "?" : texto.substring(0, 1).toUpperCase());
        l.setMinSize(tam, tam);
        l.setMaxSize(tam, tam);
        l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-background-color: linear-gradient(to bottom right, #fa243c, #ff6a3d);"
                + "-fx-text-fill: white; -fx-font-size: " + (tam / 2.4) + "px; -fx-font-weight: bold;"
                + "-fx-background-radius: " + (tam / 5) + ";");
        return l;
    }
}
