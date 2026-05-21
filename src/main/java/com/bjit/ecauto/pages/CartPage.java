package com.bjit.ecauto.pages;

import com.microsoft.playwright.Page;
import com.bjit.ecauto.base.BasePage;
import com.bjit.ecauto.utils.LogHelper;

import java.util.List;

public class CartPage extends BasePage {

    // ============================
    // LOCATORS
    // ============================
    private static final String PAGE_TITLE = ".title";
    private static final String CART_ITEMS = ".cart_item";
    private static final String CART_ITEM_NAMES = ".inventory_item_name";
    private static final String CART_ITEM_PRICES = ".inventory_item_price";
    private static final String CART_ITEM_QUANTITIES = ".cart_quantity";
    private static final String CONTINUE_SHOPPING_BUTTON = "[data-test='continue-shopping']";
    private static final String CHECKOUT_BUTTON = "[data-test='checkout']";
    private static final String REMOVE_BUTTON_PREFIX = "[data-test='remove-";

    // ============================
    // CONSTRUCTOR
    // ============================
    public CartPage(Page page) {
        super(page);
    }

    // ============================
    // PAGE VERIFICATION
    // ============================
    public boolean isOnCartPage() {
        return isVisible(PAGE_TITLE) && getText(PAGE_TITLE).equals("Your Cart");
    }

    // ============================
    // CART INFORMATION
    // ============================
    public int getCartItemCount() {
        return getElementCount(CART_ITEMS);
    }

    public List<String> getCartItemNames() {
        return getAllTexts(CART_ITEM_NAMES);
    }

    public List<String> getCartItemPrices() {
        return getAllTexts(CART_ITEM_PRICES);
    }

    public boolean isCartEmpty() {
        return getCartItemCount() == 0;
    }

    public boolean containsItem(String itemName) {
        return getCartItemNames().contains(itemName);
    }

    // ============================
    // ACTIONS
    // ============================
    public CartPage removeItem(String productSlug) {
        String selector = REMOVE_BUTTON_PREFIX + productSlug + "']";
        LogHelper.step("Removing item from cart: " + productSlug);
        click(selector);
        return this;
    }

    public CartPage removeBackpack() {
        return removeItem("sauce-labs-backpack");
    }

    public CartPage removeBikeLight() {
        return removeItem("sauce-labs-bike-light");
    }

    public ProductsPage continueShopping() {
        LogHelper.step("Continuing shopping");
        click(CONTINUE_SHOPPING_BUTTON);
        return new ProductsPage(page);
    }

    public CheckoutPage proceedToCheckout() {
        LogHelper.step("Proceeding to checkout");
        click(CHECKOUT_BUTTON);
        return new CheckoutPage(page);
    }
}