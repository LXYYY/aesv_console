package ntu.aesv_console.nodes;

import java.io.FileNotFoundException;

public class WheelControllerNode extends Node {
    public WheelControllerNode(String dir, String name,
                               String ip, int port, String messageFile, String flagFile) throws FileNotFoundException {
        super(dir, name, ip, port, messageFile, flagFile);
    }

    @Override
    public void MsgReceiveCallback(String msg) {

    }

    @Override
    public String execFileName() {
        return "teleop_wheel_controller.bat";
    }
}
