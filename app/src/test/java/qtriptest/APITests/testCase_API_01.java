package qtriptest.APITests;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import java.util.HashMap;
import java.util.UUID;
import org.testng.annotations.Test;


public class testCase_API_01 {

    @Test(description = "Verify Registration and Login", groups = "API Tests", priority = 1)
    public void testcase01() {

        // Register API
        // RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        // RestAssured.basePath = "/api/v1/register";
        String registerEndPointURL =
                "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/register";

        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.contentType(ContentType.JSON);

        // Random random = new Random();
        // int randomNumber = random.nextInt(100);
        UUID uuid = UUID.randomUUID();
        String dynamicEmail = "QA_API" + uuid + "@gmail.com";

        // Hashmap to store objects
        HashMap<Object, Object> map = new HashMap<Object, Object>();
        map.put("email", dynamicEmail);
        map.put("password", "testtest");
        map.put("confirmpassword", "testtest");

        httpRequest.body(map);
        Response response = httpRequest.post(registerEndPointURL);
        System.out.println(response.getBody().asPrettyString());
        // Validate status code = 201
        response.then().assertThat().statusCode(201);

        // Login API
        String loginEndPointURL =
                "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/login";

        RequestSpecification httpRequest2 = RestAssured.given();
        httpRequest2.contentType(ContentType.JSON);

        map.remove("confirmpassword");

        httpRequest2.body(map);
        Response response2 = httpRequest2.post(loginEndPointURL);
        System.out.println(response2.getBody().asPrettyString());
        response2.then().assertThat().statusCode(201);

        JsonPath jsonpath = response2.jsonPath();
        boolean statusValue = jsonpath.get("success");
        Assert.assertTrue(statusValue);

        String token = jsonpath.get("data.token");
        System.out.println(token);

    }

}
