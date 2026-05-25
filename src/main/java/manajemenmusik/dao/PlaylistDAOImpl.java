package manajemenmusik.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class PlaylistDAOImpl implements PlaylistDAO {

    private final ObservableList<Playlist> daftarPlaylist = FXCollections.observableArrayList();
    private static final String FILE_NAME = "playlist_database.csv";

    @Override
    public ObservableList<Playlist> getAllPlaylists() {
        return daftarPlaylist;
    }

    @Override
    public void tambahPlaylist(Playlist playlist) {
        daftarPlaylist.add(playlist);
        simpanData();
    }

    @Override
    public void hapusPlaylist(Playlist playlist) {
        daftarPlaylist.remove(playlist);
        simpanData();
    }

    @Override
    public void hapusLaguDariSemuaPlaylist(Song song) {
        for (Playlist pl : daftarPlaylist) {
            pl.getLagu().remove(song);
        }
        simpanData();
    }

    @Override
    public void simpanData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Playlist pl : daftarPlaylist) {
                writer.print(pl.getNama() + ";");
                boolean first = true;
                for (Song song : pl.getLagu()) {
                    if (!first) writer.print(",");
                    writer.print(song.getId());
                    first = false;
                }
                writer.println();
            }
        } catch (IOException e) {
            System.err.println("Gagal menyimpan data playlist: " + e.getMessage());
        }
    }

    @Override
    public void muatData(ObservableList<Song> semuaLagu) {
        daftarPlaylist.clear();
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        Map<String, Song> songMap = new HashMap<>();
        for (Song s : semuaLagu) {
            songMap.put(s.getId(), s);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String baris;
            while ((baris = reader.readLine()) != null) {
                if (baris.trim().isEmpty()) continue;
                String[] parts = baris.split(";", -1);
                if (parts.length >= 1) {
                    Playlist pl = new Playlist(parts[0]);
                    if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                        String[] songIds = parts[1].split(",");
                        for (String id : songIds) {
                            Song s = songMap.get(id.trim());
                            if (s != null) {
                                pl.tambahLagu(s);
                            }
                        }
                    }
                    daftarPlaylist.add(pl);
                }
            }
        } catch (IOException e) {
            System.err.println("Gagal memuat data playlist: " + e.getMessage());
        }
    }
}
