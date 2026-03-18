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
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #EAF2FF, #F8FAFC);");

        VBox cardLogin = new VBox(12);
        cardLogin.setAlignment(Pos.CENTER_LEFT);
        cardLogin.setPadding(new Insets(25));
        cardLogin.setMaxWidth(340);
        cardLogin.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 20;" +
                "-fx-border-radius: 20;" +
                "-fx-border-color: #DCE3EA;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0, 0, 4);"
        );

        Label lblJudul = new Label("MiniProject");
        lblJudul.setStyle(
                "-fx-font-size: 28px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #1E3A5F;"
        );

        Label lblSubJudul = new Label("Aplikasi Manajemen Musik");
        lblSubJudul.setStyle(
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #2563EB;"
        );

        Label lblDeskripsi = new Label("Silakan login untuk masuk ke sistem");
        lblDeskripsi.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #6B7280;"
        );

        Label lblUsername = new Label("Username");
        lblUsername.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;"
        );

        tfUsername = new TextField();
        tfUsername.setPromptText("Masukkan username");
        tfUsername.setPrefHeight(40);
        tfUsername.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #D0D7DE;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13px;"
        );

        Label lblPassword = new Label("Password");
        lblPassword.setStyle(
                "-fx-font-size: 13px;" +
                "-fx-font-weight: bold;" +
                "-fx-text-fill: #374151;"
        );

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Masukkan password");
        pfPassword.setPrefHeight(40);
        pfPassword.setStyle(
                "-fx-background-radius: 10;" +
                "-fx-border-radius: 10;" +
                "-fx-border-color: #D0D7DE;" +
                "-fx-padding: 10;" +
                "-fx-font-size: 13px;"
        );

        btnLogin = new Button("Masuk");
        btnLogin.setPrefWidth(290);
        btnLogin.setPrefHeight(42);
        btnLogin.setStyle(
                "-fx-background-color: #2563EB;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 12;" +
                "-fx-cursor: hand;"
        );

        Label lblInfo = new Label("Login default: admin / 12345");
        lblInfo.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #6B7280;" +
                "-fx-font-style: italic;"
        );

        btnLogin.setOnAction(e -> prosesLogin());
        pfPassword.setOnAction(e -> prosesLogin());

        cardLogin.getChildren().addAll(
                lblJudul,
                lblSubJudul,
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
