package com.bjit.ecauto.base;

import com.bjit.ecauto.contracts.ILoginActions;
import com.bjit.ecauto.pages.LoginPage;

/**
 * Abstract base class for all login-related tests.
 * Extends BaseTest to inherit Playwright lifecycle management.
 * Implements ILoginActions to enforce login test contract.
 * Provides shared factory methods and helpers for login tests.
 */
public abstract class AbstractLoginTest extends BaseTest implements ILoginActions {
    
    /**
     * Factory method to create and return a LoginPage instance.
     * Centralizes page object instantiation for all login tests.
     */
    protected LoginPage getLoginPage() {
        return new LoginPage(page);
    }
    
    /**
     * Helper method to navigate to the login page.
     * Uses ConfigReader to get the base URL.
     */
    protected LoginPage navigateToLogin() {
        LoginPage loginPage = getLoginPage();
        loginPage.navigate();
        return loginPage;
    }

    @Override
    public void testLogout() {

    }
}

