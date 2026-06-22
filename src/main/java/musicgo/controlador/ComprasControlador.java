package musicgo.controlador;

import musicgo.app.AppContext;
import musicgo.modelo.Usuario;
import musicgo.vista.usuario.ComprasVista;

/**
 * Controla el módulo "Mis compras": pide al modelo el historial del
 * usuario y el catálogo (para resolver los nombres de los productos) y
 * los pasa a la vista.
 *
 * @author Equipo MusicGO
 */
public class ComprasControlador {

    private final AppContext ctx;
    private final Usuario usuario;
    private final ComprasVista vista = new ComprasVista();

    public ComprasControlador(AppContext ctx, Usuario usuario) {
        this.ctx = ctx;
        this.usuario = usuario;
    }

    public ComprasVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.render(usuario, ctx.catalogo().getCatalogo());
    }
}
