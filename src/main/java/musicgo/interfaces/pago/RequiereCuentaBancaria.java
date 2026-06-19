package musicgo.interfaces.pago;

/**
 * Capacidad especifica de los medios de pago por transferencia (ISP).
 * Solo la implementa {@code TransferenciaBancaria}.
 *
 * @author Equipo MusicGO
 */
public interface RequiereCuentaBancaria {

    /** @return true si el numero de cuenta tiene un formato valido */
    boolean cuentaValida();

    /** @return banco emisor de la transferencia */
    String banco();
}
