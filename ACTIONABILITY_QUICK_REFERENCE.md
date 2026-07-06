# ActionabilityTest - Quick Reference Card

## 📋 What Was Created

```
playwright-testng-demo/
├── src/test/java/com/bjit/ecauto/tests/
│   └── ActionabilityTest.java ✅ NEW (18 tests, all passing)
│
└── Documentation/
    ├── ACTIONABILITY_TEST_GUIDE.md ✅ NEW (Comprehensive guide)
    └── ACTIONABILITY_QUICK_REFERENCE.md ✅ NEW (This reference)
```

---

## 🚀 Quick Start

```bash
# Run all tests
mvn -Dtest=ActionabilityTest test

# Run specific test
mvn -Dtest=ActionabilityTest#testDisabledElementNotClickable test

# Run with visible browser
mvn -Dtest=ActionabilityTest -Pheaded test

# Generate Allure report
mvn allure:serve
```

---

## 📊 Test Summary

| Category | Tests | Status |
|----------|-------|--------|
| Visibility | 3 | ✅ PASS |
| Enabled/Disabled | 3 | ✅ PASS |
| Editable State | 3 | ✅ PASS |
| Waiting | 3 | ✅ PASS |
| Viewport/Scroll | 1 | ✅ PASS |
| Real App | 1 | ✅ PASS |
| Checkbox | 2 | ✅ PASS |
| Select | 1 | ✅ PASS |
| Focus/Blur | 1 | ✅ PASS |
| **TOTAL** | **18** | **✅ 100% PASS** |

---

## 🎯 All Test Methods

### Visibility (1-3)
```
1. testIsVisibleCheckForHiddenElements .... isVisible() check
2. testHiddenElementNotActionable ........ hidden element fails
3. testElementCoveredByOverlayNotActionable element covered
```

### Enabled State (4-6)
```
4. testIsEnabledCheck ................... isEnabled() check
5. testDisabledElementNotClickable ....... disabled click fails
6. testRuntimeDisableAffectsActionability runtime disable
```

### Editable (7-9)
```
7. testIsEditableCheck .................. isEditable() check
8. testFillOnReadOnlyInputFails ......... readonly fill fails
9. testBypassEditableCheckWithEvaluate ... bypass with JS
```

### Waiting (10-12)
```
10. testWaitForElementBecomesVisible ..... wait for display
11. testWaitForActionabilityTimeout ..... timeout on hidden
12. testActionabilityTimeout ............ timeout waiting
```

### Viewport (13)
```
13. testAutoScrollIntoViewport .......... auto-scroll active
```

### Real App (15)
```
15. testLoginFormElementActionability ... login form check
```

### Checkbox (17-18)
```
17. testCheckboxActionability ........... checkbox check
18. testHiddenCheckboxNotActionable ..... hidden checkbox
```

### Select (19)
```
19. testDisabledSelectNotActionable ..... disabled select
```

### Focus (20)
```
20. testFocusOnActionableElement ........ focus/blur methods
```

### Modal (22)
```
22. testModalElementActionability ....... modal elements
```

---

## 💻 Actionability Checklist

An element must be:

- [ ] ✅ **In DOM** — exists in document
- [ ] ✅ **Visible** — not `display:none`, `visibility:hidden`, `opacity:0`
- [ ] ✅ **Enabled** — not `disabled` attribute
- [ ] ✅ **Editable** — for inputs: not `readonly`, not `disabled`
- [ ] ✅ **Not covered** — no overlay blocking it
- [ ] ✅ **Stable** — not animating/moving
- [ ] ✅ **In viewport** — scrolled into view OR auto-scrollable

---

## 🔍 Key Methods

```java
// Check visibility
boolean visible = page.isVisible("#element");
Locator loc = page.locator("#element");
assertThat(loc).isVisible();

// Check enabled
boolean enabled = page.isEnabled("#button");
assertThat(button).isEnabled();

// Check editable
boolean editable = page.isEditable("#input");

// Focus/Blur
locator.focus();
locator.blur();
assertThat(locator).isFocused();

// Set timeout
locator.click(new Locator.ClickOptions().setTimeout(5000));

// Force action (bypass checks)
locator.click(new Locator.ClickOptions().setForce(true));

// Bypass with JavaScript
locator.evaluate("el => el.disabled = false");
locator.evaluate("el => el.value = 'text'");
```

---

## ⚙️ Playwright Auto-Handles

Playwright **automatically** does these:

- ✅ Waits for element to become visible
- ✅ Checks if element is enabled/disabled
- ✅ Verifies not covered by another element
- ✅ Scrolls element into viewport
- ✅ Waits for element to stop animating
- ✅ Retries on transient failures

**You don't need to:**
- ❌ Manually scroll elements
- ❌ Check visibility manually
- ❌ Wait for animations
- ❌ Worry about covered elements

---

## 🎓 Common Errors

| Error | Meaning | Fix |
|-------|---------|-----|
| "not visible" | Hidden element | Wait for show, remove CSS hide |
| "not enabled" | Disabled element | Enable with JS or wait |
| "not editable" | Readonly/disabled input | Make editable |
| "Timeout" | Never became actionable | Increase timeout or debug state |
| "covered" | Other element on top | Remove overlay |

---

## 📈 Test Execution

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
Tests run: 18
Failures: 0
Errors: 0
Skipped: 0
Time: 163.7 seconds
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
✅ BUILD SUCCESS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## 📚 Documentation Files

1. **ACTIONABILITY_TEST_GUIDE.md** (12 KB)
   - Each test documented
   - API reference
   - Best practices
   - Error messages
   - Workflow diagram

2. **This Card** 
   - Quick reference
   - Test overview
   - Common issues
   - Quick commands

---

## 🔗 Related Files

| File | Purpose |
|------|---------|
| AccessibilityTest.java | ARIA roles, keyboard nav |
| InputHandlingTest.java | Type, click, fill, select |
| ActionabilityTest.java | Visibility, enabled, editable |
| BasePage.java | Page object helpers |
| BaseTest.java | Lifecycle management |

---

## 💡 Key Concepts

1. **Actionability = Automatic** — Playwright checks for you
2. **All conditions = AND** — All must pass (not OR)
3. **Visibility is strict** — `opacity:0` = hidden
4. **Auto-scroll** — No need to scroll manually
5. **Timeout matters** — Set realistic waits
6. **Force is exception** — Only use when necessary

---

## 🎯 Next Steps

### Extend ActionabilityTest
```bash
# Add new @Test method following name pattern:
@Test(priority = NN, description = "...")
public void testActionabilityFeature() { ... }
```

### Debug Failures
1. Check logs: `logs/automation.log`
2. View screenshot: `reports/screenshots/FAIL_*`
3. Run headed: `-Pheaded`
4. Check element state in browser dev tools

### Use in Your Tests
```java
// Before any action, verify actionability
Assert.assertTrue(page.isVisible("#myElement"));
Assert.assertTrue(page.isEnabled("#myButton"));
Assert.assertTrue(page.isEditable("#myInput"));

// Then perform action
locator.click();  // Actionability checked automatically
```

---

**Status**: ✅ Production Ready  
**Test Pass Rate**: 100% (18/18)  
**Last Updated**: July 6, 2026

