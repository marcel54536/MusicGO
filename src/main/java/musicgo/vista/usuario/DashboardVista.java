package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Estadisticas;
import musicgo.modelo.Usuario;
import musicgo.servicios.GestorEstadisticas;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

import java.util.List;
import java.util.function.Consumer;

/**
 * Panel dinamico de estadisticas: tarjetas con metricas del usuario,
 * recarga de saldo y el ranking de contenidos mas reproducidos de la
 * plataforma. Se redibuja cada vez que se entra para reflejar los datos
 * al instante.
 *
 * @author Equipo MusicGO
 */
public class DashboardVista implements Vista {

    private final VBox raiz = new VBox(Tema.ESPACIO);
    private final FlowPane metricas = new FlowPane(Tema.ESPACIO, Tema.ESPACIO);
    private final VBox top = new VBox(Tema.ESPACIO_S);
    private final TextField montoRecarga = Componentes.campo("Monto a recargar");
    private final Button recargar = Componentes.botonPrimario("Recargar saldo");

    private Consumer<Double> onRecargar = m -> { };

    public DashboardVista() {
        raiz.setPadding(new Insets(Tema.ESPACIO_L));
        VBox cardRecarga = Componentes.card(
                Componentes.subtitulo("Saldo virtual MusicGo"),
                Componentes.hbox(Tema.ESPACIO_S, montoRecarga, recargar));
        recargar.setOnAction(e -> dispararRecarga());
        raiz.getChildren().addAll(
                Componentes.titulo("Tu actividad"),
                metricas, cardRecarga,
                Componentes.subtitulo("Top de la plataforma"),
                top);
    }

    /** Redibuja metricas y ranking. */
    public void render(Usuario u, List<GestorEstadisticas.Top> tops) {
        Estadisticas e = u.getEstadisticas();
        metricas.getChildren().setAll(
                metrica(e.tiempoFormateado(), "Tiempo de escucha"),
                metrica(String.valueOf(e.getReproduccionesTotales()), "Reproducciones"),
                metrica(String.valueOf(e.getCancionesEnBiblioteca()), "Canciones en biblioteca"),
                metrica(String.valueOf(e.getComprasRealizadas()), "Compras"));

        top.getChildren().clear();
        if (tops.isEmpty()) {
            top.getChildren().add(Componentes.textoSuave("Aun no hay reproducciones registradas."));
        }
        int pos = 1;
        for (GestorEstadisticas.Top t : tops) {
            top.getChildren().add(filaTop(pos++, t));
        }
    }

    private VBox metrica(String valor, String etiqueta) {
        VBox card = Componentes.card(Componentes.titulo(valor), Componentes.textoSuave(etiqueta));
        card.setPrefWidth(220);
        return card;
    }

    private javafx.scene.layout.HBox filaTop(int pos, GestorEstadisticas.Top t) {
        var fila = Componentes.hbox(Tema.ESPACIO,
                Componentes.caratula(String.valueOf(pos), 36),
                Componentes.vbox(2, Componentes.texto(t.audio().getTitulo()),
                        Componentes.textoSuave(t.audio().creditos())),
                Componentes.espaciador(),
                Componentes.chip(t.reproducciones() + " plays"));
        fila.getStyleClass().add("card");
        fila.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        return fila;
    }

    private void dispararRecarga() {
        try {
            double m = Double.parseDouble(montoRecarga.getText().trim());
            onRecargar.accept(m);
            montoRecarga.clear();
        } catch (NumberFormatException ex) {
            musicgo.vista.ui.Alertas.error("Ingresa un monto valido para recargar.");
        }
    }

    public void setOnRecargar(Consumer<Double> c) { this.onRecargar = c; }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
