package User_Info;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class TanyaSystemTest {
    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void createUserSuccessful() {
        String body = """
            {
                "emailId": "testuser1@gmail.com",
                "userName": "TestUser1",
                "userPassword": "pass123"
            }
            """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/signup");

        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("testuser1@gmail.com"));
    }

    @Test
    public void loginPasswordFails() {
        Response response = RestAssured.given()
                .when()
                .get("/api/login/testuser1@gmail.com/wrongpw");

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Incorrect password"));
    }

    @Test
    public void createEventSuccessful() {
        String body = """
            {
                "eventName": "Birthday Party",
                "eventLocation": "East Hall",
                "eventDate": "2025-11-30",
                "eventTime": "17:00"
            }
            """;

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post("/api/events/user/1"); // assume user with id=1 exists

        assertEquals(201, response.getStatusCode());
        assertTrue(response.getBody().asString().contains("Birthday Party"));
    }

    @Test
    public void addAttendeeUpdatesCount() {

        // Join event
        RestAssured.given()
                .when()
                .post("/api/events/1/attend/1") // join event 1 as user 1
                .then()
                .statusCode(200);

        // Check attendee count
        Response countResponse = RestAssured.given()
                .when()
                .get("/api/events/1/attendees/count");

        assertEquals(200, countResponse.getStatusCode());
        assertTrue(countResponse.getBody().asString().contains("attendeeCount"));
    }
}
