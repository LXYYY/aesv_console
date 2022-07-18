package ntu.aesv_console;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        // get screens
        Rectangle2D screenBounds =
                Screen.getPrimary().getVisualBounds();
        if (Screen.getScreens().size() > 1) {
            for (Screen screen : Screen.getScreens()) {
                if (screen.getVisualBounds().getMinX() != Screen.getPrimary().getVisualBounds().getMinX()
                        || screen.getVisualBounds().getMinY() != Screen.getPrimary().getVisualBounds().getMinY()) {
                    screenBounds = screen.getVisualBounds();
                    break;
                }
            }
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("hello-view.fxml"));
        Pane root = fxmlLoader.load();
        Scene scene = new Scene(root, 720, 405);
        stage.setTitle("Startup Controller");
        stage.setScene(scene);
//        stage.setFullScreen(true);
        System.out.println("Hello World!");
        StartupController controller = fxmlLoader.getController();
        stage.setOnCloseRequest(e -> controller.shutdown());

        final double initWidth = scene.getWidth();
        final double initHeight = scene.getHeight();
//        final double ratio = initWidth / initHeight;
        final double ratio = initWidth / initHeight;

        SceneSizeChangeListener sizeListener =
                new SceneSizeChangeListener(scene, ratio,
                        initHeight, initWidth, root);
        scene.widthProperty().addListener(sizeListener);
        scene.heightProperty().addListener(sizeListener);

        stage.setX(screenBounds.getMinX()+100);
        stage.setY(screenBounds.getMinY()+100);
//        stage.setWidth(screenBounds.getWidth());
//        stage.setHeight(screenBounds.getHeight());

        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);

        stage.show();
    }

    private static class SceneSizeChangeListener implements ChangeListener<Number> {
        private final Scene scene;
        private final double ratio;
        private final double initHeight;
        private final double initWidth;
        private final Pane contentPane;

        public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth, Pane contentPane) {
            this.scene = scene;
            this.ratio = ratio;
            this.initHeight = initHeight;
            this.initWidth = initWidth;
            this.contentPane = contentPane;
        }

        @Override
        public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
            final double newWidth = scene.getWidth();
            final double newHeight = scene.getHeight();

            double scaleFactor =
                    newWidth / newHeight > ratio
                            ? newHeight / initHeight
                            : newWidth / initWidth;

            if (scaleFactor >= 1) {
                Scale scale = new Scale(scaleFactor, scaleFactor);
                scale.setPivotX(0);
                scale.setPivotY(0);
                scene.getRoot().getTransforms().setAll(scale);

                contentPane.setPrefWidth(newWidth / scaleFactor);
                contentPane.setPrefHeight(newHeight / scaleFactor);
            } else {
                contentPane.setPrefWidth(Math.max(initWidth, newWidth));
                contentPane.setPrefHeight(Math.max(initHeight, newHeight));
            }
        }
    }
}