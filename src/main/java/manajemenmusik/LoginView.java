package manajemenmusik;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {
    private final VBox root;
    private final TextField tfUsername;
    private final PasswordField pfPassword;
    private final Button btnLogin;
    private final Runnable onLoginSuccess;

    public LoginView(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;

        root = new VBox(12);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label lblJudul = new Label("Login Manajemen Musik");
        lblJudul.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        tfUsername = new TextField();
        tfUsername.setPromptText("Masukkan username");

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Masukkan password");

        btnLogin = new Button("Masuk");
        btnLogin.setMaxWidth(Double.MAX_VALUE);

        btnLogin.setOnAction(e -> prosesLogin());
        pfPassword.setOnAction(e -> prosesLogin());

        root.getChildren().addAll(
                lblJudul,
                new Label("Username"),
                tfUsername,
                new Label("Password"),
                pfPassword,
                btnLogin
        );
    }

    private void prosesLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();

        if (username.equals("admin") && password.equals("12345")) {
            tampilPesan(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang, admin.");
            onLoginSuccess.run();
        } else {
            tampilPesan(Alert.AlertType.ERROR, "Login Gagal", "Username atau password salah.");
        }
    }

    private void tampilPesan(Alert.AlertType type, String judul, String pesan) {
        Alert alert = new Alert(type);
        alert.setTitle(judul);
        alert.setHeaderText(null);
        alert.setContentText(pesan);
        alert.showAndWait();
    }

    public Parent getView() {
        return root;
    }
}