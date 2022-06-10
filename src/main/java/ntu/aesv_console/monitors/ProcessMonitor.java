package ntu.aesv_console.monitors;

import javafx.application.Platform;
import ntu.aesv_console.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;

public class ProcessMonitor {
    public final MonitorThread monitor;
    public SystemUtils.Logging logger;
    public Visualizer vis;
    public ProcessInfo info;

    public ProcessMonitor(Visualizer vis) {
        this.info = new ProcessInfo();
        this.monitor = new MonitorThread();
        this.logger = null;
        this.vis = vis;
    }

    public void setLogger(SystemUtils.Logging logger) {
        this.logger = logger;
    }

    public void setProcessName(String process_name) {
        this.info.name = process_name;
    }

    public void setVis(Visualizer vis) {
        this.vis = vis;
    }

    public void startMonitoring() {
        assert (this.info.name != null);
        monitor.setDaemon(true);
        monitor.start();
    }

    public void start() {
        startMonitoring();
    }

    public void stopMonitoring() {
        monitor.interrupt();
    }

    public void stop() {
        stopMonitoring();
        killProcess();
    }

    public PerfStat checkProcExistAndGetInfo() throws IOException,
            InterruptedException {
        String command = "powershell -command " +
                "\"Get-Process " + " -Name " + this.info.name +
                "\"";
        java.lang.Process process =
                Runtime.getRuntime().exec(command);
        BufferedReader reader = new BufferedReader(
                new java.io.InputStreamReader(process.getInputStream()));
        process.waitFor();
        if (process.exitValue() != 0) {
            logger.log("Process " + this.info.name + " " +
                    "does not " +
                    "exist.");
            return new PerfStat(false, 0, 0, 0);
        } else {
            reader.readLine();
            reader.readLine();
            reader.readLine();
            String line = reader.readLine();
            logger.log(line);
            String[] tokens = line.split("\\s+");
            float cpu = Float.parseFloat(tokens[5]);
            float mem = Float.parseFloat(tokens[4]);
            this.info.pid = tokens[6];
            return new PerfStat(true, cpu, mem, 0);
        }
    }

    public void killProcess() {
        assert (this.info.pid != null);
        String command = "powershell -command " +
                "\"Get-Process " + " -Id " + this.info.pid +
                " | Stop-Process " +
                "\"";
        System.out.println(command);
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void scanOnce() {
        try {
            PerfStat res = checkProcExistAndGetInfo();
            vis.update(res);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class ProcessInfo {
        public String name = null;
        public String pid = null;
    }

    public record PerfStat(boolean exists, float cpu,
                           float mem, float net) {
    }

    public static abstract class Visualizer {
        public abstract void showExist(boolean exists);

        public abstract void showCPU(String cpu);

        public abstract void showMem(String mem);

        public abstract void showNet(String net);

        public void update(PerfStat res) {
            Runnable func = () -> {
                if (res.exists) {
                    showExist(true);

                    // get cpu cores
                    int cores = Runtime.getRuntime().availableProcessors();
                    // get cpu usage
                    float cpu = res.cpu / cores;
                    showCPU(String.format("%02.2f", cpu) + "%");

                    // get memory size
                    long mem = Runtime.getRuntime().totalMemory();
                    // get memory usage
                    float mem_usage = res.mem / mem;
                    showMem(String.format("%02.2f",
                            mem_usage) + "%");

                    showNet("" + res.net);
                } else {
                    showExist(false);
                }
            };

            Platform.runLater(func);
        }
    }

    private class MonitorThread extends Thread {
        public final int interval = 1000;

        public void run() {
            try {
                while (true) {
                    PerfStat res = checkProcExistAndGetInfo();
                    vis.update(res);
                    sleep(interval);
                }
            } catch (IOException |
                     InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}
