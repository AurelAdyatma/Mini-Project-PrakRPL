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
    private int currentUserId;

    public PlaylistDAOImpl() {
        DatabaseConnection.initializeDatabase();
    }

    @Override
    public ObservableList<Playlist> getAllPlaylists() {
        return daftarPlaylist;
    }

    @Override
    public void tambahPlaylist(Playlist playlist) {
        String sql = "INSERT INTO playlists(nama, user_id, is_public) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, playlist.getNama());
            pstmt.setInt(2, playlist.getUserId());
            pstmt.setInt(3, playlist.isPublic() ? 1 : 0);
            pstmt.executeUpdate();
            daftarPlaylist.add(playlist);
        } catch (SQLException e) {
            System.err.println("Gagal tambah playlist: " + e.getMessage());
        }
    }

    @Override
    public void hapusPlaylist(Playlist playlist) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            // Hapus lagu-lagu di playlist
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM playlist_songs WHERE playlist_nama = ? AND playlist_user_id = ?")) {
                ps.setString(1, playlist.getNama());
                ps.setInt(2, playlist.getUserId());
                ps.executeUpdate();
            }
            // Hapus playlist
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM playlists WHERE nama = ? AND user_id = ?")) {
                ps.setString(1, playlist.getNama());
                ps.setInt(2, playlist.getUserId());
                ps.executeUpdate();
            }
            conn.commit();
            daftarPlaylist.remove(playlist);
        } catch (SQLException e) {
            System.err.println("Gagal hapus playlist: " + e.getMessage());
        }
    }

    @Override
    public void hapusLaguDariSemuaPlaylist(Song song) {
        String sql = "DELETE FROM playlist_songs WHERE song_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal hapus lagu dari playlist: " + e.getMessage());
        }
        for (Playlist pl : daftarPlaylist) {
            pl.getLagu().remove(song);
        }
    }

    @Override
    public void simpanData() {
        // Simpan relasi playlist_songs untuk semua playlist in-memory
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (Playlist pl : daftarPlaylist) {
                // Hapus relasi lama
                try (PreparedStatement ps = conn.prepareStatement(
                        "DELETE FROM playlist_songs WHERE playlist_nama = ? AND playlist_user_id = ?")) {
                    ps.setString(1, pl.getNama());
                    ps.setInt(2, pl.getUserId());
                    ps.executeUpdate();
                }
                // Insert relasi baru
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO playlist_songs(playlist_nama, playlist_user_id, song_id) VALUES(?, ?, ?)")) {
                    for (Song s : pl.getLagu()) {
                        ps.setString(1, pl.getNama());
                        ps.setInt(2, pl.getUserId());
                        ps.setString(3, s.getId());
                        ps.executeUpdate();
                    }
                }
            }
            conn.commit();
        } catch (SQLException e) {
            System.err.println("Gagal simpan playlist: " + e.getMessage());
        }
    }

    @Override
    public void togglePublic(Playlist playlist) {
        playlist.setPublic(!playlist.isPublic());
        String sql = "UPDATE playlists SET is_public = ? WHERE nama = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, playlist.isPublic() ? 1 : 0);
            pstmt.setString(2, playlist.getNama());
            pstmt.setInt(3, playlist.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal toggle public: " + e.getMessage());
        }
    }

    @Override
    public void muatData(ObservableList<Song> semuaLagu, int currentUserId) {
        this.currentUserId = currentUserId;
        daftarPlaylist.clear();

        Map<String, Song> songMap = new HashMap<>();
        for (Song s : semuaLagu) {
            songMap.put(s.getId(), s);
        }

        // Muat playlist milik user sendiri + playlist public milik user lain
        String sql = "SELECT nama, user_id, is_public FROM playlists WHERE user_id = ? OR is_public = 1";
        String sqlSongs = "SELECT song_id FROM playlist_songs WHERE playlist_nama = ? AND playlist_user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement psPlaylists = conn.prepareStatement(sql);
             PreparedStatement psSongs = conn.prepareStatement(sqlSongs)) {

            psPlaylists.setInt(1, currentUserId);
            try (ResultSet rs = psPlaylists.executeQuery()) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    int userId = rs.getInt("user_id");
                    boolean isPublic = rs.getInt("is_public") == 1;
                    Playlist pl = new Playlist(nama, userId, isPublic);

                    psSongs.setString(1, nama);
                    psSongs.setInt(2, userId);
                    try (ResultSet rsSongs = psSongs.executeQuery()) {
                        while (rsSongs.next()) {
                            Song s = songMap.get(rsSongs.getString("song_id"));
                            if (s != null) pl.tambahLagu(s);
                        }
                    }
                    daftarPlaylist.add(pl);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal memuat playlist: " + e.getMessage());
        }
    }
}
