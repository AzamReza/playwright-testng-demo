package com.bjit.ecauto.pages;

import com.microsoft.playwright.Page;
import com.bjit.ecauto.base.BasePage;
import com.bjit.ecauto.utils.LogHelper;

import java.util.List;
import java.util.stream.Collectors;

public class ProductsPage extends BasePage {

    // ============================
    // LOCATORS
    // ============================
    private static final String PAGE_TITLE = ".title";
    private static final String PRODUCT_ITEMS = ".inventory_item";
    private static final String PRODUCT_NAMES = ".inventory_item_name";
    private static final String PRODUCT_PRICES = ".inventory_item_price";
    private static final String PRODUCT_DESCRIPTIONS = ".inventory_item_desc";
    private static final String SORT_DROPDOWN = "[data-test='product-sort-container']";
    private static final String CART_BADGE = ".shopping_cart_badge";
    private static final String CART_LINK = ".shopping_cart_link";
    private static final String BURGER_MENU = "#react-burger-menu-btn";
    private static final String LOGOUT_LINK = "#logout_sidebar_link";
    private static final String ADD_TO_CART_PREFIX = "[data-test='add-to-cart-";
    private static final String REMOVE_PREFIX = "[data-test='remove-";

    // ============================
    // CONSTRUCTOR
    // ============================
    public ProductsPage(Page page) {
        super(page);
    }

    // ============================
    // PAGE VERIFICATION
    // ============================
    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    public boolean isOnProductsPage() {
        return isVisible(PAGE_TITLE) && getText(PAGE_TITLE).equals("Products");
    }

    // ============================
    // PRODUCT INFORMATION
    // ============================
    public int getProductCount() {
        return getElementCount(PRODUCT_ITEMS);
    }

    public List<String> getAllProductNames() {
        return getAllTexts(PRODUCT_NAMES);
    }

    public List<String> getAllProductPrices() {
        return getAllTexts(PRODUCT_PRICES);
    }

    public List<Double> getAllProductPricesAsNumbers() {
        return getAllProductPrices().stream()
                .map(price -> price.replace("$", ""))
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    // ============================
    // ADD TO CART
    // ============================
    public ProductsPage addToCart(String productSlug) {
        // productSlug example: "sauce-labs-backpack"
        String selector = ADD_TO_CART_PREFIX + productSlug + "']";
        LogHelper.step("Adding to cart: " + productSlug);
        click(selector);
        return this;
    }

    public ProductsPage addBackpackToCart() {
        return addToCart("sauce-labs-backpack");
    }

    public ProductsPage addBikeLightToCart() {
        return addToCart("sauce-labs-bike-light");
    }

    public ProductsPage addBoltTShirtToCart() {
        return addToCart("sauce-labs-bolt-t-shirt");
    }

    public ProductsPage addFleeceJacketToCart() {
        return addToCart("sauce-labs-fleece-jacket");
    }

    public ProductsPage addOnesieToCart() {
        return addToCart("sauce-labs-onesie");
    }

    public ProductsPage addRedTShirtToCart() {
        return addToCart("test.allthethings()-t-shirt-(red)");
    }

    // ============================
    // REMOVE FROM CART
    // ============================
    public ProductsPage removeFromCart(String productSlug) {
        String selector = REMOVE_PREFIX + productSlug + "']";
        LogHelper.step("Removing from cart: " + productSlug);
        click(selector);
        return this;
    }

    public ProductsPage removeBackpackFromCart() {
        return removeFromCart("sauce-labs-backpack");
    }

    // ============================
    // CART
    // ============================
    public int getCartItemCount() {
        if (!isVisible(CART_BADGE)) {
            return 0;
        }
        String count = getText(CART_BADGE);
        return Integer.parseInt(count);
    }

    public CartPage goToCart() {
        LogHelper.step("Navigating to cart");
        click(CART_LINK);
        return new CartPage(page);
    }

    // ============================
    // SORTING
    // ============================
    public ProductsPage sortByNameAZ() {
        LogHelper.step("Sorting: Name (A to Z)");
        selectByValue(SORT_DROPDOWN, "az");
        return this;
    }

    public ProductsPage sortByNameZA() {
        LogHelper.step("Sorting: Name (Z to A)");
        selectByValue(SORT_DROPDOWN, "za");
        return this;
    }

    public ProductsPage sortByPriceLowToHigh() {
        LogHelper.step("Sorting: Price (Low to High)");
        selectByValue(SORT_DROPDOWN, "lohi");
        return this;
    }

    public ProductsPage sortByPriceHighToLow() {
        LogHelper.step("Sorting: Price (High to Low)");
        selectByValue(SORT_DROPDOWN, "hilo");
        return this;
    }

    // ============================
    // LOGOUT
    // ============================
    public LoginPage logout() {
        LogHelper.step("Logging out");
        click(BURGER_MENU);
        click(LOGOUT_LINK);
        return new LoginPage(page);
    }
}