package com.example.postgresdemo.service;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.example.postgresdemo.exception.ApiError;
import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.TextCodec;
import org.mockito.Mock;



/*
    Aim is to toy with a JWT


    https://www.baeldung.com/java-json-web-tokens-jjwt
    https://developer.okta.com/blog/2018/10/31/jwts-with-java
 */


public class JWTHelper {

    private static Claims claims;
    private static ArrayList<String> props;



    // Obviously will want this passed in via environment variable or similar
    private static String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    /*
        Might behove us to have a builder pattern we can use as wrapper to this ??
        ?? Are there any mandatory fields we need to be aware of ??
     */



    /*
        Get a JWT with some hard coded bits.  Need to start somewhere
     */
    public static String generateTestJWT() {



        String jws = Jwts.builder()
                // Check that inserting as claim doesn't affect retrieval by method
//                .setIssuer("Stormpath")
                .claim("iss","Stormpath")
                .setSubject("msilverman")
                .claim("services", "[\"family information services\", \"housing benefit/council tax benefit\"]")
                .claim("provider", "[\"009228\", \"0099229\"]")
                // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                .signWith(
                        SignatureAlgorithm.HS256,
//                        TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")
                        TextCodec.BASE64.decode(secretKey)
                )
                .compact();

        return jws;
    }


    /*
        Take the request and check that it has the authorisation token (obviously a fail if not present)
        If have the header then check it looks ok
        Finally can check it's from where we expect, still valid, etc etc

        Request passed in direct from controller
     */
    public static boolean checkRequestAuthorisation(HttpServletRequest request){

        System.out.println("called checkRequestAuthorisation");

        ArrayList<ApiError> apiErrors =new ArrayList<>();

        String authHdr = request.getHeader("Authorization");
        if( authHdr == null ){
            System.out.println("No authorization header for request");
            // Really need an authentication version, that will return a forbidden message, but
            // ought to aspire to using the multi-level approach so can return more than one issue with auth
            apiErrors.add(new ApiError("jwt","must be provided"));
            throw new ApiValidationException(apiErrors);
        }

        // We have an autorization header, so inspect its payload
        String auth[] = authHdr.split(" ");
        if( ! auth[0].equals("Bearer") ) {
            apiErrors.add(new ApiError("jwt","must be in format 'Bearer <jwt>'"));
            throw new ApiValidationException(apiErrors);
        }




        // Probably thrown exception by the time we get here
        return true;
    }



    /*
        At some point need to deal with a JWT
     */
    public static Claims parseJWT(String jwt) {

        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims exClaims = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                .parseClaimsJws(jwt).getBody();
        return exClaims;
    }


    private static String getClaimValue( String key) {
        return claims.get(key,String.class);
    }


    public static void doIt ( String jwt) throws Exception{

        // get the contents in readiness for inspection
        claims = parseJWT( jwt );

        String sP=getClaimValue("provider");

        ObjectMapper mapper = new ObjectMapper();
        String[] a = mapper.readValue( sP, String[].class);
        ArrayList<String> l = new ArrayList<>();
        Collections.addAll(l,a);

        String[] x = claims.get("provider", String[].class);
        System.out.println(x.length);

    }


}
