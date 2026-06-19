package musicgo.controlador;

import javafx.scene.control.ChoiceDialog;
import musicgo.app.AppContext;
import musicgo.excepciones.ContenidoNoEncontradoException;
import musicgo.excepciones.PagoRechazadoException;
import musicgo.modelo.Audio;
import musicgo.modelo.Playlist;
import musicgo.modelo.Producto;
import musicgo.modelo.Usuario;
import musicgo.modelo.pago.Recibo;
import musicgo.vista.usuario.CatalogoVista;
import musicgo.vista.usuario.DialogoPago;
import musicgo.vista.ui.Alertas;

/**
 * Controla la exploracion del catalogo: reproducir, agregar a una playlist
 * y comprar productos a traves de la pasarela de pagos. Captura las
 * excepciones del modelo y las muestra como alertas.
 *
 * @author Equipo MusicGO
 */
public class CatalogoUsuarioControlador {

    private final AppContext ctx;
    private final Usuario usuario;
    private final ReproductorControlador reproductor;
    private final Runnable refrescarCabecera;
    private final CatalogoVista vista = new CatalogoVista();

    public CatalogoUsuarioControlador(AppContext ctx, Usuario usuario,
                                      ReproductorControlador reproductor, Runnable refrescarCabecera) {
        this.ctx = ctx;
        this.usuario = usuario;
        this.reproductor = reproductor;
        this.refrescarCabecera = refrescarCabecera;
        vista.setOnReproducir(reproductor::reproducir);
        vista.setOnAgregarPlaylist(this::agregarAPlaylist);
        vista.setOnComprar(this::comprar);
    }

    public CatalogoVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.render(ctx.catalogo().getCatalogo());
    }

    private void agregarAPlaylist(Audio audio) {
        var playlists = usuario.getBiblioteca().getPlaylists();
        if (playlists.isEmpty()) {
            Alertas.info("Primero crea una playlist en la seccion 'Mis playlists'.");
            return;
        }
        ChoiceDialog<Playlist> elegir = new ChoiceDialog<>(playlists.get(0), playlists);
        elegir.setTitle("Agregar a playlist");
        elegir.setHeaderText("A que playlist quieres agregar '" + audio.getTitulo() + "'?");
        elegir.showAndWait().ifPresent(pl -> {
            try {
                boolean ok = ctx.playlists().agregarAudio(usuario, pl.getId(), audio.getId());
                Alertas.info(ok ? "Agregado a '" + pl.getNombre() + "'."
                        : "Ese audio ya estaba en la playlist.");
            } catch (ContenidoNoEncontradoException ex) {
                Alertas.error(ex.getMessage());
            }
        });
    }

    private void comprar(Producto producto) {
        DialogoPago.pedir(producto, usuario).ifPresent(medio -> {
            try {
                Recibo recibo = ctx.compras().comprar(usuario, producto.getId(), medio);
                refrescarCabecera.run();
                Alertas.exito(recibo.resumen());
            } catch (PagoRechazadoException | ContenidoNoEncontradoException ex) {
                Alertas.error(ex.getMessage());
            }
        });
    }
}
