package manajemenmusik;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import manajemenmusik.controller.LoginController;
import manajemenmusik.controller.MainController;

import java.io.IOException;
import java.util.Optional;

/**
 * Main application entry point.
 * Menggunakan FXMLLoader untuk load UI (MVC Pattern).
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        tampilkanLogin(primaryStage);
    }

    private void tampilkanLogin(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manajemenmusik/view/LoginView.fxml"));
            Parent root = loader.load();
            
            // Set callback on controller
            LoginController controller = loader.getController();
            controller.setOnLoginSuccess(() -> tampilkanDashboard(stage));

            Scene scene = new Scene(root, 600, 520);
            stage.setTitle("MusikApp - Login");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanDashboard(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/manajemenmusik/view/MainView.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setOnLogout(() -> tampilkanLogin(stage));
            
            Scene scene = new Scene(root, 1350, 800);
            
            // Auto load data dan daftarkan keyboard shortcut
            controller.loadDataOtomatis();
            controller.registerShortcuts(scene);

            // Close confirmation
            stage.setOnCloseRequest(e -> {
                Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Simpan data dan keluar?", ButtonType.YES, ButtonType.NO);
                a.setTitle("Konfirmasi Keluar");
                a.setHeaderText(null);
                Optional<ButtonType> res = a.showAndWait();
                if (res.isPresent() && res.get() == ButtonType.YES) {
                    controller.saveDataOtomatis();
                } else {
                    e.consume();
                }
            });

            stage.setTitle("MusikApp - Manajemen Musik");
            stage.setResizable(true);
            stage.setMinWidth(1100);
            stage.setMinHeight(650);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
