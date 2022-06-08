package ntu.aesv_console;

import java.io.IOException;

public class Vehicle {
    public final VehicleInfo info;

    public Vehicle(VehicleInfo info) {
        this.info = info;
    }

    public void setAsSelected() {
        info.setAsSelected();
    }

    public void syncVehicleIPWithTxt(String execDir) throws IOException {
        info.syncVehicleIPWithTxt(execDir);
    }
}
