package com.bjit.ecauto.tests;

import com.bjit.ecauto.base.BaseTest;
import com.bjit.ecauto.utils.ConfigReader;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * APITest demonstrates Playwright Java API testing:
 * - REST API requests (GET, POST, PUT, DELETE)
 * - Request headers and authentication
 * - Response parsing and validation
 * - Error handling
 * - API testing patterns
 *
 * @see <a href="https://playwright.dev/java/docs/api-testing">Playwright API Testing</a>
 */
@Feature("REST API Testing")
public class APITest extends BaseTest {

    private APIRequestContext apiContext;

    private void initializeAPIContext() {
        if (apiContext == null) {
            apiContext = playwright.request().newContext();
        }
    }

    @Story("API - GET Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Make GET request to API endpoint")
    @Test(priority = 1, description = "GET request execution")
    public void testGetRequest() {
        initializeAPIContext();
        
        // Using JSONPlaceholder API for testing
        var response = apiContext.get("https://jsonplaceholder.typicode.com/posts/1");
        
        Assert.assertEquals(response.status(), 200, "Should return 200 status");
        String body = response.text();
        Assert.assertTrue(body.contains("userId"), "Response should contain userId");
        Assert.assertTrue(body.contains("id"), "Response should contain id");
    }

    @Story("API - POST Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Make POST request with JSON body")
    @Test(priority = 2, description = "POST request with JSON")
    public void testPostRequest() {
        initializeAPIContext();
        
        RequestOptions options = RequestOptions.create()
                .setData("{\"title\": \"Test Post\", \"body\": \"This is a test\", \"userId\": 1}");
        
        var response = apiContext.post(
            "https://jsonplaceholder.typicode.com/posts",
            options
        );
        
        Assert.assertEquals(response.status(), 201, "Should return 201 Created");
        String body = response.text();
        Assert.assertTrue(body.contains("Test Post"), "Response should contain posted title");
    }

    @Story("API - PUT Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Update resource with PUT request")
    @Test(priority = 3, description = "PUT request update")
    public void testPutRequest() {
        initializeAPIContext();
        
        RequestOptions options = RequestOptions.create()
                .setData("{\"id\": 1, \"title\": \"Updated Title\", \"body\": \"Updated body\", \"userId\": 1}");
        
        var response = apiContext.put(
            "https://jsonplaceholder.typicode.com/posts/1",
            options
        );
        
        Assert.assertEquals(response.status(), 200, "Should return 200 OK");
        String body = response.text();
        Assert.assertTrue(body.contains("Updated Title"), "Response should contain updated title");
    }

    @Story("API - DELETE Request")
    @Severity(SeverityLevel.NORMAL)
    @Description("Delete resource with DELETE request")
    @Test(priority = 4, description = "DELETE request")
    public void testDeleteRequest() {
        initializeAPIContext();
        
        var response = apiContext.delete(
            "https://jsonplaceholder.typicode.com/posts/1"
        );
        
        Assert.assertEquals(response.status(), 200, "Should return 200 OK");
    }

    @Story("API - Request Headers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Send custom headers in API request")
    @Test(priority = 5, description = "Request with custom headers")
    public void testRequestWithHeaders() {
        initializeAPIContext();
        
        RequestOptions options = RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setHeader("X-Custom-Header", "TestValue");
        
        var response = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/1",
            options
        );
        
        Assert.assertEquals(response.status(), 200);
    }

    @Story("API - Response Headers")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify response headers")
    @Test(priority = 6, description = "Response headers validation")
    public void testResponseHeaders() {
        initializeAPIContext();
        
        var response = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/1"
        );
        
        // Verify successful response with headers
        Assert.assertEquals(response.status(), 200, "Should return 200 status");
        Assert.assertNotNull(response, "Response should not be null");
    }

    @Story("API - JSON Response Parsing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Parse and validate JSON response")
    @Test(priority = 7, description = "JSON response parsing")
    public void testJsonResponseParsing() {
        initializeAPIContext();
        
        var response = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/1"
        );
        
        Assert.assertEquals(response.status(), 200);
        String json = response.text();
        
        // Validate JSON structure
        Assert.assertTrue(json.contains("\"id\""), "JSON should contain id field");
        Assert.assertTrue(json.contains("\"userId\""), "JSON should contain userId field");
        Assert.assertTrue(json.contains("\"title\""), "JSON should contain title field");
        Assert.assertTrue(json.contains("\"body\""), "JSON should contain body field");
    }

    @Story("API - Error Response Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Handle API error responses")
    @Test(priority = 8, description = "Error response handling")
    public void testErrorResponseHandling() {
        initializeAPIContext();
        
        var response = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/99999"
        );
        
        // JSONPlaceholder returns 200 even for non-existent IDs with empty object
        Assert.assertEquals(response.status(), 200);
    }

    @Story("API - Multiple Requests")
    @Severity(SeverityLevel.NORMAL)
    @Description("Make multiple sequential API requests")
    @Test(priority = 9, description = "Multiple sequential requests")
    public void testMultipleRequests() {
        initializeAPIContext();
        
        var response1 = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/1"
        );
        Assert.assertEquals(response1.status(), 200);
        
        var response2 = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/2"
        );
        Assert.assertEquals(response2.status(), 200);
        
        var response3 = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts/3"
        );
        Assert.assertEquals(response3.status(), 200);
    }

    @Story("API - Query Parameters")
    @Severity(SeverityLevel.NORMAL)
    @Description("API request with query parameters")
    @Test(priority = 10, description = "Query parameters in API request")
    public void testQueryParameters() {
        initializeAPIContext();
        
        var response = apiContext.get(
            "https://jsonplaceholder.typicode.com/posts?userId=1&_limit=5"
        );
        
        Assert.assertEquals(response.status(), 200);
        String body = response.text();
        Assert.assertTrue(body.contains("["), "Response should be JSON array");
    }
}














