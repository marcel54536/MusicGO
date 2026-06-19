package musicgo;

import javafx.application.Application;
import javafx.stage.Stage;
import musicgo.app.AppContext;
import musicgo.app.Router;
import musicgo.controlador.LoginControlador;

/**
 * Punto de arranque de MusicGO (Fase 2, JavaFX).
 *
 * <p>Crea el contexto de la aplicacion (datos + servicios), arma el
 * navegador y muestra la pantalla de autenticacion. Acepta opcionalmente
 * la ruta de la carpeta de datos como primer argumento.</p>
 *
 * <p>Ejecucion: {@code ./mvnw javafx:run}</p>
 *
 * @author Equipo MusicGO
 */
public class MusicGoApp extends Application {

    private AppContext contexto;

    @Override
    public void start(Stage stage) {
        String ruta = getParameters().getRaw().isEmpty()
                ? "data" : getParameters().getRaw().get(0);
        contexto = new AppContext(ruta);

        Router router = new Router(stage);
        stage.setTitle("MusicGO");
        new LoginControlador(contexto, router).iniciar();
        stage.show();
    }

    /** Cierre limpio: persiste lo pendiente y apaga el hilo de guardado. */
    @Override
    public void stop() {
        if (contexto != null) contexto.cerrar();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
