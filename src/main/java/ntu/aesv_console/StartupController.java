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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ntu.aesv_console.monitors.ProcessMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StartupController {
    final ToggleGroup vehicleSelectGroup = new ToggleGroup();
    @FXML
    public TableView VehicleInfoTableView;
    NodeManager node_manager;
    ConfigParser configParser;
    String configFilePath = "config/config.json";
    String messageFilePath = "config/config.json";
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
    private Circle StreamLED;
    @FXML
    private Circle MotionLED;
    @FXML
    private Circle ControllerLED;
    @FXML
    private Label StmCPULabel;
    @FXML
    private Label StmMemLabel;
    @FXML
    private Label StmNetLabel;
    @FXML
    private Label MtnCPULabel;
    @FXML
    private Label MtnMemLabel;
    @FXML
    private Label MtnNetLabel;
    @FXML
    private Label CtrlCPULabel;
    @FXML
    private Label CtrlMemLabel;
    @FXML
    private Label CtrlNetLabel;

    @FXML
    protected void onStreamStartButtonClick() {
        NodeStartupButtonKit.VisKit viskit =
                new NodeStartupButtonKit.VisKit();
        viskit.CPU = StmCPULabel;
        viskit.mem = StmMemLabel;
        viskit.net = StmNetLabel;
        viskit.LED = StreamLED;
        NodeStartupButtonKit kit =
                new NodeStartupButtonKit(execDir,
                        vehicleManager.getCurrentVehicle(), viskit, StreamStartButton, null, "StreamReceiverNode", node_manager);
        kit.onStartButtonClick();
    }

    @FXML
    protected void onIMUStartButtonClick() {
        NodeStartupButtonKit.VisKit viskit =
                new NodeStartupButtonKit.VisKit();
        viskit.CPU = MtnCPULabel;
        viskit.mem = MtnMemLabel;
        viskit.net = MtnNetLabel;
        viskit.LED = MotionLED;
        NodeStartupButtonKit kit =
                new NodeStartupButtonKit(execDir,
                        vehicleManager.getCurrentVehicle(), viskit, IMUStartButton, null, "IMUSynchronizerNode", node_manager);
        kit.onStartButtonClick();
    }

    @FXML
    protected void onWheelControllerStartButtonClick() {
        NodeStartupButtonKit.VisKit viskit =
                new NodeStartupButtonKit.VisKit();
        viskit.CPU = CtrlCPULabel;
        viskit.mem = CtrlMemLabel;
        viskit.net = CtrlNetLabel;
        viskit.LED = ControllerLED;
        NodeStartupButtonKit kit =
                new NodeStartupButtonKit(execDir,
                        vehicleManager.getCurrentVehicle(), viskit, ControllerStartButton, null, "WheelControllerNode", node_manager);
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


        File file = null;
        if (MessageFileTextField != null) {
            file = new File(messageFilePath);

            if (file.exists()) {
                MessageFileTextField.setText(file.getAbsolutePath());
                messageFilePath = file.getAbsolutePath();
            } else {
                throw new RuntimeException("Message file not found");
            }
        }


        if (FlagFileTextField != null) {
            file = new File(configFilePath);
            if (file.exists()) {
                FlagFileTextField.setText(file.getAbsolutePath());
                configFilePath = file.getAbsolutePath();
            } else {
                throw new RuntimeException("Flag file not found");
            }
        }

        configParser = new ConfigParser(configFilePath);
        execDir = configParser.getConfig().get("exec_dir").toString();

        initNodeManager();

        vehicleManager = fxmlLoader.getController();
        vehicleManager.setExecDir(execDir);

        if (VehicleSelectCombo != null) {
            VehicleSelectCombo.setItems(vehicleManager.getVehicleNames());
            VehicleSelectCombo.getSelectionModel().select(vehicleManager.getCurrentVehicle().info.getName());
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
        for (Object key : vehicleManager.getVehicles().keySet()) {
            VehicleInfo vehicleInfo = vehicleManager.getVehicles().get(key).info;
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
            node_manager = new NodeManager(messageFilePath, configFilePath, message -> StartupLogArea.appendText(message + '\n'));
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
        public final VisKit visKit;
        public Button button;
        public Label label;
        public String node_type;
        public NodeManager nodeManager;
        public String dir;
        public Vehicle vehicle;

        public NodeStartupButtonKit(String dir,
                                    Vehicle vehicle,
                                    VisKit visKit,
                                    Button button,
                                    Label label,
                                    String node_type,
                                    NodeManager nodeManager) {
            this.button = button;
            this.label = label;
            this.node_type = node_type;
            this.nodeManager = nodeManager;
            this.dir = dir;
            this.vehicle = vehicle;
            this.visKit = visKit;
        }

        public void onStartButtonClick() {
            if (button.getText().equals("Start")) {
//                button.setText("Starting...");
//                progressIndicator.setProgress(0.5);

                try {
                    ProcessMonitor processMonitor =
                            new ProcessMonitor(new ProcessMonitor.Visualizer() {
                                @Override
                                public void showExist(boolean exists) {
                                    if (exists) {
                                        visKit.LED.setFill(Color.GREEN);
                                    } else {
                                        visKit.LED.setFill(Color.RED);
                                    }
                                }

                                @Override
                                public void showCPU(String cpu) {
                                    visKit.CPU.setText(cpu);
                                }

                                @Override
                                public void showMem(String mem) {
                                    visKit.mem.setText(mem);
                                }

                                @Override
                                public void showNet(String net) {
                                    visKit.net.setText(net);
                                }
                            });
                    nodeManager.startNode(dir, vehicle, processMonitor, node_type);
                } catch (IOException e) {
                    e.printStackTrace();
                    WindowUtils.showExceptionInAlert(e);
                }

//                button.setText("Started");
//                button.setStyle("-fx-background-color: green");
//                progressIndicator.setProgress(1);
            } else if (button.getText().equals("Started")) {
                button.setText("Stopping...");
            }
        }

        public static class VisKit {
            public Circle LED;
            public Label CPU;
            public Label mem;
            public Label net;
        }
    }
}