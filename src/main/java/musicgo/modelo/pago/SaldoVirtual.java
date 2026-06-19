package musicgo.modelo.pago;

import musicgo.excepciones.FondosInsuficientesException;
import musicgo.excepciones.PagoRechazadoException;
import musicgo.interfaces.pago.MedioPago;
import musicgo.interfaces.pago.UsaSaldoVirtual;
import musicgo.modelo.Usuario;

/**
 * Pago con saldo virtual MusicGo. Su flujo de validacion consulta y
 * descuenta directamente la billetera del {@link Usuario}.
 *
 * <p>Implementa {@link UsaSaldoVirtual} ademas de {@link MedioPago}.
 * Lanza {@code FondosInsuficientesException} (subtipo de
 * {@code PagoRechazadoException}) cuando el saldo no alcanza.</p>
 *
 * @author Equipo MusicGO
 */
public class SaldoVirtual implements MedioPago, UsaSaldoVirtual {

    private final Usuario titular;

    public SaldoVirtual(Usuario titular) {
        this.titular = titular;
    }

    @Override
    public String etiqueta() {
        return "Saldo Virtual MusicGo";
    }

    @Override
    public double saldoDisponible() {
        return titular.getSaldo();
    }

    @Override
    public Recibo procesar(double monto) throws PagoRechazadoException {
        if (!titular.descontarSaldo(monto)) {
            throw new FondosInsuficientesException(monto, titular.getSaldo());
        }
        return Recibo.aprobado(etiqueta(), monto);
    }
}
