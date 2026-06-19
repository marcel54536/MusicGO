package musicgo.app;

import musicgo.modelo.Catalogo;
import musicgo.persistencia.RepositorioDatos;
import musicgo.persistencia.ServicioPersistencia;
import musicgo.servicios.GestorAdministradores;
import musicgo.servicios.GestorAutenticacion;
import musicgo.servicios.GestorCatalogo;
import musicgo.servicios.GestorCompras;
import musicgo.servicios.GestorEstadisticas;
import musicgo.servicios.GestorPlaylists;
import musicgo.servicios.GestorReproduccion;
import musicgo.servicios.GestorUsuarios;
import musicgo.servicios.pago.ProcesadorPago;

/**
 * Raiz de composicion (composition root): crea y cablea una sola vez
 * todas las dependencias del sistema y carga los datos iniciales. Los
 * controladores reciben este contexto y piden los servicios que
 * necesitan (inyeccion de dependencias manual — DIP).
 *
 * @author Equipo MusicGO
 */
public class AppContext {

    private final ServicioPersistencia persistencia;
    private final GestorCatalogo gestorCatalogo;
    private final GestorUsuarios gestorUsuarios;
    private final GestorAdministradores gestorAdmins;
    private final GestorAutenticacion autenticacion;
    private final GestorEstadisticas estadisticas;
    private final GestorReproduccion reproduccion;
    private final GestorPlaylists playlists;
    private final GestorCompras compras;

    public AppContext(String rutaDatos) {
        this.persistencia = new ServicioPersistencia(new RepositorioDatos(rutaDatos));

        this.gestorCatalogo = new GestorCatalogo(persistencia);
        gestorCatalogo.cargar();
        Catalogo catalogo = gestorCatalogo.getCatalogo();

        this.gestorUsuarios = new GestorUsuarios(persistencia, catalogo);
        gestorUsuarios.cargar();

        this.gestorAdmins = new GestorAdministradores(persistencia);
        gestorAdmins.cargar();

        this.autenticacion = new GestorAutenticacion(gestorUsuarios, gestorAdmins);
        this.estadisticas = new GestorEstadisticas(persistencia);
        this.reproduccion = new GestorReproduccion(catalogo, estadisticas);
        this.playlists = new GestorPlaylists(catalogo, gestorUsuarios);
        this.compras = new GestorCompras(catalogo, new ProcesadorPago(), gestorUsuarios);
    }

    public GestorCatalogo catalogo()         { return gestorCatalogo; }
    public GestorUsuarios usuarios()         { return gestorUsuarios; }
    public GestorAutenticacion autenticacion() { return autenticacion; }
    public GestorEstadisticas estadisticas() { return estadisticas; }
    public GestorReproduccion reproduccion() { return reproduccion; }
    public GestorPlaylists playlists()       { return playlists; }
    public GestorCompras compras()           { return compras; }

    /** Cierra la persistencia esperando las escrituras pendientes. */
    public void cerrar() {
        persistencia.cerrar();
    }
}
