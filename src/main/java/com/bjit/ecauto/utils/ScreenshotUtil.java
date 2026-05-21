package com.bjit.ecauto.utils;

import com.microsoft.playwright.Page;

import java.io.File;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScreenshotUtil {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    static {
        new File(ConfigReader.getScreenshotPath()).mkdirs();
    }

    // ============================
    // Capture full page screenshot
    // ============================
    public static String capture(Page page, String testName) {
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String fileName = sanitize(testName) + "_" + timestamp + ".png";
        String filePath = ConfigReader.getScreenshotPath() + fileName;

        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(filePath))
                .setFullPage(true));

        LogHelper.info("Screenshot saved: " + filePath);
        return filePath;
    }

    // ============================
    // Capture on failure
    // ============================
    public static String captureOnFailure(Page page, String testName, String errorMessage) {
        String path = capture(page, "FAIL_" + testName);
        LogHelper.error("Test FAILED: " + testName);
        LogHelper.error("Error: " + errorMessage);
        LogHelper.error("Screenshot: " + path);
        return path;
    }

    // Remove special characters from filename
    private static String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }
}