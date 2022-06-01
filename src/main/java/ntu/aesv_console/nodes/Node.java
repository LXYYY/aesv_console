package ntu.aesv_console.nodes;

import ntu.aesv_console.SystemUtils;
import ntu.aesv_console.messages.MessageParser;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Thread.sleep;

public abstract class Node {

    public final String flagFile;
    public final String messageFile;
    private final String name;
    private final String ip;
    private final int port;
    protected MessageParser msgParser;
    int status;
    private Process process;
    private NodeMonitor monitor;
    private String execDir;

    public Node(String execDir, String name, String ip,
                int port, String messageFile, String flagFile) throws FileNotFoundException {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.flagFile = flagFile;
        this.status = Status.OFFLINE;
        this.process = null;
        this.messageFile = messageFile;
        this.execDir = execDir;
        msgParser = new MessageParser(messageFile);
        setStatus(Status.UNINITIALIZED);
    }

    public abstract void MsgReceiveCallback(String msg);

    public abstract String execFileName();

    private void setStatus(int status) {
        this.status = status;
    }

    public void start(String vehicle, SystemUtils.Logging logger) throws IOException {
        logger.log("Starting " + name + "...");

        String[] command = makeExecCommand(vehicle);
        process = SystemUtils.executeCommands(execDir, name,
                command);

        SocketHandler.ReceiveCallback receiveCallback = new SocketHandler.ReceiveCallback() {
            @Override
            public void onReceive(String message) {
                MsgReceiveCallback(message);
            }
        };
//        monitor = new NodeMonitor(ip, port, msgParser, receiveCallback, logger, process.getInputStream());
//        monitor.start();
        try {
            sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        setStatus(Status.RUNNING);

        logger.log("Started " + name + "...");
    }

    public void stop() throws InterruptedException {
        if (monitor != null) {
            monitor.close();
        }
        if (process != null && process.isAlive()) {
            process.destroy();
        }
        System.out.println("Stopped " + name + "...");
    }

    private String[] makeExecCommand(String vehicle) {
        return new String[]{execFileName(), vehicle, name};
    }

    public boolean checkExecExists() {
        return SystemUtils.checkFileExists(execDir,
                execFileName());
    }

    public static class Status {
        public static final int UNINITIALIZED = -1;
        public static final int OFFLINE = 0;
        public static final int STANDBY = 1;
        public static final int WARNING = 2;
        public static final int ERROR = 3;
        public static final int RUNNING = 4;
    }
}