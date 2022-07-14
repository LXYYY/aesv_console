package ntu.aesv_console.monitors;

import javafx.application.Platform;
import ntu.aesv_console.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Supplier;

public class ProcessMonitor {
    public static final int interval = 1;
    public MonitorThread monitor;
    public SystemUtils.Logging logger;
    public Visualizer vis;
    public ProcessInfo info;
    public Process monitorExe;

    public ProcessMonitor(Visualizer vis) {
        this.info = new ProcessInfo();
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

    public void startMonitoring() throws IOException {
        String name = this.info.name;
        this.monitor = new MonitorThread() {
            @Override
            public void init() throws IOException {
                String[] cmd = new String[]{
                        "powershell", "-File", "monitor" +
                        ".ps1", name,
                        "" + interval
                };
                System.out.println(String.join(" ", cmd));
                Process p = SystemUtils.buildProcess("bin",
                        name + "Monitor", cmd).start();
                BufferedReader br =
                        new BufferedReader(new InputStreamReader(p.getInputStream()));
                this.check =
                        () -> {
                            try {
                                String line = br.readLine();
                                if (line == null) {
                                    return new PerfStat();
                                }
                                System.out.println(line);
                                return new PerfStat(line.split("\\s+"));
                            } catch (IOException e) {
                                e.printStackTrace();
                                return null;
                            }
                        };
            }
        };
        monitor.setDaemon(true);
        monitor.start();
    }

    public void start() {
        try {
            startMonitoring();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            return new PerfStat(false, "", 0, 0, 0);
        } else {
            reader.readLine();
            reader.readLine();
            reader.readLine();
            String line = reader.readLine();
            logger.log(line);
            System.out.println(line);
            String[] tokens = line.split("\\s+");
            float cpu = Float.parseFloat(tokens[5]);
            float mem = Float.parseFloat(tokens[4]);
            this.info.pid = tokens[6];
            return new PerfStat(true, this.info.pid, cpu,
                    mem, 0);
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

    public static class PerfStat {
        public boolean exists;
        public String pid;
        public float cpu;
        public float mem;
        public float net;

        PerfStat(boolean exists,
                 String pid, float cpu,
                 float mem, float net) {
            this.exists = exists;
            this.pid = pid;
            this.cpu = cpu;
            this.mem = mem;
            this.net = net;
        }

        public PerfStat() {
            this(false, null, 0, 0, 0);
        }

        public PerfStat(String[] tokens) {
            this(tokens[0].equals("1"),
                    tokens[1],
                    Float.parseFloat(tokens[2].substring(0, Math.min(6, tokens[2].length()))),
                    Float.parseFloat(tokens[3].substring(0, Math.min(6, tokens[3].length()))),
                    Float.parseFloat(tokens[4].substring(0, Math.min(6, tokens[4].length()))));
        }
    }

    private abstract class MonitorThread extends Thread {
        public Supplier<PerfStat> check;

        public abstract void init() throws IOException;

        public void run() {
            try {
                init();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                PerfStat res = check.get();
                vis.update(res);
            }
        }
    }
}
