package musicgo.modelo.pago;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Comprobante inmutable de una transaccion de pago exitosa. Es un objeto
 * de valor: solo transporta datos del resultado para que la vista los
 * muestre.
 *
 * @author Equipo MusicGO
 */
public final class Recibo {

    private final String idTransaccion;
    private final String medioUsado;
    private final double monto;
    private final LocalDateTime fecha;

    public Recibo(String idTransaccion, String medioUsado, double monto) {
        this.idTransaccion = idTransaccion;
        this.medioUsado = medioUsado;
        this.monto = monto;
        this.fecha = LocalDateTime.now();
    }

    /**
     * Fabrica un recibo aprobado generando un id de transaccion unico.
     * Centraliza la generacion del id para no repetirla en cada medio (DRY).
     *
     * @param medioUsado etiqueta del medio de pago
     * @param monto importe cobrado
     * @return recibo listo para mostrar
     */
    public static Recibo aprobado(String medioUsado, double monto) {
        String id = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Recibo(id, medioUsado, monto);
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public String getMedioUsado() {
        return medioUsado;
    }

    public double getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String resumen() {
        String f = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        return String.format("Pago aprobado por $%.2f via %s%nTransaccion %s — %s",
                monto, medioUsado, idTransaccion, f);
    }
}
