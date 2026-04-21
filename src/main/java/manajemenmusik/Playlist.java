package manajemenmusik;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Playlist {
    private String nama;
    private final ObservableList<Song> lagu = FXCollections.observableArrayList();

    public Playlist(String nama) {
        this.nama = nama;
    }

    public String getNama()        { return nama; }
    public void setNama(String v)  { this.nama = v; }
    public ObservableList<Song> getLagu() { return lagu; }

    public boolean tambahLagu(Song s) {
        if (lagu.contains(s)) return false;
        lagu.add(s);
        return true;
    }

    public void hapusLagu(Song s) {
        lagu.remove(s);
    }

    @Override
    public String toString() {
        return nama + "  (" + lagu.size() + " lagu)";
    }
}