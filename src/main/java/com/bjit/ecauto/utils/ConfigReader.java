package com.bjit.ecauto.utils;

import com.bjit.ecauto.constants.FrameworkConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

    private static Properties properties;

    static {
        properties = new Properties();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(FrameworkConstants.CONFIG_PATH))) {
            properties.load(reader);
            LogHelper.info("Configuration loaded successfully");
        } catch (IOException e) {
            System.out.println("[CONFIG] ERROR: Could not load config.properties");
            System.out.println("  " + e.getMessage());
        }
    }

    // ============================
    // Generic getters
    // ============================
    public static String get(String key) {
        // System property overrides config file
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }
        return properties.getProperty(key, "");
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value.isEmpty() ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value.isEmpty()) return defaultValue;
        return Boolean.parseBoolean(value);
    }

    // ============================
    // Typed convenience methods
    // ============================
    public static String getBaseUrl() {
        return get("base.url", "https://www.saucedemo.com");
    }

    public static String getBrowser() {
        return get("browser", "chromium");
    }

    public static boolean isHeadless() {
        return getBoolean("headless", true);
    }

    public static int getSlowMotion() {
        return getInt("slow.motion", 0);
    }

    public static int getViewportWidth() {
        return getInt("viewport.width", FrameworkConstants.DEFAULT_WIDTH);
    }

    public static int getViewportHeight() {
        return getInt("viewport.height", FrameworkConstants.DEFAULT_HEIGHT);
    }

    public static int getDefaultTimeout() {
        return getInt("default.timeout", FrameworkConstants.DEFAULT_WAIT);
    }

    public static int getNavigationTimeout() {
        return getInt("navigation.timeout", FrameworkConstants.LONG_WAIT);
    }

    public static String getStandardUsername() {
        return get("standard.username", "standard_user");
    }

    public static String getStandardPassword() {
        return get("standard.password", "secret_sauce");
    }

    public static String getScreenshotPath() {
        return get("screenshot.path", FrameworkConstants.SCREENSHOT_PATH);
    }

    public static boolean screenshotOnFailure() {
        return getBoolean("screenshot.on.failure", true);
    }
}