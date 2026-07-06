package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AssertionsTest demonstrates Playwright Java test assertions:
 * - Element visibility assertions
 * - Text content assertions
 * - Attribute value assertions
 * - Element state assertions
 * - Count and list assertions
 * - URL and navigation assertions
 * - Locator-based assertions with automatic retries
 *
 * @see <a href="https://playwright.dev/java/docs/test-assertions">Playwright Assertions</a>
 */
@Feature("Test Assertions & Validations")
public class AssertionsTest extends BaseTest {

    @Story("Assertion - Element Visibility")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify element visibility with assertions")
    @Test(priority = 1, description = "Element visibility assertion")
    public void testElementVisibility() {
        page.navigate(ConfigReader.getBaseUrl());
        
        // Element should be visible
        Locator loginButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login"));
        
        assertThat(loginButton).isVisible();
        Assert.assertTrue(page.isVisible("button"), "Button should be visible");
    }

    @Story("Assertion - Element Hidden")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert element is hidden")
    @Test(priority = 2, description = "Element hidden assertion")
    public void testElementHidden() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="hidden" style="display: none;">Hidden</button>
                </body>
                </html>
                """);
        
        Locator button = page.locator("#hidden");
        assertThat(button).isHidden();
    }

    @Story("Assertion - Text Content")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify text content with assertions")
    @Test(priority = 3, description = "Text content assertion")
    public void testTextContent() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <h1 id="title">Welcome to Swag Labs</h1>
                </body>
                </html>
                """);
        
        Locator heading = page.locator("#title");
        assertThat(heading).containsText("Swag Labs");
        assertThat(heading).hasText("Welcome to Swag Labs");
    }

    @Story("Assertion - Exact Text Match")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert exact text match")
    @Test(priority = 4, description = "Exact text assertion")
    public void testExactTextMatch() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <div id="message">Test Message</div>
                </body>
                </html>
                """);
        
        Locator message = page.locator("#message");
        assertThat(message).hasText("Test Message");
        Assert.assertEquals(message.textContent(), "Test Message");
    }

    @Story("Assertion - Attribute Values")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert element attribute values")
    @Test(priority = 5, description = "Attribute value assertion")
    public void testAttributeValues() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <input id="test-input" type="text" placeholder="Enter text" value="test value">
                </body>
                </html>
                """);
        
        Locator input = page.locator("#test-input");
        assertThat(input).hasAttribute("type", "text");
        assertThat(input).hasAttribute("placeholder", "Enter text");
        
        String value = input.inputValue();
        Assert.assertEquals(value, "test value");
    }

    @Story("Assertion - Element Enabled")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert element is enabled")
    @Test(priority = 6, description = "Enabled state assertion")
    public void testElementEnabled() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="enabled-btn">Enabled</button>
                  <button id="disabled-btn" disabled>Disabled</button>
                </body>
                </html>
                """);
        
        Locator enabledBtn = page.locator("#enabled-btn");
        Locator disabledBtn = page.locator("#disabled-btn");
        
        assertThat(enabledBtn).isEnabled();
        assertThat(disabledBtn).isDisabled();
    }

    @Story("Assertion - Checkbox Checked")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert checkbox checked state")
    @Test(priority = 7, description = "Checkbox state assertion")
    public void testCheckboxState() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <input id="checked" type="checkbox" checked>
                  <input id="unchecked" type="checkbox">
                </body>
                </html>
                """);
        
        Locator checked = page.locator("#checked");
        Locator unchecked = page.locator("#unchecked");
        
        assertThat(checked).isChecked();
        assertThat(unchecked).not().isChecked();
    }

    @Story("Assertion - Element Count")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert number of elements")
    @Test(priority = 8, description = "Element count assertion")
    public void testElementCount() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <li>Item 1</li>
                  <li>Item 2</li>
                  <li>Item 3</li>
                </body>
                </html>
                """);
        
        Locator items = page.locator("li");
        assertThat(items).hasCount(3);
        
        Assert.assertEquals(items.count(), 3);
    }

    @Story("Assertion - URL Match")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert page URL")
    @Test(priority = 9, description = "URL assertion")
    public void testUrlAssertion() {
        page.navigate(ConfigReader.getBaseUrl());
        
        assertThat(page).hasURL("*");  // Flexible URL matching
        
        String currentUrl = page.url();
        Assert.assertTrue(currentUrl.contains("saucedemo"), "URL should contain saucedemo");
    }

    @Story("Assertion - Multiple Elements Visible")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert multiple elements are visible")
    @Test(priority = 10, description = "Multiple elements assertion")
    public void testMultipleElementsVisible() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <div class="product">Product 1</div>
                  <div class="product">Product 2</div>
                  <div class="product">Product 3</div>
                </body>
                </html>
                """);
        
        assertThat(page.locator(".product")).hasCount(3);
        
        for (int i = 0; i < 3; i++) {
            assertThat(page.locator(".product").nth(i)).isVisible();
        }
    }

    @Story("Assertion - Text Pattern Matching")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert text contains pattern")
    @Test(priority = 11, description = "Text pattern assertion")
    public void testTextPatternMatching() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <p id="description">Customer Service: 1-800-SWAG-123</p>
                </body>
                </html>
                """);
        
        Locator description = page.locator("#description");
        assertThat(description).containsText("1-800");
        assertThat(description).containsText("SWAG");
    }

    @Story("Assertion - Class Presence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert element has CSS class")
    @Test(priority = 12, description = "CSS class assertion")
    public void testClassPresence() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <button id="styled-btn" class="btn btn-primary active">Click</button>
                </body>
                </html>
                """);
        
        Locator button = page.locator("#styled-btn");
        String className = button.getAttribute("class");
        
        Assert.assertTrue(className.contains("btn"), "Should have btn class");
        Assert.assertTrue(className.contains("active"), "Should have active class");
    }

    @Story("Assertion - Editable Input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert input field is editable")
    @Test(priority = 13, description = "Editable assertion")
    public void testEditableInput() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <input id="editable" type="text">
                  <input id="readonly" type="text" readonly>
                </body>
                </html>
                """);
        
        Locator editable = page.locator("#editable");
        Locator readonly = page.locator("#readonly");
        
        // Check that editable field doesn't have readonly attribute
        String editableReadonly = editable.getAttribute("readonly");
        Assert.assertNull(editableReadonly, "Editable field should not have readonly attribute");
        
        // Check that readonly field has readonly attribute
        String readonlyAttr = readonly.getAttribute("readonly");
        Assert.assertNotNull(readonlyAttr, "Readonly field should have readonly attribute");
    }

    @Story("Assertion - Element Focused")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert element has focus")
    @Test(priority = 14, description = "Focus state assertion")
    public void testElementFocused() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <input id="input1" type="text">
                </body>
                </html>
                """);
        
        Locator input = page.locator("#input1");
        input.focus();
        
        assertThat(input).isFocused();
    }

    @Story("Assertion - Not Assertion")
    @Severity(SeverityLevel.NORMAL)
    @Description("Assert negation conditions")
    @Test(priority = 15, description = "Negation assertion")
    public void testNegationAssertion() {
        page.setContent("""
                <!doctype html>
                <html>
                <body>
                  <div id="error" style="display: none;">Error</div>
                </body>
                </html>
                """);
        
        Locator error = page.locator("#error");
        assertThat(error).not().isVisible();
        assertThat(error).isHidden();
    }
}


