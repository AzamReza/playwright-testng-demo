package com.bjit.ecauto.base;

import com.bjit.ecauto.contracts.ICartActions;
import com.bjit.ecauto.pages.CartPage;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;

/**
 * Abstract base class for all shopping cart tests.
 * Extends BaseTest to inherit Playwright lifecycle management.
 * Implements ICartActions to enforce cart test contract.
 * Provides shared factory methods and helpers for cart tests.
 */
public abstract class AbstractCartTest extends BaseTest implements ICartActions {
    
    /**
     * Factory method to create and return a CartPage instance.
     * Centralizes page object instantiation for all cart tests.
     */
    protected CartPage getCartPage() {
        return new CartPage(page);
    }
    
    /**
     * Helper method to perform standard user login and return ProductsPage.
     * Used by cart tests to set up preconditions (logged in state).
     */
    protected ProductsPage loginAsStandardUser() {
        LoginPage loginPage = new LoginPage(page);
        return loginPage.loginAsStandardUser();
    }
    
    /**
     * Helper method to navigate directly to cart page.
     */
    protected CartPage navigateToCart() {
        CartPage cartPage = getCartPage();
        cartPage.navigateToPath("/cart.html");
        return cartPage;
    }
}

