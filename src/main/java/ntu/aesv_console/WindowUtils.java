package ntu.aesv_console;

import javafx.scene.control.Alert;

public class WindowUtils {

    public static void showExceptionInAlert(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error Starting Node");
        alert.setContentText("Error starting node: " + e.getMessage());
        alert.showAndWait();
    }
}
