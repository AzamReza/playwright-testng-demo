package com.bjit.ecauto.base;

import com.bjit.ecauto.contracts.IProductsActions;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;

/**
 * Abstract base class for all products page tests.
 * Extends BaseTest to inherit Playwright lifecycle management.
 * Implements IProductsActions to enforce products test contract.
 * Provides shared factory methods and helpers for products tests.
 */
public abstract class AbstractProductsTest extends BaseTest implements IProductsActions {
    
    /**
     * Factory method to create and return a ProductsPage instance.
     * Centralizes page object instantiation for all products tests.
     */
    protected ProductsPage getProductsPage() {
        return new ProductsPage(page);
    }
    
    /**
     * Helper method to navigate to products page via login.
     * Performs standard user login workflow and returns ProductsPage.
     */
    protected ProductsPage loginAndGetProductsPage() {
        LoginPage loginPage = new LoginPage(page);
        return loginPage.loginAsStandardUser();
    }
    
    /**
     * Helper method to navigate to a specific path and return ProductsPage.
     */
    protected ProductsPage navigateToProductsPath(String path) {
        ProductsPage productsPage = getProductsPage();
        productsPage.navigateToPath(path);
        return productsPage;
    }
}

