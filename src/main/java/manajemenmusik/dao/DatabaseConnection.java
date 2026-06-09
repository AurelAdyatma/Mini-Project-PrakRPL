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

            stmt.execute("PRAGMA foreign_keys = ON;");

            // Tabel users
            stmt.execute("CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "username TEXT NOT NULL UNIQUE, "
                    + "password TEXT NOT NULL"
                    + ");");

            // Tabel songs (global)
            stmt.execute("CREATE TABLE IF NOT EXISTS songs ("
                    + "id TEXT PRIMARY KEY, "
                    + "judul TEXT NOT NULL, "
                    + "artis TEXT NOT NULL, "
                    + "genre TEXT, "
                    + "durasi INTEGER, "
                    + "tahun INTEGER"
                    + ");");

            // Tabel playlists (per-user, dengan visibilitas)
            stmt.execute("CREATE TABLE IF NOT EXISTS playlists ("
                    + "nama TEXT NOT NULL, "
                    + "user_id INTEGER NOT NULL, "
                    + "is_public INTEGER DEFAULT 0, "
                    + "PRIMARY KEY (nama, user_id), "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ");");

            // Tabel playlist_songs (many-to-many)
            stmt.execute("CREATE TABLE IF NOT EXISTS playlist_songs ("
                    + "playlist_nama TEXT, "
                    + "playlist_user_id INTEGER, "
                    + "song_id TEXT, "
                    + "PRIMARY KEY (playlist_nama, playlist_user_id, song_id), "
                    + "FOREIGN KEY (playlist_nama, playlist_user_id) REFERENCES playlists(nama, user_id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE"
                    + ");");

            // Tabel user_favorites (many-to-many untuk favorit per user)
            stmt.execute("CREATE TABLE IF NOT EXISTS user_favorites ("
                    + "user_id INTEGER, "
                    + "song_id TEXT, "
                    + "PRIMARY KEY (user_id, song_id), "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (song_id) REFERENCES songs(id) ON DELETE CASCADE"
                    + ");");

            // Migrasi: tambah/hapus kolom baru jika tabel lama sudah ada
            try { stmt.execute("ALTER TABLE playlists ADD COLUMN user_id INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE playlists ADD COLUMN is_public INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE playlist_songs ADD COLUMN playlist_user_id INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE songs ADD COLUMN is_deleted INTEGER DEFAULT 0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE songs DROP COLUMN favorit"); } catch (SQLException ignored) {}

        } catch (SQLException e) {
            System.err.println("Gagal menginisialisasi database: " + e.getMessage());
        }
    }
}
