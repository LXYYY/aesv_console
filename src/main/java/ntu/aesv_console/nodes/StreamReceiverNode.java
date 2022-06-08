package ntu.aesv_console.nodes;

import ntu.aesv_console.Vehicle;

import java.io.FileNotFoundException;

public class StreamReceiverNode extends Node {


    public StreamReceiverNode(String dir,
                              Vehicle vehicle, String name,
                              String ip, int port, String messageFile, String flagFile) throws FileNotFoundException {
        super(dir, vehicle, name, ip, port, messageFile,
                flagFile);
    }

    @Override
    public void MsgReceiveCallback(String msg) {

    }

    @Override
    public String execScriptFile() {
        return "stream_receiver.bat";
    }

    @Override
    public String stopScriptFile() {
        return "disconnect.bat";
    }

    @Override
    public String exeProcessName() {
        return getVehicle().info.getName() + "_receiver";
    }
}
