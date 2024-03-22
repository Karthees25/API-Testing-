package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.UUID;

public class testCase_API_04 {

    @Test(description = "Verify registration for duplicate user == Negative Test",
            groups = "API Tests", priority = 4)
    public void testcase04() {

        String registerEndPointURL =
                "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/register";
        RequestSpecification httpRequest =
                RestAssured.given().header("Content-Type", "application/json");

        UUID uuid = UUID.randomUUID();
        String userName = "QA_API" + uuid + "@gmail.com";

        HashMap<String, String> map = new HashMap<>();
        map.put("email", userName);
        map.put("password", "testtest");
        map.put("confirmpassword", "testtest");

        httpRequest.body(map);
        Response response = httpRequest.post(registerEndPointURL);
        response.then().assertThat().statusCode(201);

        response = httpRequest.post(registerEndPointURL);
        response.then().assertThat().statusCode(400);
        String responseBody = response.getBody().asPrettyString();
        System.out.println(responseBody);

        JsonPath jsonpath = response.jsonPath();
        boolean status = jsonpath.get("success");
        Assert.assertFalse(status);

        String errorMessage = jsonpath.get("message");
        Assert.assertEquals(errorMessage, "Email already exists");
    }

}


