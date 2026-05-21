package com.bjit.ecauto.utils;

import com.bjit.ecauto.constants.FrameworkConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogHelper {

    private static final String LOG_FILE = FrameworkConstants.LOG_PATH + "automation.log";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        new File(FrameworkConstants.LOG_PATH).mkdirs();
    }

    // ============================
    // Core log method
    // ============================
    private static void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String threadName = Thread.currentThread().getName();
        String formatted = String.format("[%s] [%-5s] [%s] %s",
                timestamp, level, threadName, message);

        System.out.println(formatted);
        writeToFile(formatted);
    }

    private static synchronized void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(LOG_FILE, true))) {
            writer.write(message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write log: " + e.getMessage());
        }
    }

    // ============================
    // Log level methods
    // ============================
    public static void info(String message) {
        log("INFO", message);
    }

    public static void warn(String message) {
        log("WARN", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }

    public static void debug(String message) {
        log("DEBUG", message);
    }

    public static void step(String message) {
        log("STEP", ">> " + message);
    }

    // ============================
    // Test lifecycle logging
    // ============================
    public static void testStart(String testName) {
        log("INFO", "========================================");
        log("INFO", "TEST START: " + testName);
        log("INFO", "========================================");
    }

    public static void testEnd(String testName, String status) {
        log("INFO", "TEST END: " + testName + " => " + status);
        log("INFO", "----------------------------------------");
    }

    public static void suiteStart(String suiteName) {
        log("INFO", "################################################");
        log("INFO", "SUITE START: " + suiteName);
        log("INFO", "Browser: " + ConfigReader.getBrowser());
        log("INFO", "Headless: " + ConfigReader.isHeadless());
        log("INFO", "Base URL: " + ConfigReader.getBaseUrl());
        log("INFO", "################################################");
    }

    public static void suiteEnd() {
        log("INFO", "################################################");
        log("INFO", "SUITE COMPLETED");
        log("INFO", "################################################");
    }
}