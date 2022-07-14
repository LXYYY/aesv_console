package ntu.aesv_console.nodes;

import ntu.aesv_console.Vehicle;
import ntu.aesv_console.monitors.ProcessMonitor;

import java.io.FileNotFoundException;

public class IMUSynchronizerNode extends Node {


    public IMUSynchronizerNode(String dir,
                               Vehicle vehicle,
                               ProcessMonitor processMonitor, String name,
                               String messageFile, String flagFile) throws FileNotFoundException {
        super(dir, vehicle,processMonitor, name,
                messageFile, flagFile);
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

    @Override
    public String exeProcessName() {
        return "teleop_motion";
    }
}
