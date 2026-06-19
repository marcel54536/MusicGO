package musicgo.modelo.pago;

import musicgo.excepciones.PagoRechazadoException;
import musicgo.interfaces.pago.MedioPago;
import musicgo.interfaces.pago.RequiereTarjeta;

/**
 * Pago con tarjeta de credito. Su flujo de validacion verifica el numero
 * (16 digitos) y los datos de seguridad (CVV de 3-4 digitos y vigencia).
 *
 * <p>Implementa {@link RequiereTarjeta} ademas de {@link MedioPago}: esos
 * metodos de validacion solo tienen sentido aqui (ISP).</p>
 *
 * @author Equipo MusicGO
 */
public class TarjetaCredito implements MedioPago, RequiereTarjeta {

    private final String numero;
    private final String titular;
    private final String cvv;
    private final String vencimiento;   // formato MM/AA

    public TarjetaCredito(String numero, String titular, String cvv, String vencimiento) {
        this.numero = numero == null ? "" : numero.replaceAll("\\s", "");
        this.titular = titular;
        this.cvv = cvv == null ? "" : cvv.trim();
        this.vencimiento = vencimiento == null ? "" : vencimiento.trim();
    }

    @Override
    public String etiqueta() {
        return "Tarjeta de Credito";
    }

    @Override
    public boolean numeroValido() {
        return numero.matches("\\d{16}");
    }

    @Override
    public boolean datosSeguridadValidos() {
        return cvv.matches("\\d{3,4}") && vencimiento.matches("\\d{2}/\\d{2}");
    }

    @Override
    public Recibo procesar(double monto) throws PagoRechazadoException {
        if (titular == null || titular.isBlank()) {
            throw new PagoRechazadoException("Debes indicar el titular de la tarjeta.");
        }
        if (!numeroValido()) {
            throw new PagoRechazadoException("Numero de tarjeta invalido (deben ser 16 digitos).");
        }
        if (!datosSeguridadValidos()) {
            throw new PagoRechazadoException("CVV o fecha de vencimiento invalidos (use MM/AA).");
        }
        return Recibo.aprobado(etiqueta(), monto);
    }
}
