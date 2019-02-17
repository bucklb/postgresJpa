package com.example.postgresdemo.controller;

import com.example.postgresdemo.service.DeathDetailsService;
import io.swagger.annotations.ApiParam;
import org.mapstruct.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RequestMapping("")
@RestController
public class HomeController {

    @Autowired
    DeathDetailsService dds;

    // Need to push people to where swagger lives ...
    String REDIRECT_RTE="http://localhost:";
    String REDIRECT_PTH="/swagger-ui.html";
    int    REDIRECT_CDE=302;

    @Value("${server.port}")
    int APP_PORT;

    // Redirect the user to swagger.  Sure to be a better way than assuming localhost
    @GetMapping("")
    public void getHome(HttpServletResponse httpServletResponse) {
        String redirect=REDIRECT_RTE + APP_PORT + REDIRECT_PTH;
        httpServletResponse.setHeader("Location", redirect);
        httpServletResponse.setStatus(REDIRECT_CDE);
    }

    // To fit in, continue the ping-pong convention
    @GetMapping("/ping")
    public String getPing() {
        return "pong";
    }


    // Dummy up something that will allow generating different response (according to headers)
    @GetMapping("/details")
    public String getCase(@RequestHeader(value="jSonPath") String jSonPath){

        String ansa="not found";

        try{
            ansa = dds.getJsonPath(jSonPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("get details called with jSonPath = " + jSonPath + " gets -> " + ansa);

        return ansa;
    }





}
