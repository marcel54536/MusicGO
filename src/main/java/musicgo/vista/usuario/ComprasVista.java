package musicgo.vista.usuario;

import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import musicgo.app.Vista;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Compra;
import musicgo.modelo.Producto;
import musicgo.modelo.Usuario;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

/**
 * Módulo "Mis compras": muestra el historial de productos adquiridos por
 * el usuario (nombre, monto y fecha) y un resumen con el total gastado.
 * Solo presenta; el controlador le pasa los datos (MVC).
 *
 * @author Equipo MusicGO
 */
public class ComprasVista implements Vista {

    private final VBox raiz = new VBox(Tema.ESPACIO);
    private final FlowPane resumen = new FlowPane(Tema.ESPACIO, Tema.ESPACIO);
    private final VBox lista = new VBox(Tema.ESPACIO_S);

    public ComprasVista() {
        raiz.setPadding(new Insets(Tema.ESPACIO_L));
        ScrollPane scroll = new ScrollPane(lista);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        raiz.getChildren().addAll(
                Componentes.titulo("Mis compras"),
                resumen,
                Componentes.subtitulo("Historial"),
                scroll);
    }

    /** Redibuja el resumen y el historial a partir del usuario y el catálogo. */
    public void render(Usuario u, Catalogo catalogo) {
        double total = 0;
        for (Compra c : u.getHistorialCompras()) total += c.getMontoPagado();
        resumen.getChildren().setAll(
                metrica(String.format("$%.2f", total), "Total gastado"),
                metrica(String.valueOf(u.getHistorialCompras().size()), "Compras"),
                metrica(String.format("$%.2f", u.getSaldo()), "Saldo actual"));

        lista.getChildren().clear();
        if (u.getHistorialCompras().isEmpty()) {
            lista.getChildren().add(Componentes.textoSuave("Todavía no has comprado nada. "
                    + "Explora el catálogo y adquiere un producto especial."));
            return;
        }
        for (Compra c : u.getHistorialCompras()) {
            lista.getChildren().add(fila(c, catalogo.buscarProducto(c.getIdProducto())));
        }
    }

    private VBox metrica(String valor, String etiqueta) {
        VBox card = Componentes.card(Componentes.titulo(valor), Componentes.textoSuave(etiqueta));
        card.setPrefWidth(200);
        return card;
    }

    private HBox fila(Compra c, Producto p) {
        String nombre = (p != null) ? p.getNombre() : "Producto " + c.getIdProducto();
        HBox h = Componentes.hbox(Tema.ESPACIO,
                Componentes.caratula(nombre, 44),
                Componentes.vbox(2,
                        Componentes.texto(nombre),
                        Componentes.textoSuave("Comprado el " + c.fechaFormateada())),
                Componentes.espaciador(),
                Componentes.chip(String.format("$%.2f", c.getMontoPagado())));
        h.getStyleClass().add("card");
        h.setPadding(new Insets(Tema.ESPACIO_S, Tema.ESPACIO, Tema.ESPACIO_S, Tema.ESPACIO));
        return h;
    }

    @Override
    public Parent getRaiz() {
        return raiz;
    }
}
