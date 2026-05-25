package manajemenmusik.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manajemenmusik.model.Playlist;
import manajemenmusik.model.Song;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PlaylistDAOImpl implements PlaylistDAO {

    private final ObservableList<Playlist> daftarPlaylist = FXCollections.observableArrayList();

    public PlaylistDAOImpl() {
        DatabaseConnection.initializeDatabase();
    }

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
        // Menyimpan status in-memory daftarPlaylist ke SQLite
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false); // Transaksi
            
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM playlist_songs");
                stmt.executeUpdate("DELETE FROM playlists");
            }
            
            String insertPlaylist = "INSERT INTO playlists(nama) VALUES(?)";
            String insertSong = "INSERT INTO playlist_songs(playlist_nama, song_id) VALUES(?, ?)";
            
            try (PreparedStatement psPlaylist = conn.prepareStatement(insertPlaylist);
                 PreparedStatement psSong = conn.prepareStatement(insertSong)) {
                
                for (Playlist pl : daftarPlaylist) {
                    psPlaylist.setString(1, pl.getNama());
                    psPlaylist.executeUpdate();
                    
                    for (Song s : pl.getLagu()) {
                        psSong.setString(1, pl.getNama());
                        psSong.setString(2, s.getId());
                        psSong.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Gagal menyimpan data playlist ke DB: " + e.getMessage());
        }
    }

    @Override
    public void muatData(ObservableList<Song> semuaLagu) {
        daftarPlaylist.clear();
        
        Map<String, Song> songMap = new HashMap<>();
        for (Song s : semuaLagu) {
            songMap.put(s.getId(), s);
        }

        String sqlPlaylists = "SELECT nama FROM playlists";
        String sqlSongs = "SELECT song_id FROM playlist_songs WHERE playlist_nama = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rsPlaylists = stmt.executeQuery(sqlPlaylists)) {
            
            try (PreparedStatement psSongs = conn.prepareStatement(sqlSongs)) {
                while (rsPlaylists.next()) {
                    String nama = rsPlaylists.getString("nama");
                    Playlist pl = new Playlist(nama);
                    
                    psSongs.setString(1, nama);
                    try (ResultSet rsSongs = psSongs.executeQuery()) {
                        while (rsSongs.next()) {
                            String songId = rsSongs.getString("song_id");
                            Song s = songMap.get(songId);
                            if (s != null) {
                                pl.tambahLagu(s);
                            }
                        }
                    }
                    daftarPlaylist.add(pl);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat data playlist dari DB: " + e.getMessage());
        }
    }
}
