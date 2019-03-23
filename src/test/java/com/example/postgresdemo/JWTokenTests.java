package com.example.postgresdemo;

import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.service.JWTHelper;
import io.jsonwebtoken.Claims;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.catalina.servlet4preview.http.PushBuilder;
import org.apache.catalina.servlet4preview.http.ServletMapping;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.runners.statements.ExpectException;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/*
    Test the JWT stuff
 */
public class JWTokenTests {

    @Mock
    MockHttpServletRequest mockServletRequest;

    @Before
    public void setUp() {
        mockServletRequest = new MockHttpServletRequest();
    }



    private void soutByName(Claims c, String nm ){
        System.out.println("name : " + nm + "     value : " + c.get(nm,String.class));
    }


    @Test
    public void testCreate () {

        String tkn = JWTHelper.generateTestJWT();
        System.out.println(tkn);

        Claims c = JWTHelper.parseJWT( tkn );

        System.out.println("Get stuff by claim name directly");
        soutByName(c,"name");
        soutByName(c,"iss");

        soutByName(c,"noneSuch!");

        System.out.println("Get stuff by claim methods/directly");
        System.out.println(c.getIssuer());

    }


    @Test
    public void testHelper () throws Exception {

        String tkn = JWTHelper.generateTestJWT();

        // See the helper in readiness
        JWTHelper.doIt( tkn );

    }

    /*
        Ought to be a number of reasons why it won't work.  Easiest way to force it is to play with the token !!
     */
    @Test (expected = Exception.class)
    public void testBreak () {

        String tkn = JWTHelper.generateTestJWT();
        System.out.println(tkn);

        // Break the token
        Claims c = JWTHelper.parseJWT( tkn+"69" );

        System.out.println("Get stuff by claim name directly");
        soutByName(c,"name");
        soutByName(c,"iss");

        System.out.println("Get stuff by claim methods/directly");
        System.out.println(c.getIssuer());

    }

    // Test the header bit ===========================================================================================

    // In the absence of any headers, will throw exception
    @Test (expected = ApiValidationException.class)
    public void testNoHeaders () {
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    @Test (expected = ApiValidationException.class)
    public void testNoAuthHeader () {
        mockServletRequest.addHeader("sayso","sayNotSo");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    @Test (expected = ApiValidationException.class)
    public void testInvalidAuthFormatHeader () {
        mockServletRequest.addHeader("Authorization","sayNotSo");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    @Test (expected = ApiValidationException.class)
    public void testInvalidTokenFormatHeader () {
        mockServletRequest.addHeader("Authorization","Bearer EvenBarer");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }



}
