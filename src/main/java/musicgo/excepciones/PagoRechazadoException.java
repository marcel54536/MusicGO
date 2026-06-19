package musicgo.excepciones;

/**
 * Excepcion base de la pasarela de pagos: se lanza cuando un metodo de
 * pago no puede completar la transaccion (tarjeta invalida, cuenta mal
 * formada, etc.).
 *
 * <p>Es checked a proposito: obliga al controlador a capturarla con
 * try-catch y mostrar una alerta grafica, sin tumbar el hilo de la app
 * (requisito de robustez de la Fase 2).</p>
 *
 * @author Equipo MusicGO
 */
public class PagoRechazadoException extends Exception {

    public PagoRechazadoException(String motivo) {
        super(motivo);
    }
}
