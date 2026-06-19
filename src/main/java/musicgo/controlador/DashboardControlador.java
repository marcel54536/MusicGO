package musicgo.controlador;

import musicgo.app.AppContext;
import musicgo.modelo.Usuario;
import musicgo.vista.usuario.DashboardVista;
import musicgo.vista.ui.Alertas;

/**
 * Controla el panel de estadisticas: recarga de saldo virtual y refresco
 * del ranking de contenidos mas reproducidos de la plataforma.
 *
 * @author Equipo MusicGO
 */
public class DashboardControlador {

    private static final int TOP = 5;

    private final AppContext ctx;
    private final Usuario usuario;
    private final Runnable refrescarCabecera;
    private final DashboardVista vista = new DashboardVista();

    public DashboardControlador(AppContext ctx, Usuario usuario, Runnable refrescarCabecera) {
        this.ctx = ctx;
        this.usuario = usuario;
        this.refrescarCabecera = refrescarCabecera;
        vista.setOnRecargar(this::recargar);
    }

    public DashboardVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.render(usuario, ctx.estadisticas().topReproducidos(ctx.catalogo().getCatalogo(), TOP));
    }

    private void recargar(double monto) {
        if (monto <= 0) {
            Alertas.error("El monto debe ser mayor que cero.");
            return;
        }
        usuario.recargarSaldo(monto);
        ctx.usuarios().persistir();
        refrescarCabecera.run();
        refrescar();
        Alertas.exito(String.format("Se recargaron $%.2f a tu saldo.", monto));
    }
}
