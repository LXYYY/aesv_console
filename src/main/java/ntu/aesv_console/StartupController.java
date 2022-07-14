package ntu.aesv_console;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StartupController {
    final ToggleGroup vehicleSelectGroup = new ToggleGroup();
    @FXML
    public TableView VehicleInfoTableView;
    NodeManager node_manager;
    ConfigParser configParser;
    private String execDir;
    @FXML
    private Button StreamStartButton;
    @FXML
    private ProgressIndicator StreamStartProgressIndicator;
    @FXML
    private TextField MessageFileTextField;
    @FXML
    private Button MessageFileChooseButton;
    @FXML
    private TextArea StartupLogArea;
    @FXML
    private Button FinishStartupButton;
    @FXML
    private TextField FlagFileTextField;
    @FXML
    private ComboBox<String> VehicleSelectCombo;
    private Stage VehicleManagerStage;
    @FXML
    private VBox VehicleIconListVBox;
    private VehicleManager vehicleManager;
    @FXML
    private Button IMUStartButton;
    @FXML
    private ProgressIndicator IMUStartProgressIndicator;
    @FXML
    private Button ControllerStartButton;
    @FXML
    private ProgressIndicator ControllerStartProgressIndicator;

    @FXML
    protected void onStreamStartButtonClick() {
        NodeStartupButtonKit kit = new NodeStartupButtonKit(execDir, vehicleManager.getCurrentVehicle(), StreamStartButton, StreamStartProgressIndicator, null, "StreamReceiver", "StreamReceiverNode", 2000, node_manager);
        kit.onStartButtonClick();
    }

    @FXML
    protected void onIMUStartButtonClick() {
        NodeStartupButtonKit kit = new NodeStartupButtonKit(execDir, vehicleManager.getCurrentVehicle(), IMUStartButton, IMUStartProgressIndicator, null, "IMU", "IMUSynchronizerNode", 2001, node_manager);
        kit.onStartButtonClick();
    }

    @FXML
    protected void onWheelControllerStartButtonClick() {
        NodeStartupButtonKit kit = new NodeStartupButtonKit(execDir, vehicleManager.getCurrentVehicle(), ControllerStartButton, ControllerStartProgressIndicator, null, "IMU", "WheelControllerNode", 2002, node_manager);
        kit.onStartButtonClick();
    }

    @FXML
    protected void onMessageFileChooseButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Message File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            MessageFileTextField.setText(file.getAbsolutePath());
        }
        initNodeManager();
    }


    @FXML
    protected void onFinishStartupButtonClick() {

    }


    @FXML
    protected void onVehicleManagerButtonClick() {
        VehicleManagerStage.show();
        System.out.println("Vehicle Manager");
    }


    @FXML
    protected void onVehicleSelectComboBoxClick(ActionEvent event) {
        String vehicle = VehicleSelectCombo.getValue();
        if (vehicle != null) {
            vehicleManager.setCurrentVehicle(vehicle);
        }
    }

    public void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("vehicle-manager.fxml"));
        Parent root = (Parent) fxmlLoader.load();
//        Parent root= FXMLLoader.load(getClass().getResource("vehicle-manager.fxml"));
        VehicleManagerStage = new Stage();
        VehicleManagerStage.setTitle("Vehicle Manager");
        VehicleManagerStage.setScene(new Scene(root, 600, 400));


        File file = new File(MessageFileTextField.getText());

        if (file.exists()) {
            MessageFileTextField.setText(file.getAbsolutePath());
        } else {
            throw new RuntimeException("Message file not found");
        }

        file = new File(FlagFileTextField.getText());
        if (file.exists()) {
            FlagFileTextField.setText(file.getAbsolutePath());
        } else {
            throw new RuntimeException("Flag file not found");
        }

        configParser = new ConfigParser(FlagFileTextField.getText());
        execDir = configParser.getConfig().get("exec_dir").toString();

        initNodeManager();

        vehicleManager = fxmlLoader.getController();
        vehicleManager.setExecDir(execDir);

        if (VehicleSelectCombo != null) {
            VehicleSelectCombo.setItems(vehicleManager.getVehicles());
            VehicleSelectCombo.getSelectionModel().select(vehicleManager.getCurrentVehicle());
        }


        // set vehicle select group listener
        vehicleSelectGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
                if (vehicleSelectGroup.getSelectedToggle() != null) {
                    vehicleManager.setCurrentVehicle(vehicleSelectGroup.getSelectedToggle().getUserData().toString());
                }
            }
        });

        // for all vehiclesInfo
        for (Object key : vehicleManager.getVehiclesInfo().keySet()) {
            VehicleInfo vehicleInfo = (VehicleInfo) vehicleManager.getVehiclesInfo().get(key);
            RadioButton radioButton = new RadioButton(vehicleInfo.getName());
            radioButton.setUserData(vehicleInfo.getName());
            radioButton.setToggleGroup(vehicleSelectGroup);

            if (vehicleInfo.getName().equals(vehicleManager.getCurrentVehicle())) {
                radioButton.setSelected(true);
            }

            vehicleInfo.setComboBox(VehicleSelectCombo);
            vehicleInfo.setRadioButton(radioButton);
            vehicleInfo.setVehicleInfoTable(VehicleInfoTableView);

            // set radio button icon
            String iconPath = vehicleInfo.getIconFile();
            System.out.println("iconPath: " + iconPath);
            try {
                Image image = new Image(new FileInputStream(iconPath));
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                radioButton.setGraphic(imageView);
            } catch (Exception e) {
                System.out.println("Error loading icon: " + e.getMessage());
            }
            VehicleIconListVBox.getChildren().add(radioButton);
        }

        // hide veh info table header row
        VehicleInfoTableView.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                Pane header = (Pane) VehicleInfoTableView.lookup("TableHeaderRow");
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
            }
        });


        // make sure gui is showing the current vehicle
        vehicleManager.setCurrentVehicle(vehicleManager.getDefaultVehicle());
    }


    private void initNodeManager() {
        try {
            node_manager = new NodeManager(MessageFileTextField.getText(), FlagFileTextField.getText(), message -> StartupLogArea.appendText(message + '\n'));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void disconnect() {
        // consider a global dc script
//        try {
//            SystemUtils.executeCommands(execDir, "StopAll", "disconnect.bat");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public void shutdown() {
        System.out.println("Shutting down all nodes...");
        node_manager.stopAllNodes();
        disconnect();
    }

    @FXML
    public void onStopAllButtonClick() {
        shutdown();
    }

    @FXML
    protected void exitApplication() {
        shutdown();
        System.exit(0);
    }

    public static class NodeStartupButtonKit {
        public final int port;
        public Button button;
        public ProgressIndicator progressIndicator;
        public Label label;
        public String node_name;
        public String node_type;
        public NodeManager nodeManager;
        public String dir;
        public String vehicle;

        public NodeStartupButtonKit(String dir, String vehicle, Button button, ProgressIndicator progressIndicator, Label label, String node_type, String node_name, int port, NodeManager nodeManager) {
            this.button = button;
            this.progressIndicator = progressIndicator;
            this.label = label;
            this.node_name = node_name;
            this.node_type = node_type;
            this.nodeManager = nodeManager;
            this.port = port;
            this.dir = dir;
            this.vehicle = vehicle;
        }

        public void onStartButtonClick() {
            if (button.getText().equals("Start")) {
//                button.setText("Starting...");
//                progressIndicator.setProgress(0.5);

                try {
                    nodeManager.startNode(dir, vehicle, node_name, node_type, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    WindowUtils.showExceptionInAlert(e);
                    return;
                }

//                button.setText("Started");
//                button.setStyle("-fx-background-color: green");
//                progressIndicator.setProgress(1);
            } else if (button.getText().equals("Started")) {
                button.setText("Stopping...");
            }
        }
    }
}