package musicgo.controlador;

import javafx.scene.control.ChoiceDialog;
import musicgo.app.AppContext;
import musicgo.app.Router;
import musicgo.modelo.Administrador;
import musicgo.modelo.ArteVisualAlbum;
import musicgo.modelo.Audio;
import musicgo.modelo.Cancion;
import musicgo.modelo.EpisodioPodcast;
import musicgo.modelo.PaqueteTopTen;
import musicgo.modelo.Producto;
import musicgo.servicios.GestorCatalogo;
import musicgo.vista.admin.AdminVista;
import musicgo.vista.admin.DialogosCatalogo;
import musicgo.vista.ui.Alertas;

/**
 * Controla el back-office: traduce los formularios de {@link AdminVista}
 * en operaciones del {@link GestorCatalogo}. Tras cada cambio el catalogo
 * se persiste y se notifica, reflejandose en la app de los usuarios.
 *
 * @author Equipo MusicGO
 */
public class AdminControlador {

    private final AppContext ctx;
    private final Router router;
    private final Administrador admin;
    private AdminVista vista;

    public AdminControlador(AppContext ctx, Router router, Administrador admin) {
        this.ctx = ctx;
        this.router = router;
        this.admin = admin;
    }

    public void iniciar() {
        vista = new AdminVista(admin.getNombre());
        vista.getLogout().setOnAction(e -> new LoginControlador(ctx, router).iniciar());
        vista.getNuevaCancion().setOnAction(e -> nuevaCancion());
        vista.getNuevoPodcast().setOnAction(e -> nuevoPodcast());
        vista.getNuevoProducto().setOnAction(e -> nuevoProducto());
        vista.setOnEditarAudio(this::editarAudio);
        vista.setOnEditarProducto(this::editarProducto);
        vista.setOnEliminar(this::eliminar);
        refrescar();
        router.mostrar(vista.getRaiz());
    }

    private GestorCatalogo cat() {
        return ctx.catalogo();
    }

    private void refrescar() {
        vista.render(cat().getCatalogo());
    }

    // ---------------- altas ----------------

    private void nuevaCancion() {
        DialogosCatalogo.cancion(null).ifPresent(d -> {
            cat().crearCancion(d.titulo(), d.duracion(), d.artista(), d.album(), d.genero(), d.clas());
            refrescar();
        });
    }

    private void nuevoPodcast() {
        DialogosCatalogo.episodio(null).ifPresent(d -> {
            cat().crearEpisodio(d.titulo(), d.duracion(), d.anfitrion(), d.podcast(),
                    d.descripcion(), d.numero(), d.clas());
            refrescar();
        });
    }

    private void nuevoProducto() {
        ChoiceDialog<String> elegir = new ChoiceDialog<>("Arte visual", "Arte visual", "Paquete Top Ten");
        elegir.setTitle("Nuevo producto");
        elegir.setHeaderText("Que tipo de producto quieres crear?");
        elegir.showAndWait().ifPresent(tipo -> {
            if (tipo.startsWith("Arte")) {
                DialogosCatalogo.arteVisual(null).ifPresent(d -> {
                    cat().crearArteVisual(d.nombre(), d.precio(), d.descripcion(),
                            d.album(), d.artista(), d.formato());
                    refrescar();
                });
            } else {
                DialogosCatalogo.paquete(null).ifPresent(d -> {
                    cat().crearPaquete(d.nombre(), d.precio(), d.descripcion(), d.tematica(), d.idsCanciones());
                    refrescar();
                });
            }
        });
    }

    // ---------------- ediciones ----------------

    private void editarAudio(Audio a) {
        if (a instanceof Cancion c) {
            DialogosCatalogo.cancion(c).ifPresent(d -> {
                c.setTitulo(d.titulo()); c.setDuracionSegundos(d.duracion());
                c.setArtista(d.artista()); c.setAlbum(d.album()); c.setGenero(d.genero());
                c.setClasificacion(d.clas());
                cat().persistirYNotificar(); refrescar();
            });
        } else if (a instanceof EpisodioPodcast e) {
            DialogosCatalogo.episodio(e).ifPresent(d -> {
                e.setTitulo(d.titulo()); e.setDuracionSegundos(d.duracion());
                e.setAnfitrion(d.anfitrion()); e.setNombrePodcast(d.podcast());
                e.setDescripcion(d.descripcion()); e.setNumeroEpisodio(d.numero());
                e.setClasificacion(d.clas());
                cat().persistirYNotificar(); refrescar();
            });
        }
    }

    private void editarProducto(Producto p) {
        if (p instanceof ArteVisualAlbum av) {
            DialogosCatalogo.arteVisual(av).ifPresent(d -> {
                av.setNombre(d.nombre()); av.setPrecio(d.precio()); av.setDescripcion(d.descripcion());
                av.setAlbumAsociado(d.album()); av.setArtista(d.artista()); av.setFormato(d.formato());
                cat().persistirYNotificar(); refrescar();
            });
        } else if (p instanceof PaqueteTopTen pt) {
            DialogosCatalogo.paquete(pt).ifPresent(d -> {
                pt.setNombre(d.nombre()); pt.setPrecio(d.precio()); pt.setDescripcion(d.descripcion());
                pt.setTematica(d.tematica());
                pt.getIdsCanciones().clear();
                d.idsCanciones().forEach(pt::agregarCancion);
                cat().persistirYNotificar(); refrescar();
            });
        }
    }

    private void eliminar(String id) {
        if (Alertas.confirmar("Eliminar el elemento " + id + " del catalogo?")) {
            cat().eliminar(id);
            refrescar();
        }
    }
}
