package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.pages.CartPage;
import com.bjit.ecauto.pages.ProductsPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class CartTest extends BaseTest {

    // ============================
    // CART PAGE NAVIGATION
    // ============================
    @Test(priority = 1,
            description = "Verify cart page loads correctly with empty cart")
    public void testEmptyCartPage() {
        ProductsPage productsPage = loginAsStandardUser();
        CartPage cartPage = productsPage.goToCart();

        Assert.assertTrue(cartPage.isOnCartPage(), "Should be on Cart page");
        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Item count should be 0");
    }

    private ProductsPage loginAsStandardUser() {
        return null;
    }

    // ============================
    // CART ITEMS VERIFICATION
    // ============================
    @Test(priority = 2,
            description = "Verify single item appears in cart with correct details")
    public void testSingleItemInCart() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();

        CartPage cartPage = productsPage.goToCart();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(cartPage.isOnCartPage(), "Should be on Cart page");
        soft.assertEquals(cartPage.getCartItemCount(), 1, "Should have 1 item");
        soft.assertTrue(cartPage.containsItem("Sauce Labs Backpack"),
                "Cart should contain Backpack");

        List<String> prices = cartPage.getCartItemPrices();
        soft.assertTrue(prices.contains("$29.99"),
                "Backpack price should be $29.99");

        soft.assertAll();
    }

    @Test(priority = 3,
            description = "Verify multiple items appear in cart")
    public void testMultipleItemsInCart() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();
        productsPage.addFleeceJacketToCart();

        CartPage cartPage = productsPage.goToCart();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(cartPage.getCartItemCount(), 3, "Should have 3 items");
        soft.assertTrue(cartPage.containsItem("Sauce Labs Backpack"), "Should contain Backpack");
        soft.assertTrue(cartPage.containsItem("Sauce Labs Bike Light"), "Should contain Bike Light");
        soft.assertTrue(cartPage.containsItem("Sauce Labs Fleece Jacket"), "Should contain Fleece Jacket");
        soft.assertAll();
    }

    // ============================
    // REMOVE FROM CART
    // ============================
    @Test(priority = 4,
            description = "Verify item can be removed from cart page")
    public void testRemoveItemFromCart() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();

        CartPage cartPage = productsPage.goToCart();
        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Should start with 2 items");

        cartPage.removeBackpack();
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Should have 1 item after removal");
        Assert.assertFalse(cartPage.containsItem("Sauce Labs Backpack"),
                "Backpack should no longer be in cart");
        Assert.assertTrue(cartPage.containsItem("Sauce Labs Bike Light"),
                "Bike Light should still be in cart");
    }

    @Test(priority = 5,
            description = "Verify removing all items results in empty cart")
    public void testRemoveAllItemsFromCart() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();
        productsPage.addBikeLightToCart();

        CartPage cartPage = productsPage.goToCart();
        cartPage.removeBackpack();
        cartPage.removeBikeLight();

        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should be empty");
        Assert.assertEquals(cartPage.getCartItemCount(), 0, "Count should be 0");
    }

    // ============================
    // CONTINUE SHOPPING
    // ============================
    @Test(priority = 6,
            description = "Verify Continue Shopping returns to products page")
    public void testContinueShopping() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();

        CartPage cartPage = productsPage.goToCart();
        ProductsPage returnedPage = cartPage.continueShopping();

        Assert.assertTrue(returnedPage.isOnProductsPage(),
                "Should be back on Products page");
        Assert.assertEquals(returnedPage.getCartItemCount(), 1,
                "Cart should still have 1 item");
    }

    // ============================
    // PROCEED TO CHECKOUT
    // ============================
    @Test(priority = 7,
            description = "Verify cart proceeds to checkout step one")
    public void testProceedToCheckout() {
        ProductsPage productsPage = loginAsStandardUser();
        productsPage.addBackpackToCart();

        CartPage cartPage = productsPage.goToCart();
        var checkoutPage = cartPage.proceedToCheckout();

        Assert.assertTrue(checkoutPage.isOnStepOne(),
                "Should be on Checkout Step One");
    }
}