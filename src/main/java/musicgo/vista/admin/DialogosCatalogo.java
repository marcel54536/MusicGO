package musicgo.vista.admin;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import musicgo.modelo.ArteVisualAlbum;
import musicgo.modelo.Cancion;
import musicgo.modelo.Clasificacion;
import musicgo.modelo.EpisodioPodcast;
import musicgo.modelo.PaqueteTopTen;
import musicgo.vista.ui.Tema;

import java.util.List;
import java.util.Optional;

/**
 * Construye los formularios (Dialog) de alta/edicion del catalogo y
 * devuelve los datos capturados como records. No toca los servicios: el
 * controlador traduce el record a la operacion correspondiente (MVC).
 *
 * @author Equipo MusicGO
 */
public final class DialogosCatalogo {

    public record DatosCancion(String titulo, int duracion, String artista,
                               String album, String genero, Clasificacion clas) { }
    public record DatosEpisodio(String titulo, int duracion, String anfitrion,
                                String podcast, String descripcion, int numero, Clasificacion clas) { }
    public record DatosArte(String nombre, double precio, String descripcion,
                            String album, String artista, String formato) { }
    public record DatosPaquete(String nombre, double precio, String descripcion,
                               String tematica, List<String> idsCanciones) { }

    private DialogosCatalogo() { }

    public static Optional<DatosCancion> cancion(Cancion x) {
        TextField t = campo(x == null ? "" : x.getTitulo());
        TextField d = campo(x == null ? "" : String.valueOf(x.getDuracionSegundos()));
        TextField ar = campo(x == null ? "" : x.getArtista());
        TextField al = campo(x == null ? "" : x.getAlbum());
        TextField g = campo(x == null ? "" : x.getGenero());
        ComboBox<Clasificacion> c = combo(x);
        Dialog<ButtonType> dlg = base(x == null ? "Nueva cancion" : "Editar cancion",
                grid("Titulo", t, "Duracion (seg)", d, "Artista", ar, "Album", al, "Genero", g, "Clasificacion", c));
        return resultado(dlg, () -> new DatosCancion(t.getText(), entero(d), ar.getText(),
                al.getText(), g.getText(), c.getValue()));
    }

    public static Optional<DatosEpisodio> episodio(EpisodioPodcast x) {
        TextField t = campo(x == null ? "" : x.getTitulo());
        TextField d = campo(x == null ? "" : String.valueOf(x.getDuracionSegundos()));
        TextField an = campo(x == null ? "" : x.getAnfitrion());
        TextField po = campo(x == null ? "" : x.getNombrePodcast());
        TextField de = campo(x == null ? "" : x.getDescripcion());
        TextField nu = campo(x == null ? "" : String.valueOf(x.getNumeroEpisodio()));
        ComboBox<Clasificacion> c = combo(x);
        Dialog<ButtonType> dlg = base(x == null ? "Nuevo podcast" : "Editar podcast",
                grid("Titulo", t, "Duracion (seg)", d, "Anfitrion", an, "Podcast", po,
                        "Descripcion", de, "N. episodio", nu, "Clasificacion", c));
        return resultado(dlg, () -> new DatosEpisodio(t.getText(), entero(d), an.getText(),
                po.getText(), de.getText(), entero(nu), c.getValue()));
    }

    public static Optional<DatosArte> arteVisual(ArteVisualAlbum x) {
        TextField n = campo(x == null ? "" : x.getNombre());
        TextField p = campo(x == null ? "" : String.valueOf(x.getPrecio()));
        TextField de = campo(x == null ? "" : x.getDescripcion());
        TextField al = campo(x == null ? "" : x.getAlbumAsociado());
        TextField ar = campo(x == null ? "" : x.getArtista());
        TextField fo = campo(x == null ? "" : x.getFormato());
        Dialog<ButtonType> dlg = base(x == null ? "Nuevo arte visual" : "Editar arte visual",
                grid("Nombre", n, "Precio", p, "Descripcion", de, "Album", al, "Artista", ar, "Formato", fo));
        return resultado(dlg, () -> new DatosArte(n.getText(), decimal(p), de.getText(),
                al.getText(), ar.getText(), fo.getText()));
    }

    public static Optional<DatosPaquete> paquete(PaqueteTopTen x) {
        TextField n = campo(x == null ? "" : x.getNombre());
        TextField p = campo(x == null ? "" : String.valueOf(x.getPrecio()));
        TextField de = campo(x == null ? "" : x.getDescripcion());
        TextField te = campo(x == null ? "" : x.getTematica());
        TextField ids = campo(x == null ? "" : String.join(",", x.getIdsCanciones()));
        Dialog<ButtonType> dlg = base(x == null ? "Nuevo paquete Top Ten" : "Editar paquete",
                grid("Nombre", n, "Precio", p, "Descripcion", de, "Tematica", te, "IDs canciones (coma)", ids));
        return resultado(dlg, () -> new DatosPaquete(n.getText(), decimal(p), de.getText(),
                te.getText(), List.of(ids.getText().split("\\s*,\\s*"))));
    }

    // ---------------- helpers ----------------

    private static TextField campo(String v) {
        TextField t = new TextField(v);
        t.getStyleClass().add("campo");
        return t;
    }

    private static ComboBox<Clasificacion> combo(musicgo.modelo.Audio x) {
        ComboBox<Clasificacion> c = new ComboBox<>();
        c.getItems().setAll(Clasificacion.values());
        c.setValue(x == null ? Clasificacion.APTO_TODO_PUBLICO : x.getClasificacion());
        return c;
    }

    private static GridPane grid(Object... pares) {
        GridPane g = new GridPane();
        g.setHgap(Tema.ESPACIO);
        g.setVgap(Tema.ESPACIO_S);
        for (int i = 0; i < pares.length; i += 2) {
            g.addRow(i / 2, new javafx.scene.control.Label((String) pares[i]), (javafx.scene.Node) pares[i + 1]);
        }
        return g;
    }

    private static Dialog<ButtonType> base(String titulo, GridPane contenido) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(titulo);
        d.setHeaderText(titulo);
        d.getDialogPane().setContent(contenido);
        d.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        return d;
    }

    private static <T> Optional<T> resultado(Dialog<ButtonType> dlg, java.util.function.Supplier<T> map) {
        return dlg.showAndWait().filter(b -> b == ButtonType.OK).map(b -> map.get());
    }

    private static int entero(TextField t) {
        try { return Math.max(1, Integer.parseInt(t.getText().trim())); }
        catch (NumberFormatException e) { return 1; }
    }

    private static double decimal(TextField t) {
        try { return Math.max(0, Double.parseDouble(t.getText().trim())); }
        catch (NumberFormatException e) { return 0; }
    }
}
