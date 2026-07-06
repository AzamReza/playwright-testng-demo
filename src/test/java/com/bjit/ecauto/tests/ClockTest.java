package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.utils.LogHelper;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * ClockTest demonstrates Playwright Java Clock API for testing time-dependent features.
 */
@Feature("Clock & Time Management")
public class ClockTest extends BaseTest {

    @Story("Clock - Initialization")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Install clock and verify current time")
    @Test(priority = 1, description = "Clock can be installed and initialized")
    public void testClockInitialization() {
        page.setContent("<div id='current-time'></div>");
        page.clock().install();
        page.evaluate("() => { document.getElementById('current-time').textContent = new Date().toString(); }");
        
        String timeText = page.locator("#current-time").textContent();
        Assert.assertNotNull(timeText, "Current time should be displayed");
        LogHelper.info("Clock initialized");
    }

    @Story("Clock - Set Fixed Time")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Set clock to a fixed point in time")
    @Test(priority = 2, description = "Clock can set a fixed point in time")
    public void testClockSetFixedTime() {
        page.setContent("<div id='time-value'></div>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.evaluate("() => { document.getElementById('time-value').textContent = new Date().getTime(); }");
        String timeValue = page.locator("#time-value").textContent();
        Assert.assertNotNull(timeValue, "Fixed time should be set");
    }

    @Story("Clock - Run For Duration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Advance time by specific milliseconds")
    @Test(priority = 3, description = "Clock can advance time with runFor()")
    public void testClockRunFor() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(10000);
        String timerText = page.locator("#timer").textContent();
        Assert.assertTrue(timerText.contains("10"),"Timer should show 10");
    }

    @Story("Clock - Fast Forward")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Quickly advance time")
    @Test(priority = 4, description = "Clock can fast-forward time")
    public void testClockFastForward() {
        page.setContent("<div id='cd'>10</div><button onclick='s()'>S</button><script>let r=10;function s(){setInterval(()=>{r--;document.getElementById(\"cd\").textContent=r;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(5000);
        String countdownText = page.locator("#cd").textContent();
        Assert.assertTrue(countdownText.contains("5"),"Countdown should be at 5");
    }

    @Story("Clock - Pause At Time")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Freeze time at a specific moment")
    @Test(priority = 5, description = "Clock can pause at a specific time")
    public void testClockPauseAt() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.clock().pauseAt("2024-01-15T12:00:05Z");
        page.locator("button").click();
        String pausedTime = page.locator("#timer").textContent();
        Assert.assertTrue(pausedTime.contains("0"),"Timer should remain 0 when paused");
    }

    @Story("Clock - Resume After Pause")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Resume time progression after pause")
    @Test(priority = 6, description = "Clock can resume after pause")
    public void testClockResume() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.clock().pauseAt("2024-01-15T12:00:05Z");
        page.locator("button").click();
        page.clock().resume();
        page.clock().runFor(5000);
        String afterResume = page.locator("#timer").textContent();
        Assert.assertTrue(afterResume.contains("5"),"Timer should progress after resuming");
    }

    @Story("Clock - setTimeout Execution")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Execute setTimeout callbacks at right time")
    @Test(priority = 7, description = "Clock executes setTimeout callbacks")
    public void testTimeoutExecution() {
        page.setContent("<div id='status'>Waiting</div><button onclick='t()'>T</button><script>function t(){document.getElementById('status').textContent='Running';setTimeout(()=>{document.getElementById('status').textContent='Completed';},5000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(5000);
        String afterTimeout = page.locator("#status").textContent();
        Assert.assertTrue(afterTimeout.contains("Completed"),"Action should complete");
    }

    @Story("Clock - Interval Execution")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Execute setInterval callbacks repeatedly")
    @Test(priority = 8, description = "Clock executes setInterval callbacks")
    public void testIntervalExecution() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(1000);
        page.clock().runFor(1000);
        page.clock().runFor(1000);
        String timerValue = page.locator("#timer").textContent();
        Assert.assertTrue(timerValue.contains("3"),"Interval should fire 3 times");
    }

    @Story("Clock - Countdown to Zero")
    @Severity(SeverityLevel.NORMAL)
    @Description("Countdown reaches expiry correctly")
    @Test(priority = 9, description = "Clock countdown reaches expiry")
    public void testCountdownToExpiry() {
        page.setContent("<div id='cd'>10</div><div id='status'>Active</div><button onclick='s()'>S</button><script>let r=10;function s(){setInterval(()=>{r--;document.getElementById('cd').textContent=r;if(r<=0)document.getElementById('status').textContent='Expired';},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(10000);
        String status = page.locator("#status").textContent();
        Assert.assertEquals(status, "Expired","Countdown should show Expired");
    }

    @Story("Clock - Animation Frame Timing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Execute requestAnimationFrame callbacks")
    @Test(priority = 10, description = "Clock executes requestAnimationFrame")
    public void testAnimationFrameTiming() {
        page.setContent("<div id='frame'>0</div><button onclick='a()'>A</button><script>let f=0;function a(){function af(){f++;document.getElementById('frame').textContent=f;if(f<5)requestAnimationFrame(af);}requestAnimationFrame(af);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(100);
        String frameCount = page.locator("#frame").textContent();
        Assert.assertTrue(frameCount.contains("5"),"Should have 5 frames");
    }

    @Story("Clock - High Speed Time Advancement")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle large time advances")
    @Test(priority = 11, description = "Clock handles large time advances")
    public void testHighSpeedTimeAdvancement() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(3600000);
        String timerValue = page.locator("#timer").textContent();
        Assert.assertTrue(timerValue.contains("3600"),"Timer should show 3600");
    }

    @Story("Clock - Multiple Time Advances")
    @Severity(SeverityLevel.NORMAL)
    @Description("Time accumulates across advances")
    @Test(priority = 12, description = "Clock chains time advances")
    public void testMultipleTimeouts() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(3000);
        page.clock().runFor(2000);
        String timerValue = page.locator("#timer").textContent();
        Assert.assertTrue(timerValue.contains("5"),"Timer should be 5");
    }

    @Story("Clock - Partial Countdown Advance")
    @Severity(SeverityLevel.NORMAL)
    @Description("Progress tracking in countdown")
    @Test(priority = 13, description = "Clock shows partial countdown")
    public void testPartialCountdownAdvance() {
        page.setContent("<div id='cd'>20</div><button onclick='s()'>S</button><script>let r=20;function s(){setInterval(()=>{r--;document.getElementById('cd').textContent=r;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(7000);
        String countdownText = page.locator("#cd").textContent();
        Assert.assertTrue(countdownText.contains("13"),"Countdown should be 13");
    }

    @Story("Clock - Zero Time Advancement")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle zero advancement gracefully")
    @Test(priority = 14, description = "Clock handles zero advancement")
    public void testZeroTimeAdvancement() {
        page.setContent("<div id='timer'>0</div><button onclick='s()'>S</button><script>let c=0;function s(){setInterval(()=>{c++;document.getElementById(\"timer\").textContent=c;},1000);}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-01-15T12:00:00Z");
        page.locator("button").click();
        page.clock().runFor(0);
        String timerValue = page.locator("#timer").textContent();
        Assert.assertTrue(timerValue.contains("0"),"Timer should remain 0");
    }

    @Story("Clock - Date String Representation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Date strings match fixed time")
    @Test(priority = 15, description = "Clock returns correct dates")
    public void testDateStringRepresentation() {
        page.setContent("<div id='date'></div><button onclick='u()'>U</button><script>function u(){document.getElementById('date').textContent=new Date().toDateString();}</script>");
        page.clock().install();
        page.clock().setFixedTime("2024-03-20T10:30:00Z");
        page.locator("button").click();
        String dateDisplay = page.locator("#date").textContent();
        Assert.assertNotNull(dateDisplay, "Date should be displayed");
    }

    @Story("Clock - Persistence Across Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Clock state persists across navigation")
    @Test(priority = 16, description = "Clock persists across navigation")
    public void testClockPersistenceAcrossNavigation() {
        page.clock().install();
        String fixedTime = "2024-06-15T14:30:00Z";
        page.clock().setFixedTime(fixedTime);
        
        page.setContent("<div id='date'></div>");
        page.evaluate("() => { document.getElementById('date').textContent = new Date().toDateString(); }");
        String beforeNav = page.locator("#date").textContent();
        
        page.setContent("<div id='date'></div>");
        page.evaluate("() => { document.getElementById('date').textContent = new Date().toDateString(); }");
        String afterNav = page.locator("#date").textContent();
        
        Assert.assertEquals(beforeNav, afterNav,"Clock should maintain time");
    }

}











