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
    private String vehicle;

    public Node(String execDir, String vehicle, String name,
                String ip,
                int port, String messageFile, String flagFile) throws FileNotFoundException {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.flagFile = flagFile;
        this.status = Status.OFFLINE;
        this.process = null;
        this.messageFile = messageFile;
        this.execDir = execDir;
        this.vehicle = vehicle;
        msgParser = new MessageParser(messageFile);
        setStatus(Status.UNINITIALIZED);
    }

    public abstract void MsgReceiveCallback(String msg);

    public abstract String execScriptFile();

    public abstract String stopScriptFile();

    private void setStatus(int status) {
        this.status = status;
    }

    public void start(SystemUtils.Logging logger) throws IOException {
        logger.log("Starting " + name + "...");

        String[] command = makeExecCommand();
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

    public void stop() throws InterruptedException, IOException {
        String[] stopCommand = makeStopCommand();
        SystemUtils.executeCommands(execDir, name, stopCommand);
        if (monitor != null) {
            monitor.close();
        }
        if (process != null && process.isAlive()) {
            process.destroy();
        }
        System.out.println("Stopped " + name + "...");
    }

    private String[] makeExecCommand() {
        return new String[]{"cmd", "/c", execScriptFile(),
                vehicle, name};
    }

    private String[] makeStopCommand() {
        return new String[]{"cmd", "/c", stopScriptFile(),
                vehicle, name};
    }

    public boolean checkExecExists() {
        return SystemUtils.checkFileExists(execDir,
                execScriptFile());
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
