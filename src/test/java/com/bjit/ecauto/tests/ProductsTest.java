package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.dataproviders.TestDataProviders;
import com.bjit.ecauto.pages.LoginPage;
import com.bjit.ecauto.pages.ProductsPage;
import com.bjit.ecauto.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductsTest extends BaseTest {

    private ProductsPage productsPage;

    @BeforeMethod(dependsOnMethods = "createContext")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(page);
        productsPage = loginPage.navigate()
                .loginAs(ConfigReader.getStandardUsername(),
                        ConfigReader.getStandardPassword());
    }

    // ============================
    // PAGE VERIFICATION
    // ============================
    @Test(priority = 1,
            description = "Verify products page loads with correct title")
    public void testProductsPageLoads() {
        Assert.assertTrue(productsPage.isOnProductsPage(),
                "Should be on Products page");
        Assert.assertEquals(productsPage.getPageTitle(), "Products",
                "Page title should be 'Products'");
    }

    @Test(priority = 2,
            description = "Verify all 6 products are displayed")
    public void testProductCount() {
        int count = productsPage.getProductCount();
        Assert.assertEquals(count, 6,
                "Should display exactly 6 products");
    }

    @Test(priority = 3,
            description = "Verify all product names are displayed")
    public void testProductNamesDisplayed() {
        SoftAssert soft = new SoftAssert();
        List<String> names = productsPage.getAllProductNames();

        soft.assertEquals(names.size(), 6, "Should have 6 product names");
        soft.assertTrue(names.contains("Sauce Labs Backpack"), "Should have Backpack");
        soft.assertTrue(names.contains("Sauce Labs Bike Light"), "Should have Bike Light");
        soft.assertTrue(names.contains("Sauce Labs Bolt T-Shirt"), "Should have Bolt T-Shirt");
        soft.assertTrue(names.contains("Sauce Labs Fleece Jacket"), "Should have Fleece Jacket");
        soft.assertTrue(names.contains("Sauce Labs Onesie"), "Should have Onesie");
        soft.assertTrue(names.contains("Test.allTheThings() T-Shirt (Red)"), "Should have Red T-Shirt");
        soft.assertAll();
    }

    @Test(priority = 4,
            description = "Verify all product prices are displayed and valid")
    public void testProductPricesValid() {
        List<Double> prices = productsPage.getAllProductPricesAsNumbers();

        Assert.assertEquals(prices.size(), 6, "Should have 6 prices");

        for (Double price : prices) {
            Assert.assertTrue(price > 0,
                    "Each price should be positive, but found: " + price);
        }
    }

    // ============================
    // SORTING TESTS
    // ============================
    @Test(priority = 5,
            description = "Verify products sort by Name A to Z")
    public void testSortByNameAZ() {
        productsPage.sortByNameAZ();

        List<String> names = productsPage.getAllProductNames();
        List<String> sorted = new ArrayList<>(names);
        Collections.sort(sorted);

        Assert.assertEquals(names, sorted,
                "Products should be sorted alphabetically A-Z");
    }

    @Test(priority = 6,
            description = "Verify products sort by Name Z to A")
    public void testSortByNameZA() {
        productsPage.sortByNameZA();

        List<String> names = productsPage.getAllProductNames();
        List<String> sorted = new ArrayList<>(names);
        sorted.sort(Collections.reverseOrder());

        Assert.assertEquals(names, sorted,
                "Products should be sorted reverse alphabetically Z-A");
    }

    @Test(priority = 7,
            description = "Verify products sort by Price Low to High")
    public void testSortByPriceLowToHigh() {
        productsPage.sortByPriceLowToHigh();

        List<Double> prices = productsPage.getAllProductPricesAsNumbers();
        List<Double> sorted = new ArrayList<>(prices);
        Collections.sort(sorted);

        Assert.assertEquals(prices, sorted,
                "Prices should be sorted low to high");
    }

    @Test(priority = 8,
            description = "Verify products sort by Price High to Low")
    public void testSortByPriceHighToLow() {
        productsPage.sortByPriceHighToLow();

        List<Double> prices = productsPage.getAllProductPricesAsNumbers();
        List<Double> sorted = new ArrayList<>(prices);
        sorted.sort(Collections.reverseOrder());

        Assert.assertEquals(prices, sorted,
                "Prices should be sorted high to low");
    }

    // ============================
    // ADD TO CART TESTS
    // ============================
    @Test(priority = 9,
            description = "Verify adding single item to cart updates badge")
    public void testAddSingleItemToCart() {
        Assert.assertEquals(productsPage.getCartItemCount(), 0,
                "Cart should be empty initially");

        productsPage.addBackpackToCart();

        Assert.assertEquals(productsPage.getCartItemCount(), 1,
                "Cart badge should show 1 after adding one item");
    }

    @Test(priority = 10,
            description = "Verify adding multiple items to cart")
    public void testAddMultipleItemsToCart() {
        productsPage.addBackpackToCart()
                .addBikeLightToCart()
                .addBoltTShirtToCart();

        Assert.assertEquals(productsPage.getCartItemCount(), 3,
                "Cart badge should show 3 after adding three items");
    }

    @Test(priority = 11,
            description = "Verify adding all 6 items to cart")
    public void testAddAllItemsToCart() {
        productsPage.addBackpackToCart()
                .addBikeLightToCart()
                .addBoltTShirtToCart()
                .addFleeceJacketToCart()
                .addOnesieToCart()
                .addRedTShirtToCart();

        Assert.assertEquals(productsPage.getCartItemCount(), 6,
                "Cart badge should show 6 after adding all items");
    }

    // ============================
    // REMOVE FROM CART TESTS
    // ============================
    @Test(priority = 12,
            description = "Verify removing item from cart on products page")
    public void testRemoveItemFromProductsPage() {
        productsPage.addBackpackToCart();
        Assert.assertEquals(productsPage.getCartItemCount(), 1,
                "Cart should have 1 item");

        productsPage.removeBackpackFromCart();
        Assert.assertEquals(productsPage.getCartItemCount(), 0,
                "Cart should be empty after removing");
    }

    @Test(priority = 13,
            description = "Verify add and remove multiple items")
    public void testAddAndRemoveMultiple() {
        productsPage.addBackpackToCart()
                .addBikeLightToCart();
        Assert.assertEquals(productsPage.getCartItemCount(), 2,
                "Should have 2 items");

        productsPage.removeBackpackFromCart();
        Assert.assertEquals(productsPage.getCartItemCount(), 1,
                "Should have 1 item after removing backpack");
    }
}