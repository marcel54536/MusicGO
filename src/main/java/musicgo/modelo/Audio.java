package musicgo.modelo;

import musicgo.interfaces.Identificable;
import musicgo.interfaces.Reproducible;

/**
 * Clase abstracta que representa cualquier contenido de audio de la
 * plataforma. Canciones y episodios comparten id, titulo, duracion y
 * clasificacion, pero arman sus creditos de forma distinta: por eso
 * {@link #creditos()} y {@link #getTipo()} se dejan abstractos.
 *
 * <p>Decision de diseno: clase abstracta y no interface porque queremos
 * compartir <em>estado</em> (id, titulo, duracion, clasificacion), no
 * solo comportamiento.</p>
 *
 * @author Equipo MusicGO
 */
public abstract class Audio implements Reproducible, Identificable {

    private final String id;
    private String titulo;
    private int duracionSegundos;
    private Clasificacion clasificacion;

    protected Audio(String id, String titulo, int duracionSegundos) {
        this.id = id;
        this.titulo = titulo;
        this.duracionSegundos = duracionSegundos;
        this.clasificacion = Clasificacion.APTO_TODO_PUBLICO;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getTitulo() {
        return titulo;
    }

    @Override
    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    @Override
    public Clasificacion getClasificacion() {
        return clasificacion;
    }

    public void setTitulo(String titulo) {
        if (titulo != null && !titulo.isBlank()) this.titulo = titulo.trim();
    }

    public void setDuracionSegundos(int duracionSegundos) {
        if (duracionSegundos > 0) this.duracionSegundos = duracionSegundos;
    }

    public void setClasificacion(Clasificacion clasificacion) {
        if (clasificacion != null) this.clasificacion = clasificacion;
    }

    /** Formatea la duracion en mm:ss para mostrarla en la interfaz. */
    public String duracionFormateada() {
        return String.format("%02d:%02d", duracionSegundos / 60, duracionSegundos % 60);
    }

    /**
     * Linea de creditos especifica del tipo de audio (polimorfismo).
     * @return creditos listos para mostrar en el reproductor
     */
    @Override
    public abstract String creditos();

    /**
     * Tipo de audio, usado para serializar a JSON y discriminar al cargar.
     * @return "cancion", "episodio", ...
     */
    public abstract String getTipo();
}
