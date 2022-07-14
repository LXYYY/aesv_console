package ntu.aesv_console.nodes;

import ntu.aesv_console.SystemUtils;
import ntu.aesv_console.Vehicle;
import ntu.aesv_console.messages.MessageParser;
import ntu.aesv_console.monitors.ProcessMonitor;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.Thread.sleep;

public abstract class Node {

    public final String flagFile;
    public final String messageFile;
    private final String name;
    private final ProcessMonitor processMonitor;
    protected MessageParser msgParser;
    int status;
    private NodeMonitor monitor;
    private String execDir;
    private Vehicle vehicle;
    private SystemUtils.Logging logger;

    public Node(String execDir, Vehicle vehicle,
                ProcessMonitor processMonitor, String name,
                String messageFile, String flagFile) throws FileNotFoundException {
        this.name = name;
        this.flagFile = flagFile;
        this.status = Status.OFFLINE;
        this.messageFile = messageFile;
        this.execDir = execDir;
        this.vehicle = vehicle;
        this.processMonitor = processMonitor;
        processMonitor.setProcessName(exeProcessName());
        msgParser = new MessageParser(messageFile);
        setStatus(Status.UNINITIALIZED);
    }

    public final ProcessMonitor getProcessMonitor() {
        return processMonitor;
    }

    public void setLogger(SystemUtils.Logging logger) {
        this.logger = logger;
        this.processMonitor.setLogger(logger);
    }

    public final Vehicle getVehicle() {
        return vehicle;
    }

    public abstract void MsgReceiveCallback(String msg);

    public abstract String execScriptFile();

    public abstract String stopScriptFile();

    public abstract String exeProcessName();

    private void setStatus(int status) {
        this.status = status;
    }

    public void start() throws IOException {
        logger.log("Starting " + name + "...");

        String[] command = makeExecCommand();
        java.lang.Process scriptProcess =
                SystemUtils.executeCommands(execDir, name,
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

        processMonitor.start();

        logger.log("Started " + name + "...");
    }

    public void stop() throws InterruptedException, IOException {
        String[] stopCommand = makeStopCommand();
        SystemUtils.executeCommands(execDir, name, stopCommand);
        if (monitor != null) {
            monitor.close();
        }
        if (processMonitor != null) {
            processMonitor.stop();
        }
        System.out.println("Stopped " + name + "...");
    }

    private String[] makeExecCommand() {
        return new String[]{"cmd", "/c", execScriptFile(),
                vehicle.info.getName(), name};
    }

    private String[] makeStopCommand() {
        return new String[]{"cmd", "/c", stopScriptFile(),
                vehicle.info.getName(), name};
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
