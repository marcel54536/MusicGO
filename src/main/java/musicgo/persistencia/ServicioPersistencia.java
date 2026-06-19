package musicgo.persistencia;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Guarda datos en JSON de forma <em>inmediata</em> pero sin bloquear el
 * hilo de la interfaz: cada escritura se encola en un hilo de fondo
 * (requisito de "persistencia en caliente mediante hilos" de la Fase 2).
 *
 * <p>Usa un unico hilo daemon: las escrituras se serializan en orden,
 * evitando que dos guardados pisen el mismo archivo a la vez.</p>
 *
 * @author Equipo MusicGO
 */
public class ServicioPersistencia {

    private final RepositorioDatos repo;
    private final ExecutorService hilo;

    public ServicioPersistencia(RepositorioDatos repo) {
        this.repo = repo;
        this.hilo = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "musicgo-persistencia");
            t.setDaemon(true);
            return t;
        });
    }

    public RepositorioDatos getRepositorio() {
        return repo;
    }

    /**
     * Programa la escritura del archivo en segundo plano y retorna de
     * inmediato. La vista no se congela mientras se graba.
     *
     * @param archivo nombre del .json destino
     * @param contenido objeto serializable (Map/List)
     */
    public void guardarAsync(String archivo, Object contenido) {
        hilo.submit(() -> repo.escribir(archivo, contenido));
    }

    /**
     * Cierra el servicio esperando a que terminen las escrituras pendientes
     * (persistencia limpia al cerrar la aplicacion).
     */
    public void cerrar() {
        hilo.shutdown();
        try {
            if (!hilo.awaitTermination(5, TimeUnit.SECONDS)) {
                hilo.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            hilo.shutdownNow();
        }
    }
}
