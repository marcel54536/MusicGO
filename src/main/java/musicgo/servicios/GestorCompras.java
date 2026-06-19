package musicgo.servicios;

import musicgo.excepciones.ContenidoNoEncontradoException;
import musicgo.excepciones.PagoRechazadoException;
import musicgo.interfaces.pago.MedioPago;
import musicgo.modelo.Catalogo;
import musicgo.modelo.Compra;
import musicgo.modelo.Producto;
import musicgo.modelo.Usuario;
import musicgo.modelo.pago.Recibo;
import musicgo.servicios.pago.ProcesadorPago;

/**
 * Modulo de compras: cobra un producto especial usando el medio de pago
 * elegido y, si el cobro es aprobado, registra la compra y persiste.
 *
 * @author Equipo MusicGO
 */
public class GestorCompras {

    private final Catalogo catalogo;
    private final ProcesadorPago procesador;
    private final GestorUsuarios gestorUsuarios;

    public GestorCompras(Catalogo catalogo, ProcesadorPago procesador,
                         GestorUsuarios gestorUsuarios) {
        this.catalogo = catalogo;
        this.procesador = procesador;
        this.gestorUsuarios = gestorUsuarios;
    }

    /**
     * Compra un producto con el medio de pago indicado.
     *
     * @param usuario comprador
     * @param idProducto id del producto del catalogo
     * @param medio estrategia de pago seleccionada
     * @return recibo de la transaccion aprobada
     * @throws ContenidoNoEncontradoException si el producto no existe
     * @throws PagoRechazadoException si el pago es rechazado por el medio
     */
    public Recibo comprar(Usuario usuario, String idProducto, MedioPago medio)
            throws ContenidoNoEncontradoException, PagoRechazadoException {

        Producto p = catalogo.buscarProducto(idProducto);
        if (p == null) throw new ContenidoNoEncontradoException(idProducto);

        Recibo recibo = procesador.cobrar(medio, p.getPrecio());

        usuario.registrarCompra(new Compra(p.getId(), usuario.getAlias(), recibo.getMonto()));
        gestorUsuarios.persistir();
        return recibo;
    }
}
