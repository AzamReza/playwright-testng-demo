package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;
import com.bjit.ecauto.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    // ============================
    // POSITIVE TESTS
    // ============================
    @Test(priority = 1,
            description = "Verify standard user can login successfully")
    public void testValidLogin() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = loginPage
                .navigate()
                .loginAs(ConfigReader.getStandardUsername(),
                        ConfigReader.getStandardPassword());

        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be on Products page after login");
        Assert.assertEquals(productsPage.getPageTitle(), "Products",
                "Page title should be 'Products'");
    }

    @Test(priority = 2,
            dataProvider = "allUserTypes",
            dataProviderClass = TestDataProviders.class,
            description = "Verify login behavior for all user types")
    public void testAllUserTypes(String username, String password, boolean shouldPass) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();

        if (shouldPass) {
            ProductsPage productsPage = loginPage.loginAs(username, password);
            Assert.assertTrue(productsPage.isOnProductsPage(),
                    username + " should be able to login");
        } else {
            loginPage.loginExpectingError(username, password);
            Assert.assertTrue(loginPage.isErrorDisplayed(),
                    username + " should see error message");
        }
    }

    // ============================
    // NEGATIVE TESTS
    // ============================
    @Test(priority = 3,
            dataProvider = "invalidLoginData",
            dataProviderClass = TestDataProviders.class,
            description = "Verify error messages for invalid login attempts")
    public void testInvalidLogin(String username, String password, String expectedError) {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate()
                .loginExpectingError(username, password);

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error message should be displayed");

        String actualError = loginPage.getErrorMessage();
        Assert.assertTrue(actualError.contains(expectedError),
                "Error should contain: '" + expectedError
                        + "' but was: '" + actualError + "'");
    }

    @Test(priority = 4,
            description = "Verify locked out user cannot login")
    public void testLockedOutUser() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate()
                .loginExpectingError("locked_out_user", "secret_sauce");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be displayed for locked user");

        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("locked out"),
                "Error should mention user is locked out");
    }

    @Test(priority = 5,
            description = "Verify error can be closed after failed login")
    public void testCloseErrorMessage() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate()
                .loginExpectingError("", "");

        Assert.assertTrue(loginPage.isErrorDisplayed(),
                "Error should be visible");

        loginPage.closeError();

        Assert.assertFalse(loginPage.isErrorDisplayed(),
                "Error should be hidden after closing");
    }

    // ============================
    // LOGOUT TEST
    // ============================
    @Test(priority = 6,
            description = "Verify user can logout successfully")
    public void testLogout() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = loginPage
                .navigate()
                .loginAs(ConfigReader.getStandardUsername(),
                        ConfigReader.getStandardPassword());

        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be logged in first");

        LoginPage loggedOutPage = productsPage.logout();

        Assert.assertTrue(loggedOutPage.isOnLoginPage(),
                "Should be back on login page after logout");
    }
}