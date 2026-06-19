package musicgo.interfaces.pago;

/**
 * Capacidad especifica del pago con saldo virtual MusicGo (ISP).
 * Solo la implementa {@code SaldoVirtual}.
 *
 * @author Equipo MusicGO
 */
public interface UsaSaldoVirtual {

    /** @return saldo disponible en la billetera del usuario */
    double saldoDisponible();
}
