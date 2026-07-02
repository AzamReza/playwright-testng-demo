# Test Cases Documentation & Allure Reporting Guide

## 📋 Overview

This document describes all **36 automated test cases** organized into **4 test suites** for the SauceDemo E2E application. Test cases are documented in multiple formats and integrated with Allure reporting for detailed test analytics.

## 📁 Test Cases Files

All test cases are documented in the following formats in `src/test/resources/`:

### 1. **testcases.json** (Structured Data)
- Machine-readable format for parsing and automation
- Contains complete preconditions, steps, and expected results
- Best for: APIs, data-driven testing, integration

### 2. **testcases.csv** (Spreadsheet Compatible)
- Open with Excel, Google Sheets, or any spreadsheet application
- Easy to filter, sort, and search
- Best for: Report generation, Excel-based team collaboration

### 3. **testcases.html** (Visual Documentation)
- Beautiful HTML report with styling
- Open in any web browser
- Best for: Stakeholder communication, training materials

## 🧪 Test Suite Breakdown

### TS001: Authentication Tests (6 tests)
**Feature:** Authentication
**Status:** Active

| TC ID | Test Name | Severity | Priority |
|-------|-----------|----------|----------|
| TC_AUTH_001 | testValidLogin | BLOCKER | 1 |
| TC_AUTH_002 | testAllUserTypes | CRITICAL | 2 |
| TC_AUTH_003 | testInvalidLogin | CRITICAL | 3 |
| TC_AUTH_004 | testLockedOutUser | CRITICAL | 4 |
| TC_AUTH_005 | testCloseErrorMessage | CRITICAL | 5 |
| TC_AUTH_006 | testLogout | CRITICAL | 6 |

### TS002: Products Page Tests (13 tests)
**Feature:** Products Page
**Status:** Active

Covers: Page load, product verification, sorting, adding/removing items

### TS003: Shopping Cart Tests (7 tests)
**Feature:** Shopping Cart
**Status:** Active

Covers: Cart navigation, item management, checkout flow

### TS004: Checkout Process Tests (10 tests)
**Feature:** Checkout Process
**Status:** Active

Covers: Step one info, step two overview, pricing calculations

## 🎯 Test Case Structure

Each test case includes:

```json
{
  "id": "TC_AUTH_001",
  "testName": "testValidLogin",
  "description": "Verify standard user can login successfully",
  "severity": "BLOCKER",
  "story": "Valid Login",
  "preconditions": [
    "Application is loaded",
    "Login page is displayed"
  ],
  "steps": [
    "1. Enter username",
    "2. Enter password",
    "3. Click Login button"
  ],
  "expectedResult": [
    "User navigates to Products page",
    "Page title is 'Products'"
  ],
  "priority": 1,
  "status": "Active",
  "keywords": ["login", "positive"]
}
```

## 📊 Allure Integration

### View Test Results in Allure

After running tests:

```bash
# Generate Allure report
mvn test && mvn allure:report

# View in browser
mvn allure:serve

# Or open directly
Open target/allure-report/index.html in browser
```

### Allure Report Features Activated

✅ **Test Details**: Each test includes comprehensive preconditions and steps
✅ **Severity Levels**: Tests marked as BLOCKER, CRITICAL, or HIGH
✅ **Stories**: Tests organized by business workflow
✅ **Descriptions**: Enhanced with step-by-step details
✅ **Failure Screenshots**: Auto-captured on test failure
✅ **Detailed Logging**: LogHelper provides step-by-step execution logs
✅ **Historical Charts**: Track pass rate trends over time

### Sample Allure Annotations in Tests

Tests now include `@Description` annotations with:
- Preconditions
- Numbered steps
- Expected results

Example:
```java
@Description("Preconditions: Application loaded, login page displayed\n" +
        "Steps: Enter standard_user credentials and click Login\n" +
        "Expected: User navigates to Products page with correct title")
@Test(priority = 1,
        description = "Verify standard user can login successfully")
public void testValidLogin() {
    // Test code
}
```

## 🚀 Running Tests with Specific Criteria

### Run All Tests
```bash
mvn test
```

### Run Only BLOCKER Tests
```bash
mvn -Dgroups=blocker test  # Requires TestNG grouping configuration
```

### Run Specific Test Suite
```bash
mvn -Dtest=LoginTest test
mvn -Dtest=ProductsTest test
mvn -Dtest=CartTest test
mvn -Dtest=CheckoutTest test
```

### Run with Specific Browser
```bash
mvn -Pfirefox test
mvn -Pheaded test
mvn -Dbrowser=webkit -Dheadless=false test
```

## 📈 Interpreting Allure Reports

After running tests and viewing Allure report:

### Dashboard Tab
- **Pass Rate**: Overall test success percentage
- **Duration**: Total test execution time
- **Trends**: Pass/fail trends over multiple runs

### Suites Tab
- View tests grouped by feature
- Filter by severity level
- Sort by status (passed, failed, skipped)

### Graphs Tab
- **Severity Chart**: Distribution of BLOCKER, CRITICAL, HIGH tests
- **Status Chart**: Pie chart of passed/failed/skipped
- **Duration Chart**: Test execution time analysis

### Timeline Tab
- Visual timeline of test execution
- Identify slow tests
- See test order and dependencies

## 🔍 Test Case Details by Suite

### Authentication Suite (TS001)
**Purpose**: Verify user authentication and authorization

| Test | Precondition | Expected Result |
|------|-------------|-----------------|
| Valid Login | Fresh login, valid creds | Navigate to Products page |
| All User Types | Various user credentials | Appropriate pass/fail handling |
| Invalid Login | Invalid credentials | Error message displayed |
| Locked User | Locked out credentials | Specific locked error message |
| Close Error | Error displayed | Error closable, form reusable |
| Logout | User logged in | User on login page |

### Products Suite (TS002)
**Purpose**: Verify product page functionality and shopping cart integration

**Key Tests**:
- Product count (6 products)
- Product names displayed correctly
- Sorting (A-Z, Z-A, Price Low-High, Price High-Low)
- Add to cart (single, multiple, all items)
- Remove from cart

### Shopping Cart Suite (TS003)
**Purpose**: Verify cart management and checkout flow

**Key Tests**:
- Empty cart verification
- Single/multiple items display
- Item removal
- Continue shopping
- Proceed to checkout

### Checkout Suite (TS004)
**Purpose**: Verify checkout process and order calculation

**Key Tests**:
- Step One page load
- Valid/invalid information submission
- Step Two overview display
- Subtotal calculation (single & multiple items)
- Tax calculation (8% baseline)

## 💾 Using Test Cases in Other Tools

### Import into Test Management Tools
1. Export JSON or CSV
2. Import into:
   - TestRail
   - Zephyr
   - XRay
   - Azure DevOps Test Plans
   - qTest

### Report Generation
```bash
# Generate HTML report from JSON
# Use the testcases.html file provided

# Or create custom reports using the JSON data
```

## 🔗 Test Case References

### File Locations
- **Test Code**: `src/test/java/com/bjit/ecauto/tests/*.java`
- **Documentation**: `src/test/resources/testcases.*`
- **Allure Results**: `target/allure-results/`
- **Allure Report**: `target/allure-report/`

### Key Classes
- `AbstractLoginTest` - Base class for login tests
- `AbstractProductsTest` - Base class for product tests
- `AbstractCartTest` - Base class for cart tests
- `AbstractCheckoutTest` - Base class for checkout tests
- `BasePage` - Common page object methods
- `TestListener` - Allure listener integration

## 📝 Test Execution Best Practices

1. **Run Full Suite First**
   ```bash
   mvn test
   ```

2. **Generate Report**
   ```bash
   mvn allure:report
   ```

3. **Review Failures**
   - Check Allure report for details
   - View screenshots in `reports/screenshots/FAIL_*.png`
   - Check logs in `logs/automation.log`

4. **Track Trends**
   - Compare pass rates across runs
   - Monitor test duration changes
   - Identify flaky tests

## 🐛 Debugging Failed Tests

When a test fails:

1. **Check Allure Report**
   - View detailed steps
   - Review screenshots
   - Check error messages

2. **View Execution Logs**
   ```bash
   cat logs/automation.log
   ```

3. **Captured Screenshots**
   ```
   reports/screenshots/FAIL_<testname>.png
   ```

4. **Re-run in Headed Mode**
   ```bash
   mvn -Dheadless=false test
   ```

## 📞 Support

For test-related questions:
- Review `AGENTS.md` for architecture
- Check `README.md` for setup
- Review `ALLURE_REPORTS_GUIDE.md` for reporting

---

**Last Updated**: June 17, 2024
**Total Test Cases**: 36
**Test Suites**: 4
**Supported Browsers**: Chromium, Firefox, WebKit

