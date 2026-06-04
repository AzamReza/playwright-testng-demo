package com.bjit.ecauto.contracts;

/**
 * Contract interface defining all shopping cart test actions.
 * Ensures consistent test coverage across cart test implementations.
 */
public interface ICartActions {
    
    void testEmptyCartPage();
    
    void testSingleItemInCart();
    
    void testMultipleItemsInCart();
    
    void testRemoveItemFromCart();
    
    void testRemoveAllItemsFromCart();
    
    void testContinueShopping();
    
    void testProceedToCheckout();
}


