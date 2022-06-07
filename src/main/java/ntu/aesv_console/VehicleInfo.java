package ntu.aesv_console;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class VehicleInfo {
    private SimpleStringProperty name;
    private SimpleStringProperty iconFile;
    private SimpleStringProperty ip;
    private RadioButton radioButton;
    private ComboBox<String> comboBox;
    private TableView<Property> VehicleInfoTable;

    public VehicleInfo(String name, String ip, String iconFile) {
        this.name = new SimpleStringProperty(name);
        this.ip = new SimpleStringProperty(ip);
        this.iconFile = new SimpleStringProperty(iconFile);
        this.radioButton = new RadioButton(name);
        this.comboBox = new ComboBox<String>();
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getIconFile() {
        return iconFile.get();
    }

    public void setIconFile(String iconFile) {
        this.iconFile.set(iconFile);
    }

    public String getIP() {
        return ip.get();
    }

    public void setIP(String ip) {
        this.ip.set(ip);
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public void setComboBox(ComboBox<String> comboBox) {
        this.comboBox = comboBox;
    }

    public void setVehicleInfoTable(TableView<Property> vehicleInfoTable) {
        VehicleInfoTable = vehicleInfoTable;
    }

    public ObservableList<Property> getVehicleInfo() {
        List<Property> props = new ArrayList<>();
        props.add(new Property("Name", getName()));
        props.add(new Property("IP", getIP()));
        props.add(new Property("Icon", getIconFile()));
        return FXCollections.observableArrayList(props);
    }

    public void setAsSelected() {
        radioButton.setSelected(true);
        if (comboBox != null) {
            comboBox.getSelectionModel().select(getName());
        }
        if (VehicleInfoTable != null) {
            VehicleInfoTable.getColumns().clear();
            VehicleInfoTable.getItems().clear();

            ObservableList<Property> props = getVehicleInfo();
            TableColumn keyColumn = new TableColumn("Key");
            keyColumn.setCellValueFactory(new PropertyValueFactory<>("Key"));
            VehicleInfoTable.getColumns().add(keyColumn);

            TableColumn valueColumn = new TableColumn(
                    "Value");
            valueColumn.setCellValueFactory(new PropertyValueFactory<>("Value"));
            VehicleInfoTable.getColumns().add(valueColumn);

            VehicleInfoTable.setItems(props);
        }
    }

    public void syncVehicleIPWithTxt(String dir) throws IOException {
        File ipTxt = new File(Paths.get(dir, getName() + "IP.txt").toString());
        System.out.println(ipTxt.getAbsolutePath());
        try {
            if (ipTxt.createNewFile()) {
                WindowUtils.showExceptionInAlert(new Exception("Vehicle IP txt doesn't exist, " + "created a new one \n" + ipTxt.getAbsolutePath()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read ip from txt
        String ipFromTxt = "";
        try {
            // read ipTxt
            FileReader fileReader = new FileReader(ipTxt);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            ipFromTxt = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!ipFromTxt.equals(getIP())) {
            // write ip to txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ipTxt))) {
                writer.write(getIP());
            }
            WindowUtils.showExceptionInAlert(new Exception(getName() + " IP txt doesn't match, " + "overriding ip from " + ipFromTxt + " to " + ip));
        }
    }

    public static class Property {
        private SimpleStringProperty key;
        private SimpleStringProperty value;

        public Property(String key, String value) {
            this.key = new SimpleStringProperty(key);
            this.value = new SimpleStringProperty(value);
        }

        public String getKey() {
            return key.get();
        }

        public void setKey(String key) {
            this.key.set(key);
        }

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.get();
        }
    }
}
