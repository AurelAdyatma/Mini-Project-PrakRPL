package manajemenmusik.dao;

import javafx.collections.ObservableList;
import manajemenmusik.model.Song;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * Interface SongDAO — menerapkan Abstraction (DAO Pattern).
 * Mendefinisikan kontrak akses data lagu tanpa mengekspos implementasi.
 */
public interface SongDAO {

    void setCurrentUserId(int userId);

    boolean tambah(Song song);

    void hapus(Song song);

    void hapusSemua();

    void edit(Song orig, String id, String judul, String artis,
              String genre, int durasi, int tahun);

    void pulihkan(Song song);

    void hapusPermanen(Song song);

    ObservableList<Song> getAll();

    ObservableList<Song> getRecycleBin();

    ObservableList<Song> getFavorit();

    ObservableList<Song> filter(String keyword, String genre);

    Set<String> getGenreUnik();

    Set<String> getArtisUnik();

    Map<String, Long> getStatistikGenre();

    void exportCSV(File file) throws IOException;

    int importCSV(File file) throws IOException;
}
