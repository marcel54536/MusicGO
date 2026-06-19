package musicgo.servicios.pago;

import musicgo.excepciones.PagoRechazadoException;
import musicgo.interfaces.pago.MedioPago;
import musicgo.modelo.pago.Recibo;

/**
 * Coordina el cobro a traves de cualquier {@link MedioPago} (patron
 * Strategy). El resto de la aplicacion depende de esta abstraccion y no
 * de los medios concretos (DIP): agregar un medio nuevo no obliga a tocar
 * este servicio (OCP).
 *
 * @author Equipo MusicGO
 */
public class ProcesadorPago {

    /**
     * Ejecuta el cobro delegando la validacion en el medio elegido.
     *
     * @param medio estrategia de pago seleccionada por el usuario
     * @param monto importe a cobrar (debe ser positivo)
     * @return recibo de la transaccion aprobada
     * @throws PagoRechazadoException si el monto es invalido o el medio rechaza el pago
     */
    public Recibo cobrar(MedioPago medio, double monto) throws PagoRechazadoException {
        if (medio == null) {
            throw new PagoRechazadoException("No se selecciono un medio de pago.");
        }
        if (monto <= 0) {
            throw new PagoRechazadoException("El monto a pagar debe ser mayor que cero.");
        }
        return medio.procesar(monto);
    }
}
