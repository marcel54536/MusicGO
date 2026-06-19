package musicgo.servicios;

import musicgo.excepciones.UsuarioNoEncontradoException;
import musicgo.excepciones.UsuarioYaExisteException;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Usuario;
import musicgo.persistencia.MapeadorUsuarios;
import musicgo.persistencia.ServicioPersistencia;

import java.util.List;

/**
 * CRUD de usuarios y persistencia hacia/desde {@code usuarios.json}.
 * Mantiene la lista en memoria y la sincroniza en caliente tras cada
 * cambio (alta, edicion, recarga de saldo, compra...).
 *
 * @author Equipo MusicGO
 */
public class GestorUsuarios {

    private static final String ARCHIVO = "usuarios.json";

    private final ServicioPersistencia persistencia;
    private final Catalogo catalogo;
    private List<Usuario> usuarios = new java.util.ArrayList<>();

    public GestorUsuarios(ServicioPersistencia persistencia, Catalogo catalogo) {
        this.persistencia = persistencia;
        this.catalogo = catalogo;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /** Carga los usuarios desde el JSON. @return cantidad leida */
    public int cargar() {
        usuarios = MapeadorUsuarios.leer(persistencia.getRepositorio().leer(ARCHIVO), catalogo);
        return usuarios.size();
    }

    /** Sincroniza la lista actual a disco en segundo plano (hot persistence). */
    public void persistir() {
        persistencia.guardarAsync(ARCHIVO, MapeadorUsuarios.aLista(usuarios));
    }

    // ---------------- operaciones de negocio ----------------

    /**
     * Registra un usuario nuevo validando unicidad de alias.
     * @throws UsuarioYaExisteException si el alias ya existe
     */
    public Usuario registrar(String alias, String correo) throws UsuarioYaExisteException {
        if (alias == null || alias.isBlank()) {
            throw new IllegalArgumentException("El alias no puede estar vacio.");
        }
        if (correo == null || !correo.contains("@")) {
            throw new IllegalArgumentException("El correo no es valido.");
        }
        if (existe(alias)) {
            throw new UsuarioYaExisteException(alias.trim());
        }
        Usuario u = new Usuario(alias.trim(), correo.trim());
        usuarios.add(u);
        persistir();
        return u;
    }

    public void modificar(String aliasActual, String nuevoCorreo, boolean controlParental)
            throws UsuarioNoEncontradoException {
        Usuario u = buscar(aliasActual);
        if (u == null) throw new UsuarioNoEncontradoException(aliasActual);
        u.setCorreo(nuevoCorreo);
        u.setControlParental(controlParental);
        persistir();
    }

    public void eliminar(String alias) throws UsuarioNoEncontradoException {
        Usuario u = buscar(alias);
        if (u == null) throw new UsuarioNoEncontradoException(alias);
        usuarios.remove(u);
        persistir();
    }

    /** @return true si ya existe un alias (validacion de unicidad en vivo) */
    public boolean existe(String alias) {
        return buscar(alias) != null;
    }

    public Usuario buscar(String alias) {
        if (alias == null) return null;
        for (Usuario u : usuarios) {
            if (u.getAlias().equalsIgnoreCase(alias.trim())) return u;
        }
        return null;
    }
}
