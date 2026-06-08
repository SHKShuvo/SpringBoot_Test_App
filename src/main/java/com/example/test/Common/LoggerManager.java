package com.example.test.Common;

import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class LoggerManager {

    private static final String DEFAULT_LOG_DIR = System.getProperty("user.home") + File.separator + "test-api-logs";

    @SuppressWarnings("Duplicates")
    public static void writeErrorLog(String msg, String file) {
        String loggerPath = getLoggerPath();
        createDirectory(loggerPath);

        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        String fileName = "TEST_Error_" + file + "_" + dateFormatter.format(date) + ".log";
        String filePath = loggerPath + File.separator + fileName;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            SimpleDateFormat logFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            String msgLog = "Time : " + logFormatter.format(date) + ", Method : " + file + " , Error : " + msg;
            bw.write(msgLog);
            bw.write("\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @SuppressWarnings("Duplicates")
    public static void writeActivityLog(String msg) {
        String loggerPath = getLoggerPath();
        createDirectory(loggerPath);

        Date date = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");
        String fileName = "TEST_Logs" + "_" + dateFormatter.format(date) + ".log";
        String filePath = loggerPath + File.separator + fileName;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            SimpleDateFormat logFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss");
            String msgLog = "Time : " + logFormatter.format(date) + " - " + msg;
            bw.write(msgLog);
            bw.write("\n");
        } catch (Exception e) {
            writeErrorLog(" Error ==> " + e.getMessage(), ", writeActivityLog");
        }
    }

    private static String getLoggerPath() {
        String envPath = System.getenv("APP_LOG_PATH");
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath.trim();
        }
        return DEFAULT_LOG_DIR;
    }

    private static void createDirectory(String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
