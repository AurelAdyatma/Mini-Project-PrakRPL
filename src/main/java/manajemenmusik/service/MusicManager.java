package manajemenmusik.service;

import javafx.collections.ObservableList;
import manajemenmusik.dao.*;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;
import manajemenmusik.model.User;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * MusicManager — menerapkan Singleton Pattern.
 * Menjadi pusat koordinasi antara DAO dan logika bisnis.
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
    private final UserDAO userDAO;

    // ---- Session ----
    private User currentUser;

    private MusicManager() {
        this.songDAO = new SongDAOImpl();
        this.playlistDAO = new PlaylistDAOImpl();
        this.userDAO = new UserDAOImpl();
    }

    // ---- Auth ----

    public boolean register(String username, String password) {
        return userDAO.register(username, password);
    }

    public User login(String username, String password) {
        User user = userDAO.login(username, password);
        if (user != null) {
            this.currentUser = user;
            songDAO.setCurrentUserId(user.getId());
        }
        return user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        this.currentUser = null;
        songDAO.setCurrentUserId(-1);
    }

    // ---- Delegasi ke SongDAO ----

    public ObservableList<Song> getDaftarLagu() {
        return songDAO.getAll();
    }

    public ObservableList<Song> getRecycleBin() {
        return songDAO.getRecycleBin();
    }

    public boolean tambahLagu(Song song) {
        return songDAO.tambah(song);
    }

    public void hapusLagu(Song song) {
        songDAO.hapus(song);
        playlistDAO.hapusLaguDariSemuaPlaylist(song);
    }

    public void pulihkanLagu(Song song) {
        songDAO.pulihkan(song);
    }

    public void hapusPermanenLagu(Song song) {
        songDAO.hapusPermanen(song);
    }

    public void hapusSemuaLagu() {
        songDAO.hapusSemua();
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

    public void togglePublicPlaylist(Playlist pl) {
        playlistDAO.togglePublic(pl);
    }

    public void muatPlaylist() {
        if (currentUser != null) {
            playlistDAO.muatData(songDAO.getAll(), currentUser.getId());
        }
    }
}
