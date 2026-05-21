package com.bjit.ecauto.constants;

public final class FrameworkConstants {

    private FrameworkConstants() {}

    // Paths
    public static final String CONFIG_PATH = "src/test/resources/config/config.properties";
    public static final String SCREENSHOT_PATH = "reports/screenshots/";
    public static final String LOG_PATH = "logs/";

    // Timeouts
    public static final int SHORT_WAIT = 3000;
    public static final int DEFAULT_WAIT = 10000;
    public static final int LONG_WAIT = 30000;

    // Viewport
    public static final int DEFAULT_WIDTH = 1920;
    public static final int DEFAULT_HEIGHT = 1080;

    // Test Status
    public static final String PASS = "PASSED";
    public static final String FAIL = "FAILED";
    public static final String SKIP = "SKIPPED";

    // Date Format
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd_HH-mm-ss";
}