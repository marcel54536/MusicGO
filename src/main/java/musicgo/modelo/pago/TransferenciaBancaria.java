package musicgo.modelo.pago;

import musicgo.excepciones.PagoRechazadoException;
import musicgo.interfaces.pago.MedioPago;
import musicgo.interfaces.pago.RequiereCuentaBancaria;

/**
 * Pago por transferencia bancaria. Su flujo de validacion verifica que
 * el numero de cuenta tenga el formato esperado (20 digitos) y que se
 * indique el banco emisor.
 *
 * <p>Implementa {@link RequiereCuentaBancaria} ademas de {@link MedioPago};
 * no arrastra metodos de tarjeta ni de saldo que no necesita (ISP).</p>
 *
 * @author Equipo MusicGO
 */
public class TransferenciaBancaria implements MedioPago, RequiereCuentaBancaria {

    private final String numeroCuenta;
    private final String banco;

    public TransferenciaBancaria(String numeroCuenta, String banco) {
        this.numeroCuenta = numeroCuenta == null ? "" : numeroCuenta.replaceAll("[\\s-]", "");
        this.banco = banco;
    }

    @Override
    public String etiqueta() {
        return "Transferencia Bancaria";
    }

    @Override
    public boolean cuentaValida() {
        return numeroCuenta.matches("\\d{20}");
    }

    @Override
    public String banco() {
        return banco;
    }

    @Override
    public Recibo procesar(double monto) throws PagoRechazadoException {
        if (banco == null || banco.isBlank()) {
            throw new PagoRechazadoException("Debes indicar el banco emisor.");
        }
        if (!cuentaValida()) {
            throw new PagoRechazadoException("Numero de cuenta invalido (deben ser 20 digitos).");
        }
        return Recibo.aprobado(etiqueta() + " (" + banco + ")", monto);
    }
}
