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
import java.util.*;

import static org.assertj.core.util.DateUtil.now;

/*
    Test the JWT stuff
 */
public class JWTokenTests {

    // TODO : is it enough to check that exceptions are thrown (rather tha what the exceptions say ??)
    // TODO : will need to amend controller tests to check that a JwtValidationException gets FORBIDDEN (not bad request)


    // Obviously will want this passed in via environment variable or similar
    private static String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    @Mock
    MockHttpServletRequest mockServletRequest;


    // Lots of possible test cases where we want to try out various oddities.  Allow main claims to ccome in
    public static String createBespokeJWT(long iatIntvl, long expIntvl, HashMap<String,String> hm ) {

        // Create via builder, so get one
        JwtBuilder bldr = Jwts.builder();

        // O means don't include EXP
        if(expIntvl != 0 ){
            bldr.setExpiration(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds(expIntvl).toEpochMilli())));
        }
        // O means don't include IAT
        if(iatIntvl != 0 ){
            bldr.setIssuedAt(Date.from(Instant.ofEpochMilli(
                    now().toInstant().plusSeconds(iatIntvl).toEpochMilli())));
        }

        // TODO : Via stream worth investigating
        for(String id : hm.keySet()) {
            bldr.claim(id,hm.get(id));
        }

        // Encrypt/compact it and send it back. TODO : will need to revisit !!
        return bldr.signWith(
                SignatureAlgorithm.HS256,
                TextCodec.BASE64.decode(secretKey)
        ).compact();

    }

    /*
        Rather than litter the tests with this block in various flavours, centralise it & use remove to drop elements
     */
    public static HashMap<String,String> validClaimsHashMap() {
        HashMap<String,String> hm = new HashMap<>();
        hm.put("iss","dwp-eas");
        hm.put("sub","msilverman");
        hm.put("aud","Circus");
        hm.put("services","[\"family information services\", \"housing benefit/council tax benefit\"]");
        hm.put("provider","[\"009228\", \"0099229\"]");
        return hm;
    }


    /*
        Get a JWT that will work (by feeding "good" values to generic generator)
     */
    public static String generateValidJwt() {

        // Put issueAt & Expiry at -10 minutes & +10 minutes.  Grab the bits we need to do a valid token
        return createBespokeJWT(-6000, 6000, validClaimsHashMap());
    }


    @Before
    public void setUp() {
        // Main point of entry is the request and its headers
        mockServletRequest = new MockHttpServletRequest();
    }



    // === Test the header (exists, isn't malformed ====================================================================

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

    // === Test broken tokens ====================================================================

    // If we give it a dicky token (mess up the header block) then should get an exception
    @Test (expected = ApiValidationException.class)
    public void testInValidated_TokenHeaderHeader () {
        String jwt = generateValidJwt();
        mockServletRequest.addHeader("Authorization","Bearer xyz" + jwt);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    // If we give it a mis-signed token then expect an exception (with different message).  Just tack stuff on then of a good token
    @Test (expected = JwtValidationException.class)
    public void testInValidated_TokenSignatureHeader () {
        String jwt = generateValidJwt();
        mockServletRequest.addHeader("Authorization","Bearer " + jwt + 99);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }






    // === Tests below are of how token is treated (we've checked what dodgy headers cause above) =====================

    // Valid token. Should pass without exception & allow us to check service/provider
    @Test
    public void testValidTokenHeader () {
        String jwt = generateValidJwt();
        mockServletRequest.addHeader("Authorization","Bearer " + jwt);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);

        // If we fail this assert very likely that the (apparently valid jwt has been seen as invalid)
        assert(v);

        // check provider & service. TODO : put these as constants ??
        assert(JWTHelper.providerAllowed("0099229"));
        assert(!JWTHelper.providerAllowed("noneSuch"));

        assert(JWTHelper.serviceAllowed("family information services"));
        assert(!JWTHelper.serviceAllowed("noneSuch"));
    }


    /*
        Missing dates are bad
     */
    @Test (expected = ApiValidationException.class)
    public void testMissingExpiryValue () {
        String tkn = createBespokeJWT( -6000, 0, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testMissingIssuedAtValue () {
        String tkn = createBespokeJWT( 0, 6000, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    /*
        Dates that are the wrong side of "now" are bad
     */
    @Test (expected = ApiValidationException.class)
    public void testHistoricExpiryValue () {
        // Will depend if we offer any leeway/skew on the expiry
        String tkn = createBespokeJWT( -6000, -100, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testFutureIssuedAtValue () {
        String tkn = createBespokeJWT( +100, 6000, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }

    /*
        Issuer?
     */
    @Test (expected = ApiValidationException.class)
    public void testMissingIssuer () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.remove("iss");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testUnknownIssuer () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.put("iss","noneSuch. No, really. No idea who you're talking about!!!!!");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }



//    @Test (expected = Exception.class)
//    public void testExpiredDate () {
//    }
//
//    @Test (expected = Exception.class)
//    public void testMalformedClaims () {
//        HashMap<String,String> hm = validClaimsHashMap();
//
//        // Override our key payload with garbage
//        hm.put("services","garbage");
//        hm.put("provider", "garbage");
//    }


}
