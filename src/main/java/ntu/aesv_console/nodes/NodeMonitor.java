package ntu.aesv_console.nodes;

import ntu.aesv_console.SystemUtils;
import ntu.aesv_console.messages.MessageParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NodeMonitor extends Thread {
    private final SocketHandler handler;
    private final SystemUtils.Logging logger;
    private final InputStream stdout;
    private BufferedReader stdoutReader;
    private boolean stopRequested;

    public NodeMonitor(String host, int port, MessageParser messageParser,
                       SocketHandler.ReceiveCallback receiveCallback,
                       SystemUtils.Logging logger, InputStream stdout) {
        this.logger = logger;
        this.stdout = stdout;
        stdoutReader = new BufferedReader(new InputStreamReader(stdout));
        this.handler = new SocketHandler(host, port, messageParser, receiveCallback);
        this.stopRequested = false;
    }

    @Override
    public void run() {
        handler.connectAndWaitForStandby();
        while (!stopRequested) {
            logger.log(handler.readLine());
        }
    }

    public void close() throws InterruptedException {
        this.stopRequested = true;
        handler.close();
        this.join();
    }
}
