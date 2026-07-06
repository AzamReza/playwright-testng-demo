# Quick Reference: Running the New Tests

## Created Test Classes (40 total tests)

✅ **APITest.java** - 10 REST API test methods  
✅ **AssertionsTest.java** - 15 assertion test methods  
✅ **AuthenticationTest.java** - 15 authentication test methods  

---

## Base URL Configuration

**Configured URL**: `https://www.saucedemo.com`

Located in: `src/test/resources/config/config.properties`
```properties
base.url=https://www.saucedemo.com
```

---

## Run Tests with Maven

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
# API Tests only
mvn -Dtest=APITest test

# Assertions Tests only
mvn -Dtest=AssertionsTest test

# Authentication Tests only
mvn -Dtest=AuthenticationTest test
```

### Run with Browser Profiles
```bash
# Headless Chrome (default)
mvn test

# Headed Chrome (visible browser)
mvn -Pheaded test

# Firefox
mvn -Pfirefox test

# WebKit
mvn -Dtest=APITest -Dbrowser=webkit test
```

### Run Specific Test Method
```bash
# Run just one test method
mvn -Dtest=APITest#testGetRequest test
mvn -Dtest=AuthenticationTest#testValidLogin test
```

---

## Test Statistics

| Test Suite | Count | Source | Reference |
|---|---|---|---|
| **APITest** | 10 | Playwright Request API | https://playwright.dev/java/docs/api-testing |
| **AssertionsTest** | 15 | Playwright Assertions | https://playwright.dev/java/docs/test-assertions |
| **AuthenticationTest** | 15 | SauceDemo Login Flow | https://playwright.dev/java/docs/auth |
| **Total** | **40** | - | - |

---

## Project Integration

✅ All tests extend `BaseTest` for proper lifecycle management  
✅ Registered in `testng.xml` for suite execution  
✅ Use `ConfigReader` for configuration management  
✅ Allure annotations for reporting  
✅ Screenshot capture on failure  
✅ Automatic logging to `logs/automation.log`  

---

## Key Credentials (SauceDemo)

```properties
# Standard user (works normally)
Username: standard_user
Password: secret_sauce

# Problem user (UI glitches)
Username: problem_user
Password: secret_sauce

# Locked out user
Username: locked_out_user
Password: secret_sauce
```

---

## View Test Results

### Logs
```bash
# View test execution logs
cat logs/automation.log
```

### Screenshots (on failure)
```bash
# Failed test screenshots
ls reports/screenshots/FAIL_*.png
```

### Allure Reports
```bash
# Generate HTML report
mvn allure:report

# View in browser (opens at http://localhost:4040)
mvn allure:serve
```

---

## Compilation

### Clean Build
```bash
mvn clean compile test-compile
```

### Build Status
```
BUILD SUCCESS - All 23 test classes compile ✓
```

---

## Framework Files Modified

| File | Change | Status |
|---|---|---|
| `src/test/java/.../tests/APITest.java` | ✨ NEW | Created |
| `src/test/java/.../tests/AssertionsTest.java` | ✨ NEW | Created |
| `src/test/java/.../tests/AuthenticationTest.java` | ✨ NEW | Created |
| `src/test/resources/testng.xml` | ✏️ UPDATED | Added 3 test classes |
| `NEW_TEST_CASES_SUMMARY.md` | ✨ NEW | Created |

---

## Example Commands

```bash
# Run all new tests with reporting
mvn clean test -DskipTests=false -Dgroups="REST API Testing,Test Assertions & Validations,Authentication & Session Management"

# Run Authentication tests in headed mode
mvn -Dtest=AuthenticationTest -Pheaded test

# Run Assertions tests and generate report
mvn -Dtest=AssertionsTest test && mvn allure:report

# Run API tests and serve live report
mvn -Dtest=APITest test && mvn allure:serve
```

---

## Test Execution Flow

```
TestNG Suite
├── Login Tests (existing)
├── Products Tests (existing)
├── Cart Tests (existing)
├── Checkout Tests (existing)
├── Accessibility Tests (existing)
├── ✨ Authentication Tests (NEW - 15 methods)
├── ✨ Assertions Tests (NEW - 15 methods)
└── ✨ API Tests (NEW - 10 methods)
    
Total: 8 test classes, 70+ test methods
```

---

## Dependencies

- **Playwright 1.48.0** - Browser automation
- **TestNG 7.10.2** - Test framework
- **JDK 17** - Java version
- **Maven 3.8.9+** - Build tool

---

## Notes

- All tests use `https://www.saucedemo.com` as configured
- Tests are isolated and can run independently
- Parallel execution is currently disabled in `testng.xml`
- Screenshots captured automatically on failure
- Each test has @Severity levels for filtering (CRITICAL, NORMAL, MINOR)
- Allure reporting enabled for detailed test documentation

---

**Created**: July 6, 2026  
**Framework**: Playwright Java + TestNG + Allure  
**Status**: ✅ Ready to Run

