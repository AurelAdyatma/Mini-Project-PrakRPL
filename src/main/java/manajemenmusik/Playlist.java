package manajemenmusik;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String namaPlaylist;
    private List<Song> daftarLagu;

    public Playlist(String namaPlaylist) {
        this.namaPlaylist = namaPlaylist;
        this.daftarLagu = new ArrayList<>();
    }

    public String getNamaPlaylist() {
        return namaPlaylist;
    }

    public List<Song> getDaftarLagu() {
        return daftarLagu;
    }

    public void tambahLagu(Song lagu) {
        daftarLagu.add(lagu);
    }

    public void hapusLagu(Song lagu) {
        daftarLagu.remove(lagu);
    }
}