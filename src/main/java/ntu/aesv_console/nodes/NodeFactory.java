package ntu.aesv_console.nodes;

import ntu.aesv_console.Vehicle;
import ntu.aesv_console.monitors.ProcessMonitor;

import java.io.FileNotFoundException;

public class NodeFactory {
    public final static String ip = "127.0.0.1";
    private static String messageFile = "";
    private static String flagFile = "";

    public NodeFactory() {
    }

    public Node createNode(String dir, Vehicle vehicle,
                           ProcessMonitor processMonitor,
                           String type) throws FileNotFoundException {
        return switch (type) {
            case "StreamReceiverNode" ->
                    new StreamReceiverNode(dir, vehicle,
                            processMonitor, type,
                            messageFile,
                            flagFile);
            case "IMUSynchronizerNode" ->
                    new IMUSynchronizerNode(dir, vehicle,
                            processMonitor, type,
                            messageFile,
                            flagFile);
            case "WheelControllerNode" ->
                    new WheelControllerNode(dir, vehicle,
                            processMonitor, type,
                            messageFile,
                            flagFile);
            default -> null;
        };
    }

    public void setMessageFile(String file) {
        messageFile = file;
    }

    public void setFlagFile(String file) {
        flagFile = file;
    }
}
