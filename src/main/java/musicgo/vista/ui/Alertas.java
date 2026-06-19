package musicgo.vista.ui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Helper para mostrar dialogos JavaFX de forma uniforme. Los controladores
 * lo usan al capturar excepciones del modelo, cumpliendo el requisito de
 * desplegar alertas informativas sin colapsar el hilo de la aplicacion.
 *
 * @author Equipo MusicGO
 */
public final class Alertas {

    private Alertas() { }

    public static void error(String mensaje) {
        mostrar(Alert.AlertType.ERROR, "Algo salio mal", mensaje);
    }

    public static void info(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, "MusicGO", mensaje);
    }

    public static void exito(String mensaje) {
        mostrar(Alert.AlertType.INFORMATION, "Listo", mensaje);
    }

    /** Advertencia (p. ej. contenido bloqueado por control parental). */
    public static void advertencia(String mensaje) {
        mostrar(Alert.AlertType.WARNING, "Atencion", mensaje);
    }

    /** @return true si el usuario confirma la accion */
    public static boolean confirmar(String mensaje) {
        Alert a = nuevo(Alert.AlertType.CONFIRMATION, "Confirmar", mensaje);
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get() == ButtonType.OK;
    }

    private static void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        nuevo(tipo, titulo, mensaje).showAndWait();
    }

    private static Alert nuevo(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        return a;
    }
}
