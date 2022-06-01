package ntu.aesv_console;

import ntu.aesv_console.ConfigParser;
import ntu.aesv_console.VehicleInfo;
import ntu.aesv_console.WindowUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxListCell;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class VehicleManager {
    @FXML
    private ListView VehicleList;

    @FXML
    private TextField VehicleNameTextField;

    @FXML
    private TextField VehicleIPTextField;

    private ObservableList<String> vehicles;

    private ConfigParser configParser;

    private Map vehicleConfig;

    private Map vehiclesInfo;

    private String currentVehicle;

    private String execDir;

    public Map getVehiclesInfo() {
        return vehiclesInfo;
    }

    public ObservableList<String> getVehicles() {
        return vehicles;
    }

    @FXML
    private void initialize() {
        vehicles = FXCollections.observableArrayList();
        vehiclesInfo = new HashMap<String, VehicleInfo>();

        configParser = new ConfigParser("config/vehicle_config.yaml");
        vehicleConfig = configParser.getConfig();
        // print out the key and value pairs
        for (Object key : vehicleConfig.keySet()) {
            if (key.toString().equals("default")) {
                continue;
            }
            System.out.println(key + ": " + vehicleConfig.get(key));
            vehicles.add((String) key);
            Map data = (Map) vehicleConfig.get(key);
            VehicleInfo vehicleInfo = new VehicleInfo(key.toString(), data.get("ip").toString(), data.get("icon").toString());
            vehiclesInfo.put(key.toString(), vehicleInfo);
        }

        VehicleList.setItems(vehicles);
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

    public String getDefaultVehicle() {
        return vehicleConfig.get("default").toString();
    }

    public String getCurrentVehicle() {
        return currentVehicle;
    }

    public Boolean setCurrentVehicle(String vehicle) {
        if (vehicles.contains(vehicle)) {
            currentVehicle = vehicle;
            ((VehicleInfo) vehiclesInfo.get(vehicle)).setAsSelected();
            try {
                ((VehicleInfo) vehiclesInfo.get(vehicle)).syncVehicleIPWithTxt(execDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Current vehicle set to " + currentVehicle);
            return true;
        }
        return false;
    }
}
