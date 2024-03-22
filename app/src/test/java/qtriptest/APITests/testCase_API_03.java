package qtriptest.APITests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class testCase_API_03 {

    @Test(description = "Verify Reservation API", groups = "API Tests", priority = 3)
    public void testcase03(){

        // Create new user and register
        String registerEndPointURL =
                "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/register";
        RequestSpecification httpRequest = RestAssured.given();
        httpRequest.contentType(ContentType.JSON);

        // Using UUID to create random-ID
        UUID uuid = UUID.randomUUID();
        String dynamicEmail = "QA_API" + uuid + "@gmail.com";
        

        // Hashmap to store objects
        HashMap<Object, Object> map = new HashMap<>();
        map.put("email", dynamicEmail);
        map.put("password", "test123");
        map.put("confirmpassword", "test123");

        httpRequest.body(map);
        Response response = httpRequest.post(registerEndPointURL);
        System.out.println(response.getBody().asPrettyString());
        response.then().assertThat().statusCode(201);
        
        String name = "QA_API"+uuid;
        
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
        
        // Extracting token from login response
        String bearerToken = response2.jsonPath().get("data.token");
        String userId = response2.jsonPath().get("data.id");

        httpRequest2.body(bearerToken).toString();
        httpRequest2.body(userId).toString();

        // Booking Reservation
        String bookingEndpointURL = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/reservations/new";
        RequestSpecification httpRequest3 = RestAssured.given();
        httpRequest3.contentType(ContentType.JSON);
        httpRequest3.headers("Authorization","Bearer "+bearerToken);

        Map<Object, Object> bookingMap = new HashMap<>();
        // Using userId obtained from registration
        bookingMap.put("userId", userId); 
        bookingMap.put("name", name);
        bookingMap.put("date", "2024-02-10");
        bookingMap.put("person","2");
        bookingMap.put("adventure", "2447910730");


        httpRequest3.body(bookingMap);
        Response response3 = httpRequest3.post(bookingEndpointURL);
        System.out.println(response3.getBody().asPrettyString());
        response3.then().assertThat().statusCode(200);

        // Verify if the reservation is available in the /api/v1/reservations call
        String reservationsEndpointURL = "https://content-qtripdynamic-qa-backend.azurewebsites.net/api/v1/reservations";
        RequestSpecification httpRequest4 = RestAssured.given();
        httpRequest4.contentType(ContentType.JSON);
        httpRequest4.headers("Authorization","Bearer "+bearerToken);

        // Pass the user ID to get reservations
        httpRequest4.queryParam("id", userId); 
        
        Response response4 = httpRequest4.get(reservationsEndpointURL);
        System.out.println(response4.getBody().asPrettyString());
        response4.then().assertThat().statusCode(200);
        
        // Check if the booking is available in the reservations
        JsonPath jsonpath = response4.jsonPath();
        boolean isBookingFound = jsonpath.getList("bookings.name").contains("name");
        Assert.assertFalse(isBookingFound, "Booking is found in reservations");
    }
}
