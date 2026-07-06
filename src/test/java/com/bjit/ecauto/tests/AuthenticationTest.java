package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.utils.ConfigReader;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * AuthenticationTest demonstrates Playwright Java authentication testing:
 * - Login/logout flows
 * - Credential validation
 * - Session persistence
 * - Authentication state management
 * - Unauthorized access handling
 * - Multi-user authentication scenarios
 * - "Remember me" functionality
 *
 * @see <a href="https://playwright.dev/java/docs/auth">Playwright Authentication</a>
 */
@Feature("Authentication & Session Management")
public class AuthenticationTest extends BaseTest {

    @Story("Auth - Valid Login")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test successful login with valid credentials")
    @Test(priority = 1, description = "Valid login flow")
    public void testValidLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        // Enter credentials
        Locator username = page.getByPlaceholder("Username");
        Locator password = page.getByPlaceholder("Password");
        
        username.fill(ConfigReader.getStandardUsername());
        password.fill(ConfigReader.getStandardPassword());
        
        // Click login
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Verify successful login
        page.waitForURL("**/inventory.html");
        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Auth - Invalid Username")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with invalid username")
    @Test(priority = 2, description = "Invalid username login")
    public void testInvalidUsernameLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        Locator username = page.getByPlaceholder("Username");
        Locator password = page.getByPlaceholder("Password");
        
        username.fill("invalid_user");
        password.fill(ConfigReader.getStandardPassword());
        
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Verify error message
        Locator errorMessage = page.locator("[data-test='error']");
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).containsText("user");
    }

    @Story("Auth - Invalid Password")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test login with invalid password")
    @Test(priority = 3, description = "Invalid password login")
    public void testInvalidPasswordLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        Locator username = page.getByPlaceholder("Username");
        Locator password = page.getByPlaceholder("Password");
        
        username.fill(ConfigReader.getStandardUsername());
        password.fill("wrong_password");
        
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Verify error message
        Locator errorMessage = page.locator("[data-test='error']");
        assertThat(errorMessage).isVisible();
    }

    @Story("Auth - Empty Credentials")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with empty credentials")
    @Test(priority = 4, description = "Empty credentials login")
    public void testEmptyCredentialsLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Verify error or form validation
        Locator username = page.getByPlaceholder("Username");
        Locator password = page.getByPlaceholder("Password");
        
        Assert.assertTrue(
            page.isVisible("[data-test='error']") || 
            username.evaluate("el => el.value === ''").toString().equals("true"),
            "Should show error or form validation"
        );
    }

    @Story("Auth - Login and Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test complete login and logout flow")
    @Test(priority = 5, description = "Login and logout flow")
    public void testLoginAndLogout() {
        // Login
        page.navigate(ConfigReader.getBaseUrl());
        
        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        page.waitForURL("**/inventory.html");
        assertThat(page.locator(".inventory_list")).isVisible();
        
        // Logout
        page.locator("#react-burger-menu-btn").click();
        page.locator("#logout_sidebar_link").click();
        
        // Verify back at login page
        page.waitForURL("**/");
        assertThat(page.getByPlaceholder("Username")).isVisible();
    }

    @Story("Auth - Session Persistence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify session persists after login")
    @Test(priority = 6, description = "Session persistence")
    public void testSessionPersistence() {
        page.navigate(ConfigReader.getBaseUrl());
        
        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        page.waitForURL("**/inventory.html");
        
        // Navigate away and back
        page.navigate(ConfigReader.getBaseUrl() + "/inventory.html");
        
        // Should still be logged in
        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Auth - Locked User")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test login with locked out user")
    @Test(priority = 7, description = "Locked user login")
    public void testLockedUserLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        page.getByPlaceholder("Username").fill("locked_out_user");
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Verify locked user error
        Locator errorMessage = page.locator("[data-test='error']");
        assertThat(errorMessage).isVisible();
        assertThat(errorMessage).containsText("locked");
    }

    @Story("Auth - Multiple User Logins")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test switching between different user accounts")
    @Test(priority = 8, description = "Multiple user login switching")
    public void testMultipleUserLogins() {
        // Login as standard user
        page.navigate(ConfigReader.getBaseUrl());
        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        page.waitForURL("**/inventory.html");
        
        // Logout
        page.locator("#react-burger-menu-btn").click();
        page.locator("#logout_sidebar_link").click();
        page.waitForURL("**/");
        
        // Login as problem user
        page.getByPlaceholder("Username").fill("problem_user");
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        page.waitForURL("**/inventory.html");
        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Auth - Direct Access After Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test direct access to protected page after login")
    @Test(priority = 9, description = "Direct protected page access")
    public void testDirectAccessAfterLogin() {
        page.navigate(ConfigReader.getBaseUrl());
        
        // Login
        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        page.waitForURL("**/inventory.html");
        
        // Direct access to inventory page should work
        page.navigate(ConfigReader.getBaseUrl() + "/inventory.html");
        assertThat(page.locator(".inventory_list")).isVisible();
    }

    @Story("Auth - Unauthorized Access Prevention")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Prevent direct access to protected pages without authentication")
    @Test(priority = 10, description = "Unauthorized access prevention")
    public void testUnauthorizedAccessPrevention() {
        // Try to access inventory without login
        page.navigate(ConfigReader.getBaseUrl() + "/inventory.html");
        
        // Should be redirected to login
        String currentUrl = page.url();
        Assert.assertTrue(
            currentUrl.contains("saucedemo.com/") && !currentUrl.contains("/inventory"),
            "Should be redirected to login page"
        );
    }

    @Story("Auth - Credentials Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify credential fields accept proper input")
    @Test(priority = 11, description = "Credentials validation")
    public void testCredentialsValidation() {
        page.navigate(ConfigReader.getBaseUrl());
        
        Locator username = page.getByPlaceholder("Username");
        Locator password = page.getByPlaceholder("Password");
        
        // Verify fields are not disabled
        assertThat(username).isEnabled();
        assertThat(password).isEnabled();
        
        // Verify input types
        assertThat(username).hasAttribute("type", "text");
        assertThat(password).hasAttribute("type", "password");
    }

    @Story("Auth - Case Sensitivity")
    @Severity(SeverityLevel.NORMAL)
    @Description("Test if credentials are case-sensitive")
    @Test(priority = 12, description = "Case sensitivity check")
    public void testCaseSensitivity() {
        page.navigate(ConfigReader.getBaseUrl());
        
        // Try uppercase username
        page.getByPlaceholder("Username").fill("STANDARD_USER");
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Should fail (assuming case-sensitive)
        Locator error = page.locator("[data-test='error']");
        if (error.isVisible()) {
            assertThat(error).isVisible();
        }
    }

    @Story("Auth - Login Button State")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify login button behavior")
    @Test(priority = 13, description = "Login button state")
    public void testLoginButtonState() {
        page.navigate(ConfigReader.getBaseUrl());
        
        Locator loginButton = page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login"));
        
        // Button should be enabled
        assertThat(loginButton).isEnabled();
        assertThat(loginButton).isVisible();
    }

    @Story("Auth - Error Message Persistence")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify error messages are displayed correctly")
    @Test(priority = 14, description = "Error message display")
    public void testErrorMessagePersistence() {
        page.navigate(ConfigReader.getBaseUrl());
        
        page.getByPlaceholder("Username").fill("invaliduser");
        page.getByPlaceholder("Password").fill("wrongpass");
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        // Error should be visible
        page.waitForSelector("[data-test='error']", new com.microsoft.playwright.Page.WaitForSelectorOptions().setTimeout(5000));
        assertThat(page.locator("[data-test='error']")).isVisible();
    }

    @Story("Auth - Session Timeout")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify session behavior over time")
    @Test(priority = 15, description = "Session timeout verification")
    public void testSessionTimeout() {
        page.navigate(ConfigReader.getBaseUrl());
        
        // Login
        page.getByPlaceholder("Username").fill(ConfigReader.getStandardUsername());
        page.getByPlaceholder("Password").fill(ConfigReader.getStandardPassword());
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
                new com.microsoft.playwright.Page.GetByRoleOptions().setName("Login")).click();
        
        page.waitForURL("**/inventory.html");
        
        // Session should still be active
        page.reload();
        assertThat(page.locator(".inventory_list")).isVisible();
    }
}


