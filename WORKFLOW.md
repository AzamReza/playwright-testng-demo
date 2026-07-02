# WORKFLOW.md — Execution Flow & Method Tracing

This document provides a **step-by-step execution trace** of test methods in this framework. Learn exactly which classes, methods, and files are called and in what order.

## Table of Contents

1. [Overview](#overview)
2. [Test Lifecycle](#test-lifecycle)
3. [testValidLogin() Detailed Flow](#testvalidlogin-detailed-flow)
4. [Configuration Lookup Points](#configuration-lookup-points)
5. [Class & File Reference](#class--file-reference)
6. [Other Common Workflows](#other-common-workflows)
7. [Log Output Examples](#log-output-examples)
8. [Troubleshooting Workflow](#troubleshooting-workflow)

---

## Overview

When a test method executes, it follows a structured lifecycle:

```
SUITE SETUP (@BeforeSuite)
    ↓
CLASS SETUP (@BeforeClass)
    ↓
[For Each Test Method]:
    → METHOD SETUP (@BeforeMethod)
    → EXECUTE TEST METHOD
    → METHOD TEARDOWN (@AfterMethod)
    ↓
CLASS TEARDOWN (@AfterClass)
    ↓
SUITE TEARDOWN (@AfterSuite)
```

### Why This Matters
- **Isolation:** Each test gets a fresh browser context with no state from previous tests
- **Resource management:** Playwright/Browser instances are reused efficiently
- **Observability:** Each stage logs details, making debugging easier

---

## Test Lifecycle

### @BeforeSuite — Once for Entire Test Run

**File:** `src/test/java/com/bjit/ecauto/base/BaseTest.java`  
**Method:** `suiteSetup()` (Line 21)

```java
@BeforeSuite
public void suiteSetup() {
    LogHelper.suiteStart("SauceDemo Automation Suite");
    playwright = Playwright.create();
    LogHelper.info("Playwright instance created");
}
```

**What Happens:**
1. Creates single Playwright instance (expensive resource, reused for all tests)
2. Logs suite start with configuration details
3. No cleanup needed here — held until @AfterSuite

**Output to logs/automation.log:**
```
[2025-01-15 10:23:45.123] [INFO ] [main] ################################################
[2025-01-15 10:23:45.123] [INFO ] [main] SUITE START: SauceDemo Automation Suite
[2025-01-15 10:23:45.123] [INFO ] [main] Browser: chromium
[2025-01-15 10:23:45.123] [INFO ] [main] Headless: true
[2025-01-15 10:23:45.123] [INFO ] [main] Base URL: https://www.saucedemo.com
```

---

### @BeforeClass — Once Per Test Class

**File:** `src/test/java/com/bjit/ecauto/base/BaseTest.java`  
**Method:** `launchBrowser()` (Line 40)

```java
@BeforeClass
public void launchBrowser() {
    String browserType = ConfigReader.getBrowser();           // Read from config
    boolean headless = ConfigReader.isHeadless();             // Read from config
    int slowMotion = ConfigReader.getSlowMotion();            // Read from config
    
    BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
        .setHeadless(headless)
        .setSlowMo(slowMotion);
    
    switch (browserType.toLowerCase()) {
        case "firefox":
            browser = playwright.firefox().launch(launchOptions);
            break;
        case "webkit":
            browser = playwright.webkit().launch(launchOptions);
            break;
        default:
            browser = playwright.chromium().launch(launchOptions);
            break;
    }
    
    LogHelper.info(browserType + " browser launched");
}
```

**What Happens:**
1. Reads browser type from config (chromium/firefox/webkit)
2. Reads headless flag (can override with -Dheadless=false)
3. Creates browser process (shared for all tests in this class)
4. Logs browser launch

**Output:**
```
[2025-01-15 10:23:47.345] [INFO ] [main] Launching browser: chromium | headless: true | slowMotion: 0ms
[2025-01-15 10:23:47.456] [INFO ] [main] chromium browser launched
```

---

### @BeforeMethod — Before Each Test Method

**File:** `src/test/java/com/bjit/ecauto/base/BaseTest.java`  
**Method:** `createContext(ITestResult result)` (Line 80)

```java
@BeforeMethod
public void createContext(ITestResult result) {
    LogHelper.testStart(result.getMethod().getMethodName());
    
    context = browser.newContext(new Browser.NewContextOptions()
        .setViewportSize(
            ConfigReader.getViewportWidth(),
            ConfigReader.getViewportHeight()));
    
    page = context.newPage();
    LogHelper.info("New browser context and page created");
}
```

**What Happens:**
1. Logs test start
2. Creates fresh BrowserContext (isolated cookies/storage)
3. Creates fresh Page object
4. Sets viewport size from config (1920x1080)

**Output:**
```
[2025-01-15 10:23:48.456] [INFO ] [main] ========================================
[2025-01-15 10:23:48.456] [INFO ] [main] TEST START: testValidLogin
[2025-01-15 10:23:48.456] [INFO ] [main] ========================================
[2025-01-15 10:23:48.567] [INFO ] [main] New browser context and page created
```

**Key Point:** `this.page` is now available to test methods and page objects!

---

### @AfterMethod — After Each Test Method

**File:** `src/test/java/com/bjit/ecauto/base/BaseTest.java`  
**Method:** `closeContext(ITestResult result)` (Line 93)

```java
@AfterMethod
public void closeContext(ITestResult result) {
    String testName = result.getMethod().getMethodName();
    
    // Screenshot on failure
    if (result.getStatus() == ITestResult.FAILURE) {
        if (page != null && ConfigReader.screenshotOnFailure()) {
            String errorMsg = result.getThrowable() != null
                ? result.getThrowable().getMessage() : "Unknown error";
            ScreenshotUtil.captureOnFailure(page, testName, errorMsg);
        }
        LogHelper.testEnd(testName, "FAILED");
    } else if (result.getStatus() == ITestResult.SKIP) {
        LogHelper.testEnd(testName, "SKIPPED");
    } else {
        LogHelper.testEnd(testName, "PASSED");
    }
    
    if (context != null) {
        context.close();
        LogHelper.debug("Browser context closed");
    }
}
```

**What Happens:**
1. Checks test result status
2. **If FAILED:** Takes screenshot and saves to `reports/screenshots/FAIL_*.png`
3. Logs test end status
4. Closes browser context (cleanup)

**Output on PASS:**
```
[2025-01-15 10:23:51.334] [INFO ] [main] TEST END: testValidLogin => PASSED
[2025-01-15 10:23:51.334] [INFO ] [main] ----------------------------------------
[2025-01-15 10:23:51.445] [DEBUG] [main] Browser context closed
```

**Output on FAIL:**
```
[2025-01-15 10:23:51.334] [ERROR] [main] Test FAILED: testValidLogin
[2025-01-15 10:23:51.334] [ERROR] [main] Error: Element #user-name not found
[2025-01-15 10:23:51.334] [ERROR] [main] Screenshot: reports/screenshots/FAIL_testValidLogin_2025-01-15_10-23-47.png
[2025-01-15 10:23:51.334] [INFO ] [main] TEST END: testValidLogin => FAILED
[2025-01-15 10:23:51.334] [INFO ] [main] ----------------------------------------
```

---

## testValidLogin() Detailed Flow

### Test Code
```java
@Test(priority = 1, description = "Verify standard user can login successfully")
public void testValidLogin() {
    ProductsPage productsPage = navigateToLogin()
            .loginAs(ConfigReader.getStandardUsername(),
                    ConfigReader.getStandardPassword());

    Assert.assertTrue(productsPage.isOnProductsPage(),
            "Should be on Products page after login");
    Assert.assertEquals(productsPage.getPageTitle(), "Products",
            "Page title should be 'Products'");
}
```

---

### Step 1: navigateToLogin()

**File:** `src/test/java/com/bjit/ecauto/base/AbstractLoginTest.java`  
**Method:** `navigateToLogin()` (Line 26)

```java
protected LoginPage navigateToLogin() {
    LoginPage loginPage = getLoginPage();    // Step 1.1
    loginPage.navigate();                     // Step 1.2
    return loginPage;
}
```

#### Step 1.1: getLoginPage()

**File:** `src/test/java/com/bjit/ecauto/base/AbstractLoginTest.java`  
**Method:** `getLoginPage()` (Line 18)

```java
protected LoginPage getLoginPage() {
    return new LoginPage(page);
}
```

**What Happens:**
1. Creates new LoginPage instance
2. Passes `page` object (created in @BeforeMethod) to constructor
3. LoginPage constructor calls `super(page)` → BasePage stores the page reference

**Why This Pattern?**
- Factory method centralizes page object creation
- Single place to modify if LoginPage constructor changes
- Easy to mock for testing

---

#### Step 1.2: loginPage.navigate()

**File:** `src/main/java/com/bjit/ecauto/pages/LoginPage.java`  
**Method:** `navigate()` (Line 29)

```java
public LoginPage navigate() {
    navigateTo("https://www.saucedemo.com");
    LogHelper.info("Login page loaded");
    return this;
}
```

**Calls:** `BasePage.navigateTo(String url)` (Line 23)

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `navigateTo()` (Line 23)

```java
protected void navigateTo(String url) {
    LogHelper.step("Navigating to: " + url);
    page.navigate(url, new Page.NavigateOptions()
        .setTimeout(ConfigReader.getNavigationTimeout()));
}
```

**What Happens:**
1. Logs navigation step
2. Gets navigation timeout from ConfigReader: `30000` ms
3. Calls Playwright's `page.navigate(url, timeout)`
4. Browser loads the page
5. Returns (implicit wait for page load)

**Log Output:**
```
[2025-01-15 10:23:48.678] [STEP ] [main] >> Navigating to: https://www.saucedemo.com
[2025-01-15 10:23:49.789] [INFO ] [main] Login page loaded
```

**ConfigReader Lookup:**
```
ConfigReader.getNavigationTimeout()
    ↓ Check system property: System.getProperty("navigation.timeout")
    ↓ If not found, read from config.properties: navigation.timeout=30000
    ↓ Returns 30000
```

---

### Step 2: .loginAs(username, password)

**File:** `src/main/java/com/bjit/ecauto/pages/LoginPage.java`  
**Method:** `loginAs()` (Line 63)

```java
public ProductsPage loginAs(String username, String password) {
    LogHelper.info("Logging in as: " + username);
    enterUsername(username);           // Step 2.1
    enterPassword(password);           // Step 2.2
    return clickLogin();               // Step 2.3
}
```

**Arguments:**
- `username` = `ConfigReader.getStandardUsername()` → `"standard_user"`
- `password` = `ConfigReader.getStandardPassword()` → `"secret_sauce"`

#### Step 2.1: enterUsername(username)

**File:** `src/main/java/com/bjit/ecauto/pages/LoginPage.java`  
**Method:** `enterUsername()` (Line 35)

```java
public LoginPage enterUsername(String username) {
    type(USERNAME_INPUT, username);
    return this;
}
```

**Where:** `USERNAME_INPUT = "#user-name"` (CSS selector)

**Calls:** `BasePage.type()` (Line 36)

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `type()` (Line 36)

```java
protected void type(String selector, String text) {
    LogHelper.step("Typing '" + text + "' into " + selector);
    waitForVisible(selector);                    // Wait for element
    page.fill(selector, text);                   // Clear and type
}
```

**Detailed Execution:**

1. **Log the action:**
   ```
   LogHelper.step("Typing 'standard_user' into #user-name")
   ```
   Output: `[STEP ] >> Typing 'standard_user' into #user-name`

2. **Wait for element visibility:**
   ```
   waitForVisible("#user-name")
   ```
   **File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`
   **Method:** `waitForVisible()` (Line 115)

   ```java
   protected void waitForVisible(String selector) {
       page.waitForSelector(selector, new Page.WaitForSelectorOptions()
           .setState(WaitForSelectorState.VISIBLE)
           .setTimeout(ConfigReader.getDefaultTimeout()));
   }
   ```

   **Timeout lookup:**
   ```
   ConfigReader.getDefaultTimeout()
       ↓ Check system property
       ↓ If not found, read from config.properties: default.timeout=10000
       ↓ Returns 10000 ms (10 seconds)
   ```

   **What happens:**
   - Browser locates element with selector `#user-name`
   - Waits for it to be visible (not hidden)
   - If found within 10s → continue
   - If not found → TimeoutException → Test fails

3. **Type the text:**
   ```java
   page.fill("#user-name", "standard_user")
   ```

   **What happens:**
   - Focus on the input field
   - Clear existing text
   - Type "standard_user"
   - Input now contains: `standard_user`

**Log Output:**
```
[2025-01-15 10:23:50.001] [STEP ] [main] >> Typing 'standard_user' into #user-name
```

---

#### Step 2.2: enterPassword(password)

**File:** `src/main/java/com/bjit/ecauto/pages/LoginPage.java`  
**Method:** `enterPassword()` (Line 40)

```java
public LoginPage enterPassword(String password) {
    typeSecure(PASSWORD_INPUT, password);
    return this;
}
```

**Where:** `PASSWORD_INPUT = "#password"` (CSS selector)

**Calls:** `BasePage.typeSecure()` (Line 42)

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `typeSecure()` (Line 42)

```java
protected void typeSecure(String selector, String text) {
    LogHelper.step("Typing [MASKED] into " + selector);    // Don't log password!
    waitForVisible(selector);
    page.fill(selector, text);
}
```

**Key Difference from `type()`:**
- Logs `[MASKED]` instead of actual password
- Security best practice: passwords never appear in logs

**Execution:**
1. Log: `[STEP ] >> Typing [MASKED] into #password`
2. Wait for `#password` element (10 seconds)
3. Fill with "secret_sauce" (but logs show [MASKED])

**Log Output:**
```
[2025-01-15 10:23:50.112] [STEP ] [main] >> Typing [MASKED] into #password
```

---

#### Step 2.3: clickLogin()

**File:** `src/main/java/com/bjit/ecauto/pages/LoginPage.java`  
**Method:** `clickLogin()` (Line 45)

```java
public ProductsPage clickLogin() {
    click(LOGIN_BUTTON);
    return new ProductsPage(page);
}
```

**Where:** `LOGIN_BUTTON = "#login-button"` (CSS selector)

**Calls:** `BasePage.click()` (Line 48)

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `click()` (Line 48)

```java
protected void click(String selector) {
    LogHelper.step("Clicking: " + selector);
    waitForVisible(selector);
    page.click(selector);
}
```

**Execution:**
1. Log: `[STEP ] >> Clicking: #login-button`
2. Wait for button to be visible (10 seconds)
3. Click the button → Form submits to server
4. Server validates credentials
5. Server redirects to `/inventory` (products page)
6. Page loads with products

**Log Output:**
```
[2025-01-15 10:23:50.223] [STEP ] [main] >> Clicking: #login-button
```

**After Click — Page Navigation:**
```
User enters credentials
         ↓
Click login button
         ↓
Form submits to server (POST /login)
         ↓
Server validates: standard_user / secret_sauce
         ↓
✓ Valid → Set session cookie
         ↓
Redirect to https://www.saucedemo.com/inventory.html
         ↓
Browser loads products page
         ↓
ProductsPage instance created with new page content
```

---

### Step 3: Assert.assertTrue(productsPage.isOnProductsPage())

**Calls:** `ProductsPage.isOnProductsPage()` (Line 42)

**File:** `src/main/java/com/bjit/ecauto/pages/ProductsPage.java`  
**Method:** `isOnProductsPage()` (Line 42)

```java
public boolean isOnProductsPage() {
    return isVisible(PAGE_TITLE) && getText(PAGE_TITLE).equals("Products");
}
```

**Where:** `PAGE_TITLE = ".title"` (CSS class selector)

#### Breakdown:

**1. isVisible(".title")**

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `isVisible()` (Line 104)

```java
protected boolean isVisible(String selector) {
    return page.isVisible(".title");
}
```

**What happens:**
- Browser checks if element with class "title" exists
- Checks if it's visible (not hidden by CSS)
- Returns `true` or `false`

**Expected:** `true` (Products page has `.title` element)

**2. getText(".title")**

**File:** `src/main/java/com/bjit/ecauto/base/BasePage.java`  
**Method:** `getText()` (Line 70)

```java
protected String getText(String selector) {
    waitForVisible(selector);
    return page.textContent(selector).trim();
}
```

**Execution:**
1. Wait for `.title` to be visible (10 seconds)
2. Get text content: `"Products"`
3. Trim whitespace
4. Return: `"Products"`

**3. Final Boolean:**
```java
isVisible(".title") && getText(".title").equals("Products")
↓
true && "Products".equals("Products")
↓
true && true
↓
true ✓
```

#### TestNG Assert:

**TestNG Framework**

```java
Assert.assertTrue(true, "Should be on Products page after login")
    ↓ Since condition is true → Assertion passes ✓
    ↓ No exception thrown
    ↓ Test continues
```

**If it had been false:**
```java
Assert.assertTrue(false, "Should be on Products page after login")
    ↓ AssertionError thrown
    ↓ Test marked as FAILED
    ↓ @AfterMethod captures screenshot
    ↓ Message logged: "Should be on Products page after login"
```

---

### Step 4: Assert.assertEquals(productsPage.getPageTitle(), "Products")

**Calls:** `ProductsPage.getPageTitle()` (Line 38)

**File:** `src/main/java/com/bjit/ecauto/pages/ProductsPage.java`  
**Method:** `getPageTitle()` (Line 38)

```java
public String getPageTitle() {
    return getText(PAGE_TITLE);
}
```

**Calls:** `BasePage.getText(".title")` (same as Step 3.2)

**Execution:**
1. Wait for `.title` visible (10 seconds)
2. Get text: `"Products"`
3. Return: `"Products"`

#### TestNG Assert:

```java
Assert.assertEquals("Products", "Products", "Page title should be 'Products'")
    ↓ "Products".equals("Products") = true ✓
    ↓ Assertion passes
```

---

## Configuration Lookup Points

During test execution, ConfigReader is queried at these points:

### @BeforeClass
```
1. ConfigReader.getBrowser()            → "chromium"
2. ConfigReader.isHeadless()            → true
3. ConfigReader.getSlowMotion()         → 0
```

### @BeforeMethod
```
4. ConfigReader.getViewportWidth()      → 1920
5. ConfigReader.getViewportHeight()     → 1080
```

### testValidLogin() execution
```
6. ConfigReader.getStandardUsername()   → "standard_user"
7. ConfigReader.getStandardPassword()   → "secret_sauce"
8. ConfigReader.getNavigationTimeout()  → 30000 (in navigateTo)
9. ConfigReader.getDefaultTimeout()     → 10000 (in waitForVisible, multiple times)
10. ConfigReader.getDefaultTimeout()    → 10000 (in isOnProductsPage checks)
```

### @AfterMethod
```
11. ConfigReader.screenshotOnFailure()  → true (if test failed)
```

### Where Values Come From

**Priority Order:**
1. **JVM System Properties** (highest priority)
   ```bash
   mvn -Dbase.url=https://staging.com test
   mvn -Dheadless=false test
   mvn -Dcdefault.timeout=5000 test
   ```

2. **config.properties** (default)
   ```properties
   src/test/resources/config/config.properties:
   base.url=https://www.saucedemo.com
   browser=chromium
   headless=true
   default.timeout=10000
   navigation.timeout=30000
   standard.username=standard_user
   standard.password=secret_sauce
   ```

---

## Class & File Reference

| Execution Step | Class | File | Method | Line | Purpose |
|---|---|---|---|---|---|
| 0.1 | BaseTest | `base/BaseTest.java` | @BeforeSuite | 21 | Create Playwright |
| 0.2 | BaseTest | `base/BaseTest.java` | @BeforeClass | 40 | Launch Browser |
| 0.3 | BaseTest | `base/BaseTest.java` | @BeforeMethod | 80 | Create Context+Page |
| 1.0 | AbstractLoginTest | `base/AbstractLoginTest.java` | navigateToLogin() | 26 | Navigate to login |
| 1.1 | AbstractLoginTest | `base/AbstractLoginTest.java` | getLoginPage() | 18 | Create LoginPage |
| 1.2 | LoginPage | `pages/LoginPage.java` | navigate() | 29 | Call navigateTo() |
| 1.3 | BasePage | `base/BasePage.java` | navigateTo() | 23 | Navigate to URL |
| 2.0 | LoginPage | `pages/LoginPage.java` | loginAs() | 63 | Login flow |
| 2.1 | LoginPage | `pages/LoginPage.java` | enterUsername() | 35 | Type username |
| 2.1b | BasePage | `base/BasePage.java` | type() | 36 | Type with wait |
| 2.2 | LoginPage | `pages/LoginPage.java` | enterPassword() | 40 | Type password |
| 2.2b | BasePage | `base/BasePage.java` | typeSecure() | 42 | Type (masked) |
| 2.3 | LoginPage | `pages/LoginPage.java` | clickLogin() | 45 | Click login button |
| 2.3b | BasePage | `base/BasePage.java` | click() | 48 | Click with wait |
| 3.0 | ProductsPage | `pages/ProductsPage.java` | isOnProductsPage() | 42 | Check if on products page |
| 3.1 | BasePage | `base/BasePage.java` | isVisible() | 104 | Check element visible |
| 3.2 | BasePage | `base/BasePage.java` | getText() | 70 | Get element text |
| 4.0 | ProductsPage | `pages/ProductsPage.java` | getPageTitle() | 38 | Get page title |
| 5.0 | Assert | TestNG | assertEquals() | - | Verify page title |
| 6.0 | BaseTest | `base/BaseTest.java` | @AfterMethod | 93 | Close context |

---

## Other Common Workflows

### Workflow: Login with Invalid Credentials (testInvalidLogin)

```java
public void testInvalidLogin(String username, String password, String expectedError) {
    navigateToLogin()
            .loginExpectingError(username, password);

    Assert.assertTrue(getLoginPage().isErrorDisplayed(),
            "Error message should be displayed");

    String actualError = getLoginPage().getErrorMessage();
    Assert.assertTrue(actualError.contains(expectedError),
            "Error should contain: '" + expectedError + "'");
}
```

**Flow:**
```
navigateToLogin()  [same as before]
    ↓
.loginExpectingError(username, password)
    ↓ LoginPage.loginExpectingError() → enterUsername → enterPassword → clickLoginExpectingError()
    ↓ clickLoginExpectingError() calls click() but doesn't create ProductsPage (returns LoginPage)
    ↓
.isErrorDisplayed()
    ↓ Check if error message element visible
    ↓
.getErrorMessage()
    ↓ Get text of error element: "[data-test='error']"
    ↓
Assert contains expected error text
```

**Key Difference:**
- After `clickLoginExpectingError()`, page doesn't redirect (invalid login)
- Still on login page with error message
- Page object remains LoginPage, not ProductsPage

---

### Workflow: Logout (testLogout)

```java
public void testLogout() {
    ProductsPage productsPage = navigateToLogin()
            .loginAs(ConfigReader.getStandardUsername(),
                    ConfigReader.getStandardPassword());

    Assert.assertTrue(productsPage.isOnProductsPage());

    LoginPage loggedOutPage = productsPage.logout();

    Assert.assertTrue(loggedOutPage.isOnLoginPage());
}
```

**Flow:**
```
navigateToLogin().loginAs()  [same as testValidLogin]
    ↓ Returns ProductsPage
    ↓
productsPage.logout()
    ↓ ProductsPage.logout() [Line 164]
    ↓ click(BURGER_MENU)     → "#react-burger-menu-btn"
    ↓ click(LOGOUT_LINK)     → "#logout_sidebar_link"
    ↓ return new LoginPage(page)  → Page now showing login page again
    ↓
loggedOutPage.isOnLoginPage()
    ↓ Check if LOGIN_BUTTON visible: "#login-button"
    ↓ Returns true if button found
```

---

### Workflow: Data-Driven Login (testAllUserTypes)

```java
@Test(dataProvider = "allUserTypes", dataProviderClass = TestDataProviders.class)
public void testAllUserTypes(String username, String password, boolean shouldPass) {
    getLoginPage().navigate();

    if (shouldPass) {
        ProductsPage productsPage = getLoginPage().loginAs(username, password);
        Assert.assertTrue(productsPage.isOnProductsPage(),
                username + " should be able to login");
    } else {
        getLoginPage().loginExpectingError(username, password);
        Assert.assertTrue(getLoginPage().isErrorDisplayed(),
                username + " should see error message");
    }
}
```

**Data from TestDataProviders:**
```java
@DataProvider(name = "allUserTypes")
public static Object[][] allUserTypes() {
    return new Object[][] {
        { "standard_user",           "secret_sauce", true  },   // Run 1
        { "locked_out_user",         "secret_sauce", false },   // Run 2
        { "problem_user",            "secret_sauce", true  },   // Run 3
        { "performance_glitch_user", "secret_sauce", true  },   // Run 4
        { "error_user",              "secret_sauce", true  },   // Run 5
        { "visual_user",             "secret_sauce", true  }    // Run 6
    };
}
```

**Execution:**
- Test runs **6 times** with different data
- Each run:
  1. Gets fresh context (@BeforeMethod)
  2. Navigates to login
  3. Either logins successfully (if shouldPass=true) or expects error
  4. Closes context (@AfterMethod)

**In Test Reports:**
```
testAllUserTypes[0] with "standard_user" → PASSED
testAllUserTypes[1] with "locked_out_user" → PASSED
testAllUserTypes[2] with "problem_user" → PASSED
testAllUserTypes[3] with "performance_glitch_user" → PASSED
testAllUserTypes[4] with "error_user" → PASSED
testAllUserTypes[5] with "visual_user" → PASSED
```

---

## Log Output Examples

### Successful Test (Full Log)

```
[2025-01-15 10:23:45.123] [INFO ] [main] ################################################
[2025-01-15 10:23:45.123] [INFO ] [main] SUITE START: SauceDemo Automation Suite
[2025-01-15 10:23:45.123] [INFO ] [main] Browser: chromium
[2025-01-15 10:23:45.123] [INFO ] [main] Headless: true
[2025-01-15 10:23:45.123] [INFO ] [main] Base URL: https://www.saucedemo.com
[2025-01-15 10:23:45.123] [INFO ] [main] ################################################
[2025-01-15 10:23:46.234] [INFO ] [main] Launching browser: chromium | headless: true | slowMotion: 0ms
[2025-01-15 10:23:47.345] [INFO ] [main] chromium browser launched
[2025-01-15 10:23:48.456] [INFO ] [main] ========================================
[2025-01-15 10:23:48.456] [INFO ] [main] TEST START: testValidLogin
[2025-01-15 10:23:48.456] [INFO ] [main] ========================================
[2025-01-15 10:23:48.567] [INFO ] [main] New browser context and page created
[2025-01-15 10:23:48.678] [STEP ] [main] >> Navigating to: https://www.saucedemo.com
[2025-01-15 10:23:49.789] [INFO ] [main] Login page loaded
[2025-01-15 10:23:49.890] [INFO ] [main] Logging in as: standard_user
[2025-01-15 10:23:50.001] [STEP ] [main] >> Typing 'standard_user' into #user-name
[2025-01-15 10:23:50.112] [STEP ] [main] >> Typing [MASKED] into #password
[2025-01-15 10:23:50.223] [STEP ] [main] >> Clicking: #login-button
[2025-01-15 10:23:51.334] [INFO ] [main] TEST END: testValidLogin => PASSED
[2025-01-15 10:23:51.334] [INFO ] [main] ----------------------------------------
[2025-01-15 10:23:51.445] [DEBUG] [main] Browser context closed
```

---

### Failed Test (Element Not Found)

```
[2025-01-15 10:23:45.123] [INFO ] [main] SUITE START: SauceDemo Automation Suite
...
[2025-01-15 10:23:48.456] [INFO ] [main] TEST START: testValidLogin
[2025-01-15 10:23:48.456] [INFO ] [main] ========================================
[2025-01-15 10:23:48.567] [INFO ] [main] New browser context and page created
[2025-01-15 10:23:48.678] [STEP ] [main] >> Navigating to: https://www.saucedemo.com/wrong-page
[2025-01-15 10:23:49.789] [INFO ] [main] Login page loaded
[2025-01-15 10:23:49.890] [INFO ] [main] Logging in as: standard_user
[2025-01-15 10:23:50.001] [STEP ] [main] >> Typing 'standard_user' into #user-name
[2025-01-15 10:23:50.112] [STEP ] [main] >> Typing [MASKED] into #password
[2025-01-15 10:23:50.223] [STEP ] [main] >> Clicking: #login-button
[2025-01-15 10:23:60.334] [ERROR] [main] Test FAILED: testValidLogin
[2025-01-15 10:23:60.334] [ERROR] [main] Error: Timeout 10000ms exceeded waiting for locator('#user-name')
[2025-01-15 10:23:60.334] [ERROR] [main] Screenshot: reports/screenshots/FAIL_testValidLogin_2025-01-15_10-23-56.png
[2025-01-15 10:23:60.334] [INFO ] [main] TEST END: testValidLogin => FAILED
[2025-01-15 10:23:60.334] [INFO ] [main] ----------------------------------------
```

**File created:** `reports/screenshots/FAIL_testValidLogin_2025-01-15_10-23-56.png`

---

## Troubleshooting Workflow

### Test Fails: How to Debug

#### Step 1: Check logs/automation.log

```bash
# On Windows
type logs/automation.log | findstr "ERROR\|FAIL"

# Find the error
[ERROR] Test FAILED: testValidLogin
[ERROR] Error: Timeout waiting for selector #user-name
```

#### Step 2: Find the screenshot

```bash
# List failure screenshots
dir /s reports/screenshots/FAIL_*

# Open the most recent FAIL_*.png to see page state
```

#### Step 3: Run in headed mode

```bash
# See browser actually click/type
mvn -Dheadless=false test -Dtest=LoginTest#testValidLogin
```

#### Step 4: Add slowdown

```bash
# See each action in slow motion (2 seconds per action)
mvn -Dslow.motion=2000 test -Dtest=LoginTest#testValidLogin
```

#### Step 5: Increase timeout temporarily

```bash
# Give elements 20s instead of 10s to appear
mvn -Ddefault.timeout=20000 test -Dtest=LoginTest#testValidLogin
```

#### Step 6: Check config values

If page won't load, verify:
```bash
# Is base.url correct?
mvn -Dbase.url=https://www.saucedemo.com test

# Is viewport correct?
mvn -Dviewport.width=1920 -Dviewport.height=1080 test
```

#### Step 7: Trace execution manually

1. Open `testValidLogin()` in IDE
2. Set breakpoint on `navigateToLogin()`
3. Run test with debugger: `mvn -Dheadless=false test -Dtest=LoginTest#testValidLogin`
4. Step through code line by line
5. Inspect variable values in debugger
6. Watch browser as code executes

---

### Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Timeout waiting for selector | Element selector wrong | Check CSS selector, update in page object |
| Timeout waiting for selector | Page didn't load | Increase `navigation.timeout`, check base URL |
| AssertionError: Should be on Products page | Login didn't work | Check credentials in config.properties |
| AssertionError: Should be on Products page | Wrong page selector | Update `.title` selector if website changed |
| Browser not found | Playwright binaries missing | Run `mvn clean install` |
| Tests run headless and I can't see | Forgot profile flag | Run `mvn -Pheaded test` |
| Test passes locally but fails in CI | Different timeouts needed | Increase `default.timeout` in config.properties |
| Password logged in output | Using `type()` instead of `typeSecure()` | Use `typeSecure()` for sensitive fields |

---

## Quick Reference: Running Tests

### Run Commands

```bash
# Run all tests (headless chromium, default)
mvn test

# Run specific test method
mvn -Dtest=LoginTest#testValidLogin test

# Run with different browsers
mvn -Pfirefox test          # Firefox headless
mvn -Pheaded test           # Chromium visible
mvn -Dbrowser=webkit test   # WebKit headless

# Run with custom config
mvn -Dheadless=false test                           # Visible browser
mvn -Dslow.motion=2000 test                          # 2s delay per action
mvn -Ddefault.timeout=20000 test                     # 20s element waits
mvn -Dbase.url=https://staging.com test              # Different environment

# Generate Allure report (after tests)
mvn allure:report
allure open target/allure-report
```

---

## Summary

Every test follows this pattern:

```
1. TEST STARTS
   ├─ @BeforeSuite (once)      → Playwright.create()
   ├─ @BeforeClass (once/class)→ Browser.launch()
   ├─ @BeforeMethod (each test)→ Context.create() + Page.create()
   │
   ├─ TEST METHOD EXECUTES
   │  └─ Calls page objects → Calls BasePage helpers → Calls Playwright API
   │
   ├─ @AfterMethod (cleanup)   → Screenshot if failed, Context.close()
   ├─ @AfterClass (once/class) → Browser.close()
   └─ @AfterSuite (once)       → Playwright.close()

2. Each level uses ConfigReader for:
   - Browser type (chromium/firefox/webkit)
   - Headless flag (true/false)
   - Timeouts (element wait, page navigation)
   - Viewport size
   - Base URL
   - Credentials

3. All actions logged to logs/automation.log with timestamp + level

4. On failure, screenshot saved to reports/screenshots/FAIL_*.png

5. Test results viewable in Allure reports or TestNG HTML reports
```

---

**This document is your guide to understanding every step of test execution!**

