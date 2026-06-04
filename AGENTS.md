# AGENTS.md — How an AI assistant should work with this repo

This repository is a small Java-based E2E framework using Playwright + TestNG. The notes below highlight the precise, discoverable patterns and commands an AI coding agent needs to be productive here.

1) Big picture
- Project type: Maven Java project (JDK 17) that runs Playwright-based UI tests. See `pom.xml` for versions (Java 17, Playwright 1.48.0, TestNG 7.10.2).
- Source layout of interest:
  - Tests: `src/test/java/com/bjit/ecauto/tests/*` (TestNG tests)
  - Page objects & framework: `src/main/java/com/bjit/ecauto/pages/*`, `base/*`, `utils/*`, `constants/*`
  - TestNG suite: `src/test/resources/testng.xml`
  - Config: `src/test/resources/config/config.properties`

2) Runtime & lifecycle conventions (important for changes/bug fixes)
- Playwright lifecycle is managed in `BaseTest`:
  - `@BeforeSuite` creates a single Playwright instance
  - `@BeforeClass` launches a Browser (shared per test class)
  - `@BeforeMethod` creates a fresh BrowserContext and Page per test method
  - `@AfterMethod` captures screenshots on failure (via `ScreenshotUtil`) and closes the context
  - Implication: tests expect a single Browser per class and isolated contexts per test — avoid changing this unless you update `BaseTest` and all tests accordingly.

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
- Page Object pattern: page classes extend `BasePage` and accept a Playwright `Page` in the constructor. Example: `new LoginPage(page)` in tests. Use `BasePage` helpers (`type`, `click`, `waitForVisible`, `getText`).
- Navigation: prefer `navigateToPath(String path)` which uses `ConfigReader.getBaseUrl()`; a notable inconsistency exists in `LoginPage.navigate()` which hard-codes the full URL — be careful when updating base URL behavior.
- Test data: `TestDataProviders` (in `src/test/java/com/bjit/ecauto/dataproviders`) is the canonical place for DataProviders.
- Logging and reporting:
  - Logs are written to `logs/automation.log` via `LogHelper`.
  - Screenshots saved to `reports/screenshots/` via `ScreenshotUtil`; failures are prefixed with `FAIL_`.

6) Debugging tips for agents
- When diagnosing a flaky UI test, check these places in order:
  1. `logs/automation.log` (LogHelper output)
 2. `reports/screenshots/` for `FAIL_*` images
 3. `src/test/resources/config/config.properties` for timeouts and viewport values
 4. `BaseTest` lifecycle — ensure context/browser lifetime is not being changed unexpectedly
- To run tests with visible browser for local debug use `-Pheaded` or `-Dheadless=false`.

7) Integration & external dependencies
- Playwright Java is an external dependency (see `pom.xml` property `playwright.version`). There are no project scripts to install native browsers here — assume standard Playwright Java behavior (browsers downloaded or managed by the Playwright runtime). If tests fail to launch browsers locally, ensure the machine has network access or that Playwright's required browser binaries are present.

8) Where to change things safely
- Add new Page Objects in `src/main/java/com/bjit/ecauto/pages` and follow the `BasePage` helper methods.
- Add tests to `src/test/java/com/bjit/ecauto/tests`; include them in `testng.xml` if you want them to run in the suite ordering.
- Change timeouts or viewport sizes in `src/test/resources/config/config.properties` or pass JVM overrides for temporary runs.

9) Quick file references (examples used above)
- `pom.xml` (profiles and surefire config)
- `src/test/resources/testng.xml` (suite and listener)
- `src/main/java/com/bjit/ecauto/base/BaseTest.java`
- `src/main/java/com/bjit/ecauto/base/BasePage.java`
- `src/main/java/com/bjit/ecauto/utils/ConfigReader.java`
- `src/main/java/com/bjit/ecauto/utils/LogHelper.java`
- `src/main/java/com/bjit/ecauto/utils/ScreenshotUtil.java`

If you need deeper edits (upgrade Playwright/TestNG, change lifecycle, or add CI automation), I can produce a targeted plan and the code changes.

