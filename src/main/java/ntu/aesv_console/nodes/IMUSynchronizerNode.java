package ntu.aesv_console.nodes;

import java.io.FileNotFoundException;

public class IMUSynchronizerNode extends Node {


    public IMUSynchronizerNode(String dir,
                               String vehicle, String name,
                               String ip, int port, String messageFile, String flagFile) throws FileNotFoundException {
        super(dir, vehicle, name, ip, port, messageFile,
                flagFile);
    }

    @Override
    public void MsgReceiveCallback(String msg) {

    }

    @Override
    public String execScriptFile() {
        return "teleop_motion.bat";
    }

    @Override
    public String stopScriptFile() {
        return "disconnect.bat";
    }
}
