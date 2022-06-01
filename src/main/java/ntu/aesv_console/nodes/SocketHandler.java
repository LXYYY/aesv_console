package ntu.aesv_console.nodes;

import ntu.aesv_console.messages.MessageParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class SocketHandler {
    private Socket socket;
    private ServerSocket serverSocket;
    private final String host;
    private final int port;
    private boolean exit;

    private PrintWriter out;
    private BufferedReader in;

    private MessageParser messageParser;

    public static interface ReceiveCallback {
        public void onReceive(String message);
    }

    private ReceiveCallback receiveCallback;

    private static class ReceiveThread extends Thread {
        private BufferedReader in;
        private final ReceiveCallback callback;

        public ReceiveThread(BufferedReader in, ReceiveCallback callback) {
            this.in = in;
            this.callback = callback;
        }

        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String msg = in.readLine();
                    if (!msg.isEmpty()) {
                        callback.onReceive(msg);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ReceiveThread receiveThread;

    public SocketHandler(String host, int port, MessageParser messageParser, ReceiveCallback receiveCallback) {
        this.host = host;
        this.port = port;
        this.messageParser = messageParser;
        this.receiveCallback = receiveCallback;
        this.exit = false;
        this.receiveThread = null;
    }

    private String connect() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.socket = serverSocket.accept();
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("Connected to " + host + ":" + port + " " + socket.isConnected());
        } catch (Exception e) {
            return e.toString();
        }
        return null;
    }

    public String readLine() {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void connectAndWaitForStandby() {
        while (!exit && socket == null || !socket.isConnected()) {
            String errStr = connect();
            if (errStr != null) {
                errStr = errStr + host + " " + port;
                System.out.println(errStr);
            }
        }

        String msgStr = readLine();
        while (!exit && !Objects.equals(msgStr, messageParser.getHeader("standby"))) {
            try {
                msgStr = readLine();
                if (msgStr != null)
                    System.out.println(msgStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.setReceiveCallback(receiveCallback);

        System.out.println("Standby received");
    }

    public void readHeader() {
    }

    public void disconnect() {
        try {
            if (socket != null && socket.isConnected())
                socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        exit = true;
        if (receiveThread != null) {
            this.receiveThread.interrupt();
        }
        disconnect();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public int send(String message) {
        try {
            socket.getOutputStream().write(message.getBytes());
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void setReceiveCallback(ReceiveCallback callback) {
        receiveThread = new ReceiveThread(in, callback);
        receiveThread.start();
    }

}
