package com.bjit.ecauto.pages;

import com.microsoft.playwright.Page;
import com.bjit.ecauto.base.BasePage;
import com.bjit.ecauto.utils.LogHelper;

import java.util.List;

public class CheckoutPage extends BasePage {

    // ============================
    // LOCATORS — Step One
    // ============================
    private static final String PAGE_TITLE = ".title";
    private static final String FIRST_NAME_INPUT = "[data-test='firstName']";
    private static final String LAST_NAME_INPUT = "[data-test='lastName']";
    private static final String POSTAL_CODE_INPUT = "[data-test='postalCode']";
    private static final String CONTINUE_BUTTON = "[data-test='continue']";
    private static final String CANCEL_BUTTON = "[data-test='cancel']";
    private static final String ERROR_MESSAGE = "[data-test='error']";

    // ============================
    // LOCATORS — Step Two (Overview)
    // ============================
    private static final String OVERVIEW_ITEMS = ".cart_item";
    private static final String OVERVIEW_ITEM_NAMES = ".inventory_item_name";
    private static final String OVERVIEW_ITEM_PRICES = ".inventory_item_price";
    private static final String SUBTOTAL_LABEL = ".summary_subtotal_label";
    private static final String TAX_LABEL = ".summary_tax_label";
    private static final String TOTAL_LABEL = ".summary_info_label.summary_total_label";
    private static final String FINISH_BUTTON = "[data-test='finish']";

    // ============================
    // LOCATORS — Complete Page
    // ============================
    private static final String COMPLETE_HEADER = ".complete-header";
    private static final String COMPLETE_TEXT = ".complete-text";
    private static final String BACK_HOME_BUTTON = "[data-test='back-to-products']";
    private static final String PONY_EXPRESS_IMAGE = ".pony_express";

    // ============================
    // CONSTRUCTOR
    // ============================
    public CheckoutPage(Page page) {
        super(page);
    }

    // ============================
    // PAGE STATE CHECKS
    // ============================
    public String getStepTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isOnStepOne() {
        return isVisible(PAGE_TITLE)
                && getText(PAGE_TITLE).contains("Your Information");
    }

    public boolean isOnStepTwo() {
        return isVisible(PAGE_TITLE)
                && getText(PAGE_TITLE).contains("Overview");
    }

    public boolean isOnCompletePage() {
        return isVisible(COMPLETE_HEADER);
    }

    // ============================
    // STEP ONE — Input Actions (void — no chaining)
    // ============================
    public void enterFirstName(String firstName) {
        type(FIRST_NAME_INPUT, firstName);
    }

    public void enterLastName(String lastName) {
        type(LAST_NAME_INPUT, lastName);
    }

    public void enterPostalCode(String postalCode) {
        type(POSTAL_CODE_INPUT, postalCode);
    }

    public void clickContinue() {
        click(CONTINUE_BUTTON);
    }

    public void clickContinueExpectingError() {
        click(CONTINUE_BUTTON);
    }

    public CartPage clickCancel() {
        click(CANCEL_BUTTON);
        return new CartPage(page);
    }

    // ============================
    // STEP ONE — Error
    // ============================
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }

    public boolean isErrorDisplayed() {
        return isVisible(ERROR_MESSAGE);
    }

    // ============================
    // STEP TWO — Overview Info
    // ============================
    public int getOverviewItemCount() {
        return getElementCount(OVERVIEW_ITEMS);
    }

    public List<String> getOverviewItemNames() {
        return getAllTexts(OVERVIEW_ITEM_NAMES);
    }

    public List<String> getOverviewItemPrices() {
        return getAllTexts(OVERVIEW_ITEM_PRICES);
    }

    public String getSubtotal() {
        String raw = getText(SUBTOTAL_LABEL);
        return raw.replace("Item total: ", "");
    }

    public double getSubtotalAsNumber() {
        return Double.parseDouble(getSubtotal().replace("$", ""));
    }

    public String getTax() {
        String raw = getText(TAX_LABEL);
        return raw.replace("Tax: ", "");
    }

    public double getTaxAsNumber() {
        return Double.parseDouble(getTax().replace("$", ""));
    }

    public String getTotal() {
        String raw = getText(TOTAL_LABEL);
        return raw.replace("Total: ", "");
    }

    public double getTotalAsNumber() {
        return Double.parseDouble(getTotal().replace("$", ""));
    }

    public void clickFinish() {
        LogHelper.step("Clicking Finish to complete order");
        click(FINISH_BUTTON);
    }

    public CartPage clickCancelOverview() {
        click(CANCEL_BUTTON);
        return new CartPage(page);
    }

    // ============================
    // COMPLETE PAGE
    // ============================
    public String getCompleteHeader() {
        return getText(COMPLETE_HEADER);
    }

    public String getCompleteText() {
        return getText(COMPLETE_TEXT);
    }

    public boolean isOrderComplete() {
        return isVisible(COMPLETE_HEADER)
                && getCompleteHeader().contains("Thank you for your order");
    }

    public boolean isPonyExpressImageVisible() {
        return isVisible(PONY_EXPRESS_IMAGE);
    }

    public ProductsPage clickBackHome() {
        LogHelper.step("Clicking Back Home");
        click(BACK_HOME_BUTTON);
        return new ProductsPage(page);
    }
}