package com.example.postgresdemo;

import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.JwtValidationException;
import com.example.postgresdemo.service.JWTHelper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletRequest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.util.DateUtil.now;

/*
    Test the JWT stuff
 */
public class JWTokenTests {

    // Obviously will want this passed in via environment variable or similar
    private static String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    @Mock
    MockHttpServletRequest mockServletRequest;


    // Lots of possible test cases where we want to try out various oddities
    public static String createBespokeJWT(long iatIntvl, long expIntvl) {

        // Create via builder
        JwtBuilder bldr = Jwts.builder();

        // O means don't include EXP
        if(expIntvl != 0 ){
            bldr.setExpiration(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds(expIntvl).toEpochMilli())));
        }
        // O means don't include EXP
        if(iatIntvl != 0 ){
            bldr.setIssuedAt(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds(iatIntvl).toEpochMilli())));
        }


        /// More conventional bits can be spun through
        bldr
                .claim("iss","dwp-eas")
                .claim("sub","msilverman")
                .claim("aud","Circus")

                // Non-standard claims we need to exchange
                .claim("services", "[\"family information services\", \"housing benefit/council tax benefit\"]")
                .claim("provider", "[\"009228\", \"0099229\"]");


        // Encrypt/compat it and send it back
        return bldr.signWith(
                SignatureAlgorithm.HS256,
                TextCodec.BASE64.decode(secretKey)
        ).compact();

    }





    /*
        Get a JWT that will work
     */
    public static String generateTestJWT() {

//        // ?? Need to play with nbf (not before) ??
        long iatIntvl = -6000;  // issued 10 minutes ago
        long expIntvl =  6000;  // expires in 10 minutes
//
//
//        Date d = now();
//        // 10 minutes ahead
//        Long l=  d.toInstant().plusSeconds(-600L).toEpochMilli();
//        System.out.println(now());
//        System.out.println(Date.from(Instant.ofEpochMilli(l)));
        if(2>1) {
            return createBespokeJWT(-6000, -6000);
        }







        // Setting dates using claims of iat & exp seems too fraught to bother with
        String jws = Jwts.builder()
            // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
            .setIssuedAt(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds( iatIntvl ).toEpochMilli())))
            // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
            .setExpiration(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds( expIntvl ).toEpochMilli())))

            // Bespoke settings that can be mucked around with
//                .setIssuer("dwp-eas")
//                .setSubject("msilverman")
//                .setAudience("Circus")
            // Generic stuff to replicate bespoke (easier to play with up)
            .claim("iss","dwp-eas")
            .claim("sub","msilverman")
            .claim("aud","Circus")

            // Non-standard claims we need to exchange
            .claim("services", "[\"family information services\", \"housing benefit/council tax benefit\"]")
            .claim("provider", "[\"009228\", \"0099229\"]")

            .signWith(
                    SignatureAlgorithm.HS256,
                    TextCodec.BASE64.decode(secretKey)
            )
            .compact();

        return jws;
    }




    @Before
    public void setUp() {
        mockServletRequest = new MockHttpServletRequest();
    }





    /*
        Ought to be a number of reasons why it won't work.  Easiest way to force it is to play with the token !!
     */
    @Test (expected = Exception.class)
    public void testBreak () {

        String tkn = generateTestJWT();

        // Break the token (by appending guff to break signature)
        Claims c = JWTHelper.parseJWT( tkn+"69" );

    }

    // Test the header bit ===========================================================================================

    // In the absence of any headers, will throw exception
    @Test (expected = ApiValidationException.class)
    public void testInvalid_NoHeaders () {
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // Headers, but not an Authorization one
    @Test (expected = ApiValidationException.class)
    public void testInvalid_NoAuthHeader () {
        mockServletRequest.addHeader("sayso","sayNotSo");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // Authorization but value doesn't start "Bearer "
    @Test (expected = ApiValidationException.class)
    public void testInvalid_AuthFormat () {
        mockServletRequest.addHeader("Authorization","sayNotSo");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // Authorization and starts "Bearer " but token is missing
    @Test (expected = ApiValidationException.class)
    public void testInvalid_TokenMissing () {
        mockServletRequest.addHeader("Authorization","Bearer ");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // Authorization and starts "Bearer " but token is not a.b.c
    @Test (expected = ApiValidationException.class)
    public void testInvalid_TokenFormat () {
        mockServletRequest.addHeader("Authorization","Bearer Even.BarerYet");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // Authorization and starts "Bearer ", token in format a.b.c but token is rubbish
    @Test (expected = ApiValidationException.class)
    public void testInvalid_TokenRubbish () {
        mockServletRequest.addHeader("Authorization","Bearer Even.Barer.Yet");
        JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // If we give it a dicky token
    @Test (expected = ApiValidationException.class)
    public void testInValidated_TokenHeaderHeader () {
        String jwt = generateTestJWT();
        mockServletRequest.addHeader("Authorization","Bearer xyz" + jwt);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // If we give it a mis-signed token then expect rather different treatment
    @Test (expected = JwtValidationException.class)
    public void testInValidated_TokenSignatureHeader () {
        String jwt = generateTestJWT();
        mockServletRequest.addHeader("Authorization","Bearer " + jwt + 99);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);

    }









    // Test with a meaningful token
    @Test
    public void testValidTokenHeader () {
        String jwt = generateTestJWT();
        mockServletRequest.addHeader("Authorization","Bearer " + jwt);
        boolean v=false;
        try {
            v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            System.out.println(avEx.toString());
        }

        // If we fail this assert very likely that the (apparently valid jwt has been seen as invalid)
        assert(v);

        // check provider & service
        assert(JWTHelper.providerAllowed("0099229"));
        assert(!JWTHelper.providerAllowed("noneSuch"));

        assert(JWTHelper.serviceAllowed("family information services"));
        assert(!JWTHelper.serviceAllowed("noneSuch"));


    }




}
