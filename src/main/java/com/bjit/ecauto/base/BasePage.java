package com.bjit.ecauto.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.bjit.ecauto.utils.ConfigReader;
import com.bjit.ecauto.utils.LogHelper;

import java.util.List;
import java.util.stream.Collectors;

public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    // ============================
    // Navigation
    // ============================
    protected void navigateTo(String url) {
        LogHelper.step("Navigating to: " + url);
        page.navigate(url, new Page.NavigateOptions()
                .setTimeout(ConfigReader.getNavigationTimeout()));
    }

    protected void navigateToPath(String path) {
        navigateTo(ConfigReader.getBaseUrl() + path);
    }

    // ============================
    // Input Actions
    // ============================
    protected void type(String selector, String text) {
        LogHelper.step("Typing '" + text + "' into " + selector);
        waitForVisible(selector);
        page.fill(selector, text);
    }

    protected void typeSecure(String selector, String text) {
        LogHelper.step("Typing [MASKED] into " + selector);
        waitForVisible(selector);
        page.fill(selector, text);
    }

    protected void click(String selector) {
        LogHelper.step("Clicking: " + selector);
        waitForVisible(selector);
        page.click(selector);
    }

    protected void clearAndType(String selector, String text) {
        page.fill(selector, "");
        type(selector, text);
    }

    // ============================
    // Dropdown
    // ============================
    protected void selectByValue(String selector, String value) {
        LogHelper.step("Selecting value '" + value + "' from " + selector);
        page.selectOption(selector, value);
    }

    // ============================
    // Get Information
    // ============================
    protected String getText(String selector) {
        waitForVisible(selector);
        return page.textContent(selector).trim();
    }

    protected String getInputValue(String selector) {
        return page.inputValue(selector);
    }

    protected String getPageTitle() {
        return page.title();
    }

    protected String getCurrentUrl() {
        return page.url();
    }

    // ============================
    // Get Multiple Elements
    // ============================
    protected List<String> getAllTexts(String selector) {
        return page.locator(selector).allTextContents()
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }

    protected int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    // ============================
    // Visibility & State
    // ============================
    protected boolean isVisible(String selector) {
        return page.isVisible(selector);
    }

    protected boolean isEnabled(String selector) {
        return page.isEnabled(selector);
    }

    // ============================
    // Wait Methods
    // ============================
    protected void waitForVisible(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(ConfigReader.getDefaultTimeout()));
    }

    protected void waitForHidden(String selector) {
        page.waitForSelector(selector, new Page.WaitForSelectorOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(ConfigReader.getDefaultTimeout()));
    }

    protected void waitForUrl(String urlPart) {
        LogHelper.step("Waiting for URL containing: " + urlPart);
        page.waitForURL("**" + urlPart + "**");
    }
}