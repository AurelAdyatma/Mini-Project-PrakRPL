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
        Scene scene = new Scene(loginView.getView(), 400, 300);

        stage.setTitle("Login - Manajemen Musik");
        stage.setScene(scene);
        stage.show();
    }

    private void tampilkanDashboard(Stage stage) {
        MainView mainView = new MainView();
        Scene scene = new Scene(mainView.getView(), 950, 550);

        stage.setTitle("Aplikasi Manajemen Musik");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}