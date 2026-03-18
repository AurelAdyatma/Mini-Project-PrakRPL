package manajemenmusik;

public class Song {
    private String id;
    private String judul;
    private String artis;
    private String genre;
    private int durasi;

    public Song(String id, String judul, String artis, String genre, int durasi) {
        this.id = id;
        this.judul = judul;
        this.artis = artis;
        this.genre = genre;
        this.durasi = durasi;
    }

    public String getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getArtis() {
        return artis;
    }

    public String getGenre() {
        return genre;
    }

    public int getDurasi() {
        return durasi;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setArtis(String artis) {
        this.artis = artis;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setDurasi(int durasi) {
        this.durasi = durasi;
    }
}