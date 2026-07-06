# InputHandlingTest — Playwright Java Input Actions Guide

This guide documents the `InputHandlingTest` class, which demonstrates Playwright's comprehensive input handling and keyboard interaction APIs following the patterns from `AccessibilityTest`.

## Overview

**File**: `src/test/java/com/bjit/ecauto/tests/InputHandlingTest.java`  
**Feature**: Input Handling & Keyboard Interactions  
**Base Class**: `BaseTest` (manages Playwright lifecycle)  
**Test Cases**: 25+ comprehensive input action examples  

### Key Documentation
- [Playwright Java Input Docs](https://playwright.dev/java/docs/input)
- [Playwright Actions](https://playwright.dev/java/docs/input#actions)

---

## Test Categories & Methods

### 1. **TEXT INPUT ACTIONS** (Priority 1-4)

#### `testTextInputFillAndRetrieval()` — Priority 1
- **Pattern**: Use `fill()` for direct value assignment
- **Use Case**: Setting input field values programmatically
- **API**: `locator.fill("value")`, `locator.inputValue()`
- **Event Triggers**: ❌ Does NOT trigger input/change events
- **Example**:
  ```java
  Locator usernameInput = page.getByPlaceholder("Username");
  usernameInput.fill("standard_user");
  String value = usernameInput.inputValue();
  ```

#### `testClearAndRetype()` — Priority 2
- **Pattern**: Clear with `fill("")` then fill new value
- **Use Case**: Replacing existing input without typing
- **Assertion**: Use `assertThat(locator).hasValue()`

#### `testTypeVsFillBehavior()` — Priority 3
- **Key Difference**:
  - `fill()` = Direct value assignment (no events)
  - `type()` = Simulates keypress (triggers keydown/input/keyup events)
- **Demo**: Compares event triggering with a JavaScript event counter
- **Use**: When you need to test event handlers or simulate real typing

#### `testTypeWithDelay()` — Priority 4
- **Pattern**: `locator.type(text, new Locator.TypeOptions().setDelay(ms))`
- **Use Case**: Simulate human-like typing speed
- **Example**:
  ```java
  usernameInput.type("standard_user", 
      new Locator.TypeOptions().setDelay(100));
  ```

---

### 2. **KEYBOARD NAVIGATION & PRESS** (Priority 5-10)

#### `testTabKeyNavigation()` — Priority 5 ⭐ CRITICAL
- **Pattern**: Focus element and call `press("Tab")`
- **Use Case**: Verify focus order through form controls
- **Assertion**: `assertThat(locator).isFocused()`
- **Example**:
  ```java
  usernameInput.click();
  assertThat(usernameInput).isFocused();
  usernameInput.press("Tab");
  assertThat(passwordInput).isFocused();
  ```

#### `testShiftTabReverseNavigation()` — Priority 6
- **Pattern**: `press("Shift+Tab")` for backward focus navigation
- **Use**: Testing reverse tab order compliance

#### `testEnterKeyFormSubmission()` — Priority 7 ⭐ CRITICAL
- **Pattern**: Fill form fields, then `press("Enter")` on submit field
- **Use Case**: Keyboard-only form submission (accessibility)
- **Example**:
  ```java
  passwordInput.fill(ConfigReader.getStandardPassword());
  passwordInput.press("Enter");
  page.waitForURL("**/inventory.html");
  ```

#### `testEscapeKeyClosesBehavior()` — Priority 8
- **Pattern**: Open dialog, then `press("Escape")`
- **Use**: Testing modal/dialog close handlers

#### `testSingleCharacterKeyPress()` — Priority 9
- **Pattern**: `press("Backspace")`, `press("Delete")`, `press("End")`, etc.
- **Use Case**: Character-level editing (delete, move cursor)

#### `testKeyboardCombinations()` — Priority 10
- **Pattern**: `press("Control+A")`, `press("Shift+Tab")`, etc.
- **Use**: Testing key combinations (select-all, cut-copy-paste)

---

### 3. **CLICK ACTIONS** (Priority 11-13)

#### `testCheckboxToggle()` — Priority 11
- **Pattern**: `click()` toggles checkbox checked state
- **Assertion**: `assertThat(checkbox).isChecked()`
- **Example**:
  ```java
  checkbox.click();
  assertThat(checkbox).isChecked();
  checkbox.click();
  assertThat(checkbox).not().isChecked();
  ```

#### `testRadioButtonMutualExclusion()` — Priority 12
- **Pattern**: Radio buttons in group are mutually exclusive
- **Use**: Verify only one radio can be selected
- **Example**: Click optionB → optionA becomes unchecked

#### `testDoubleClickEditMode()` — Priority 13
- **Pattern**: `click(new Locator.ClickOptions().setClickCount(2))`
- **Use Case**: Triggering dblclick event handlers
- **Events**: Triggers dblclick JavaScript event
- **Example**:
  ```java
  span.click(new Locator.ClickOptions().setClickCount(2));
  // Triggers dblclick event on span
  ```

---

### 4. **SELECT/DROPDOWN ACTIONS** (Priority 14-16)

#### `testSelectOptionByValue()` — Priority 14
- **Pattern**: `selectOption("value")`
- **Use Case**: Choosing option by value attribute
- **Example**:
  ```java
  select.selectOption("us");
  Assert.assertEquals(select.inputValue(), "us");
  ```

#### `testSelectOptionByLabel()` — Priority 15
- **Pattern**: Use value binding (simpler than label in latest versions)
- **Use Case**: Select by visible text or value

#### `testMultiSelectOptions()` — Priority 16
- **Pattern**: `selectOption(new String[]{"val1", "val2"})`
- **Use**: Multi-select fields with multiple choices
- **Verify**: Use `page.evaluate()` to read all selected values

---

### 5. **FOCUS & BLUR EVENTS** (Priority 17-18)

#### `testFocusMethod()` — Priority 17
- **Pattern**: `locator.focus()`
- **Use**: Programmatically set focus without clicking
- **Events**: Triggers focus event
- **Assertion**: `assertThat(locator).isFocused()`

#### `testBlurMethod()` — Priority 18
- **Pattern**: `locator.blur()`
- **Use**: Remove focus to trigger blur/change events
- **Events**: Triggers blur event (often triggers validation)

---

### 6. **INPUT VALIDATION** (Priority 19-22)

#### `testEmailInputValidation()` — Priority 19
- **HTML5 Validation**: `<input type="email" />`
- **Pattern**: `page.evaluate("() => form.checkValidity()")`
- **Use**: Verify HTML5 email format validation
- **Invalid**: "not-an-email" → validation fails
- **Valid**: "valid@example.com" → validation passes

#### `testRequiredFieldValidation()` — Priority 20
- **HTML5 Validation**: `<input type="text" required />`
- **Use**: Verify form rejects submission with empty required field

#### `testNumberInputMinMaxValidation()` — Priority 21
- **HTML5 Validation**: `<input type="number" min="1" max="10" />`
- **Use**: Verify numeric constraints are enforced
- **Invalid**: 15 (above max), 0 (below min)
- **Valid**: 5 (within range)

#### `testPatternValidation()` — Priority 22
- **HTML5 Validation**: `<input pattern="[0-9]{3}-[0-9]{3}-[0-9]{4}" />`
- **Use**: Verify regex pattern matching
- **Example**: Phone number validation (XXX-XXX-XXXX format)

---

### 7. **ADVANCED INTERACTIONS** (Priority 23-25)

#### `testAutocompleteSelection()` — Priority 23
- **HTML Element**: `<datalist>` with autocomplete suggestions
- **Pattern**: Fill, then arrow keys to select suggestion
- **Use**: Testing autocomplete UI interactions

#### `testTextAreaMultilineInput()` — Priority 24
- **Element**: `<textarea rows="5" cols="50">`
- **Pattern**: `fill()` with newline characters
- **Use**: Multi-line text input testing

#### `testMaskedInputInteraction()` — Priority 25
- **Use Case**: Credit card, phone number with formatting masks
- **Pattern**: `type()` with JavaScript event handlers
- **Demo**: Simulates formatted input (1234 5678 9012 3456)

---

## Running Tests

### Run Full InputHandlingTest Suite
```bash
mvn -Dtest=InputHandlingTest test
```

### Run Single Test by Name
```bash
mvn -Dtest=InputHandlingTest#testTabKeyNavigation test
```

### Run with Different Browser
```bash
mvn -Dtest=InputHandlingTest -Dbrowser=firefox test
mvn -Dtest=InputHandlingTest -Dbrowser=webkit test
```

### Run in Headed Mode
```bash
mvn -Dtest=InputHandlingTest -Pheaded test
```

---

## Key API Reference

### Input Methods
| Method | Event Trigger | Use Case |
|--------|---------------|----|
| `fill(text)` | ❌ No | Direct value assignment (fastest) |
| `type(text)` | ✅ Yes | Simulate human typing |
| `type(text, delay)` | ✅ Yes | Typed input with delay (slow) |
| `press(key)` | ✅ Yes | Single keystroke (Tab, Enter, Escape) |
| `click()` | ✅ Yes | Activate button/checkbox/radio |
| `click(clickCount)` | ✅ Yes | Double-click (clickCount=2) |
| `selectOption(value)` | ✅ Yes | Choose dropdown option |
| `focus()` | ✅ Yes | Set keyboard focus |
| `blur()` | ✅ Yes | Remove focus (triggers change) |

### Locator APIs
| Locator Method | Purpose |
|---|---|
| `getByPlaceholder(text)` | Find input by placeholder text |
| `getByRole(role)` | Find by ARIA role (BUTTON, TEXTBOX) |
| `locator(selector)` | CSS selector |
| `inputValue()` | Get current input value |
| `isVisible()` | Check element visibility |
| `isFocused()` | Check if element has focus |
| `isChecked()` | Check checkbox/radio state |

### Common Keyboard Keys
```
"Enter"        // Submit form
"Tab"          // Move focus forward
"Shift+Tab"    // Move focus backward
"Escape"       // Cancel/close
"Backspace"    // Delete previous character
"Delete"       // Delete next character
"ArrowUp"      // Move cursor or select previous
"ArrowDown"    // Move cursor or select next
"Control+A"    // Select all
"Control+C"    // Copy
"Control+V"    // Paste
```

---

## Best Practices

1. **Use `fill()` for setup** — Fastest way to populate test data
2. **Use `type()` for interaction testing** — When event handlers matter
3. **Always verify with assertions** — Use `assertThat()` for Playwright assertions
4. **Test both keyboard and mouse** — Accessibility requires both paths
5. **Use Allure annotations** — `@Feature`, `@Story`, `@Description` for reporting
6. **Isolate test data** — Each test should be independent via `@BeforeMethod` context
7. **Check validation with evaluate()** — Use `page.evaluate()` for form.checkValidity()

---

## Allure Report Integration

All tests include Allure annotations:
- `@Feature("Input Handling & Keyboard Interactions")`
- `@Story(...)` — Individual interaction story
- `@Severity(SeverityLevel.*)` — Test criticality level
- `@Description(...)` — Detailed test purpose

### Generate Allure Report
```bash
mvn allure:report
mvn allure:serve
```

---

## Debugging Tips

1. **Test flaky keyboard interaction**:
   - Add delay to `type()`: `new Locator.TypeOptions().setDelay(100)`
   - Use `page.waitFor()` after press() if expecting CSS changes

2. **Verify event firing**:
   - Use JavaScript event counter in fixture
   - Read with `locator.textContent()` or `page.evaluate()`

3. **Screenshot failures**:
   - Checks `reports/screenshots/FAIL_*` for interaction state
   - Logs in `logs/automation.log`

4. **Validation rules**:
   - Use `page.evaluate("() => element.validationMessage")` to get error message
   - Check `element.validity` for detailed validation state

---

## Related Test Files

- **AccessibilityTest** — Role-based locators and accessibility patterns
- **LoginTest** — Real application login interaction
- **ProductsTest** — Product list interaction
- **BasePage** — Base page object with input helpers (type, click, etc.)

---

## Version Info

- **Playwright**: 1.48.0
- **TestNG**: 7.10.2
- **Java**: 17
- **Created**: July 2026

---

## Further Learning

- [Playwright Java Input Documentation](https://playwright.dev/java/docs/input)
- [TestNG Documentation](https://testng.org/)
- [Allure Reports](https://docs.qameta.io/allure/)

