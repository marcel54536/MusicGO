package musicgo.excepciones;

/**
 * Se lanza cuando el saldo virtual MusicGo del usuario no alcanza para
 * cubrir el monto de una compra.
 *
 * <p>Hereda de {@link PagoRechazadoException} para que el controlador
 * pueda capturar todos los fallos de pago con un solo catch, pero a la
 * vez distinguir este caso si quiere dar un mensaje mas especifico (LSP).</p>
 *
 * @author Equipo MusicGO
 */
public class FondosInsuficientesException extends PagoRechazadoException {

    public FondosInsuficientesException(double requerido, double disponible) {
        super(String.format(
                "Fondos insuficientes: se requieren $%.2f y solo hay $%.2f de saldo.",
                requerido, disponible));
    }
}
