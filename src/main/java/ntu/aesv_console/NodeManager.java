package ntu.aesv_console;

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

    public void startNode(String dir, String vehicle, String type, String name, Integer port) throws IOException {
        logger.log("Starting node: " + name);
        Node node = nodeFactory.createNode(dir, type, name,
                port);
        if (node == null) {
            throw new RuntimeException(type + ": Node not found");
        }
        if (!node.checkExecExists()) {
            throw new RuntimeException("Node executable " +
                    "not found: " + node.execFileName());
        }
        node.start(vehicle, logger);
        nodes.put(name, node);
    }

    public void stopAllNodes() {
        for (Node node : nodes.values()) {
            try {
                node.stop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
