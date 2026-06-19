package musicgo.vista.ui;

import javafx.scene.Scene;

/**
 * Punto unico del tema visual: aplica la hoja de estilos y expone unas
 * pocas constantes de espaciado. Los colores viven en el CSS (un solo
 * lugar para cambiar la identidad visual).
 *
 * @author Equipo MusicGO
 */
public final class Tema {

    /** Ruta de la hoja de estilos dentro de los recursos del proyecto. */
    public static final String CSS = "/musicgo/vista/ui/estilos.css";

    public static final double ESPACIO = 16;
    public static final double ESPACIO_S = 8;
    public static final double ESPACIO_L = 24;

    private Tema() { }

    /** Adjunta la hoja de estilos a una escena. */
    public static void aplicar(Scene scene) {
        var url = Tema.class.getResource(CSS);
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
    }
}
