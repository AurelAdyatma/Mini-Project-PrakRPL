package manajemenmusik.dao;

import manajemenmusik.model.Admin;
import manajemenmusik.model.RegularUser;
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
        String sql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, "admin".equalsIgnoreCase(username) ? "Admin" : "User");
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
        String sql = "SELECT id, username, password, role FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String uname = rs.getString("username");
                    String pass = rs.getString("password");
                    String role = rs.getString("role");
                    
                    User user;
                    if ("Admin".equalsIgnoreCase(role)) {
                        user = new Admin(uname, pass);
                    } else {
                        user = new RegularUser(uname, pass);
                    }
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
