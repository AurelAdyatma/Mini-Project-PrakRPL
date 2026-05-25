package manajemenmusik.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import manajemenmusik.model.User;
import manajemenmusik.service.MusicManager;

/**
 * Controller untuk LoginView.fxml — MVC Pattern (Controller layer).
 * Mendukung Register dan Login menggunakan SQLite.
 */
public class LoginController {

    @FXML private TextField tfUsername;
    @FXML private PasswordField pfPassword;

    private final MusicManager manager = MusicManager.getInstance();
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

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username dan password tidak boleh kosong.");
            return;
        }

        User user = manager.login(username, password);
        if (user != null) {
            System.out.println("Login berhasil sebagai " + user.getRole() + ": " + user.getUsername());
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
        }
    }

    @FXML
    private void onRegister() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Register Gagal", "Username dan password tidak boleh kosong.");
            return;
        }

        if (password.length() < 3) {
            showAlert(Alert.AlertType.ERROR, "Register Gagal", "Password minimal 3 karakter.");
            return;
        }

        if (manager.register(username, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Register Berhasil",
                    "Akun \"" + username + "\" berhasil dibuat!\nSilakan login.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Register Gagal",
                    "Username \"" + username + "\" sudah digunakan.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
