package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.utils.ConfigReader;
import com.deque.html.axecore.playwright.AxeBuilder;
import com.deque.html.axecore.results.AxeResults;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Feature("Accessibility")
public class AccessibilityTest extends BaseTest {

    private static final List<String> WCAG_AA_TAGS = Arrays.asList(
            "wcag2a", "wcag2aa", "wcag21a", "wcag21aa");

    private void openLoginPage() {
        page.navigate(ConfigReader.getBaseUrl());
    }

    private AxeBuilder makeAxeBuilder() {
        // Shared fixture-style axe setup used by multiple tests.
        return new AxeBuilder(page).withTags(WCAG_AA_TAGS);
    }

    private void assertNoViolations(AxeResults results, String context) {
        List<String> violations = results.getViolations().stream()
                .map(rule -> rule.getId() + "(" + rule.getNodes().size() + " nodes)")
                .collect(Collectors.toList());
        Assert.assertTrue(results.getViolations().isEmpty(),
                "Axe violations found for " + context + ": " + violations);
    }

    private void loadAccessibleFixturePage() {
        page.setContent("""
                <!doctype html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <title>Accessible Fixture</title>
                </head>
                <body>
                  <header>
                    <button id="menu-button"
                            aria-label="Navigation Menu"
                            aria-controls="navigation-menu-flyout"
                            aria-expanded="false">
                      Menu
                    </button>
                  </header>

                  <nav id="navigation-menu-flyout" hidden>
                    <ul>
                      <li><a href="#products">Products</a></li>
                      <li><a href="#about">About</a></li>
                    </ul>
                  </nav>

                  <main id="content">
                    <h1>Accessible Test Fixture</h1>
                    <form>
                      <label for="email">Email</label>
                      <input id="email" type="email" autocomplete="email" />
                      <button type="submit">Submit</button>
                    </form>
                  </main>

                  <script>
                    const btn = document.getElementById('menu-button');
                    const flyout = document.getElementById('navigation-menu-flyout');
                    btn.addEventListener('click', () => {
                      const expanded = btn.getAttribute('aria-expanded') === 'true';
                      btn.setAttribute('aria-expanded', expanded ? 'false' : 'true');
                      flyout.hidden = expanded;
                    });
                  </script>
                </body>
                </html>
                """);
    }

    @Story("Accessible Roles on Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: SauceDemo login page is loaded\n" +
            "Steps: Query fields and actions with role-based locators\n" +
            "Expected: Login form controls are discoverable via accessibility roles")
    @Test(priority = 1, description = "Verify login form controls are discoverable by accessibility roles")
    public void testLoginFormControlsAreAccessibleByRole() {
        openLoginPage();

        Locator textboxes = page.getByRole(AriaRole.TEXTBOX);
        assertThat(textboxes).hasCount(2);

        Locator loginButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));
        assertThat(loginButton).isVisible();
    }

    @Story("Keyboard Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: SauceDemo login page is loaded\n" +
            "Steps: Move focus with keyboard Tab key\n" +
            "Expected: Focus order follows Username -> Password -> Login")
    @Test(priority = 2, description = "Verify login form keyboard focus order")
    public void testLoginFormKeyboardFocusOrder() {
        openLoginPage();

        Locator usernameInput = page.getByPlaceholder("Username");
        Locator passwordInput = page.getByPlaceholder("Password");
        Locator loginButton = page.getByRole(AriaRole.BUTTON,
                new Page.GetByRoleOptions().setName("Login"));

        usernameInput.click();
        assertThat(usernameInput).isFocused();

        usernameInput.press("Tab");
        assertThat(passwordInput).isFocused();

        passwordInput.press("Tab");
        assertThat(loginButton).isFocused();
    }

    @Story("Accessible Error Feedback")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Preconditions: SauceDemo login page is loaded\n" +
            "Steps: Submit invalid credentials from data provider\n" +
            "Expected: Error feedback is visibly presented with actionable dismiss control")
    @Test(priority = 3,
            dataProvider = "accessibilityInvalidLoginData",
            dataProviderClass = TestDataProviders.class,
            description = "Verify invalid login shows clear inline error feedback")
    public void testInvalidLoginErrorFeedbackIsPerceivable(String username, String password, String expectedErrorFragment) {
        openLoginPage();

        page.getByPlaceholder("Username").fill(username);
        page.getByPlaceholder("Password").fill(password);
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        Locator errorBanner = page.locator("[data-test='error']");
        Locator closeErrorButton = page.locator(".error-button");

        assertThat(errorBanner).isVisible();
        assertThat(errorBanner).containsText(expectedErrorFragment);
        assertThat(closeErrorButton).isVisible();
    }

    @Story("Keyboard Submission")
    @Severity(SeverityLevel.NORMAL)
    @Description("Preconditions: Valid credentials are available\n" +
            "Steps: Type credentials and press Enter from password field\n" +
            "Expected: User can submit login form without mouse interaction")
    @Test(priority = 4, description = "Verify login form supports keyboard-only submission")
    public void testLoginSupportsKeyboardSubmission() {
        openLoginPage();

        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        Locator passwordInput = page.getByPlaceholder("Password");
        passwordInput.fill(ConfigReader.getStandardPassword());
        passwordInput.press("Enter");

        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Axe Full Page Scan")
    @Severity(SeverityLevel.NORMAL)
    @Description("Runs an axe scan against the full SauceDemo login page and verifies the scan result is produced")
    @Test(priority = 5, description = "Example 1: run axe scan on an entire page")
    public void testAxeCanScanEntireLoginPage() {
        openLoginPage();

        AxeResults results = new AxeBuilder(page).analyze();
        Assert.assertFalse(results.isErrored(), "Axe scan should complete without runtime errors");
        Assert.assertNotNull(results.getViolations(), "Axe scan should return violations list");
    }

    @Story("Axe Include Scope")
    @Severity(SeverityLevel.NORMAL)
    @Description("Example 2 style scan: interact with UI, wait for state, then scan only one section with include()")
    @Test(priority = 6, description = "Example 2: include only a specific section after interaction")
    public void testAxeIncludeForSpecificSectionAfterInteraction() {
        loadAccessibleFixturePage();

        page.locator("#menu-button").click();
        page.locator("#navigation-menu-flyout").waitFor();

        AxeResults results = new AxeBuilder(page)
                .include(Arrays.asList("#navigation-menu-flyout"))
                .analyze();

        assertNoViolations(results, "navigation menu flyout include scan");
    }

    @Story("Axe WCAG Tags")
    @Severity(SeverityLevel.NORMAL)
    @Description("Example 3 style scan using withTags() to focus on WCAG A/AA checks")
    @Test(priority = 7, description = "Example 3: run axe scan with WCAG tags")
    public void testAxeScanWithWcagTags() {
        loadAccessibleFixturePage();

        AxeResults results = new AxeBuilder(page)
                .withTags(WCAG_AA_TAGS)
                .analyze();

        assertNoViolations(results, "WCAG tagged scan");
    }

    @Story("Axe Exclude Known Element")
    @Severity(SeverityLevel.NORMAL)
    @Description("Demonstrates excluding a known problematic element using exclude()")
    @Test(priority = 8, description = "Handling known issues: exclude problematic element from scan")
    public void testAxeExcludeKnownIssueElement() {
        page.setContent("""
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8" /><title>Exclude Example</title></head>
                <body>
                  <main>
                    <h1>Exclude Known Issue</h1>
                    <label for="search">Search</label>
                    <input id="search" type="text" />
                    <section id="known-issue">
                      <input id="unlabeled" type="text" />
                    </section>
                  </main>
                </body>
                </html>
                """);

        AxeResults results = new AxeBuilder(page)
                .exclude(Arrays.asList("#known-issue"))
                .analyze();

        assertNoViolations(results, "exclude known issue element");
    }

    @Story("Axe Disable Rules")
    @Severity(SeverityLevel.NORMAL)
    @Description("Demonstrates disabling a specific axe rule for temporary known debt")
    @Test(priority = 9, description = "Handling known issues: disable specific axe rule")
    public void testAxeDisableSpecificRule() {
        page.setContent("""
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8" /><title>Disable Rules Example</title></head>
                <body>
                  <main>
                    <h1>Missing Alt Example</h1>
                    <img src="/logo.png" />
                  </main>
                </body>
                </html>
                """);

        AxeResults baseline = new AxeBuilder(page).analyze();
        boolean hasImageAltViolation = baseline.getViolations().stream()
                .anyMatch(rule -> "image-alt".equals(rule.getId()));
        Assert.assertTrue(hasImageAltViolation, "Baseline scan should include image-alt violation");

        AxeResults resultsWithRuleDisabled = new AxeBuilder(page)
                .disableRules(Arrays.asList("image-alt"))
                .analyze();

        boolean imageAltStillPresent = resultsWithRuleDisabled.getViolations().stream()
                .anyMatch(rule -> "image-alt".equals(rule.getId()));
        Assert.assertFalse(imageAltStillPresent,
                "image-alt should be absent when the rule is disabled");
    }

    @Story("Axe Violation Fingerprints")
    @Severity(SeverityLevel.NORMAL)
    @Description("Creates compact violation fingerprints (ruleId + target) and validates known issue signatures")
    @Test(priority = 10, description = "Handling known issues: fingerprint expected violations")
    public void testAxeViolationFingerprintsForKnownIssues() {
        page.setContent("""
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8" /><title>Fingerprint Example</title></head>
                <body>
                  <main>
                    <h1>Known Violations Fixture</h1>
                    <img src="/hero.png" />
                    <span role="invalid">Bad role</span>
                  </main>
                </body>
                </html>
                """);

        AxeResults results = new AxeBuilder(page).analyze();

        Set<ViolationFingerprint> fingerprints = results.getViolations().stream()
                .flatMap(rule -> rule.getNodes().stream()
                        .map(node -> new ViolationFingerprint(rule.getId(), String.valueOf(node.getTarget()))))
                .collect(Collectors.toSet());

        boolean hasAriaRolesFingerprint = fingerprints.stream()
                .anyMatch(fp -> "aria-roles".equals(fp.ruleId()) && fp.target().contains("span"));
        boolean hasImageAltFingerprint = fingerprints.stream()
                .anyMatch(fp -> "image-alt".equals(fp.ruleId()) && fp.target().contains("img"));

        Assert.assertTrue(hasAriaRolesFingerprint,
                "Expected aria-roles fingerprint for invalid role element");
        Assert.assertTrue(hasImageAltFingerprint,
                "Expected image-alt fingerprint for image without alt text");
    }

    @Story("Shared Axe Fixture")
    @Severity(SeverityLevel.NORMAL)
    @Description("Uses a shared fixture-style AxeBuilder configuration and adds test-specific include() scope")
    @Test(priority = 11, description = "Use fixture-style shared axe configuration")
    public void testAxeFixtureStyleSharedConfiguration() {
        page.setContent("""
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8" /><title>Fixture Builder Example</title></head>
                <body>
                  <main>
                    <h1>Fixture Builder</h1>
                    <section id="specific-element-under-test">
                      <label for="full-name">Full name</label>
                      <input id="full-name" type="text" />
                    </section>
                    <section id="commonly-reused-element-with-known-issue">
                      <input id="missing-common-label" type="text" />
                    </section>
                  </main>
                </body>
                </html>
                """);

        AxeResults results = makeAxeBuilder()
                .exclude(Arrays.asList("#commonly-reused-element-with-known-issue"))
                .include(Arrays.asList("#specific-element-under-test"))
                .analyze();

        assertNoViolations(results, "fixture-style shared axe configuration");
    }

    private record ViolationFingerprint(String ruleId, String target) {
    }
}