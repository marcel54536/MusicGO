package musicgo.persistencia;

import musicgo.modelo.Administrador;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Lee los administradores desde {@code administradores.json}. Es solo de
 * lectura a proposito: el enunciado prohibe registrar administradores
 * desde la interfaz (abstraccion del registro publico).
 *
 * @author Equipo MusicGO
 */
public final class MapeadorAdministradores {

    private MapeadorAdministradores() { }

    /** @return lista de administradores definidos en el JSON (vacia si no hay) */
    public static List<Administrador> leer(Object raiz) {
        List<Administrador> admins = new ArrayList<>();
        if (raiz == null) return admins;
        for (Object o : JsonParser.asList(raiz)) {
            Map<String, Object> m = JsonParser.asMap(o);
            admins.add(new Administrador(
                    JsonParser.asString(m.get("usuario")),
                    JsonParser.asString(m.get("clave")),
                    JsonParser.asString(m.get("nombre"))));
        }
        return admins;
    }
}
