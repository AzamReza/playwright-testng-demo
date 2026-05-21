package com.bjit.ecauto.listeners;

import com.bjit.ecauto.utils.LogHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    private int passed = 0;
    private int failed = 0;
    private int skipped = 0;

    @Override
    public void onStart(ITestContext context) {
        LogHelper.info("Test Suite starting: " + context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        LogHelper.info("");
        LogHelper.info("==========================================");
        LogHelper.info("         FINAL TEST RESULTS               ");
        LogHelper.info("==========================================");
        LogHelper.info("  Passed  : " + passed);
        LogHelper.info("  Failed  : " + failed);
        LogHelper.info("  Skipped : " + skipped);
        LogHelper.info("  Total   : " + (passed + failed + skipped));

        if ((passed + failed) > 0) {
            double passRate = (passed * 100.0) / (passed + failed);
            LogHelper.info("  Pass Rate: " + String.format("%.1f%%", passRate));
        }

        LogHelper.info("==========================================");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        passed++;
        LogHelper.info("[PASSED] " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        failed++;
        LogHelper.error("[FAILED] " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            LogHelper.error("  Reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        skipped++;
        LogHelper.warn("[SKIPPED] " + result.getMethod().getMethodName());
        if (result.getThrowable() != null) {
            LogHelper.warn("  Reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        // Handled in BaseTest @BeforeMethod
    }
}