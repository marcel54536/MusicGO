package musicgo.servicios;

import musicgo.modelo.Administrador;
import musicgo.persistencia.MapeadorAdministradores;
import musicgo.persistencia.ServicioPersistencia;

import java.util.List;

/**
 * Carga y consulta los administradores definidos en
 * {@code administradores.json}. Es de solo lectura: el registro de
 * administradores esta abstraido de la interfaz (regla de la Fase 2).
 *
 * @author Equipo MusicGO
 */
public class GestorAdministradores {

    private static final String ARCHIVO = "administradores.json";

    private final ServicioPersistencia persistencia;
    private List<Administrador> administradores = new java.util.ArrayList<>();

    public GestorAdministradores(ServicioPersistencia persistencia) {
        this.persistencia = persistencia;
    }

    /** Carga los administradores desde el JSON. @return cantidad leida */
    public int cargar() {
        administradores = MapeadorAdministradores.leer(persistencia.getRepositorio().leer(ARCHIVO));
        return administradores.size();
    }

    /**
     * Busca un administrador cuyas credenciales coincidan.
     *
     * @param usuario alias tecleado
     * @param clave clave tecleada
     * @return el administrador validado o {@code null} si no coincide ninguno
     */
    public Administrador validar(String usuario, String clave) {
        for (Administrador a : administradores) {
            if (a.credencialesValidas(usuario, clave)) return a;
        }
        return null;
    }
}
