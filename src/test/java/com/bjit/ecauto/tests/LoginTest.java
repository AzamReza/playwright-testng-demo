package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.AbstractLoginTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;
import com.bjit.ecauto.utils.ConfigReader;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

@Feature("Authentication")
public class LoginTest extends AbstractLoginTest {

    // ============================
    // POSITIVE TESTS
    // ============================
    @Override
    @Story("Valid Login")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Preconditions: Application loaded, login page displayed\n" +
            "Steps: Enter standard_user credentials and click Login\n" +
            "Expected: User navigates to Products page with correct title")
    @Test(priority = 1,
            description = "Verify standard user can login successfully")
    public void testValidLogin() {
        ProductsPage productsPage = navigateToLogin()
                .loginAs(ConfigReader.getStandardUsername(),
                        ConfigReader.getStandardPassword());

        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be on Products page after login");
        Assert.assertEquals(productsPage.getPageTitle(), "Products",
                "Page title should be 'Products'");
    }

    @Override
    @Story("Login with Multiple User Types")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Data for all user types (valid, locked, problem, glitch)\n" +
            "Steps: For each user type, attempt login with provided credentials\n" +
            "Expected: Valid users navigate to Products page; Invalid users see error")
    @Test(priority = 2,
            dataProvider = "allUserTypes",
            dataProviderClass = TestDataProviders.class,
            description = "Verify login behavior for all user types")
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

    // ============================
    // NEGATIVE TESTS
    // ============================
    @Override
    @Story("Invalid Login Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Test data with invalid credentials prepared\n" +
            "Steps: Enter invalid credentials and click Login\n" +
            "Expected: Error message displayed with expected error text")
    @Test(priority = 3,
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProviders.class,
            description = "Verify error messages for invalid login attempts")
    public void testInvalidLogin(String username, String password, String expectedError) {
        navigateToLogin()
                .loginExpectingError(username, password);

        Assert.assertTrue(getLoginPage().isErrorDisplayed(),
                "Error message should be displayed");

        String actualError = getLoginPage().getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError),
                "Error should contain: '" + expectedError
                        + "' but was: '" + actualError + "'");
    }

    @Override
    @Story("Locked User Prevention")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Locked out user credentials available (locked_out_user)\n" +
            "Steps: Attempt login with locked out user credentials\n" +
            "Expected: Error message indicates user is locked out")
    @Test(priority = 4,
            description = "Verify locked out user cannot login")
    public void testLockedOutUser() {
        navigateToLogin()
                .loginExpectingError("locked_out_user", "secret_sauce");

        Assert.assertTrue(getLoginPage().isErrorDisplayed(),
                "Error should be displayed for locked user");

        String error = getLoginPage().getErrorMessage();
        Assert.assertTrue(error.contains("locked out"),
                "Error should mention user is locked out");
    }

    @Override
    @Story("Error Message Handling")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: Failed login attempt shows error message\n" +
            "Steps: Click error close button (X)\n" +
            "Expected: Error message disappears, form ready for new input")
    @Test(priority = 5,
            description = "Verify error can be closed after failed login")
    public void testCloseErrorMessage() {
        navigateToLogin()
                .loginExpectingError("", "");

        Assert.assertTrue(getLoginPage().isErrorDisplayed(),
                "Error should be visible");

        getLoginPage().closeError();

        Assert.assertFalse(getLoginPage().isErrorDisplayed(),
                "Error should be hidden after closing");
    }

    // ============================
    // LOGOUT TEST
    // ============================
    @Override
    @Story("Session Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: User logged in, on Products page\n" +
            "Steps: Click hamburger menu, select Logout\n" +
            "Expected: User logged out, redirected to login page, session cleared")
    @Test(priority = 6,
            description = "Verify user can logout successfully")
    public void testLogout() {
        ProductsPage productsPage = navigateToLogin()
                .loginAs(ConfigReader.getStandardUsername(),
                        ConfigReader.getStandardPassword());

        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be logged in first");

        LoginPage loggedOutPage = productsPage.logout();

        Assert.assertTrue(loggedOutPage.isOnLoginPage(),
                "Should be back on login page after logout");
    }
}