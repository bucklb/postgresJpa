package com.example.postgresdemo;

import com.example.postgresdemo.controller.HomeController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.containsString;

// Seems important to use this specific "given"
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class HomeControllerTests {

    @Test
    public void NodAndSmile(){

    }

    // To fit in, continue the ping-pong convention. In this case a simple text string gets returned ...
    @Test
    public void basicPingTest() {

        // Create the test controller outside the "given" command such that we can inject a mocked service/repo
        HomeController hc = new HomeController();

        given().
                standaloneSetup(hc).
                when().
                get("/ping").
                then().
                statusCode(200).
                body(containsString("pong"));
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


}
