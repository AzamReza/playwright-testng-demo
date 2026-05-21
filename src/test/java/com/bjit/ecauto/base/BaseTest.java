package com.bjit.ecauto.base;

import com.microsoft.playwright.*;
import com.bjit.ecauto.utils.ConfigReader;
import com.bjit.ecauto.utils.LogHelper;
import com.bjit.ecauto.utils.ScreenshotUtil;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class BaseTest {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    // ============================
    // SUITE LEVEL — once for all tests
    // ============================
    @BeforeSuite
    public void suiteSetup() {
        LogHelper.suiteStart("SauceDemo Automation Suite");
        playwright = Playwright.create();
        LogHelper.info("Playwright instance created");
    }

    @AfterSuite
    public void suiteTeardown() {
        if (playwright != null) {
            playwright.close();
            LogHelper.info("Playwright instance closed");
        }
        LogHelper.suiteEnd();
    }

    // ============================
    // CLASS LEVEL — once per test class
    // ============================
    @BeforeClass
    public void launchBrowser() {
        String browserType = ConfigReader.getBrowser();
        boolean headless = ConfigReader.isHeadless();
        int slowMotion = ConfigReader.getSlowMotion();

        LogHelper.info("Launching browser: " + browserType
                + " | headless: " + headless
                + " | slowMotion: " + slowMotion + "ms");

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(slowMotion);

        switch (browserType.toLowerCase()) {
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                browser = playwright.chromium().launch(launchOptions);
                break;
        }

        LogHelper.info(browserType + " browser launched");
    }

    @AfterClass
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
            LogHelper.info("Browser closed");
        }
    }

    // ============================
    // METHOD LEVEL — fresh context per test
    // ============================
    @BeforeMethod
    public void createContext(ITestResult result) {
        LogHelper.testStart(result.getMethod().getMethodName());

        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(
                        ConfigReader.getViewportWidth(),
                        ConfigReader.getViewportHeight()));

        page = context.newPage();
        LogHelper.info("New browser context and page created");
    }

    @AfterMethod
    public void closeContext(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        // Screenshot on failure
        if (result.getStatus() == ITestResult.FAILURE) {
            if (page != null && ConfigReader.screenshotOnFailure()) {
                String errorMsg = result.getThrowable() != null
                        ? result.getThrowable().getMessage() : "Unknown error";
                ScreenshotUtil.captureOnFailure(page, testName, errorMsg);
            }
            LogHelper.testEnd(testName, "FAILED");
        } else if (result.getStatus() == ITestResult.SKIP) {
            LogHelper.testEnd(testName, "SKIPPED");
        } else {
            LogHelper.testEnd(testName, "PASSED");
        }

        if (context != null) {
            context.close();
            LogHelper.debug("Browser context closed");
        }
    }
}