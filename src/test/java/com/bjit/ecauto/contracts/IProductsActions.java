package com.bjit.ecauto.contracts;

/**
 * Contract interface defining all products page test actions.
 * Ensures consistent test coverage across products test implementations.
 */
public interface IProductsActions {
    
    void testProductsPageLoaded();
    
    void testProductCount();
    
    void testProductNamesDisplayed();
    
    void testProductPricesValid();
    
    void testSortByNameAZ();
    
    void testSortByNameZA();
    
    void testSortByPriceLowToHigh();
    
    void testSortByPriceHighToLow();
    
    void testAddSingleItemToCart();
    
    void testAddMultipleItemsToCart();
    
    void testAddAllItemsToCart();
    
    void testRemoveItemFromProductsPage();
    
    void testAddAndRemoveMultiple();
}


