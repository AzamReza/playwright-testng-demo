# AGENTS.md — How an AI assistant should work with this repo

This repository is a small Java-based E2E framework using Playwright + TestNG. The notes below highlight the precise, discoverable patterns and commands an AI coding agent needs to be productive here.

1) Big picture
- Project type: Maven Java project (JDK 17) that runs Playwright-based UI tests. See `pom.xml` for versions (Java 17, Playwright 1.48.0, TestNG 7.10.2).
- Source layout of interest:
  - Tests: `src/test/java/com/bjit/ecauto/tests/*` (TestNG test classes, typically extending Abstract*Test base classes)
  - Abstract test bases: `src/main/java/com/bjit/ecauto/base/Abstract*Test` (one per feature: AbstractLoginTest, AbstractProductsTest, AbstractCartTest, AbstractCheckoutTest) — manage lifecycle and provide page object helpers
  - Page objects & framework: `src/main/java/com/bjit/ecauto/pages/*`, `base/*`, `utils/*`, `constants/*`
  - Test contracts: `src/test/java/com/bjit/ecauto/contracts/*` (interfaces like ILoginActions, IProductsActions — define test contract requirements)
  - Test listeners: `src/test/java/com/bjit/ecauto/listeners/TestListener` (registered in testng.xml, hooks into test lifecycle for logging/reporting)
  - Data providers: `src/test/java/com/bjit/ecauto/dataproviders/TestDataProviders` (centralized @DataProvider methods)
  - TestNG suite: `src/test/resources/testng.xml`
  - Config: `src/test/resources/config/config.properties`

2) Runtime & lifecycle conventions (important for changes/bug fixes)
- Playwright lifecycle is managed in abstract base test classes (one per feature, e.g., `AbstractLoginTest`):
  - `@BeforeSuite` creates a single Playwright instance (shared across all tests)
  - `@BeforeClass` launches a Browser (shared per test class)
  - `@BeforeMethod` creates a fresh BrowserContext and Page per test method
  - `@AfterMethod` captures screenshots on failure (via `ScreenshotUtil`) and closes the context
  - Test classes extend the appropriate Abstract*Test and gain access to page object helpers
  - Implication: tests expect a single Browser per class and isolated contexts per test — avoid changing the abstract base classes without updating all dependent tests.
- Test listeners (registered in `testng.xml`):
  - `TestListener` implements `ITestListener` to hook into test lifecycle
  - Logs test start/finish events and maintains pass/fail/skip counters
  - Useful for custom reporting, hooks, and failure handling

3) Configuration & overrides
- Canonical config file: `src/test/resources/config/config.properties`.
- `ConfigReader` loads that file but always prefers JVM system properties if present (System.getProperty). Example overrides:
  - `mvn -Dbrowser=firefox -Dheadless=false test`
  - Profiles in `pom.xml` also set `browser`/`headless` by id: `smoke` (default), `headed`, `firefox`.

4) How tests are executed (use these exact commands)
- Run full suite (default smoke profile, headless):
  - mvn test
- Run with a different profile (headed or firefox):
  - mvn -Pheaded test
  - mvn -Pfirefox test
- Override individual properties directly:
  - mvn -Dbrowser=webkit -Dheadless=false test

5) Project-specific patterns & code style to follow
- Abstract Test Base Classes: Each feature area has an abstract test base (e.g., `AbstractLoginTest` in `src/main/java/com/bjit/ecauto/base/`). Concrete tests extend these to inherit lifecycle management and convenient page object access methods (e.g., `navigateToLogin()`, `getLoginPage()`). Example: `public class LoginTest extends AbstractLoginTest`.
- Test Contracts: Interfaces in `src/test/java/com/bjit/ecauto/contracts/` (e.g., `ILoginActions`, `ICartActions`) define the contract of test behaviors. Abstract test classes implement these contracts, ensuring consistent method signatures across tests. Useful for enforcing test patterns and IDEs for code completion.
- Page Object pattern: page classes extend `BasePage` and accept a Playwright `Page` in the constructor. Example: `new LoginPage(page)` in tests. Use `BasePage` helpers (`type`, `click`, `waitForVisible`, `getText`, `navigateToPath`, `getAllTexts`, `getElementCount`, etc.). See `BasePage.java` for full API.
- Navigation: prefer `navigateToPath(String path)` which uses `ConfigReader.getBaseUrl()`; a notable inconsistency exists in `LoginPage.navigate()` which hard-codes the full URL — be careful when updating base URL behavior.
- Test data: `TestDataProviders` (in `src/test/java/com/bjit/ecauto/dataproviders`) is the canonical place for DataProviders. Methods are annotated with `@DataProvider` and referenced in test methods.
- Logging and reporting:
  - Logs are written to `logs/automation.log` via `LogHelper` (methods: `.info()`, `.step()`, `.error()`).
  - `ScreenshotUtil` captures automatic failure screenshots saved to `reports/screenshots/`; failures prefixed with `FAIL_`.
  - Allure reports are generated from `target/allure-results/` (test results JSON) and published to `target/allure-report/` (HTML). Use `mvn allure:report` or `mvn allure:serve` to generate/view reports.

6) Debugging tips for agents
- When diagnosing a flaky UI test, check these places in order:
  1. `logs/automation.log` (LogHelper output)
  2. `reports/screenshots/` for `FAIL_*` images
  3. `src/test/resources/config/config.properties` for timeouts and viewport values
  4. Abstract*Test lifecycle — inspect the appropriate abstract test base class to ensure context/browser lifetime is not being changed unexpectedly
- To run tests with visible browser for local debug use `-Pheaded` or `-Dheadless=false`.

7) Integration & external dependencies
- Playwright Java is an external dependency (see `pom.xml` property `playwright.version`). There are no project scripts to install native browsers here — assume standard Playwright Java behavior (browsers downloaded or managed by the Playwright runtime). If tests fail to launch browsers locally, ensure the machine has network access or that Playwright's required browser binaries are present.
- Allure TestNG integration: `allure-testng` dependency and `allure-maven` plugin (in pom.xml) enable detailed HTML reporting. Test results are written to `target/allure-results/` as JSON files. Generate the HTML report with `mvn allure:report` or serve with `mvn allure:serve` (which opens http://localhost:4040). See `ALLURE_REPORTS_GUIDE.md` for detailed Allure setup.

8) Where to change things safely
- Add new Abstract Test Base Classes in `src/main/java/com/bjit/ecauto/base/` for new feature areas. Name pattern: `Abstract<Feature>Test` (e.g., `AbstractLoginTest`). Implement the test contract interface (e.g., `ILoginActions`) in the abstract class.
- Add new Test Contract Interfaces in `src/test/java/com/bjit/ecauto/contracts/` to define test behaviors. Example: `ILoginActions` with methods like `void testValidLogin()`, `void testInvalidLogin()`.
- Add new Page Objects in `src/main/java/com/bjit/ecauto/pages/` and follow `BasePage` helper methods.
- Add concrete test classes to `src/test/java/com/bjit/ecauto/tests/`; extend the appropriate Abstract*Test base class and include them in `testng.xml` if you want them to run in the suite ordering.
- Add new DataProviders to `src/test/java/com/bjit/ecauto/dataproviders/TestDataProviders.java`; annotate with `@DataProvider` and reference by name in test methods.
- Change timeouts or viewport sizes in `src/test/resources/config/config.properties` or pass JVM overrides for temporary runs.

9) Quick file references (examples used above)
- `pom.xml` (profiles, surefire, allure-maven plugin config)
- `src/test/resources/testng.xml` (suite definition, TestListener registration)
- `src/main/java/com/bjit/ecauto/base/AbstractLoginTest.java` (example abstract test base with lifecycle)
- `src/main/java/com/bjit/ecauto/base/BasePage.java` (page object helpers: click, type, waitForVisible, navigateToPath, getText, getAllTexts, getElementCount, isVisible, isEnabled, waitForHidden, waitForUrl)
- `src/test/java/com/bjit/ecauto/contracts/ILoginActions.java` (example test contract interface)
- `src/test/java/com/bjit/ecauto/listeners/TestListener.java` (test lifecycle listener)
- `src/test/java/com/bjit/ecauto/dataproviders/TestDataProviders.java` (centralized data providers)
- `src/main/java/com/bjit/ecauto/utils/ConfigReader.java` (config file + JVM property override logic)
- `src/main/java/com/bjit/ecauto/utils/LogHelper.java` (logging: info, step, error)
- `src/main/java/com/bjit/ecauto/utils/ScreenshotUtil.java` (screenshot capture on failure)
- `src/main/java/com/bjit/ecauto/constants/FrameworkConstants.java` (constants like DEFAULT_WAIT, LONG_WAIT, viewport defaults)

If you need deeper edits (upgrade Playwright/TestNG, change lifecycle, or add CI automation), I can produce a targeted plan and the code changes.

