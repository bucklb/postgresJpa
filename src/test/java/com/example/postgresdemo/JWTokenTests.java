package com.example.postgresdemo;

import com.example.postgresdemo.exception.ApiError;
import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.JwtValidationException;
import com.example.postgresdemo.service.JWTHelper;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    // NOTE : will throw IllegalStateException if NO claims are provided (via dates OR hashMap)
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

        // Create hashMap contents as claims.  TODO : Via stream worth investigating??
        for(String id : hm.keySet()) {
            bldr.claim(id,hm.get(id));
        }

        // Encrypt/compact it and send it back. TODO : will need to revisit if signing ever changes
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
        hm.put("sub","octuplets");
        hm.put("aud","figures");
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



    // === Test broken tokens raise exceptions ====================================================================

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
        Issuer? Needs to be present and recognised
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

    /*
        Provider/Service? Needs to be present and array
     */
    @Test (expected = ApiValidationException.class)
    public void testMissingProvider () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.remove("provider");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testMissingServices () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.remove("services");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testMalformedProvider () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.put("provider", "random garbage");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }
    @Test (expected = ApiValidationException.class)
    public void testMalformedServices () {
        HashMap<String,String> hm = validClaimsHashMap();
        hm.put("services", "random garbage");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);
        boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
    }



    // === Tests below are of how exceptions get reported (already confirmed raising, but not payload) ================

    // Expect to see multiple issues reported if multiple issues
    @Test
    public void testBespokeClaimsBothWrong(){
        List<ApiError> apiErrors = null;

        // Provide no entry for provider & a dodgy value for services
        HashMap<String,String> hm = validClaimsHashMap();
        hm.remove("provider");
        hm.put("services", "random garbage");
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);

        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        // Want to see two issues reported.  One for services, one for provider
        assertNotNull (apiErrors );
        assertEquals(2,apiErrors.size());

        // Services message is odd ...
        assertEquals("services", apiErrors.get(0).getField());

        assertEquals("provider", apiErrors.get(1).getField());
        assert(apiErrors.get(1).getLocalizedErrorMessage().contains("present"));
    }

    // If both dates are missing, ought to report both
    @Test
    public void testBothDatesMissing(){
        List<ApiError> apiErrors = null;

        // expired and without issuedAt
        String tkn = createBespokeJWT( 0, 0, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);

        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        // Want to see two issues reported.  One for iat & another for exp
        assertNotNull (apiErrors );
        assertEquals(2,apiErrors.size());

        assertEquals("iat", apiErrors.get(0).getField());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("historic"));

        assertEquals("exp", apiErrors.get(1).getField());
        assert(apiErrors.get(1).getLocalizedErrorMessage().contains("future"));
    }

    // If both dates are shonky.  The expired test is "native" so doesn't sit too well with multiple issue collection
    @Test
    public void testBothDatesShonky(){
        List<ApiError> apiErrors = null;

        // expired and without issuedAt
        String tkn = createBespokeJWT( 6000, 0, validClaimsHashMap());
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);

        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        // Want to see two issues reported.  One for iat & another for exp
        assertNotNull (apiErrors );
        assertEquals(2,apiErrors.size());

        assertEquals("iat", apiErrors.get(0).getField());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("historic"));

        assertEquals("exp", apiErrors.get(1).getField());
        assert(apiErrors.get(1).getLocalizedErrorMessage().contains("future"));
    }

    // Make sure that missing/invalid issuer reported as expected
    @Test
    public void testIssuerMissingResponse(){
        List<ApiError> apiErrors = null;

        // Remove the issuer
        HashMap<String,String> hm = validClaimsHashMap();
        hm.remove("iss");

        // dates OK
        String tkn = createBespokeJWT( -6000, 6000, hm);
        mockServletRequest.addHeader("Authorization","Bearer " + tkn);

        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        assertNotNull (apiErrors );
        assertEquals(1, apiErrors.size());

        // SHould report that iss cannot be null/empty
        assertEquals("iss", apiErrors.get(0).getField());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("null/empty"));
    }

    // Check that malformed get flagged
    @Test
    public void testInvalidTokenResponse () {
        List<ApiError> apiErrors = null;

        mockServletRequest.addHeader("Authorization","Bearer Even.Barer.Yet");
        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        assertNotNull (apiErrors );
        assertEquals(1, apiErrors.size());

        // SHould report that iss cannot be null/empty
        assertEquals("jwt", apiErrors.get(0).getField());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("Unable to read"));

    }

    // Check that missing get flagged
    @Test
    public void testMissingHeaderResponse () {
        List<ApiError> apiErrors = null;

        try {
            boolean v = JWTHelper.checkRequestAuthorisation(mockServletRequest);
        } catch (ApiValidationException avEx) {
            apiErrors = avEx.getApiErrors();
        }

        assertNotNull (apiErrors );
        assertEquals(1, apiErrors.size());

        // Should report that token must be provided
        assertEquals("jwt", apiErrors.get(0).getField());
        assert(apiErrors.get(0).getLocalizedErrorMessage().contains("must be provided"));

    }

}
