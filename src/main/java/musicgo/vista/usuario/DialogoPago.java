package musicgo.vista.usuario;

import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import musicgo.interfaces.pago.MedioPago;
import musicgo.modelo.Producto;
import musicgo.modelo.Usuario;
import musicgo.modelo.pago.SaldoVirtual;
import musicgo.modelo.pago.TarjetaCredito;
import musicgo.modelo.pago.TransferenciaBancaria;
import musicgo.vista.ui.Componentes;
import musicgo.vista.ui.Tema;

import java.util.Optional;

/**
 * Pasarela de pagos multimodal: el usuario elige el medio y la vista
 * muestra los campos propios de cada uno. Al confirmar construye el
 * {@link MedioPago} concreto. No procesa el cobro (eso es del controlador
 * via el servicio): solo arma la estrategia elegida.
 *
 * @author Equipo MusicGO
 */
public final class DialogoPago {

    private DialogoPago() { }

    public static Optional<MedioPago> pedir(Producto producto, Usuario usuario) {
        ComboBox<String> tipo = new ComboBox<>();
        tipo.getItems().addAll("Tarjeta de Credito", "Transferencia Bancaria", "Saldo Virtual MusicGo");
        tipo.setValue("Tarjeta de Credito");

        TextField f1 = Componentes.campo("");
        TextField f2 = Componentes.campo("");
        TextField f3 = Componentes.campo("");
        TextField f4 = Componentes.campo("");
        VBox campos = new VBox(Tema.ESPACIO_S);

        Runnable pintar = () -> pintarCampos(tipo.getValue(), campos, usuario, f1, f2, f3, f4);
        tipo.setOnAction(e -> pintar.run());
        pintar.run();

        VBox contenido = Componentes.vbox(Tema.ESPACIO,
                Componentes.subtitulo(producto.getNombre()),
                Componentes.textoSuave("Total a pagar: $" + String.format("%.2f", producto.getPrecio())),
                tipo, campos);

        Dialog<MedioPago> dlg = new Dialog<>();
        dlg.setTitle("Pasarela de pagos");
        dlg.setHeaderText("Elige tu metodo de pago");
        dlg.getDialogPane().setContent(contenido);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dlg.setResultConverter(b -> b == ButtonType.OK
                ? construir(tipo.getValue(), usuario, f1, f2, f3, f4) : null);
        return dlg.showAndWait();
    }

    private static void pintarCampos(String tipo, VBox campos, Usuario u,
                                     TextField f1, TextField f2, TextField f3, TextField f4) {
        switch (tipo) {
            case "Tarjeta de Credito" -> {
                prompts(f1, "Numero (16 digitos)", f2, "Titular", f3, "CVV", f4, "Vencimiento MM/AA");
                campos.getChildren().setAll(f1, f2, f3, f4);
            }
            case "Transferencia Bancaria" -> {
                prompts(f1, "Numero de cuenta (20 digitos)", f2, "Banco emisor", null, null, null, null);
                campos.getChildren().setAll(f1, f2);
            }
            default -> campos.getChildren().setAll(
                    Componentes.textoSuave(String.format("Saldo disponible: $%.2f", u.getSaldo())));
        }
    }

    private static MedioPago construir(String tipo, Usuario u,
                                       TextField f1, TextField f2, TextField f3, TextField f4) {
        return switch (tipo) {
            case "Tarjeta de Credito" ->
                    new TarjetaCredito(f1.getText(), f2.getText(), f3.getText(), f4.getText());
            case "Transferencia Bancaria" ->
                    new TransferenciaBancaria(f1.getText(), f2.getText());
            default -> new SaldoVirtual(u);
        };
    }

    private static void prompts(TextField a, String pa, TextField b, String pb,
                                TextField c, String pc, TextField d, String pd) {
        a.setPromptText(pa); a.clear();
        b.setPromptText(pb); b.clear();
        if (c != null) { c.setPromptText(pc); c.clear(); }
        if (d != null) { d.setPromptText(pd); d.clear(); }
    }
}
