package manajemenmusik.dao;

import manajemenmusik.model.Admin;
import manajemenmusik.model.User;

import java.sql.*;

/**
 * UserDAOImpl — implementasi autentikasi user menggunakan SQLite.
 */
public class UserDAOImpl implements UserDAO {

    public UserDAOImpl() {
        DatabaseConnection.initializeDatabase();
    }

    @Override
    public boolean register(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // UNIQUE constraint failed = username sudah ada
            System.err.println("Gagal register: " + e.getMessage());
            return false;
        }
    }

    @Override
    public User login(String username, String password) {
        String sql = "SELECT id, username, password FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String pass = rs.getString("password");
                    // Gunakan Admin sebagai concrete class (polymorphism)
                    Admin user = new Admin(uname, pass);
                    user.setId(id);
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal login: " + e.getMessage());
        }
        return null;
    }
}
