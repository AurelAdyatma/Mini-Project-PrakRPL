package manajemenmusik.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manajemenmusik.dao.SongDAO;
import manajemenmusik.dao.SongDAOImpl;
import manajemenmusik.dao.PlaylistDAO;
import manajemenmusik.dao.PlaylistDAOImpl;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * MusicManager — menerapkan Singleton Pattern.
 * Menjadi pusat koordinasi antara DAO dan logika bisnis (playlist, statistik).
 */
public class MusicManager {

    // ---- Singleton Pattern ----
    private static MusicManager instance;

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    // ---- Dependencies ----
    private final SongDAO songDAO;
    private final PlaylistDAO playlistDAO;

    private MusicManager() {
        this.songDAO = new SongDAOImpl();
        this.playlistDAO = new PlaylistDAOImpl();
        this.playlistDAO.muatData(this.songDAO.getAll());
    }

    // ---- Delegasi ke DAO ----

    public ObservableList<Song> getDaftarLagu() {
        return songDAO.getAll();
    }

    public boolean tambahLagu(Song song) {
        return songDAO.tambah(song);
    }

    public void hapusLagu(Song song) {
        songDAO.hapus(song);
        playlistDAO.hapusLaguDariSemuaPlaylist(song);
    }

    public void editLagu(Song orig, String id, String judul, String artis,
                         String genre, int durasi, int tahun) {
        songDAO.edit(orig, id, judul, artis, genre, durasi, tahun);
    }

    public ObservableList<Song> filter(String keyword, String genre) {
        return songDAO.filter(keyword, genre);
    }

    public ObservableList<Song> getDaftarFavorit() {
        return songDAO.getFavorit();
    }

    public Set<String> getGenreUnik() {
        return songDAO.getGenreUnik();
    }

    public Set<String> getArtisUnik() {
        return songDAO.getArtisUnik();
    }

    public Map<String, Long> getStatistikGenre() {
        return songDAO.getStatistikGenre();
    }

    public void exportCSV(File file) throws IOException {
        songDAO.exportCSV(file);
    }

    public int importCSV(File file) throws IOException {
        return songDAO.importCSV(file);
    }

    // ---- Playlist Management ----

    public ObservableList<Playlist> getDaftarPlaylist() {
        return playlistDAO.getAllPlaylists();
    }

    public void tambahPlaylist(Playlist pl) {
        playlistDAO.tambahPlaylist(pl);
    }

    public void hapusPlaylist(Playlist pl) {
        playlistDAO.hapusPlaylist(pl);
    }
    
    public void simpanPlaylist() {
        playlistDAO.simpanData();
    }
}
