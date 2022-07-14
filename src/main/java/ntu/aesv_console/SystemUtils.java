package ntu.aesv_console;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtils {
    public static Process executeCommands(String dir, String logPrefix, String... commands) throws IOException {
        Process process = buildProcess(dir, logPrefix,
                commands).start();
        System.out.println(logPrefix + ": " + "Process started.");
        return process;
    }

    public static ProcessBuilder buildProcess(String dir,
                                              String logPrefix, String... commands) throws IOException {
        System.out.println(logPrefix + ": " + String.join(" ", commands));
        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.directory(new File(dir)); //Set current directory
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        Date date = new Date();
        File logFile = new File("log/" + logPrefix + "-" + dateFormat.format(date) + ".txt"); //Log errors in specified log file.
        pb.redirectError(logFile);
        return pb;
    }

    public static boolean checkFileExists(String dir,
                                          String fileName) {
        File file = new File(Paths.get(dir, fileName).toString());
        return file.exists();
    }

    public interface Logging {
        public void log(String message);
    }
}
