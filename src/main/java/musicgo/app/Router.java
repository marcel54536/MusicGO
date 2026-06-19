package musicgo.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import musicgo.vista.ui.Tema;

/**
 * Navegador de la aplicacion: mantiene una unica {@link Scene} y le va
 * cambiando la raiz segun la pantalla activa (login, back-office, app de
 * usuario). Centraliza la transicion entre vistas para no repetirla.
 *
 * @author Equipo MusicGO
 */
public class Router {

    private final Stage stage;
    private Scene scene;

    public Router(Stage stage) {
        this.stage = stage;
    }

    /**
     * Coloca una vista en pantalla. La primera vez crea la escena y le
     * aplica el tema; despues solo intercambia la raiz.
     *
     * @param raiz nodo raiz de la vista a mostrar
     */
    public void mostrar(Parent raiz) {
        if (scene == null) {
            scene = new Scene(raiz, 1120, 720);
            Tema.aplicar(scene);
            stage.setScene(scene);
            stage.setMinWidth(960);
            stage.setMinHeight(640);
        } else {
            scene.setRoot(raiz);
        }
    }

    public Stage getStage() {
        return stage;
    }
}
