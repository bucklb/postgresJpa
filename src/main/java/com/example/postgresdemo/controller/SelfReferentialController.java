package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.service.JWTHelper;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/*
    Idea is to get a controller that calls itself ...


    https://stackoverflow.com/questions/19238715/how-to-set-an-accept-header-on-spring-resttemplate-request
    https://www.baeldung.com/rest-template

    Cannot use getFor... with headers.
    https://stackoverflow.com/questions/16781680/http-get-with-headers-using-resttemplate
 */


@RequestMapping("self")
@RestController
public class SelfReferentialController {

    // To fit in, continue the ping-pong convention
    @GetMapping("/ping")
    public String getPing() throws ApiValidationException {
        return "self pong";
    }

    // To fit in, continue the ping-pong convention
    @GetMapping("/pong")
    public String doPong() throws ApiValidationException {

        boolean basic=false;

        ResponseEntity<String> resp = null;
        RestTemplate rt = new RestTemplate();
        String url="http://localhost:8889/ping";
        String msg = "";

        if ( basic ) {

            // No headers
            resp = rt.getForEntity(url, String.class);

            // Look at handling returned headers
            HttpHeaders outHeaders = rt.headForHeaders(url);

            msg = "Basic : " + url + " : " + resp.getBody();

        } else {

            // Pass headers in to see what happens with authorisation
            HttpHeaders inHeaders = new HttpHeaders();

            inHeaders.setContentType(MediaType.APPLICATION_JSON);
            String tkn = JWTHelper.getTestJWT();

//            tkn="SomethingBenign";

            inHeaders.add("Authorization", "Bearer " + tkn);

            HttpEntity<String> entity = new HttpEntity<String>("parameters", inHeaders);


            System.out.println("Check contains");
            System.out.println(entity.getHeaders().containsKey("authorization"));


            resp = rt.getForEntity(url, String.class);
            resp = rt.exchange(url, HttpMethod.GET, entity, String.class);

            msg = "Headered : " + url + " : " + resp.getBody();

            // Look at handling returned headers. What to do with them ???
            HttpHeaders outHeaders = rt.headForHeaders(url);






        }

        return msg;
    }


}
