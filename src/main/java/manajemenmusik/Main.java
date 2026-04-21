package manajemenmusik;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        tampilkanLogin(primaryStage);
    }

    private void tampilkanLogin(Stage stage) {
        LoginView loginView = new LoginView(() -> tampilkanDashboard(stage));
        Scene scene = new Scene(loginView.getView(), 600, 520);
        muatCSS(scene);

        stage.setTitle("MusikApp - Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private void tampilkanDashboard(Stage stage) {
        MainView mainView = new MainView(() -> tampilkanLogin(stage));
        Scene scene = new Scene(mainView.getView(), 1350, 800);
        muatCSS(scene);

        // -- New: Load data, register shortcuts --
        mainView.loadDataOtomatis();
        mainView.registerShortcuts(scene);

        // -- New: Close confirmation & Auto-save --
        stage.setOnCloseRequest(e -> {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION,
                    "Simpan data dan keluar?",
                    javafx.scene.control.ButtonType.YES, javafx.scene.control.ButtonType.NO);
            a.setTitle("Konfirmasi Keluar");
            a.setHeaderText(null);
            
            java.util.Optional<javafx.scene.control.ButtonType> res = a.showAndWait();
            if (res.isPresent() && res.get() == javafx.scene.control.ButtonType.YES) {
                mainView.saveDataOtomatis();
            } else {
                e.consume(); // Cancel close
            }
        });

        stage.setTitle("MusikApp - Manajemen Musik");
        stage.setResizable(true);
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.setMaximized(true); // Membuka jendela dalam kondisi maksimal (full screen)
        stage.show();
    }

    private void muatCSS(Scene scene) {
        java.net.URL css = getClass().getResource("/manajemenmusik/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
