# New Test Cases Summary

Created three comprehensive test suites following Playwright Java documentation and project patterns.

## Overview

All test cases use the configured base URL: **`https://www.saucedemo.com`** from `config.properties`

---

## 1. APITest (10 test methods)

**Location**: `src/test/java/com/bjit/ecauto/tests/APITest.java`

**Reference**: https://playwright.dev/java/docs/api-testing

### Purpose
Demonstrates REST API testing using Playwright's APIRequestContext with JSONPlaceholder as a mock API.

### Test Cases

| # | Test Method | Description |
|---|---|---|
| 1 | `testGetRequest()` | Make GET request to API endpoint |
| 2 | `testPostRequest()` | Make POST request with JSON body |
| 3 | `testPutRequest()` | Update resource with PUT request |
| 4 | `testDeleteRequest()` | Delete resource with DELETE request |
| 5 | `testRequestWithHeaders()` | Send custom headers in API request |
| 6 | `testResponseHeaders()` | Verify response headers |
| 7 | `testJsonResponseParsing()` | Parse and validate JSON response |
| 8 | `testErrorResponseHandling()` | Handle API error responses |
| 9 | `testMultipleRequests()` | Make multiple sequential API requests |
| 10 | `testQueryParameters()` | API request with query parameters |

### Key Features
- REST API operations (GET, POST, PUT, DELETE)
- Request/response header handling
- JSON payload serialization
- Response parsing and validation
- Error response handling
- Sequential multiple requests

---

## 2. AssertionsTest (15 test methods)

**Location**: `src/test/java/com/bjit/ecauto/tests/AssertionsTest.java`

**Reference**: https://playwright.dev/java/docs/test-assertions

### Purpose
Demonstrates Playwright assertion capabilities with automatic retry logic for reliable test validation.

### Test Cases

| # | Test Method | Description |
|---|---|---|
| 1 | `testElementVisibility()` | Verify element visibility with assertions |
| 2 | `testElementHidden()` | Assert element is hidden |
| 3 | `testTextContent()` | Verify text content with assertions |
| 4 | `testExactTextMatch()` | Assert exact text match |
| 5 | `testAttributeValues()` | Assert element attribute values |
| 6 | `testElementEnabled()` | Assert element is enabled |
| 7 | `testCheckboxState()` | Assert checkbox checked state |
| 8 | `testElementCount()` | Assert number of elements |
| 9 | `testUrlAssertion()` | Assert page URL |
| 10 | `testMultipleElementsVisible()` | Assert multiple elements are visible |
| 11 | `testTextPatternMatching()` | Assert text contains pattern |
| 12 | `testClassPresence()` | Assert element has CSS class |
| 13 | `testEditableInput()` | Assert input field is editable |
| 14 | `testElementFocused()` | Assert element has focus |
| 15 | `testNegationAssertion()` | Assert negation conditions |

### Key Features
- Element visibility assertions
- Text content validation
- Attribute value checking
- Element state assertions (enabled, disabled, checked)
- Element counting
- URL validation
- CSS class checking
- Input field editability
- Focus state validation
- Negation assertions

---

## 3. AuthenticationTest (15 test methods)

**Location**: `src/test/java/com/bjit/ecauto/tests/AuthenticationTest.java`

**Reference**: https://playwright.dev/java/docs/auth

### Purpose
Demonstrates authentication flows and session management on SauceDemo application.

### Test Cases

| # | Test Method | Description |
|---|---|---|
| 1 | `testValidLogin()` | Test successful login with valid credentials |
| 2 | `testInvalidUsernameLogin()` | Test login with invalid username |
| 3 | `testInvalidPasswordLogin()` | Test login with invalid password |
| 4 | `testEmptyCredentialsLogin()` | Test login with empty credentials |
| 5 | `testLoginAndLogout()` | Test complete login and logout flow |
| 6 | `testSessionPersistence()` | Verify session persists after login |
| 7 | `testLockedUserLogin()` | Test login with locked out user |
| 8 | `testMultipleUserLogins()` | Test switching between different user accounts |
| 9 | `testDirectAccessAfterLogin()` | Test direct access to protected page after login |
| 10 | `testUnauthorizedAccessPrevention()` | Prevent direct access without authentication |
| 11 | `testCredentialsValidation()` | Verify credential fields accept proper input |
| 12 | `testCaseSensitivity()` | Test if credentials are case-sensitive |
| 13 | `testLoginButtonState()` | Verify login button behavior |
| 14 | `testErrorMessagePersistence()` | Verify error messages display correctly |
| 15 | `testSessionTimeout()` | Verify session behavior over time |

### Key Features
- Valid login flow
- Invalid credential handling
- Empty field validation
- Login/Logout cycles
- Session persistence
- Protected page access
- Unauthorized access prevention
- User account switching
- Error message validation
- Session timeout handling

### Credentials Used (from config.properties)
- **Standard User**: `standard_user` / `secret_sauce`
- **Locked User**: `locked_out_user`
- **Problem User**: `problem_user`

---

## Integration with Framework

### Configuration
- All tests use `ConfigReader.getBaseUrl()` pointing to `https://www.saucedemo.com`
- Tests inherit from `BaseTest` for Playwright lifecycle management
- TestNG listener automatically logs test execution and captures failure screenshots

### Execution
```bash
# Run all tests
mvn test

# Run specific test class
mvn -Dgroups="Authentication & Session Management" test

# Run with headed browser
mvn -Pheaded test

# Run with Firefox
mvn -Pfirefox test
```

### TestNG Suite Configuration
- All three test classes are registered in `src/test/resources/testng.xml`
- Tests are organized into separate `<test>` sections:
  - Authentication Tests (15 methods)
  - Assertions Tests (15 methods)
  - API Tests (10 methods)

### Reporting
- Each test is annotated with Allure markers (@Feature, @Story, @Severity, @Description)
- Screenshots are automatically captured on test failure
- Test logs are written to `logs/automation.log`
- Allure HTML reports are available in `target/allure-report/`

---

## Key Patterns Followed

✅ All files follow the project structure guidelines  
✅ Tests extend appropriate base classes (BaseTest)  
✅ Allure annotations used for reporting  
✅ Page Object pattern for locators  
✅ ConfigReader for configuration management  
✅ Consistent naming conventions  
✅ Comprehensive documentation in JavaDoc  
✅ Test data from config.properties  

---

## Running the Tests

```bash
# Compile and test
mvn clean compile test-compile

# Run entire suite
mvn test

# Run specific test
mvn -Dtest=APITest test
mvn -Dtest=AssertionsTest test
mvn -Dtest=AuthenticationTest test

# Generate Allure report
mvn allure:report
mvn allure:serve
```

---

## Total Test Coverage

- **Total Test Methods**: 40 (10 + 15 + 15)
- **Test Classes**: 3 new comprehensive suites
- **Allure Stories**: 40 story-level tests documented
- **Coverage Areas**: API Testing, Assertions, Authentication & Session Management

