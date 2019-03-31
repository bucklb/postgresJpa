package com.example.postgresdemo;

import com.example.postgresdemo.controller.HomeController;
import com.example.postgresdemo.service.JwtHelper;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import static org.hamcrest.CoreMatchers.containsString;

// Seems important to use this specific "given"
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class HomeControllerTests {

    @Mock
    JwtHelper mockHelper;

    @Test
    public void NodAndSmile(){

    }

    // To fit in, continue the ping-pong convention. In this case a simple text string gets returned ...
    @Test
    public void basicPingTest() {

        // Create the test controller outside the "given" command such that we can inject a mocked service/repo
        HomeController hc = new HomeController();

        // Removed authentication (to jwtController) so expect a 200 now
        given().
                standaloneSetup(hc).
                when().
                get("/ping").
                then().
                statusCode(200).
                body(containsString(""));
    }

    // Home should redirect us to a swagger-ui.html location.  Check redirected and location at least includes swagger
    @Test
    public void homeRedirects() {

        given().
                standaloneSetup(new HomeController()).
                when().
                get("/").
                then().
                statusCode(302).
                header("Location",containsString("swagger-ui.html"));
    }

    // Want to start looking at behaviour with jwt token (or lack thereof)
    @Test
    public void jwtPingTest() {

        // Create the test controller outside the "given" command such that we can inject a mocked service/repo
        HomeController hc = new HomeController();

        // The headers are the key bit
        // Pass headers in to see what happens with authorisation
        HttpHeaders inHeaders = new HttpHeaders();

        inHeaders.setContentType(MediaType.APPLICATION_JSON);
        String tkn = JwtHelper.generateTestJWT();
        inHeaders.add("Authorization", "Bearer " + tkn);


        // The default handler should kick in and turn any ApiValidationException to a 400.
            given().
                    standaloneSetup(hc)
                    .headers(inHeaders)
                    .when()
                    .get("/ping")
                    .then()
                    .statusCode(200)
                    .body(containsString(""));


    }





}
