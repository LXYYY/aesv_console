package ntu.aesv_console;

import javafx.scene.control.*;

import java.io.*;
import java.nio.file.Paths;

public class VehicleInfo {
    private String name;
    private String ip;
    private String iconFile;

    private RadioButton radioButton;
    private ComboBox<String> comboBox;

    public VehicleInfo(String name, String ip, String iconFile) {
        this.name = name;
        this.ip = ip;
        this.iconFile = iconFile;
        this.radioButton = new RadioButton(name);
        this.comboBox = new ComboBox<String>();
    }

    public void setRadioButton(RadioButton radioButton) {
        this.radioButton = radioButton;
    }

    public RadioButton getRadioButton() {
        return radioButton;
    }

    public void setComboBox(ComboBox<String> comboBox) {
        this.comboBox = comboBox;
    }

    public ComboBox<String> getComboBox() {
        return comboBox;
    }

    public void setAsSelected() {
        radioButton.setSelected(true);
        comboBox.getSelectionModel().select(name);
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return iconFile;
    }

    public void syncVehicleIPWithTxt(String dir) throws IOException {
        File ipTxt = new File(Paths.get(dir,
                name + "IP.txt").toString());
        System.out.println(ipTxt.getAbsolutePath());
        try {
            if (ipTxt.createNewFile()) {
                WindowUtils.showExceptionInAlert(new Exception(
                        "Vehicle IP txt doesn't exist, " +
                                "created a new one \n" +
                                ipTxt.getAbsolutePath()));
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

        if (!ipFromTxt.equals(ip)) {
            // write ip to txt
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(ipTxt))) {
                writer.write(ip);
            }
            WindowUtils.showExceptionInAlert(new Exception(
                    name + " IP txt doesn't match, " +
                            "overriding ip from " + ipFromTxt + " to " + ip));
        }
    }
}
