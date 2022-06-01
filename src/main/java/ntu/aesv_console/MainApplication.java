package ntu.aesv_console;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("Startup Controller");
        stage.setScene(scene);
        stage.show();
        System.out.println("Hello World!");
        StartupController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(e -> controller.shutdown());
    }

    public static void main(String[] args) {
        launch();
    }
}