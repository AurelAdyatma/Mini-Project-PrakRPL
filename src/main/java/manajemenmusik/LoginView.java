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

        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        root.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #0F3D91, #2563EB, #60A5FA);" +
                "-fx-font-family: 'Segoe UI';"
        );

        VBox cardLogin = new VBox(14);
        cardLogin.setAlignment(Pos.CENTER_LEFT);
        cardLogin.setPadding(new Insets(28));
        cardLogin.setMaxWidth(360);
        cardLogin.setStyle(
                "-fx-background-color: rgba(255,255,255,0.95);" +
                "-fx-background-radius: 24;" +
                "-fx-border-radius: 24;" +
                "-fx-border-color: rgba(255,255,255,0.35);" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 22, 0, 0, 8);"
        );

        Label lblMini = new Label("MiniProject");
        lblMini.setStyle(
                "-fx-font-size: 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #12356E;"
        );

        Label lblJudul = new Label("Aplikasi Manajemen Musik");
        lblJudul.setStyle(
                "-fx-font-size: 19px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2563EB;"
        );

        Label lblDeskripsi = new Label("Silakan login untuk masuk ke sistem");
        lblDeskripsi.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #64748B;"
        );

        Label lblUsername = new Label("Username");
        lblUsername.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E293B;"
        );

        tfUsername = new TextField();
        tfUsername.setPromptText("Masukkan username");
        tfUsername.setPrefHeight(42);
        tfUsername.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: #BFDBFE;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13px;"
        );

        Label lblPassword = new Label("Password");
        lblPassword.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E293B;"
        );

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Masukkan password");
        pfPassword.setPrefHeight(42);
        pfPassword.setStyle(
                "-fx-background-color: #F8FAFC;" +
                "-fx-background-radius: 12;" +
                "-fx-border-radius: 12;" +
                "-fx-border-color: #BFDBFE;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13px;"
        );

        btnLogin = new Button("Masuk");
        btnLogin.setPrefWidth(304);
        btnLogin.setPrefHeight(44);
        btnLogin.setStyle(
                "-fx-background-color: linear-gradient(to right, #1D4ED8, #2563EB);" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 14;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(37,99,235,0.35), 10, 0, 0, 4);"
        );

        Label lblInfo = new Label("Login default: admin / 12345");
        lblInfo.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #64748B;" +
                "-fx-font-style: italic;"
        );

        btnLogin.setOnAction(e -> prosesLogin());
        pfPassword.setOnAction(e -> prosesLogin());

        cardLogin.getChildren().addAll(
                lblMini,
                lblJudul,
                lblDeskripsi,
                lblUsername,
                tfUsername,
                lblPassword,
                pfPassword,
                btnLogin,
                lblInfo
        );

        root.getChildren().add(cardLogin);
    }

    private void prosesLogin() {
        String username = tfUsername.getText().trim();
        String password = pfPassword.getText().trim();

        if (username.equals("admin") && password.equals("12345")) {
            tampilPesan(Alert.AlertType.INFORMATION, "Login Berhasil", "Selamat datang di aplikasi.");
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
