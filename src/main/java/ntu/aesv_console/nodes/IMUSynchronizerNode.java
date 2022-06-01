package ntu.aesv_console.nodes;

import java.io.FileNotFoundException;

public class IMUSynchronizerNode extends Node {


    public IMUSynchronizerNode(String dir, String name,
                               String ip, int port, String messageFile, String flagFile) throws FileNotFoundException {
        super(dir, name, ip, port, messageFile, flagFile);
    }

    @Override
    public void MsgReceiveCallback(String msg) {

    }

    @Override
    public String execFileName() {
        return "teleop_motion.bat";
    }
}
