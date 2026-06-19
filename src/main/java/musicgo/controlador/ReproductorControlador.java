package musicgo.controlador;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;
import musicgo.app.AppContext;
import musicgo.excepciones.ContenidoNoEncontradoException;
import musicgo.excepciones.ContenidoRestringidoException;
import musicgo.modelo.Audio;
import musicgo.modelo.Usuario;
import musicgo.vista.usuario.ReproductorBarra;
import musicgo.vista.ui.Alertas;

import java.util.List;

/**
 * Controla el mini reproductor: valida la reproduccion (incluido el
 * control parental), anima la barra de progreso y gestiona la cola
 * (anterior / siguiente). La "duracion" se simula en pocos segundos para
 * que la demo sea agil.
 *
 * @author Equipo MusicGO
 */
public class ReproductorControlador {

    private static final double SEGUNDOS_SIMULADOS = 12;

    private final ReproductorBarra barra;
    private final AppContext ctx;
    private final Usuario usuario;
    private Runnable alReproducir = () -> { };

    private List<Audio> cola = List.of();
    private int indice = 0;
    private Timeline linea;
    private Audio actual;

    public ReproductorControlador(ReproductorBarra barra, AppContext ctx, Usuario usuario) {
        this.barra = barra;
        this.ctx = ctx;
        this.usuario = usuario;
        barra.getPlayPausa().setOnAction(e -> alternar());
        barra.getSiguiente().setOnAction(e -> siguiente());
        barra.getAnterior().setOnAction(e -> anterior());
    }

    /** Callback que se dispara tras una reproduccion (para refrescar stats). */
    public void setAlReproducir(Runnable r) {
        this.alReproducir = r;
    }

    /** Reproduce un unico audio. */
    public void reproducir(Audio a) {
        cola = List.of(a);
        indice = 0;
        intentar(a);
    }

    private void intentar(Audio a) {
        try {
            ctx.reproduccion().reproducir(usuario, a.getId());
        } catch (ContenidoRestringidoException ex) {
            Alertas.advertencia(ex.getMessage());
            return;
        } catch (ContenidoNoEncontradoException ex) {
            Alertas.error(ex.getMessage());
            return;
        }
        actual = a;
        barra.cargar(a);
        animar(a);
        alReproducir.run();
    }

    private void animar(Audio a) {
        if (linea != null) linea.stop();
        barra.getBarra().setProgress(0);
        linea = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(barra.getBarra().progressProperty(), 0)),
                new KeyFrame(Duration.seconds(SEGUNDOS_SIMULADOS),
                        new KeyValue(barra.getBarra().progressProperty(), 1)));
        barra.getBarra().progressProperty().addListener((o, v, p) ->
                barra.getTiempo().setText(tiempo(p.doubleValue(), a.getDuracionSegundos())));
        linea.setOnFinished(e -> siguiente());
        linea.play();
        barra.marcarReproduciendo(true);
    }

    private void alternar() {
        if (linea == null) return;
        if (linea.getStatus() == javafx.animation.Animation.Status.RUNNING) {
            linea.pause();
            barra.marcarReproduciendo(false);
        } else {
            linea.play();
            barra.marcarReproduciendo(true);
        }
    }

    private void siguiente() {
        if (indice < cola.size() - 1) {
            intentar(cola.get(++indice));
        } else if (linea != null) {
            linea.stop();
            barra.marcarReproduciendo(false);
        }
    }

    private void anterior() {
        if (indice > 0) intentar(cola.get(--indice));
    }

    private String tiempo(double progreso, int duracion) {
        int transcurrido = (int) Math.round(progreso * duracion);
        return String.format("%d:%02d / %d:%02d",
                transcurrido / 60, transcurrido % 60, duracion / 60, duracion % 60);
    }
}
