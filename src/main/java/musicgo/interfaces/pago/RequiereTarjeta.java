package musicgo.interfaces.pago;

/**
 * Capacidad especifica de los medios de pago basados en tarjeta (ISP).
 * Solo la implementa {@code TarjetaCredito}; una transferencia o el saldo
 * virtual NO se ven forzados a tener estos metodos.
 *
 * @author Equipo MusicGO
 */
public interface RequiereTarjeta {

    /** @return true si el numero de tarjeta tiene un formato valido */
    boolean numeroValido();

    /** @return true si la tarjeta no esta vencida y el CVV es correcto */
    boolean datosSeguridadValidos();
}
