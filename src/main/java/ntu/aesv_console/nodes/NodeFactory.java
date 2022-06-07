package ntu.aesv_console.nodes;

import java.io.FileNotFoundException;

public class NodeFactory {
    public final static String ip = "127.0.0.1";
    private static String messageFile = "";
    private static String flagFile = "";

    public NodeFactory() {
    }

    public Node createNode(String dir, String vehicle, String type, String name, int port) throws FileNotFoundException {
        return switch (type) {
            case "StreamReceiverNode" ->
                    new StreamReceiverNode(dir, vehicle, name, ip, port, messageFile, flagFile);
            case "IMUSynchronizerNode" ->
                    new IMUSynchronizerNode(dir, vehicle, name, ip, port, messageFile, flagFile);
            case "WheelControllerNode" ->
                    new WheelControllerNode(dir, vehicle, name, ip, port, messageFile, flagFile);
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
