package com.bjit.ecauto.dataproviders;

import org.testng.annotations.DataProvider;

public class TestDataProviders {

    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return new Object[][] {
                { "standard_user", "secret_sauce" }
        };
    }

    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        return new Object[][] {
                // { username,              password,         expectedError }
                { "locked_out_user",        "secret_sauce",   "Sorry, this user has been locked out" },
                { "standard_user",          "wrong_password", "Username and password do not match" },
                { "",                       "secret_sauce",   "Username is required" },
                { "standard_user",          "",               "Password is required" },
                { "",                       "",               "Username is required" },
                { "nonexistent_user",       "secret_sauce",   "Username and password do not match" }
        };
    }

    @DataProvider(name = "allUserTypes")
    public static Object[][] allUserTypes() {
        return new Object[][] {
                { "standard_user",             "secret_sauce", true  },
                { "locked_out_user",           "secret_sauce", false },
                { "problem_user",              "secret_sauce", true  },
                { "performance_glitch_user",   "secret_sauce", true  },
                { "error_user",                "secret_sauce", true  },
                { "visual_user",               "secret_sauce", true  }
        };
    }

    @DataProvider(name = "sortOptions")
    public static Object[][] sortOptions() {
        return new Object[][] {
                // { sortValue,   sortDescription }
                { "az",   "Name (A to Z)" },
                { "za",   "Name (Z to A)" },
                { "lohi", "Price (Low to High)" },
                { "hilo", "Price (High to Low)" }
        };
    }

    @DataProvider(name = "checkoutData")
    public static Object[][] checkoutData() {
        return new Object[][] {
                { "John",  "Doe",   "10001" },
                { "Jane",  "Smith", "90210" },
                { "Alex",  "Brown", "60601" }
        };
    }

    @DataProvider(name = "invalidCheckoutData")
    public static Object[][] invalidCheckoutData() {
        return new Object[][] {
                // { firstName, lastName, postalCode, expectedError }
                { "",      "Doe",   "10001", "Error: First Name is required" },
                { "John",  "",      "10001", "Error: Last Name is required" },
                { "John",  "Doe",   "",      "Error: Postal Code is required" }
        };
    }

    @DataProvider(name = "productSlugs")
    public static Object[][] productSlugs() {
        return new Object[][] {
                { "sauce-labs-backpack",      "Sauce Labs Backpack",          "$29.99" },
                { "sauce-labs-bike-light",    "Sauce Labs Bike Light",        "$9.99"  },
                { "sauce-labs-bolt-t-shirt",  "Sauce Labs Bolt T-Shirt",      "$15.99" },
                { "sauce-labs-fleece-jacket", "Sauce Labs Fleece Jacket",      "$49.99" },
                { "sauce-labs-onesie",        "Sauce Labs Onesie",            "$7.99"  }
        };
    }
}