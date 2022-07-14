package ntu.aesv_console;

import ntu.aesv_console.monitors.ProcessMonitor;
import ntu.aesv_console.nodes.Node;
import ntu.aesv_console.nodes.NodeFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NodeManager {
    private final NodeFactory nodeFactory;
    private final SystemUtils.Logging logger;
    Map<String, Node> nodes;

    public NodeManager(String messageFile, String flagFile, SystemUtils.Logging logger) throws IOException {
        this.nodeFactory = new NodeFactory();
        File tmpMessageFile = new File(messageFile);
        if (!tmpMessageFile.exists()) {
            throw new FileNotFoundException("Message file not found: " + messageFile);
        }
        this.nodeFactory.setMessageFile(messageFile);
        this.nodeFactory.setFlagFile(flagFile);
        this.nodes = new HashMap<>();
        this.logger = logger;
    }

    public void startNode(String dir, Vehicle vehicle,
                          ProcessMonitor processMonitor,
                          String type) throws IOException {
        logger.log("Starting node: " + type);
        Node node = nodeFactory.createNode(dir, vehicle,
                processMonitor,
                type);
        if (node == null) {
            throw new RuntimeException(type + ": Node not found");
        }
        if (!node.checkExecExists()) {
            throw new RuntimeException("Node executable " +
                    "not found: " + node.execScriptFile());
        }
        node.setLogger(logger);
        node.start();
        nodes.put(type, node);
    }

    public void stopAllNodes() {
        for (Node node : nodes.values()) {
            try {
                node.stop();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        nodes.clear();
    }
}
