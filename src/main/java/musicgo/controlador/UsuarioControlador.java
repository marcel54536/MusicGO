package musicgo.controlador;

import javafx.application.Platform;
import musicgo.app.AppContext;
import musicgo.app.Router;
import musicgo.modelo.Usuario;
import musicgo.vista.usuario.AppUsuarioVista;

/**
 * Coordinador de la app del usuario final: arma el armazon, crea los
 * sub-controladores (catalogo, playlists, estadisticas, reproductor),
 * conecta la navegacion y se suscribe a los cambios del catalogo para
 * reflejar en vivo lo que modifica el administrador.
 *
 * @author Equipo MusicGO
 */
public class UsuarioControlador {

    private final AppContext ctx;
    private final Router router;
    private final Usuario usuario;

    private AppUsuarioVista shell;
    private CatalogoUsuarioControlador catalogoCtrl;
    private PlaylistControlador playlistCtrl;
    private ComprasControlador comprasCtrl;
    private DashboardControlador dashboardCtrl;
    private Runnable observadorCatalogo;

    public UsuarioControlador(AppContext ctx, Router router, Usuario usuario) {
        this.ctx = ctx;
        this.router = router;
        this.usuario = usuario;
    }

    public void iniciar() {
        shell = new AppUsuarioVista(usuario);
        ReproductorControlador reproductor = new ReproductorControlador(
                shell.getReproductor(), ctx, usuario);
        Runnable cabecera = () -> shell.actualizarSaldo(usuario.getSaldo());

        catalogoCtrl = new CatalogoUsuarioControlador(ctx, usuario, reproductor, cabecera);
        playlistCtrl = new PlaylistControlador(ctx, usuario, reproductor);
        comprasCtrl = new ComprasControlador(ctx, usuario);
        dashboardCtrl = new DashboardControlador(ctx, usuario, cabecera);

        shell.getNavCatalogo().setOnAction(e -> irCatalogo());
        shell.getNavPlaylists().setOnAction(e -> irPlaylists());
        shell.getNavCompras().setOnAction(e -> irCompras());
        shell.getNavEstadisticas().setOnAction(e -> irEstadisticas());
        shell.getControlParental().selectedProperty().addListener((o, a, activo) -> {
            usuario.setControlParental(activo);
            ctx.usuarios().persistir();
        });
        shell.getLogout().setOnAction(e -> salir());

        observadorCatalogo = () -> Platform.runLater(catalogoCtrl::refrescar);
        ctx.catalogo().agregarObservador(observadorCatalogo);

        irCatalogo();
        router.mostrar(shell.getRaiz());
    }

    private void irCatalogo() {
        catalogoCtrl.refrescar();
        shell.mostrarContenido(catalogoCtrl.getVista().getRaiz());
        shell.marcarNav(shell.getNavCatalogo());
    }

    private void irPlaylists() {
        playlistCtrl.refrescar();
        shell.mostrarContenido(playlistCtrl.getVista().getRaiz());
        shell.marcarNav(shell.getNavPlaylists());
    }

    private void irCompras() {
        comprasCtrl.refrescar();
        shell.mostrarContenido(comprasCtrl.getVista().getRaiz());
        shell.marcarNav(shell.getNavCompras());
    }

    private void irEstadisticas() {
        dashboardCtrl.refrescar();
        shell.mostrarContenido(dashboardCtrl.getVista().getRaiz());
        shell.marcarNav(shell.getNavEstadisticas());
    }

    private void salir() {
        ctx.catalogo().quitarObservador(observadorCatalogo);
        ctx.usuarios().persistir();
        new LoginControlador(ctx, router).iniciar();
    }
}
