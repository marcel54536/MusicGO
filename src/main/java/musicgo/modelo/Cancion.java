package musicgo.modelo;

/**
 * Cancion del catalogo de MusicGO. Ademas de los datos comunes a todo
 * {@link Audio} guarda artista, album y genero.
 *
 * @author Equipo MusicGO
 */
public class Cancion extends Audio {

    private String artista;
    private String album;
    private String genero;

    public Cancion(String id, String titulo, int duracionSegundos,
                   String artista, String album, String genero) {
        super(id, titulo, duracionSegundos);
        this.artista = artista;
        this.album = album;
        this.genero = genero;
    }

    public String getArtista() {
        return artista;
    }

    public String getAlbum() {
        return album;
    }

    public String getGenero() {
        return genero;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String getTipo() {
        return "cancion";
    }

    /** Creditos polimorficos: artista, album y genero. */
    @Override
    public String creditos() {
        return artista + "  ·  " + album + "  ·  " + genero;
    }

    @Override
    public String toString() {
        return "[Cancion] " + getId() + " - " + getTitulo() + " | " + artista
                + " (" + album + ") - " + duracionFormateada();
    }
}
