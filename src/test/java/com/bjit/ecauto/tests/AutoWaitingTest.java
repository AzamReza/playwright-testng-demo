package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AutoWaitingTest demonstrates Playwright Java input actions patterns:
 * - Text input (fill, type, clear)
 * - Keyboard interactions (press, Tab, Enter, Escape)
 * - Click interactions (checkbox, radio, buttons)
 * - Select/dropdown operations
 * - Focus and blur events
 * - Input validation
 *
 * @see <a href="https://playwright.dev/java/docs/input">Playwright Input Actions</a>
 */
@Feature("Input Handling & Keyboard Interactions")
public class AutoWaitingTest extends BaseTest {

    private void openLoginPage() {
        page.navigate(ConfigReader.getBaseUrl());
    }

    // ============================
    // TEXT INPUT ACTIONS
    // ============================

    @Story("Text Input - Fill Action")
    @Severity(SeverityLevel.NORMAL)
    @Description("Preconditions: Login page is loaded\n" +
            "Steps: Use fill() to set username field value\n" +
            "Expected: Input reflects filled value")
    @Test(priority = 1, description = "Fill text input with value and verify")
    public void testTextInputFillAndRetrieval() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");
        String testUsername = "standard_user";

        usernameInput.fill(testUsername);
        String retrievedValue = usernameInput.inputValue();

        Assert.assertEquals(retrievedValue, testUsername,
                "Input value should match filled value");
    }

    @Story("Text Input - Clear and Type")
    @Severity(SeverityLevel.NORMAL)
    @Description("Preconditions: Login page is loaded\n" +
            "Steps: Fill initial value, clear with fill(''), then type new value\n" +
            "Expected: Input contains final value only")
    @Test(priority = 2, description = "Clear input field and replace with new value")
    public void testClearAndRetype() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");
        String initialValue = "locked_out_user";
        String newValue = "standard_user";

        usernameInput.fill(initialValue);
        assertThat(usernameInput).hasValue(initialValue);

        usernameInput.fill("");
        assertThat(usernameInput).hasValue("");

        usernameInput.fill(newValue);
        assertThat(usernameInput).hasValue(newValue);
    }

    @Story("Text Input - Type vs Fill Behavior")
    @Severity(SeverityLevel.NORMAL)
    @Description("Demonstrates key difference: fill() = direct value assignment (no events), " +
            "type() = simulates keypress (triggers input/keydown/keyup events)")
    @Test(priority = 3, description = "Compare type() vs fill() event triggering")
    public void testTypeVsFillBehavior() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="fill-input" type="text" placeholder="Use fill()" />
                  <input id="type-input" type="text" placeholder="Use type()" />
                  <div id="keypress-count">Keypresses: 0</div>
                  <script>
                    let keypressCount = 0;
                    document.getElementById('type-input').addEventListener('keydown', () => {
                      keypressCount++;
                      document.getElementById('keypress-count').textContent = 'Keypresses: ' + keypressCount;
                    });
                  </script>
                </body>
                </html>
                """);

        Locator fillInput = page.locator("#fill-input");
        Locator typeInput = page.locator("#type-input");
        Locator keypressCount = page.locator("#keypress-count");

        // fill() assigns value directly - NO keypress events triggered
        fillInput.fill("hello");
        assertThat(fillInput).hasValue("hello");

        // Type() simulates user typing - DOES trigger keypress events
        typeInput.focus();
        typeInput.type("hello", new Locator.TypeOptions().setDelay(50));
        assertThat(typeInput).hasValue("hello");

        String countText = keypressCount.textContent();
        int count = Integer.parseInt(countText.replace("Keypresses: ", ""));
        Assert.assertTrue(count > 0,
                "type() should trigger keypress events (count: " + count + ")");
    }

    @Story("Text Input - Type with Delay")
    @Severity(SeverityLevel.NORMAL)
    @Description("Demonstrates type() with delay to simulate human typing speed")
    @Test(priority = 4, description = "Type with delay for human-like interaction")
    public void testTypeWithDelay() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");

        // Type with 100ms delay between keystrokes (slow human typing)
        usernameInput.type(ConfigReader.getStandardUsername(),
                new Locator.TypeOptions().setDelay(100));

        assertThat(usernameInput).hasValue(ConfigReader.getStandardUsername());
    }

    // ============================
    // KEYBOARD NAVIGATION - PRESS ACTION
    // ============================

    @Story("Keyboard - Tab Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Login page is loaded\n" +
            "Steps: Click first input, press Tab to move focus through form\n" +
            "Expected: Focus order is Username -> Password -> Login button")
    @Test(priority = 5, description = "Verify Tab key navigation through form controls")
    public void testTabKeyNavigation() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");
        Locator passwordInput = page.getByPlaceholder("Password");
        Locator loginButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));

        // Focus first input
        usernameInput.click();
        assertThat(usernameInput).isFocused();

        // Tab to password field
        usernameInput.press("Tab");
        assertThat(passwordInput).isFocused();

        // Tab to login button
        passwordInput.press("Tab");
        assertThat(loginButton).isFocused();
    }

    @Story("Keyboard - Shift+Tab Reverse Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify Shift+Tab navigates focus backwards through form")
    @Test(priority = 6, description = "Shift+Tab reverse focus navigation")
    public void testShiftTabReverseNavigation() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");
        Locator passwordInput = page.getByPlaceholder("Password");

        // Move forward to password
        usernameInput.click();
        usernameInput.press("Tab");
        assertThat(passwordInput).isFocused();

        // Move backward to username
        passwordInput.press("Shift+Tab");
        assertThat(usernameInput).isFocused();
    }

    @Story("Keyboard - Enter Key Form Submission")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Valid credentials available\n" +
            "Steps: Fill form and press Enter from password field\n" +
            "Expected: Form submits and user navigates to inventory page")
    @Test(priority = 7, description = "Submit form using Enter key from password field")
    public void testEnterKeyFormSubmission() {
        openLoginPage();

        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        Locator passwordInput = page.getByPlaceholder("Password");
        passwordInput.fill(ConfigReader.getStandardPassword());

        // Press Enter to submit form (keyboard-only submission)
        passwordInput.press("Enter");

        // Verify navigation to products page
        page.waitForURL("**/inventory.html");
        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Keyboard - Escape Key Close Behavior")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify Escape key closes dialogs or cancels modal interactions")
    @Test(priority = 8, description = "Escape key closes dialog elements")
    public void testEscapeKeyClosesBehavior() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <button id="open-modal">Open Modal</button>
                  <dialog id="modal">
                    <p>Modal Content - Press Escape to close</p>
                    <button id="close-btn">Close</button>
                  </dialog>
                  <script>
                    document.getElementById('open-modal').addEventListener('click', () => {
                      document.getElementById('modal').showModal();
                    });
                    document.getElementById('modal').addEventListener('cancel', () => {
                      document.getElementById('modal').close();
                    });
                  </script>
                </body>
                </html>
                """);

        Locator openBtn = page.locator("#open-modal");
        Locator modal = page.locator("#modal");

        openBtn.click();
        assertThat(modal).isVisible();

        // Close modal with Escape key
        modal.press("Escape");
        page.waitForFunction("() => !document.getElementById('modal').open");
        assertThat(modal).not().isVisible();
    }

    @Story("Keyboard - Single Character Keys")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify press() with individual character keys (Backspace, Delete, etc)")
    @Test(priority = 9, description = "Use press() for single character deletion")
    public void testSingleCharacterKeyPress() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="field" type="text" value="hello world" />
                </body>
                </html>
                """);

        Locator field = page.locator("#field");

        // Move cursor to end and delete characters
        field.click();
        field.press("End");
        field.press("Backspace");  // Remove 'd'
        field.press("Backspace");  // Remove 'l'

        assertThat(field).hasValue("hello wor");
    }

    @Story("Keyboard - Key Combinations")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify press() with key combinations (Ctrl+A to select all, etc)")
    @Test(priority = 10, description = "Use press() for key combinations")
    public void testKeyboardCombinations() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="field" type="text" value="original text" />
                  <div id="selected">Not selected</div>
                  <script>
                    document.getElementById('field').addEventListener('select', () => {
                      document.getElementById('selected').textContent = 'Selected!';
                    });
                  </script>
                </body>
                </html>
                """);

        Locator field = page.locator("#field");
        Locator selected = page.locator("#selected");

        field.click();
        field.press("Control+A");  // Select all
        
        Assert.assertTrue(selected.textContent().contains("Selected"),
                "Ctrl+A should trigger select event");
    }

    // ============================
    // CLICK ACTIONS
    // ============================

    @Story("Click - Checkbox Toggle")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify click() toggles checkbox checked state")
    @Test(priority = 11, description = "Toggle checkbox with single and double click")
    public void testCheckboxToggle() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <label>
                    <input id="terms" type="checkbox" />
                    I agree to terms
                  </label>
                </body>
                </html>
                """);

        Locator checkbox = page.locator("#terms");

        assertThat(checkbox).not().isChecked();

        // Click to check
        checkbox.click();
        assertThat(checkbox).isChecked();

        // Click to uncheck
        checkbox.click();
        assertThat(checkbox).not().isChecked();
    }

    @Story("Click - Radio Button Group")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify only one radio button in group can be selected")
    @Test(priority = 12, description = "Radio button mutual exclusion via click")
    public void testRadioButtonMutualExclusion() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <fieldset>
                    <legend>Choose shipping method:</legend>
                    <label><input type="radio" name="shipping" value="standard" /> Standard (5-7 days)</label>
                    <label><input type="radio" name="shipping" value="express" /> Express (2-3 days)</label>
                    <label><input type="radio" name="shipping" value="overnight" /> Overnight</label>
                  </fieldset>
                </body>
                </html>
                """);

        Locator standard = page.locator("input[value='standard']");
        Locator express = page.locator("input[value='express']");
        Locator overnight = page.locator("input[value='overnight']");

        standard.click();
        assertThat(standard).isChecked();

        express.click();
        assertThat(standard).not().isChecked();
        assertThat(express).isChecked();

        overnight.click();
        assertThat(express).not().isChecked();
        assertThat(overnight).isChecked();
    }

    @Story("Click - DoubleClick Action")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify click() with multiple clicks triggers double-click event")
    @Test(priority = 13, description = "Double-click triggers edit mode")
    public void testDoubleClickEditMode() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <span id="editable" style="border: 1px solid gray; padding: 5px;">Double-click to edit</span>
                  <input id="edit-field" type="text" hidden />
                  <script>
                    const span = document.getElementById('editable');
                    const input = document.getElementById('edit-field');
                    span.addEventListener('dblclick', () => {
                      span.hidden = true;
                      input.value = span.textContent;
                      input.hidden = false;
                      input.focus();
                    });
                    input.addEventListener('blur', () => {
                      span.textContent = input.value;
                      span.hidden = false;
                      input.hidden = true;
                    });
                  </script>
                </body>
                </html>
                """);

        Locator span = page.locator("#editable");
        Locator input = page.locator("#edit-field");

        assertThat(span).isVisible();
        assertThat(input).not().isVisible();

        // Click twice to trigger dblclick event
        span.click(new Locator.ClickOptions().setClickCount(2));

        assertThat(span).not().isVisible();
        assertThat(input).isVisible();

        input.fill("New text");
        input.blur();

        assertThat(span).isVisible();
        Assert.assertEquals(span.textContent(), "New text");
    }

    // ============================
    // SELECT/DROPDOWN ACTIONS
    // ============================

    @Story("Select - Choose by Value")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify selectOption() by value attribute")
    @Test(priority = 14, description = "Select dropdown option by value")
    public void testSelectOptionByValue() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <label for="country">Country:</label>
                  <select id="country">
                    <option value="">-- Select --</option>
                    <option value="us">United States</option>
                    <option value="uk">United Kingdom</option>
                    <option value="ca">Canada</option>
                    <option value="au">Australia</option>
                  </select>
                </body>
                </html>
                """);

        Locator select = page.locator("#country");

        select.selectOption("us");
        Assert.assertEquals(select.inputValue(), "us");

        select.selectOption("uk");
        Assert.assertEquals(select.inputValue(), "uk");

        select.selectOption("au");
        Assert.assertEquals(select.inputValue(), "au");
    }

    @Story("Select - Choose by Label")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify selectOption() by option label text")
    @Test(priority = 15, description = "Select dropdown option by visible label")
    public void testSelectOptionByLabel() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <select id="country">
                    <option value="">-- Select --</option>
                    <option value="us">United States</option>
                    <option value="uk">United Kingdom</option>
                    <option value="ca">Canada</option>
                  </select>
                </body>
                </html>
                """);

        Locator select = page.locator("#country");

        select.selectOption("ca");
        Assert.assertEquals(select.inputValue(), "ca");

        select.selectOption("uk");
        Assert.assertEquals(select.inputValue(), "uk");
    }

    @Story("Select - Multiple Selected Values")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify multi-select element allows multiple options")
    @Test(priority = 16, description = "Multi-select choose multiple options")
    public void testMultiSelectOptions() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <select id="interests" multiple>
                    <option value="sports">Sports</option>
                    <option value="music">Music</option>
                    <option value="tech">Technology</option>
                    <option value="travel">Travel</option>
                  </select>
                </body>
                </html>
                """);

        Locator select = page.locator("#interests");

        select.selectOption(new String[]{"sports", "tech"});

        Object selectedValuesObj = page.evaluate("""
                () => {
                  return Array.from(document.querySelector('#interests').selectedOptions)
                    .map(o => o.value);
                }
                """);

        String selectedValues = selectedValuesObj.toString();
        Assert.assertTrue(selectedValues.contains("sports"),
                "Sports should be selected (found: " + selectedValues + ")");
        Assert.assertTrue(selectedValues.contains("tech"),
                "Tech should be selected (found: " + selectedValues + ")");
    }

    // ============================
    // FOCUS & BLUR EVENTS
    // ============================

    @Story("Focus - Set Focus via focus()")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify focus() method sets keyboard focus to element")
    @Test(priority = 17, description = "Use focus() to set element focus")
    public void testFocusMethod() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="field1" type="text" />
                  <input id="field2" type="text" />
                  <div id="status">None focused</div>
                  <script>
                    document.getElementById('field1').addEventListener('focus', () => {
                      document.getElementById('status').textContent = 'Field1 focused';
                    });
                    document.getElementById('field2').addEventListener('focus', () => {
                      document.getElementById('status').textContent = 'Field2 focused';
                    });
                  </script>
                </body>
                </html>
                """);

        Locator field1 = page.locator("#field1");
        Locator field2 = page.locator("#field2");
        Locator status = page.locator("#status");

        field1.focus();
        assertThat(field1).isFocused();
        Assert.assertEquals(status.textContent(), "Field1 focused");

        field2.focus();
        assertThat(field2).isFocused();
        Assert.assertEquals(status.textContent(), "Field2 focused");
    }

    @Story("Blur - Remove Focus via blur()")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify blur() method removes focus from element")
    @Test(priority = 18, description = "Use blur() to remove element focus")
    public void testBlurMethod() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="field" type="text" />
                  <div id="status">Not blurred</div>
                  <script>
                    document.getElementById('field').addEventListener('blur', () => {
                      document.getElementById('status').textContent = 'Blurred!';
                    });
                  </script>
                </body>
                </html>
                """);

        Locator field = page.locator("#field");
        Locator status = page.locator("#status");

        field.focus();
        assertThat(field).isFocused();

        field.blur();
        Assert.assertEquals(status.textContent(), "Blurred!");
    }

    // ============================
    // INPUT VALIDATION
    // ============================

    @Story("Validation - Email Input Type")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify HTML5 email input validation")
    @Test(priority = 19, description = "Email input type validation")
    public void testEmailInputValidation() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <form id="form">
                    <input id="email" type="email" required />
                    <button type="submit">Submit</button>
                  </form>
                </body>
                </html>
                """);

        Locator emailInput = page.locator("#email");

        // Invalid email format
        emailInput.fill("not-an-email");
        boolean isValid = (Boolean) page.evaluate(
                "() => document.getElementById('form').checkValidity();");
        Assert.assertFalse(isValid,
                "Form should be invalid with bad email format");

        // Valid email format
        emailInput.fill("valid@example.com");
        isValid = (Boolean) page.evaluate(
                "() => document.getElementById('form').checkValidity();");
        Assert.assertTrue(isValid,
                "Form should be valid with correct email format");
    }

    @Story("Validation - Required Field")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify HTML5 required field validation")
    @Test(priority = 20, description = "Required input field validation")
    public void testRequiredFieldValidation() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <form id="form">
                    <input id="username" type="text" required />
                    <button type="submit">Submit</button>
                  </form>
                </body>
                </html>
                """);

        Locator usernameInput = page.locator("#username");

        // Empty (required)
        usernameInput.fill("");
        boolean isValid = (Boolean) page.evaluate(
                "() => document.getElementById('form').checkValidity();");
        Assert.assertFalse(isValid,
                "Form should be invalid when required field is empty");

        // Filled
        usernameInput.fill("testuser");
        isValid = (Boolean) page.evaluate(
                "() => document.getElementById('form').checkValidity();");
        Assert.assertTrue(isValid,
                "Form should be valid when required field is filled");
    }

    @Story("Validation - Number Input Min/Max")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify HTML5 number input min/max constraint validation")
    @Test(priority = 21, description = "Number input min/max constraint validation")
    public void testNumberInputMinMaxValidation() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <form id="form">
                    <input id="quantity" type="number" min="1" max="10" required />
                    <button type="submit">Submit</button>
                  </form>
                </body>
                </html>
                """);

        Locator quantityInput = page.locator("#quantity");

        // Above max
        quantityInput.fill("15");
        boolean isValid = (Boolean) page.evaluate(
                "() => document.getElementById('quantity').checkValidity();");
        Assert.assertFalse(isValid, "Input above max should be invalid");

        // Within range
        quantityInput.fill("5");
        isValid = (Boolean) page.evaluate(
                "() => document.getElementById('quantity').checkValidity();");
        Assert.assertTrue(isValid, "Input within range should be valid");

        // Below min
        quantityInput.fill("0");
        isValid = (Boolean) page.evaluate(
                "() => document.getElementById('quantity').checkValidity();");
        Assert.assertFalse(isValid, "Input below min should be invalid");
    }

    @Story("Validation - Text Pattern")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify HTML5 pattern attribute regular expression validation")
    @Test(priority = 22, description = "Input pattern regex validation")
    public void testPatternValidation() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <form id="form">
                    <input id="phone" type="text" pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}" required />
                    <button type="submit">Submit</button>
                  </form>
                </body>
                </html>
                """);

        Locator phoneInput = page.locator("#phone");

        // Invalid format
        phoneInput.fill("1234567890");
        boolean isValid = (Boolean) page.evaluate(
                "() => document.getElementById('phone').checkValidity();");
        Assert.assertFalse(isValid,
                "Phone without pattern should be invalid");

        // Valid format (XXX-XXX-XXXX)
        phoneInput.fill("123-456-7890");
        isValid = (Boolean) page.evaluate(
                "() => document.getElementById('phone').checkValidity();");
        Assert.assertTrue(isValid,
                "Phone matching pattern should be valid");
    }

    // ============================
    // ADVANCED INPUT INTERACTIONS
    // ============================

    @Story("Advanced - Autocomplete Interaction")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify interaction with datalist autocomplete suggestions")
    @Test(priority = 23, description = "Select from autocomplete datalist")
    public void testAutocompleteSelection() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="browser" type="text" list="browsers" />
                  <datalist id="browsers">
                    <option value="Chrome">
                    <option value="Firefox">
                    <option value="Safari">
                    <option value="Edge">
                  </datalist>
                </body>
                </html>
                """);

        Locator input = page.locator("#browser");

        input.fill("Fire");
        page.keyboard().press("ArrowDown");  // Select first suggestion
        page.keyboard().press("Enter");

        Assert.assertTrue(input.inputValue().contains("Fire"),
                "Input should contain Fire prefix after suggestion");
    }

    @Story("Advanced - TextArea Multi-line Input")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify interaction with multi-line textarea elements")
    @Test(priority = 24, description = "Fill textarea with multi-line text")
    public void testTextAreaMultilineInput() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <textarea id="message" rows="5" cols="50"></textarea>
                </body>
                </html>
                """);

        Locator textarea = page.locator("#message");
        String multilineText = "Line 1\\nLine 2\\nLine 3";

        textarea.fill(multilineText);
        Assert.assertEquals(textarea.inputValue(), multilineText);
    }

    @Story("Advanced - Masked Input Behavior")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify interaction with masked input fields (credit card, phone)")
    @Test(priority = 25, description = "Interact with masked input fields")
    public void testMaskedInputInteraction() {
        page.setContent("""
                <!doctype html>
                <html>
                <head><meta charset="UTF-8" /></head>
                <body>
                  <input id="card" type="text" placeholder="1234 5678 9012 3456" maxlength="19" />
                  <script>
                    document.getElementById('card').addEventListener('input', (e) => {
                      let value = e.target.value.replace(/\\s/g, '');
                      let masked = value.match(/.{1,4}/g)?.join(' ') || value;
                      e.target.value = masked;
                    });
                  </script>
                </body>
                </html>
                """);

        Locator cardInput = page.locator("#card");

        cardInput.type("1234567890123456", new Locator.TypeOptions().setDelay(50));

        String value = cardInput.inputValue();
        Assert.assertTrue(value.contains(" "),
                "Card input should be formatted with spaces");
    }
}

