# Playwright TestNG Demo

A modern, scalable E2E automation framework built with **Playwright Java**, **TestNG**, and **Maven**. This project demonstrates best practices for browser automation testing with WebDriver protocol, comprehensive logging, and Allure reporting.

## 🚀 Features

- **Playwright 1.48.0** — Fast, reliable cross-browser automation (Chromium, Firefox, WebKit)
- **TestNG 7.10.2** — Powerful test framework with data-driven testing support
- **Page Object Model** — Clean, maintainable test architecture
- **Data-Driven Testing** — DataProvider-based parameterized tests
- **Allure Reports** — Detailed HTML test reports with screenshots and logs
- **Multi-Browser Support** — Native profiles for Chromium, Firefox, and WebKit
- **Configurable Execution** — JVM properties override configuration files for flexible test runs
- **Automated Screenshots** — Failure screenshots captured automatically
- **Structured Logging** — Centralized logging to `logs/automation.log`

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Git** (for cloning the repository)

No additional browser driver setup required — Playwright manages browser binaries automatically.

## 🛠️ Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/AzamReza/playwright-testng-demo.git
   cd playwright-testng-demo
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

   Maven will automatically download Playwright, TestNG, and other dependencies defined in `pom.xml`.

## 🎯 Quick Start

### Run All Tests (Default Smoke Profile - Headless Chromium)
```bash
mvn test
```

### Run Tests with Different Browsers
```bash
# Firefox (headless)
mvn -Pfirefox test

# Chromium (headed/visible)
mvn -Pheaded test
```

### Run with Custom Configuration
```bash
# Override browser and headless mode
mvn -Dbrowser=webkit -Dheadless=false test

# Set custom base URL
mvn -Dbase.url=https://staging.example.com test
```

### Run Specific Test Class
```bash
mvn -Dtest=LoginTest test
```

### Generate Allure Reports
```bash
# After running tests, generate report
mvn allure:report

# Open report in browser (if allure is installed globally)
allure open target/allure-report
```

## 📁 Project Structure

```
playwright-testng-demo/
├── src/
│   ├── main/
│   │   ├── java/com/bjit/ecauto/
│   │   │   ├── base/           # BaseTest, BasePage lifecycle & helpers
│   │   │   ├── pages/          # Page Object classes
│   │   │   ├── constants/      # Test data constants & selectors
│   │   │   └── utils/          # ConfigReader, LogHelper, ScreenshotUtil
│   │   └── resources/          # Configuration files
│   └── test/
│       ├── java/com/bjit/ecauto/
│       │   ├── tests/          # TestNG test classes
│       │   ├── dataproviders/  # @DataProvider methods
│       │   ├── listeners/      # TestListener for hooks
│       │   └── contracts/      # Test contracts/assertions
│       └── resources/
│           ├── config/         # config.properties
│           └── testng.xml      # TestNG suite definition
├── logs/                        # automation.log (runtime logs)
├── reports/
│   ├── screenshots/            # Test failure screenshots
│   └── (allure-report/)        # Generated Allure HTML report
├── pom.xml                      # Maven project configuration
└── README.md                    # This file
```

## 🔧 Configuration

Edit `src/test/resources/config/config.properties` to customize test behavior:

```properties
# Application
base.url=https://www.saucedemo.com

# Browser Settings
browser=chromium              # chromium, firefox, webkit
headless=true                # false for headed mode
viewport.width=1920
viewport.height=1080
slow.motion=0                 # Slow down action execution (ms)

# Test Credentials (example for SauceDemo)
standard.username=standard_user
standard.password=secret_sauce

# Timeouts
default.timeout=10000         # General element wait timeout
navigation.timeout=30000      # Page navigation timeout

# Reporting
screenshot.path=reports/screenshots/
screenshot.on.failure=true    # Auto-capture screenshots on failure
log.path=logs/automation.log
```

### Override via JVM Properties
JVM system properties take precedence over config.properties:
```bash
mvn -Dbase.url=https://staging.example.com -Dheadless=false test
```

## 🏗️ Architecture & Patterns

### Page Object Model
Page classes extend `BasePage` and encapsulate UI interactions:

```java
public class LoginPage extends BasePage {
    private static final String USERNAME_INPUT = "input[name='username']";
    private static final String PASSWORD_INPUT = "input[name='password']";
    private static final String LOGIN_BUTTON = "button[type='submit']";

    public LoginPage(Page page) {
        super(page);
    }

    public void login(String username, String password) {
        type(USERNAME_INPUT, username);
        type(PASSWORD_INPUT, password);
        click(LOGIN_BUTTON);
    }
}
```

### Base Test Lifecycle
`BaseTest` manages Playwright lifecycle:
- `@BeforeSuite` — Creates a single Playwright instance
- `@BeforeClass` — Launches a Browser (shared per test class)
- `@BeforeMethod` — Creates fresh BrowserContext and Page per test
- `@AfterMethod` — Captures screenshot on failure, closes context

### BasePage Helpers
Reusable methods for common interactions:
```java
click(String selector);
type(String selector, String text);
getText(String selector);
waitForVisible(String selector);
navigateToPath(String path);  // Uses ConfigReader.getBaseUrl()
```

## 📊 Test Execution Flow

1. **TestNG Suite Execution** — `testng.xml` defines test order and grouping
2. **Test Listener** — `TestListener` hooks into test lifecycle for custom reporting
3. **Failure Handling** — `ScreenshotUtil` auto-captures `FAIL_*` screenshots
4. **Logging** — `LogHelper` writes detailed logs to `logs/automation.log`
5. **Allure Integration** — Test results sent to Allure for HTML reporting

## 🐛 Debugging Tips

### View Logs
```bash
# Check runtime logs
cat logs/automation.log

# Or on Windows
type logs/automation.log
```

### Run Tests in Headed Mode
```bash
mvn -Dheadless=false test
```

### Take Manual Screenshots
The framework auto-captures screenshots on failure. Check `reports/screenshots/FAIL_*.png`

### Check Test Results
After running tests:
```bash
# View surefire reports
# target/surefire-reports/

# View Allure results (if installed)
mvn allure:report && allure open target/allure-report
```

## 📦 Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Playwright Java | 1.48.0 | Browser automation |
| TestNG | 7.10.2 | Test framework |
| Allure TestNG | 2.25.0 | Advanced reporting |
| Maven Surefire | 3.5.1 | Test execution |

See `pom.xml` for full dependency list.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Follow the Page Object Model pattern for new tests
4. Add data providers to `dataproviders/` for parameterized tests
5. Commit changes (`git commit -m 'Add new test for X'`)
6. Push to branch (`git push origin feature/your-feature`)
7. Open a Pull Request

## 📝 Test Examples

### Data-Driven Login Test
```java
@Test(dataProvider = "loginCredentials", dataProviderClass = TestDataProviders.class)
public void testLoginWithMultipleUsers(String username, String password) {
    loginPage.login(username, password);
    // assertions...
}
```

### Parameterized Product Test
```java
@Test(dataProvider = "products")
public void testProductFiltering(String productName) {
    productsPage.filterByName(productName);
    // assertions...
}
```

## 🚨 Troubleshooting

### Tests fail with "Browser not found"
- Ensure Maven downloaded Playwright binaries (run `mvn clean install`)
- Check internet connectivity for first-time browser downloads

### "Element not found" errors
- Increase `default.timeout` in `config.properties`
- Verify selectors in `pages/` are correct for target website
- Run with `-Dheadless=false` to debug element visibility

### Flaky tests
1. Check `logs/automation.log` for timing issues
2. Review failure screenshots in `reports/screenshots/`
3. Adjust timeouts in config.properties or add explicit waits in tests

## 📚 References

- [Playwright Java Documentation](https://playwright.dev/java/)
- [TestNG Documentation](https://testng.org/)
- [Allure Reports](https://docs.qameta.io/allure/)
- [SauceDemo Test App](https://www.saucedemo.com) (demo target)

## 📄 License

This project is licensed under the MIT License — see LICENSE file for details.

## 👨‍💻 Author

Created as a demonstration of modern Java E2E testing practices with Playwright and TestNG.

---

**Happy Testing! 🎉**

For more detailed information, see [AGENTS.md](AGENTS.md) and [ALLURE_REPORTS_GUIDE.md](ALLURE_REPORTS_GUIDE.md).

