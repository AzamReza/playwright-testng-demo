package com.bjit.ecauto.base;

import com.bjit.ecauto.contracts.ICheckoutActions;
import com.bjit.ecauto.pages.CartPage;
import com.bjit.ecauto.pages.CheckoutPage;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;

/**
 * Abstract base class for all checkout tests.
 * Extends BaseTest to inherit Playwright lifecycle management.
 * Implements ICheckoutActions to enforce checkout test contract.
 * Provides shared factory methods and helpers for checkout tests.
 */
public abstract class AbstractCheckoutTest extends BaseTest implements ICheckoutActions {
    
    /**
     * Factory method to create and return a CheckoutPage instance.
     * Centralizes page object instantiation for all checkout tests.
     */
    protected CheckoutPage getCheckoutPage() {
        return new CheckoutPage(page);
    }
    
    /**
     * Helper method to navigate to the first step of checkout.
     * Performs login, adds item to cart, and navigates to checkout step 1.
     */
    protected CheckoutPage goToCheckoutStepOne() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = loginPage.loginAsStandardUser();
        productsPage.addBackpackToCart();
        
        CartPage cartPage = productsPage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        return checkoutPage;
    }
    
    /**
     * Helper method to navigate to the second step of checkout with a single item.
     * Includes filling checkout step 1 form.
     */
    protected CheckoutPage goToCheckoutStepTwo() {
        CheckoutPage checkoutPage = goToCheckoutStepOne();
        checkoutPage.enterFirstName("John");
        checkoutPage.enterLastName("Doe");
        checkoutPage.enterPostalCode("12345");
        checkoutPage.clickContinue();
        return checkoutPage;
    }
    
    /**
     * Helper method to navigate to checkout step 2 with multiple items.
     * Adds multiple items to cart before checkout.
     */
    protected CheckoutPage goToCheckoutStepTwoWithMultipleItems() {
        LoginPage loginPage = new LoginPage(page);
        ProductsPage productsPage = loginPage.loginAsStandardUser();
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.addOnesieToCart();
        
        CartPage cartPage = productsPage.goToCart();
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        checkoutPage.enterFirstName("Jane");
        checkoutPage.enterLastName("Smith");
        checkoutPage.enterPostalCode("54321");
        checkoutPage.clickContinue();
        return checkoutPage;
    }
}



