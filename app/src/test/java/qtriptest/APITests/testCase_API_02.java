package qtriptest.APITests;

import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.*;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.RestAssured;
import java.io.File;


public class testCase_API_02 {

    @Test(description = "Verify the search city API", groups = "API Tests", priority = 2)
    public void testCase_02() {
        RestAssured.baseURI = "https://content-qtripdynamic-qa-backend.azurewebsites.net";
        // RestAssured.basePath = "/api/v1/cities";
        RequestSpecification searchRequest = RestAssured.given();
        Response searchResponse = searchRequest.queryParam("q", "beng").get("/api/v1/cities");
        // Validate status code = 200
        searchResponse.then().assertThat().statusCode(200);

        // Validate the result is an array of size 1
        int size =
                searchResponse.then().assertThat().extract().body().jsonPath().getList("$").size();
        System.out.println(size);
        Assert.assertEquals(size, 1);

        // Validate that the description contains "100+ Places"
        String response = searchResponse.getBody().asPrettyString();
        System.out.println(response);
        JsonPath jsonPath = new JsonPath(response);
        String desc = jsonPath.getString("description");
        System.out.println(desc);
        Assert.assertEquals(desc, "[100+ Places]");

        // Validate JSON Schema
        searchResponse.then().assertThat().body(JsonSchemaValidator
                .matchesJsonSchema(new File(".//src//test//resources//schema.json")));
    }
}
