package musicgo.interfaces.pago;

import musicgo.excepciones.PagoRechazadoException;
import musicgo.modelo.pago.Recibo;

/**
 * Contrato minimo y comun a TODO medio de pago (ISP).
 *
 * <p>Solo declara lo que cualquier medio necesita: una etiqueta y la
 * accion de pagar. Los detalles de validacion propios de cada medio
 * (numero de tarjeta, cuenta bancaria, saldo) viven en interfaces
 * separadas y especificas, de modo que ninguna implementacion se vea
 * obligada a definir metodos que no le aplican.</p>
 *
 * @author Equipo MusicGO
 */
public interface MedioPago {

    /** @return nombre legible del medio (ej. "Tarjeta de Credito") */
    String etiqueta();

    /**
     * Ejecuta el cobro aplicando el flujo de validacion propio del medio.
     *
     * @param monto importe a cobrar
     * @return recibo de la transaccion si fue aprobada
     * @throws PagoRechazadoException si la validacion del medio falla
     */
    Recibo procesar(double monto) throws PagoRechazadoException;
}
