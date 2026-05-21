package com.bjit.ecauto.pages;

import com.microsoft.playwright.Page;
import com.bjit.ecauto.base.BasePage;
import com.bjit.ecauto.utils.LogHelper;
import com.bjit.ecauto.pages.LoginPage;

public class LoginPage extends BasePage {

    // ============================
    // LOCATORS
    // ============================
    private static final String USERNAME_INPUT = "#user-name";
    private static final String PASSWORD_INPUT = "#password";
    private static final String LOGIN_BUTTON = "#login-button";
    private static final String ERROR_MESSAGE = "[data-test='error']";
    private static final String ERROR_CLOSE_BUTTON = ".error-button";

    // ============================
    // CONSTRUCTOR
    // ============================
    public LoginPage(Page page) {
        super(page);
    }

    // ============================
    // ACTIONS
    // ============================
    public LoginPage navigate() {
        navigateTo("https://www.saucedemo.com");
        LogHelper.info("Login page loaded");
        return this;
    }

    public LoginPage enterUsername(String username) {
        type(USERNAME_INPUT, username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        typeSecure(PASSWORD_INPUT, password);
        return this;
    }

    public ProductsPage clickLogin() {
        click(LOGIN_BUTTON);
        return new ProductsPage(page);
    }

    public LoginPage clickLoginExpectingError() {
        click(LOGIN_BUTTON);
        return this;
    }


    // Shortcut for tests to instantly log in using default standard user credentials
    public ProductsPage loginAsStandardUser() {
        return navigate().loginAs("standard_user", "secret_sauce");
    }


    // Convenience method — complete login flow
    public ProductsPage loginAs(String username, String password) {
        LogHelper.info("Logging in as: " + username);
        enterUsername(username);
        enterPassword(password);
        return clickLogin();
    }

    // Login expecting failure
    public LoginPage loginExpectingError(String username, String password) {
        LogHelper.info("Attempting login with: " + username + " (expecting error)");
        enterUsername(username);
        enterPassword(password);
        return clickLoginExpectingError();
    }

    // ============================
    // GETTERS
    // ============================
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isVisible(ERROR_MESSAGE);
    }

    public boolean isOnLoginPage() {
        return isVisible(LOGIN_BUTTON);
    }

    // ============================
    // ACTIONS on error
    // ============================
    public LoginPage closeError() {
        click(ERROR_CLOSE_BUTTON);
        return this;
    }
}