package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.service.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/*
    Stop re-using controllers to experiment with new functionality.  Create extra ones instead
 */
@RequestMapping("jwt/")
@RestController
public class JwtController {

    @Autowired
    JwtHelper jwtHelper;

    @Autowired
    HttpServletRequest httpServletRequest;

    /*
        Spin through headers (for debug)
     */
    private void zipThruRequestHeaders(HttpServletRequest request){
        System.out.println("Headers ______________________");
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);

            System.out.println("Header : " + key + "   value : " + value);

//            map.put(key, value);
        }
        System.out.println("Headers -=-=-=-=-=-=-=-=-=-=-=-");
    }


    // Check that things blow up here if jwt is malformed/missing
    @GetMapping("")
    public void doJwt() {

        // Want to run through headers we may have got. But not necessarily ALL the time
        if (10>1) { zipThruRequestHeaders(httpServletRequest); }

        try {
            // Devolve request checking to JwtHelper
            jwtHelper.checkRequestAuthorisation(httpServletRequest);
        } catch (Exception e) {
            // rethrow to pick up any interactionId
            throw new ApplicationException(httpServletRequest, e);
        }

    }


}
