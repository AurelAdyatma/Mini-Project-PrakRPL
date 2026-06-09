package manajemenmusik.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import manajemenmusik.model.Song;

import java.io.*;
import java.sql.*;
import java.util.*;

public class SongDAOImpl implements SongDAO {

    public SongDAOImpl() {
        DatabaseConnection.initializeDatabase();
    }

    @Override
    public boolean tambah(Song song) {
        String sql = "INSERT INTO songs(id, judul, artis, genre, durasi, tahun, favorit) VALUES(?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getId());
            pstmt.setString(2, song.getJudul());
            pstmt.setString(3, song.getArtis());
            pstmt.setString(4, song.getGenre());
            pstmt.setInt(5, song.getDurasi());
            pstmt.setInt(6, song.getTahun());
            pstmt.setInt(7, song.isFavorit() ? 1 : 0);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal tambah lagu: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void hapus(Song song) {
        String sql = "UPDATE songs SET is_deleted = 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal hapus lagu: " + e.getMessage());
        }
    }

    @Override
    public void hapusSemua() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM playlist_songs");
            stmt.executeUpdate("UPDATE songs SET is_deleted = 1");
        } catch (SQLException e) {
            System.err.println("Gagal hapus semua lagu: " + e.getMessage());
        }
    }

    @Override
    public void pulihkan(Song song) {
        String sql = "UPDATE songs SET is_deleted = 0 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal pulihkan lagu: " + e.getMessage());
        }
    }

    @Override
    public void hapusPermanen(Song song) {
        String sql = "DELETE FROM songs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, song.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal hapus permanen lagu: " + e.getMessage());
        }
    }

    @Override
    public void edit(Song orig, String id, String judul, String artis, String genre, int durasi, int tahun) {
        String sql = "UPDATE songs SET id = ?, judul = ?, artis = ?, genre = ?, durasi = ?, tahun = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, judul);
            pstmt.setString(3, artis);
            pstmt.setString(4, genre);
            pstmt.setInt(5, durasi);
            pstmt.setInt(6, tahun);
            pstmt.setString(7, orig.getId());
            pstmt.executeUpdate();

            // Update di object-nya (jika UI masih terikat dengannya)
            orig.setId(id);
            orig.setJudul(judul);
            orig.setArtis(artis);
            orig.setGenre(genre);
            orig.setDurasi(durasi);
            orig.setTahun(tahun);
        } catch (SQLException e) {
            System.err.println("Gagal edit lagu: " + e.getMessage());
        }
    }

    @Override
    public ObservableList<Song> getAll() {
        ObservableList<Song> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM songs WHERE is_deleted = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getAll: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Song> getRecycleBin() {
        ObservableList<Song> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM songs WHERE is_deleted = 1";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getRecycleBin: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Song> getFavorit() {
        ObservableList<Song> list = FXCollections.observableArrayList();
        String sql = "SELECT * FROM songs WHERE favorit = 1 AND is_deleted = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToSong(rs));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getFavorit: " + e.getMessage());
        }
        return list;
    }

    @Override
    public ObservableList<Song> filter(String keyword, String genre) {
        ObservableList<Song> list = FXCollections.observableArrayList();
        String kw = (keyword == null) ? "" : keyword.toLowerCase();
        boolean filterGenre = genre != null && !genre.equals("Semua Genre");

        String sql = "SELECT * FROM songs WHERE is_deleted = 0";
        if (filterGenre) {
            sql += " AND genre = ?";
        }
        if (!kw.isBlank()) {
            sql += " AND (LOWER(id) LIKE ? OR LOWER(judul) LIKE ? OR LOWER(artis) LIKE ? OR LOWER(genre) LIKE ?)";
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int paramIndex = 1;
            if (filterGenre) {
                pstmt.setString(paramIndex++, genre);
            }
            if (!kw.isBlank()) {
                String likeKw = "%" + kw + "%";
                pstmt.setString(paramIndex++, likeKw);
                pstmt.setString(paramIndex++, likeKw);
                pstmt.setString(paramIndex++, likeKw);
                pstmt.setString(paramIndex++, likeKw);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToSong(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal filter: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Set<String> getGenreUnik() {
        Set<String> set = new TreeSet<>();
        String sql = "SELECT DISTINCT genre FROM songs WHERE is_deleted = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                set.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getGenreUnik: " + e.getMessage());
        }
        return set;
    }

    @Override
    public Set<String> getArtisUnik() {
        Set<String> set = new TreeSet<>();
        String sql = "SELECT DISTINCT artis FROM songs WHERE is_deleted = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                set.add(rs.getString("artis"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getArtisUnik: " + e.getMessage());
        }
        return set;
    }

    @Override
    public Map<String, Long> getStatistikGenre() {
        Map<String, Long> map = new LinkedHashMap<>();
        String sql = "SELECT genre, COUNT(*) as jumlah FROM songs WHERE is_deleted = 0 GROUP BY genre ORDER BY jumlah DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("genre"), rs.getLong("jumlah"));
            }
        } catch (SQLException e) {
            System.err.println("Gagal getStatistikGenre: " + e.getMessage());
        }
        return map;
    }

    private Song mapResultSetToSong(ResultSet rs) throws SQLException {
        Song s = new Song(
                rs.getString("id"),
                rs.getString("judul"),
                rs.getString("artis"),
                rs.getString("genre"),
                rs.getInt("durasi"),
                rs.getInt("tahun")
        );
        s.setFavorit(rs.getInt("favorit") == 1);

        // Listeners to update DB if changed from UI (like favorite toggle)
        s.favoritProperty().addListener((obs, oldV, newV) -> {
            updateFavoritStatus(s.getId(), newV);
        });

        return s;
    }

    private void updateFavoritStatus(String id, boolean favorit) {
        String sql = "UPDATE songs SET favorit = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, favorit ? 1 : 0);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Gagal update favorit: " + e.getMessage());
        }
    }

    // ---- CSV Import / Export (Jembatan Data) ----

    @Override
    public void exportCSV(File file) throws IOException {
        ObservableList<Song> daftarLagu = getAll();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            pw.println("ID,Judul,Artis,Genre,Durasi,Tahun,Favorit");
            daftarLagu.forEach(s -> pw.println(s.toCSV()));
        }
    }

    @Override
    public int importCSV(File file) throws IOException {
        int count = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = parseCsvLine(line);
                if (p.length >= 6) {
                    try {
                        Song s = new Song(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), Integer.parseInt(p[5]));
                        if (p.length >= 7) s.setFavorit(Boolean.parseBoolean(p[6]));
                        if (tambah(s)) count++;
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
        return count;
    }

    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString().trim());
        return result.toArray(new String[0]);
    }
}
