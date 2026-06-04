package com.bjit.ecauto.contracts;

/**
 * Contract interface defining all login-related test actions.
 * Ensures consistent test coverage across login test implementations.
 */
public interface ILoginActions {
    
    void testValidLogin();
    
    void testAllUserTypes(String username, String password, boolean shouldPass);
    
    void testInvalidLogin(String username, String password, String expectedError);
    
    void testLockedOutUser();
    
    void testCloseErrorMessage();
    
    void testLogout();
}


