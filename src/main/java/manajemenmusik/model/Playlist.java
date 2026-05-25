package manajemenmusik.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model Playlist — menerapkan Encapsulation (private fields + getter/setter).
 * Mendukung multi-user dan visibilitas public/private.
 */
public class Playlist {

    private String nama;
    private int userId;
    private boolean isPublic;
    private final ObservableList<Song> lagu = FXCollections.observableArrayList();

    public Playlist(String nama) {
        this.nama = nama;
    }

    public Playlist(String nama, int userId, boolean isPublic) {
        this.nama = nama;
        this.userId = userId;
        this.isPublic = isPublic;
    }

    // ---- Encapsulation ----

    public String getNama()       { return nama; }
    public void setNama(String v) { this.nama = v; }

    public int getUserId()        { return userId; }
    public void setUserId(int v)  { this.userId = v; }

    public boolean isPublic()         { return isPublic; }
    public void setPublic(boolean v)  { this.isPublic = v; }

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
