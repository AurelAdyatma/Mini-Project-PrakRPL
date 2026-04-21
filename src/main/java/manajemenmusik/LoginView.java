package manajemenmusik;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class LoginView {
    private final VBox root;
    private final TextField tfUsername;
    private final PasswordField pfPassword;
    private final Runnable onLoginSuccess;

    public LoginView(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;

        root = new VBox();
        root.getStyleClass().add("login-bg");
        root.setAlignment(Pos.CENTER);
        root.setFillWidth(true);

        VBox card = new VBox(18);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(44, 44, 44, 44));
        card.setMaxWidth(420);

        // ---- Logo & branding ----
        Label logo = new Label("🎵");
        logo.getStyleClass().add("login-logo");

        Label brand = new Label("MusikApp");
        brand.getStyleClass().add("login-brand");

        Label title = new Label("Selamat Datang Kembali");
        title.getStyleClass().add("login-title");

        Label subtitle = new Label("Masuk untuk mengelola koleksi musikmu");
        subtitle.getStyleClass().add("login-subtitle");

        // ---- Spacer ----
        Region sp1 = new Region();
        sp1.setPrefHeight(8);

        // ---- Username ----
        Label lblUser = new Label("USERNAME");
        lblUser.getStyleClass().add("label-field");

        tfUsername = new TextField();
        tfUsername.setPromptText("Masukkan username...");
        tfUsername.getStyleClass().add("text-field");
        tfUsername.setPrefHeight(46);
        tfUsername.setMaxWidth(Double.MAX_VALUE);

        // ---- Password ----
        Label lblPass = new Label("PASSWORD");
        lblPass.getStyleClass().add("label-field");

        pfPassword = new PasswordField();
        pfPassword.setPromptText("Masukkan password...");
        pfPassword.getStyleClass().add("password-field");
        pfPassword.setPrefHeight(46);
        pfPassword.setMaxWidth(Double.MAX_VALUE);

        // ---- Login button ----
        Button btnLogin = new Button("Masuk");
        btnLogin.getStyleClass().add("btn-login");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setOnAction(e -> prosesLogin());
        pfPassword.setOnAction(e -> prosesLogin());
        tfUsername.setOnAction(e -> pfPassword.requestFocus());

        // ---- Hint ----
        Label hint = new Label("Login default: admin / 12345");
        hint.getStyleClass().add("login-hint");

        card.getChildren().addAll(
                logo, brand, title, subtitle, sp1,
                lblUser, tfUsername,
                lblPass, pfPassword,
                btnLogin, hint
        );

        VBox wrapper = new VBox(card);
        wrapper.setAlignment(Pos.CENTER);
        VBox.setVgrow(card, Priority.NEVER);

        root.getChildren().add(wrapper);
        VBox.setVgrow(wrapper, Priority.ALWAYS);
    }

    private void prosesLogin() {
        String user = tfUsername.getText().trim();
        String pass = pfPassword.getText().trim();
        if ("admin".equals(user) && "12345".equals(pass)) {
            onLoginSuccess.run();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Gagal");
            alert.setHeaderText(null);
            alert.setContentText("Username atau password salah.");
            alert.showAndWait();
        }
    }

    public Parent getView() {
        return root;
    }
}
