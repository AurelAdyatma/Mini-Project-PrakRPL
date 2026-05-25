package manajemenmusik.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import manajemenmusik.model.Admin;
import manajemenmusik.model.User;

/**
 * Controller untuk LoginView.fxml — MVC Pattern (Controller layer).
 * Menggunakan polymorphism melalui User.getRole() saat autentikasi.
 */
public class LoginController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;

    private Runnable onLoginSuccess;

    // ---- Dipanggil oleh Main untuk menyetel callback ----
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }

    @FXML
    private void onUsernameEnter() {
        pfPassword.requestFocus();
    }

    @FXML
    private void onLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();

        // Polymorphism: Admin extends User, menggunakan authenticate() dari User
        User admin = new Admin("admin", "12345");

        if (admin.authenticate(username, password)) {
            System.out.println("Login berhasil sebagai " + admin.getRole());
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Username atau password salah.");
            alert.showAndWait();
        }
    }
}
