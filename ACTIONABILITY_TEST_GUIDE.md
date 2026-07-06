# ActionabilityTest — Playwright Java Actionability Guide

## Overview

**File**: `src/test/java/com/bjit/ecauto/tests/ActionabilityTest.java`  
**Feature**: Element Actionability Checks & Waits  
**Base Class**: `BaseTest` (manages Playwright lifecycle)  
**Test Cases**: 18 comprehensive actionability examples  
**Status**: ✅ 100% Passing (18/18)

### Key Documentation
- [Playwright Java Actionability Docs](https://playwright.dev/java/docs/actionability)
- [Actionability Checks](https://playwright.dev/java/docs/actionability#checking-if-element-is-hidden)

---

## What is Actionability?

**Actionability** is an automatic precondition check that Playwright performs before executing user actions. An element must meet ALL of these requirements to be **actionable**:

| Requirement | Check Method | Details |
|-------------|--------------|---------|
| **In DOM** | Always checked | Element must exist in the document |
| **Visible** | `isVisible()` | Not `display:none`, `visibility:hidden`, or `opacity:0` |
| **Enabled** | `isEnabled()` | Not `disabled`, not in disabled fieldset |
| **Editable** | `isEditable()` | For inputs: not `readonly`, not `disabled` |
| **Not covered** | Automatic | No other element blocking it |
| **Stable** | Automatic | Element is not animating or moving |
| **In viewport** | Automatic | Element is scrolled into view if needed |

---

## Test Methods (18 Total)

### 1. Visibility Checks (Priority 1-3)

#### `testIsVisibleCheckForHiddenElements()` — Priority 1
- **Concept**: `isVisible()` returns true only for visible elements
- **Tests**:
  - ✅ Visible element → true
  - ✅ Hidden button → false
  - ✅ `display: none` → false
  - ✅ `visibility: hidden` → false
- **API**:
  ```java
  boolean visible = page.isVisible("#button");
  ```

#### `testHiddenElementNotActionable()` — Priority 2 ⭐ CRITICAL
- **Concept**: Hidden elements fail actionability checks
- **Expected**: Click on hidden element throws exception
- **Use Case**: Verify defensive error handling

#### `testElementCoveredByOverlayNotActionable()` — Priority 3
- **Concept**: Element covered by overlay is not clickable
- **Solution**: Remove overlay → element becomes clickable
- **Demo**: Shows how to fix coverage issues

---

### 2. Enabled/Disabled State (Priority 4-6)

#### `testIsEnabledCheck()` — Priority 4
- **Concept**: `isEnabled()` checks `disabled` attribute
- **Tests**:
  - ✅ Enabled button → true
  - ✅ Disabled button → false
  - ✅ Enabled input → true
  - ✅ Disabled input → false

#### `testDisabledElementNotClickable()` — Priority 5 ⭐ CRITICAL
- **Concept**: Disabled elements cannot be clicked
- **Verification**: Click event counter stays at 0
- **Error**: "not enabled" or contains "disabled"

#### `testRuntimeDisableAffectsActionability()` — Priority 6
- **Concept**: Runtime `disabled` changes affect actionability
- **Flow**:
  1. Enable button → clickable
  2. Add `disabled` attribute → not clickable
  3. Remove `disabled` → clickable again

---

### 3. Editable State (Priority 7-9)

#### `testIsEditableCheck()` — Priority 7
- **Concept**: `isEditable()` checks readonly/disabled status
- **Editable**:
  - Text inputs
  - Textareas
- **Non-editable**:
  - Readonly inputs
  - Disabled inputs
  - Readonly textareas

#### `testFillOnReadOnlyInputFails()` — Priority 8
- **Concept**: `fill()` fails on readonly inputs
- **Error**: Click throws focusing on editable check
- **Use Case**: Catch attempt to modify read-only fields

#### `testBypassEditableCheckWithEvaluate()` — Priority 9
- **Concept**: Use `evaluate()` to bypass editable check
- **Pattern**:
  ```java
  input.evaluate("el => el.value = 'test'");
  ```
- **Use**: For testing edge cases (not recommended in production)

---

### 4. Waiting for Actionability (Priority 10-12)

#### `testWaitForElementBecomesVisible()` — Priority 10 ⭐ CRITICAL
- **Concept**: Element hidden → displayed after delay
- **Flow**:
  1. Element is `display: none`
  2. JavaScript changes to `display: block` after 300ms
  3. Click action waits for visibility
  4. Click succeeds

#### `testWaitForActionabilityTimeout()` — Priority 11
- **Concept**: Action times out if element never becomes actionable
- **Timeout**: 2 seconds
- **Result**: Exception thrown
- **Use**: Set appropriate timeouts for your app

#### `testActionabilityTimeout()` — Priority 12
- **Concept**: Action times out on permanently hidden element
- **Pattern**:
  ```java
  button.click(new Locator.ClickOptions().setTimeout(2000));
  ```

---

### 5. Viewport & Scrolling (Priority 13)

#### `testAutoScrollIntoViewport()` — Priority 13
- **Concept**: Element outside viewport auto-scrolls into view
- **Flow**:
  1. Button is below fold (not in viewport)
  2. Click action auto-scrolls element into view
  3. Click succeeds
  4. Element now visible
- **Automatic**: You DON'T need to manually scroll

---

### 6. Checkbox Actionability (Priority 17-18)

#### `testCheckboxActionability()` — Priority 17
- **Concept**: Checkbox must be visible and enabled to check
- **Tests**:
  - ✅ Visible and enabled → can check
  - ✅ Can uncheck
  - ✅ State reflects correctly

#### `testHiddenCheckboxNotActionable()` — Priority 18
- **Concept**: Hidden checkbox cannot be checked
- **Expected**: Exception on click
- **Error**: "not visible"

---

### 7. Select/Dropdown (Priority 19)

#### `testDisabledSelectNotActionable()` — Priority 19
- **Concept**: Disabled `<select>` cannot have option selected
- **Flow**:
  1. Select is disabled
  2. `selectOption()` fails
  3. Exception: "not enabled" or "disabled"

---

### 8. Focus & Blur (Priority 20)

#### `testFocusOnActionableElement()` — Priority 20
- **Concept**: `focus()` works on actionable elements
- **Flow**:
  1. Input is editable
  2. `focus()` sets focus
  3. `isFocused()` returns true
  4. `blur()` removes focus

---

### 9. Login Form (Priority 15)

#### `testLoginFormElementActionability()` — Priority 15 ⭐ CRITICAL
- **Concept**: Real-world actionability verification
- **Tests**:
  - ✅ Username input visible, enabled
  - ✅ Password input visible, enabled
  - ✅ Login button visible, enabled
  - ✅ Successful form submission
- **Use Case**: End-to-end validation of real app

---

### 10. Modal Dialog (Priority 22)

#### `testModalElementActionability()` — Priority 22
- **Concept**: Modal elements must still be actionable
- **Tests**:
  - ✅ Modal button is visible
  - ✅ Modal button is enabled
  - ✅ Can click modal button
- **Use**: Verify modal interactions work

---

## Running Tests

### Run All ActionabilityTest
```bash
mvn -Dtest=ActionabilityTest test
```

### Run Single Test
```bash
mvn -Dtest=ActionabilityTest#testDisabledElementNotClickable test
```

### Run with Different Browser
```bash
mvn -Dtest=ActionabilityTest -Dbrowser=firefox test
mvn -Dtest=ActionabilityTest -Dbrowser=webkit test
```

### Run in Headed Mode
```bash
mvn -Dtest=ActionabilityTest -Pheaded test
```

---

## Key Playwright APIs

### Visibility Checks
```java
boolean visible = page.isVisible("#element");
```

### Enabled Checks
```java
boolean enabled = page.isEnabled("#button");
```

### Editable Checks
```java
boolean editable = page.isEditable("#input");
```

### Focus Management
```java
locator.focus();   // Set focus
locator.blur();    // Remove focus
assertThat(locator).isFocused();
```

### Bypass Actionability (Use Cautiously)
```java
// Force click (ignores visibility/enabled checks)
locator.click(new Locator.ClickOptions().setForce(true));

// Use evaluate to manipulate DOM directly
locator.evaluate("el => el.disabled = false");
locator.evaluate("el => el.value = 'text'");
```

### Timeouts
```java
locator.click(new Locator.ClickOptions().setTimeout(5000));  // 5 seconds
```

---

## Actionability Workflow

```
User Action (e.g., click())
         ↓
┌─────────────────────────────┐
│ Actionability Checks:       │
│ 1. Element in DOM?          │
│ 2. Element visible?         │  → isVisible()
│ 3. Element enabled?         │  → isEnabled()
│ 4. Element editable? (input)│  → isEditable()
│ 5. Not covered by overlay?  │
│ 6. Stable (not moving)?     │
│ 7. In viewport? (auto-scroll)│
└─────────────────────────────┘
         ↓
    ✅ All checks pass?
         ↓
    Execute Action
```

---

## Error Messages

| Error | Cause | Solution |
|-------|-------|----------|
| "not visible" | Element has `display:none`, `visibility:hidden`, or `opacity:0` | Wait for element, show element with CSS |
| "not enabled" | Element has `disabled` attribute | Enable element, wait for enable |
| "not editable" | Input is `readonly` or `disabled` | Make input editable, wait for change |
| "action timeout" | Element never becomes actionable | Increase timeout, debug element state |
| "element is hidden" | Covered by another element | Remove overlay, reposition elements |

---

## Best Practices

✅ **Always rely on actionability** — Don't manually scroll or check visibility  
✅ **Use appropriate timeouts** — Consider app's expected delays  
✅ **Test with real elements** — HTML fixtures closely match real app  
✅ **Verify errors** — Expect exceptions on invalid interactions  
✅ **Avoid `force=true`** — Only use when absolutely necessary  
✅ **Use `evaluate()` sparingly** — For testing only, not production scripts

---

## Allure Report Integration

All tests include Allure annotations:
- `@Feature("Element Actionability Checks & Waits")`
- `@Story(...)` — Individual actionability scenario
- `@Severity(SeverityLevel.*)` — CRITICAL for core functionality
- `@Description(...)` — Detailed test purpose

### Generate Report
```bash
mvn allure:serve
```

---

## Execution Results

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Tests run: 18
Failures: 0
Errors: 0
Skipped: 0
Time elapsed: 163.7 seconds
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ BUILD SUCCESS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## Related Test Files

- **AccessibilityTest** — ARIA roles, keyboard navigation
- **InputHandlingTest** — Text input, form submission
- **ActionabilityTest** — Visibility, enabled, editable checks
- **LoginTest** — Real application workflows
- **BasePage** — Base page object methods

---

## Key Takeaways

1. **Actionability is automatic** — Playwright handles it for you
2. **All conditions must pass** — Checks are AND, not OR
3. **Auto-scroll is built-in** — No manual scrolling needed
4. **Visibility is strict** — Even `opacity:0` counts as hidden
5. **Timeouts matter** — Set appropriate waits for your app
6. **Errors are informative** — "not visible" vs "not enabled" help debug

---

## Version Info

| Component | Version |
|-----------|---------|
| Playwright Java | 1.48.0 |
| TestNG | 7.10.2 |
| Java | 17 |

---

## References

- 📖 [Playwright Java Actionability](https://playwright.dev/java/docs/actionability)
- 📖 [Actionability Checks](https://playwright.dev/java/docs/actionability#checking-if-element-is-hidden)
- 📖 [Waiting for Elements](https://playwright.dev/java/docs/locators#waiting)

---

**Last Updated**: July 6, 2026  
**Status**: ✅ Complete & Production Ready  
**All Tests Passing**: 18/18 ✓

