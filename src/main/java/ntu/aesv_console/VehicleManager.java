package ntu.aesv_console;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VehicleManager {
    @FXML
    private ListView VehicleList;

    @FXML
    private TextField VehicleNameTextField;

    @FXML
    private TextField VehicleIPTextField;

    private ObservableList<String> vehicleNames;

    private ConfigParser configParser;

    private Map vehicleConfig;

    private Map<String, Vehicle> vehicles;

    private Vehicle currentVehicle;

    private String execDir;

    public Map<String, Vehicle> getVehicles() {
        return vehicles;
    }

    public ObservableList<String> getVehicleNames() {
        return vehicleNames;
    }

    @FXML
    private void initialize() {
        vehicleNames = FXCollections.observableArrayList();
        vehicles = new HashMap<String, Vehicle>();

        configParser = new ConfigParser("config/vehicle_config.yaml");
        vehicleConfig = configParser.getConfig();
        // print out the key and value pairs
        for (Object key : vehicleConfig.keySet()) {
            if (key.toString().equals("default")) {
                continue;
            }
            System.out.println(key + ": " + vehicleConfig.get(key));
            vehicleNames.add((String) key);
            Map data = (Map) vehicleConfig.get(key);
            VehicleInfo vehicleInfo = new VehicleInfo(key.toString(), data.get("ip").toString(), data.get("icon").toString());
            vehicles.put(key.toString(), new Vehicle(vehicleInfo));
        }

        VehicleList.setItems(vehicleNames);
        System.out.println("VehicleManager initialized");
        VehicleList.setCellFactory(ComboBoxListCell.forListView(vehicles));

        VehicleList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> ov,
                                        String old_val, String new_val) {
                        VehicleNameTextField.setText(new_val);
                        Map vehicle = (Map) vehicleConfig.get(new_val);
                        VehicleIPTextField.setText(vehicle.get("ip").toString());
                    }
                });

        currentVehicle = getDefaultVehicle();
    }

    public void setExecDir(String execDir) {
        this.execDir = execDir;
    }

    public Vehicle getDefaultVehicle() {
        return vehicles.get(vehicleConfig.get("default").toString());
    }

    public Vehicle getCurrentVehicle() {
        return currentVehicle;
    }

    public boolean setCurrentVehicle(String vehicleName) {
        return setCurrentVehicle(vehicles.get(vehicleName));

    }

    public boolean setCurrentVehicle(Vehicle vehicle) {
        assert vehicles.containsKey(vehicle.info.getName());
        if (vehicles.containsKey(vehicle.info.getName())) {
            currentVehicle = vehicle;
            currentVehicle.setAsSelected();
            try {
                currentVehicle.syncVehicleIPWithTxt(execDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Current vehicle set to " + currentVehicle.info.getName());
            return true;
        }
        return false;
    }
}
