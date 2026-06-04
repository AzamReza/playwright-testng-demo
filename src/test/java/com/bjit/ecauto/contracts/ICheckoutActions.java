package com.bjit.ecauto.contracts;

/**
 * Contract interface defining all checkout test actions.
 * Ensures consistent test coverage across checkout test implementations.
 */
public interface ICheckoutActions {
    
    void testCheckoutStepOneLoads();
    
    void testValidCheckoutInfo(String firstName, String lastName, String postalCode);
    
    void testInvalidCheckoutInfo(String firstName, String lastName, String postalCode, String expectedError);
    
    void testAllFieldsEmpty();
    
    void testCancelOnStepOne();
    
    void testOverviewShowsSingleItem();
    
    void testOverviewShowsMultipleItems();
    
    void testSubtotalSingleItem();
    
    void testSubtotalMultipleItems();
    
    void testTaxCalculation();
}


