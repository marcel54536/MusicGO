package musicgo.modelo;

/**
 * Episodio de podcast del catalogo. A diferencia de {@link Cancion}
 * guarda anfitrion, nombre del podcast, descripcion y numero de episodio.
 *
 * @author Equipo MusicGO
 */
public class EpisodioPodcast extends Audio {

    private String anfitrion;
    private String nombrePodcast;
    private String descripcion;
    private int numeroEpisodio;

    public EpisodioPodcast(String id, String titulo, int duracionSegundos,
                           String anfitrion, String nombrePodcast,
                           String descripcion, int numeroEpisodio) {
        super(id, titulo, duracionSegundos);
        this.anfitrion = anfitrion;
        this.nombrePodcast = nombrePodcast;
        this.descripcion = descripcion;
        this.numeroEpisodio = numeroEpisodio;
    }

    public String getAnfitrion() {
        return anfitrion;
    }

    public String getNombrePodcast() {
        return nombrePodcast;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getNumeroEpisodio() {
        return numeroEpisodio;
    }

    public void setAnfitrion(String anfitrion) {
        this.anfitrion = anfitrion;
    }

    public void setNombrePodcast(String nombrePodcast) {
        this.nombrePodcast = nombrePodcast;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setNumeroEpisodio(int numeroEpisodio) {
        if (numeroEpisodio > 0) this.numeroEpisodio = numeroEpisodio;
    }

    @Override
    public String getTipo() {
        return "episodio";
    }

    /** Creditos polimorficos: podcast, numero de episodio y anfitrion. */
    @Override
    public String creditos() {
        return nombrePodcast + "  ·  Ep. " + numeroEpisodio + "  ·  " + anfitrion;
    }

    @Override
    public String toString() {
        return "[Podcast] " + getId() + " - " + getTitulo() + " | "
                + nombrePodcast + " ep#" + numeroEpisodio
                + " - " + duracionFormateada();
    }
}
