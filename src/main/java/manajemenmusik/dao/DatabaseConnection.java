package manajemenmusik.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:musikapp.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Create songs table
            String createSongsTable = "CREATE TABLE IF NOT EXISTS songs ("
                    + "id TEXT PRIMARY KEY, "
                    + "judul TEXT NOT NULL, "
                    + "artis TEXT NOT NULL, "
                    + "genre TEXT, "
                    + "durasi INTEGER, "
                    + "tahun INTEGER, "
                    + "favorit INTEGER DEFAULT 0"
                    + ");";
            stmt.execute(createSongsTable);

            // Create playlists table
            String createPlaylistsTable = "CREATE TABLE IF NOT EXISTS playlists ("
                    + "nama TEXT PRIMARY KEY"
                    + ");";
            stmt.execute(createPlaylistsTable);

            // Create playlist_songs table (many-to-many relationship)
            String createPlaylistSongsTable = "CREATE TABLE IF NOT EXISTS playlist_songs ("
                    + "playlist_nama TEXT, "
                    + "song_id TEXT, "
                    + "PRIMARY KEY (playlist_nama, song_id), "
                    + "FOREIGN KEY (playlist_nama) REFERENCES playlists(nama) ON DELETE CASCADE, "
                    + "FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE"
                    + ");";
            stmt.execute(createPlaylistSongsTable);

        } catch (SQLException e) {
            System.err.println("Gagal menginisialisasi database: " + e.getMessage());
        }
    }
}
