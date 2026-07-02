package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.AbstractCheckoutTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.pages.CartPage;
import com.bjit.ecauto.pages.CheckoutPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

@Feature("Checkout Process")
public class CheckoutTest extends AbstractCheckoutTest {

    // ========================================================
    // STEP ONE — CUSTOMER INFORMATION TESTS
    // ========================================================

    @Override
    @Story("Checkout Step One - Information")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Preconditions: User in cart with items, clicked 'Proceed to Checkout'\n" +
            "Steps: Verify checkout page loads and displays step one form\n" +
            "Expected: On Checkout Step One, title contains 'Your Information', form fields visible")
    @Test(priority = 1,
            description = "Verify checkout step one page loads correctly")
    public void testCheckoutStepOneLoads() {
        CheckoutPage checkoutPage = goToCheckoutStepOne();

        Assert.assertTrue(checkoutPage.isOnStepOne(),
                "Should be on Checkout: Your Information page");
        Assert.assertTrue(checkoutPage.getStepTitle().contains("Your Information"),
                "Title should contain 'Your Information'");
    }

    @Override
    @Test(priority = 2,
            dataProvider = "checkoutValidData",
            dataProviderClass = TestDataProviders.class,
            description = "Verify valid customer info proceeds to step two")
    public void testValidCheckoutInfo(String firstName, String lastName, String postalCode) {
        CheckoutPage checkoutPage = goToCheckoutStepOne();

        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(postalCode);
        checkoutPage.clickContinue();

        Assert.assertTrue(checkoutPage.isOnStepTwo(),
                "Should proceed to Checkout Overview for: "
                        + firstName + " " + lastName);
    }

    @Override
    @Test(priority = 3,
            dataProvider = "checkoutInvalidData",
            dataProviderClass = TestDataProviders.class,
            description = "Verify error messages for missing checkout info")
    public void testInvalidCheckoutInfo(String firstName, String lastName,
                                        String postalCode, String expectedError) {
        CheckoutPage checkoutPage = goToCheckoutStepOne();

        checkoutPage.enterFirstName(firstName);
        checkoutPage.enterLastName(lastName);
        checkoutPage.enterPostalCode(postalCode);
        checkoutPage.clickContinueExpectingError();

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
                "Error message should be displayed");
        Assert.assertEquals(checkoutPage.getErrorMessage(), expectedError,
                "Error message should match expected");
    }

    @Override
    @Test(priority = 4,
            description = "Verify all empty fields shows first name required error")
    public void testAllFieldsEmpty() {
        CheckoutPage checkoutPage = goToCheckoutStepOne();

        checkoutPage.clickContinueExpectingError();

        Assert.assertTrue(checkoutPage.isErrorDisplayed(),
                "Error should be displayed");
        Assert.assertTrue(
                checkoutPage.getErrorMessage().contains("First Name is required"),
                "Should show first name required error");
    }

    @Override
    @Test(priority = 5,
            description = "Verify cancel on step one returns to cart")
    public void testCancelOnStepOne() {
        CheckoutPage checkoutPage = goToCheckoutStepOne();

        CartPage cartPage = checkoutPage.clickCancel();

        Assert.assertTrue(cartPage.isOnCartPage(),
                "Should return to cart page after cancel");
        Assert.assertEquals(cartPage.getCartItemCount(), 1,
                "Cart should still have the item");
    }

    // ========================================================
    // STEP TWO — ORDER OVERVIEW TESTS
    // ========================================================

    @Override
    @Story("Checkout Step Two - Overview")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Preconditions: User on Checkout Step Two for single item (Backpack)\n" +
            "Steps: Verify step two page displays, check item details and price\n" +
            "Expected: Shows 1 item (Backpack), price $29.99")
    @Test(priority = 6,
            description = "Verify checkout overview shows correct single item")
    public void testOverviewShowsSingleItem() {
        CheckoutPage checkoutPage = goToCheckoutStepTwo();

        SoftAssert soft = new SoftAssert();
        soft.assertTrue(checkoutPage.isOnStepTwo(),
                "Should be on Checkout Overview");
        soft.assertEquals(checkoutPage.getOverviewItemCount(), 1,
                "Should show 1 item in overview");

        List<String> itemNames = checkoutPage.getOverviewItemNames();
        soft.assertTrue(itemNames.contains("Sauce Labs Backpack"),
                "Overview should contain Backpack");

        List<String> itemPrices = checkoutPage.getOverviewItemPrices();
        soft.assertTrue(itemPrices.contains("$29.99"),
                "Backpack price should be $29.99");

        soft.assertAll();
    }

    @Override
    @Test(priority = 7,
            description = "Verify checkout overview shows correct multiple items")
    public void testOverviewShowsMultipleItems() {
        CheckoutPage checkoutPage = goToCheckoutStepTwoWithMultipleItems();

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(checkoutPage.getOverviewItemCount(), 3,
                "Should show 3 items in overview");

        List<String> names = checkoutPage.getOverviewItemNames();
        soft.assertTrue(names.contains("Sauce Labs Backpack"), "Should contain Backpack");
        soft.assertTrue(names.contains("Sauce Labs Bike Light"), "Should contain Bike Light");
        soft.assertTrue(names.contains("Sauce Labs Onesie"), "Should contain Onesie");

        soft.assertAll();
    }

    @Override
    @Test(priority = 8,
            description = "Verify subtotal calculation is correct for single item")
    public void testSubtotalSingleItem() {
        CheckoutPage checkoutPage = goToCheckoutStepTwo();

        double subtotal = checkoutPage.getSubtotalAsNumber();
        Assert.assertEquals(subtotal, 29.99,
                "Subtotal should be $29.99 for Backpack");
    }

    @Override
    @Test(priority = 9,
            description = "Verify subtotal calculation is correct for multiple items")
    public void testSubtotalMultipleItems() {
        CheckoutPage checkoutPage = goToCheckoutStepTwoWithMultipleItems();

        // Backpack $29.99 + Bike Light $9.99 + Onesie $7.99 = $47.97
        double expectedSubtotal = 29.99 + 9.99 + 7.99;
        double actualSubtotal = checkoutPage.getSubtotalAsNumber();

        Assert.assertEquals(actualSubtotal, expectedSubtotal, 0.01,
                "Subtotal should equal sum of item prices");
    }

    @Override
    @Story("Checkout Step Two - Pricing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Preconditions: User on Checkout Overview, subtotal calculated\n" +
            "Steps: Retrieve subtotal and tax amounts, verify tax = 8% of subtotal\n" +
            "Expected: Tax calculated correctly (approximately 8% with 0.02 tolerance)")
    @Test(priority = 10,
            description = "Verify tax is calculated correctly")
    public void testTaxCalculation() {
        CheckoutPage checkoutPage = goToCheckoutStepTwo();

        // SauceDemo calculates tax at roughly 8% (e.g., $2.40 for $29.99 subtotal)
        double subtotal = checkoutPage.getSubtotalAsNumber();
        double actualTax = checkoutPage.getTaxAsNumber();
        double expectedTax = Math.round((subtotal * 0.08) * 100.0) / 100.0;

        Assert.assertEquals(actualTax, expectedTax, 0.02,
                "Tax calculation should match 8% regional baseline");
    }
}
