package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.microsoft.playwright.Dialog;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * DialogsTest demonstrates Playwright Java dialog handling:
 * - Alert dialogs (accept)
 * - Confirm dialogs (accept/dismiss)
 * - Prompt dialogs (text input)
 * - Dialog type detection
 * - Multiple sequential dialogs
 * - Dialog message and default values
 *
 * @see <a href="https://playwright.dev/java/docs/dialogs">Playwright Dialogs</a>
 */
@Feature("Dialog & Alert Handling")
public class DialogsTest extends BaseTest {

    @Story("Dialog - Alert Dialog")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle alert dialog with accept")
    @Test(priority = 1, description = "Alert dialog handling")
    public void testAlertDialog() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <button id="alert-btn" onclick="alert('Hello Alert')">Show Alert</button>
                  <div id="result">Not clicked</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            Assert.assertEquals(dialog.type(), "alert", "Should be alert type");
            Assert.assertEquals(dialog.message(), "Hello Alert", "Should have correct message");
            dialog.accept();
        });
        
        page.locator("#alert-btn").click();
        
        assertThat(page.locator("#result")).containsText("Not clicked");
    }

    @Story("Dialog - Confirm Dialog Accept")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle confirm dialog with accept")
    @Test(priority = 2, description = "Confirm dialog accept")
    public void testConfirmDialogAccept() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <button id="confirm-btn" onclick="
                    if(confirm('Confirm Action?')) {
                      document.getElementById('result').textContent = 'Confirmed';
                    }
                  ">Show Confirm</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            Assert.assertEquals(dialog.type(), "confirm");
            dialog.accept();
        });
        
        page.locator("#confirm-btn").click();
        
        assertThat(page.locator("#result")).containsText("Confirmed");
    }

    @Story("Dialog - Confirm Dialog Dismiss")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle confirm dialog with dismiss")
    @Test(priority = 3, description = "Confirm dialog dismiss")
    public void testConfirmDialogDismiss() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <button id="confirm-btn" onclick="
                    if(confirm('Confirm Action?')) {
                      document.getElementById('result').textContent = 'Confirmed';
                    } else {
                      document.getElementById('result').textContent = 'Dismissed';
                    }
                  ">Show Confirm</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            Assert.assertEquals(dialog.type(), "confirm");
            dialog.dismiss();
        });
        
        page.locator("#confirm-btn").click();
        
        assertThat(page.locator("#result")).containsText("Dismissed");
    }

    @Story("Dialog - Prompt Dialog with Input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle prompt dialog with text input")
    @Test(priority = 4, description = "Prompt dialog with input")
    public void testPromptDialogWithInput() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <button id="prompt-btn" onclick="
                    let input = prompt('Enter name:');
                    if(input) {
                      document.getElementById('result').textContent = 'Name: ' + input;
                    }
                  ">Show Prompt</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            Assert.assertEquals(dialog.type(), "prompt");
            dialog.accept("John Doe");
        });
        
        page.locator("#prompt-btn").click();
        
        assertThat(page.locator("#result")).containsText("Name: John Doe");
    }

    @Story("Dialog - Prompt Dialog Default Value")
    @Severity(SeverityLevel.NORMAL)
    @Description("Prompt dialog shows and uses default value")
    @Test(priority = 5, description = "Prompt dialog default value")
    public void testPromptDialogDefaultValue() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="prompt-btn" onclick="
                    let input = prompt('Enter value:', 'default-text');
                    document.getElementById('result').textContent = input || 'No input';
                  ">Show Prompt</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            Assert.assertEquals(dialog.defaultValue(), "default-text", "Should have default value");
            dialog.accept();
        });
        
        page.locator("#prompt-btn").click();
        
        assertThat(page.locator("#result")).containsText("default-text");
    }

    @Story("Dialog - Multiple Dialogs Sequential")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle multiple dialogs in sequence")
    @Test(priority = 6, description = "Multiple sequential dialogs")
    public void testMultipleDialogsSequential() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="dialogs-btn" onclick="
                    alert('First');
                    alert('Second');
                  ">Show Dialogs</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        AtomicInteger dialogCount = new AtomicInteger(0);
        
        page.onDialog(dialog -> {
            dialogCount.incrementAndGet();
            dialog.accept();
        });
        
        page.locator("#dialogs-btn").click();
        
        Assert.assertEquals(dialogCount.get(), 2, "Should handle 2 dialogs");
    }

    @Story("Dialog - Prompt Dismiss")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dismiss prompt dialog returns null")
    @Test(priority = 7, description = "Prompt dialog dismiss")
    public void testPromptDialogDismiss() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="prompt-btn" onclick="
                    let input = prompt('Enter name:');
                    document.getElementById('result').textContent = input === null ? 'Dismissed' : 'Accepted';
                  ">Show Prompt</button>
                  <div id="result">Pending</div>
                </body>
                </html>
                """);
        
        page.onDialog(dialog -> {
            dialog.dismiss();
        });
        
        page.locator("#prompt-btn").click();
        
        assertThat(page.locator("#result")).containsText("Dismissed");
    }

    @Story("Dialog - Dialog Type Property")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dialog type differentiates alert/confirm/prompt")
    @Test(priority = 8, description = "Dialog type identification")
    public void testDialogTypeProperty() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="alert-btn" onclick="alert('Alert')">Alert</button>
                  <button id="confirm-btn" onclick="confirm('Confirm')">Confirm</button>
                  <button id="prompt-btn" onclick="prompt('Prompt')">Prompt</button>
                </body>
                </html>
                """);
        
        AtomicReference<String> alertType = new AtomicReference<>();
        AtomicReference<String> confirmType = new AtomicReference<>();
        AtomicReference<String> promptType = new AtomicReference<>();
        
        page.onceDialog(dialog -> {
            alertType.set(dialog.type());
            dialog.accept();
        });
        
        page.locator("#alert-btn").click();
        Assert.assertEquals(alertType.get(), "alert", "First dialog should be alert");
        
        page.onceDialog(dialog -> {
            confirmType.set(dialog.type());
            dialog.accept();
        });
        
        page.locator("#confirm-btn").click();
        Assert.assertEquals(confirmType.get(), "confirm", "Second dialog should be confirm");
        
        page.onceDialog(dialog -> {
            promptType.set(dialog.type());
            dialog.accept();
        });
        
        page.locator("#prompt-btn").click();
        Assert.assertEquals(promptType.get(), "prompt", "Third dialog should be prompt");
    }

    @Story("Dialog - Dialog Message Property")
    @Severity(SeverityLevel.NORMAL)
    @Description("Dialog message property contains dialog text")
    @Test(priority = 9, description = "Dialog message content")
    public void testDialogMessageProperty() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button onclick="alert('Custom Alert Message')">Alert</button>
                  <button onclick="confirm('Custom Confirm Message')">Confirm</button>
                  <button onclick="prompt('Custom Prompt Message')">Prompt</button>
                </body>
                </html>
                """);
        
        page.onceDialog(dialog -> {
            Assert.assertEquals(dialog.message(), "Custom Alert Message");
            dialog.accept();
        });
        
        page.locator("button:nth-child(1)").click();
    }

    @Story("Dialog - Remove Dialog Handler")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle multiple dialog types correctly")
    @Test(priority = 10, description = "Dialog type handling")
    public void testDialogTypeHandling() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="alert-btn" onclick="alert('Alert')">Alert</button>
                  <div id="result">No alert</div>
                </body>
                </html>
                """);
        
        AtomicInteger alertCount = new AtomicInteger(0);
        
        page.onDialog(dialog -> {
            alertCount.incrementAndGet();
            dialog.accept();
        });
        
        page.locator("#alert-btn").click();
        Assert.assertEquals(alertCount.get(), 1);
    }
}


