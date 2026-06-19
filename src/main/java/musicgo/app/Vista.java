package musicgo.app;

import javafx.scene.Parent;

/**
 * Contrato minimo de toda vista JavaFX del proyecto: exponer su nodo
 * raiz para que el {@link Router} la coloque en pantalla. Las vistas solo
 * arman la interfaz; no contienen logica de negocio (MVC).
 *
 * @author Equipo MusicGO
 */
public interface Vista {

    /** @return nodo raiz de la vista, listo para mostrarse */
    Parent getRaiz();
}
