package com.example.postgresdemo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequestMapping("")
@RestController
public class HomeController {

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



}
