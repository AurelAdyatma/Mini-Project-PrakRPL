package manajemenmusik;

import javafx.beans.property.*;

public class Song {
    private final StringProperty id      = new SimpleStringProperty();
    private final StringProperty judul   = new SimpleStringProperty();
    private final StringProperty artis   = new SimpleStringProperty();
    private final StringProperty genre   = new SimpleStringProperty();
    private final IntegerProperty durasi = new SimpleIntegerProperty();
    private final IntegerProperty tahun  = new SimpleIntegerProperty();
    private final BooleanProperty favorit = new SimpleBooleanProperty(false);

    public Song(String id, String judul, String artis, String genre, int durasi, int tahun) {
        setId(id); setJudul(judul); setArtis(artis);
        setGenre(genre); setDurasi(durasi); setTahun(tahun);
    }

    public Song(String id, String judul, String artis, String genre, int durasi) {
        this(id, judul, artis, genre, durasi, 2020);
    }

    // -- Properties (for JavaFX bindings) --
    public StringProperty  idProperty()     { return id; }
    public StringProperty  judulProperty()  { return judul; }
    public StringProperty  artisProperty()  { return artis; }
    public StringProperty  genreProperty()  { return genre; }
    public IntegerProperty durasiProperty() { return durasi; }
    public IntegerProperty tahunProperty()  { return tahun; }
    public BooleanProperty favoritProperty() { return favorit; }

    // -- Getters --
    public String  getId()      { return id.get(); }
    public String  getJudul()   { return judul.get(); }
    public String  getArtis()   { return artis.get(); }
    public String  getGenre()   { return genre.get(); }
    public int     getDurasi()  { return durasi.get(); }
    public int     getTahun()   { return tahun.get(); }
    public boolean isFavorit()  { return favorit.get(); }

    // -- Setters --
    public void setId(String v)      { id.set(v); }
    public void setJudul(String v)   { judul.set(v); }
    public void setArtis(String v)   { artis.set(v); }
    public void setGenre(String v)   { genre.set(v); }
    public void setDurasi(int v)     { durasi.set(v); }
    public void setTahun(int v)      { tahun.set(v); }
    public void setFavorit(boolean v){ favorit.set(v); }
    public void toggleFavorit()      { favorit.set(!favorit.get()); }

    /** CSV format: id,judul,artis,genre,durasi,tahun,favorit */
    public String toCSV() {
        return csvEscape(getId()) + "," + csvEscape(getJudul()) + "," +
               csvEscape(getArtis()) + "," + csvEscape(getGenre()) + "," +
               getDurasi() + "," + getTahun() + "," + isFavorit();
    }

    private String csvEscape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    @Override
    public String toString() {
        return getJudul() + " - " + getArtis();
    }
}