package musicgo.controlador;

import javafx.scene.control.TextInputDialog;
import musicgo.app.AppContext;
import musicgo.excepciones.UsuarioNoEncontradoException;
import musicgo.modelo.Playlist;
import musicgo.modelo.Usuario;
import musicgo.vista.usuario.PlaylistsVista;
import musicgo.vista.ui.Alertas;

/**
 * Controla la gestion de playlists del usuario: crear, eliminar, quitar
 * audios y la funcion "Compartir Playlist" (busca al destinatario por
 * alias y le entrega una copia).
 *
 * @author Equipo MusicGO
 */
public class PlaylistControlador {

    private final AppContext ctx;
    private final Usuario usuario;
    private final PlaylistsVista vista = new PlaylistsVista();

    public PlaylistControlador(AppContext ctx, Usuario usuario) {
        this.ctx = ctx;
        this.usuario = usuario;
        vista.setOnCrear(this::crear);
        vista.setOnEliminar(this::eliminar);
        vista.setOnCompartir(this::compartir);
        vista.setOnQuitar(this::quitar);
    }

    public PlaylistsVista getVista() {
        return vista;
    }

    public void refrescar() {
        vista.render(usuario);
    }

    private void crear(String nombre) {
        try {
            ctx.playlists().crearPlaylist(usuario, nombre);
            refrescar();
        } catch (IllegalArgumentException ex) {
            Alertas.error(ex.getMessage());
        }
    }

    private void eliminar(Playlist p) {
        if (Alertas.confirmar("Eliminar la playlist '" + p.getNombre() + "'?")) {
            ctx.playlists().eliminarPlaylist(usuario, p.getId());
            refrescar();
        }
    }

    private void quitar(Playlist p, String idAudio) {
        ctx.playlists().removerAudio(usuario, p.getId(), idAudio);
        refrescar();
    }

    private void compartir(Playlist p) {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Compartir playlist");
        dlg.setHeaderText("Compartir '" + p.getNombre() + "'");
        dlg.setContentText("Alias del usuario destino:");
        dlg.showAndWait().ifPresent(alias -> {
            try {
                ctx.playlists().compartir(usuario, alias, p.getId());
                Alertas.exito("Playlist compartida con @" + alias + ".");
            } catch (UsuarioNoEncontradoException | IllegalArgumentException ex) {
                Alertas.error(ex.getMessage());
            }
        });
    }
}
