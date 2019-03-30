package com.example.postgresdemo.service;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.example.postgresdemo.exception.ApiError;
import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.exception.JwtValidationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.TextCodec;
import javassist.bytecode.stackmap.BasicBlock;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;

import static java.time.LocalDateTime.now;

/*
    Maybe should move the "generate a fakeJWT" here and make it rather more configurable (to test the different cases)
 */


/*
    Aim is to toy with a JWT


    https://www.baeldung.com/java-json-web-tokens-jjwt
    https://developer.okta.com/blog/2018/10/31/jwts-with-java
 */


public class JWTHelper {

    static Logger log = LoggerFactory.getLogger(JWTHelper.class);

    private static final String BEARER = "Bearer";
    private static final String AUTHTN = "Authorization";
    private static final String SERVICE   = "services";
    private static final String PROVIDER  = "provider";

    // Specifics we want to see
    private static final String ISSUER_ID = "dwp-eas";


    private static String jwt;
    private static Claims claims;
    private static List<String> provider;
    private static List<String> services;

    private static final String EXPIRATION = "exp";
    private static final String ISSUED_AT  = "iat";
    private static final String ISSUER     = "iss";
    private static final String EXPIRATION_TXT = "expiration ("+EXPIRATION+")";
    private static final String ISSUED_AT_TXT  = "issuedAt ("+ISSUED_AT+")";
    private static final String ISSUER_TXT     = "issuer ("+ISSUER+")";


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
                .setIssuer("dwp-eas")
                .setSubject("msilverman")
                .setAudience("Circus")
                .claim("services", "[\"family information services\", \"housing benefit/council tax benefit\"]")
                .claim("provider", "[\"009228\", \"0099229\"]")
                // Fri Jun 24 2016 15:33:42 GMT-0400 (EDT)
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
//                .setIssuedAt(Date.from(Instant.ofEpochSecond(4622470422L)))
                // Sat Jun 24 2116 15:33:42 GMT-0400 (EDT)
//                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
//                .claim("exp","")
                .signWith(
                        SignatureAlgorithm.HS256,
//                        TextCodec.BASE64.decode("Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=")
                        TextCodec.BASE64.decode(secretKey)
                )
                .compact();

        return jws;
    }

    /*
        Check that the headers have something usable.  Not yet found a way to get the headers off a request
        Raises bad request (via ApiValidation) but could raise as forbidden (via JwtValidation)
     */
    private static String checkHeadersForJwt(HttpServletRequest request) {

        String authHdr = request.getHeader(AUTHTN);

        // No header isn't good
        if( authHdr == null ){
            System.out.println("No authorization header for request");
            // Really need an authentication version, that will return a forbidden message, but
            // ought to aspire to using the multi-level approach so can return more than one issue with auth
            throw new ApiValidationException("jwt","must be provided in an " + AUTHTN + " header");
        }

        // We have an authorization header, so inspect its payload
        String auth[] = authHdr.split(" ");
        if( ! auth[0].equals("Bearer") ) {
            System.out.println("Authorization doesn't start with Bearer ");
            throw new ApiValidationException("jwt","must be provided as '" + BEARER + " <jwt>'");
        }

        // Also check that there's a "payload"
        if( auth.length<2 || auth[1]==null || auth[1].isEmpty() ) {
            System.out.println("Authorization has no token");
            throw new ApiValidationException("jwt","must be provided as : '" + BEARER + " <jwt>' and must have non-empty jwt");
        }

        // Picked what should be the jwt out.  Someone else can check if it looks kosher
        return auth[1];
    }




    /*
    If it's a decent token we'll get claims from it.  Otherwise we'll throw exception(s)
     */
    private static Claims parseJwtIntoClaims(String jwt) {

        Claims jtwClaims=null;

        // If it's OK we'll get claims, otherwise it's not goo
        try {
            jtwClaims = Jwts
                    .parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(secretKey))
                    .parseClaimsJws(jwt).getBody();
        } catch (MalformedJwtException mjEx) {
            // We can choose that this is a bad request (could equally throw as forbidden)
            log.info("Unable to use jwt " , mjEx );
            throw new ApiValidationException("jwt", mjEx.getMessage());
        } catch (SignatureException seEx) {
            // Dodgy signature probably better treated as forbidden (eventually)
            log.info( "Unable to use jwt " , seEx );
            throw new JwtValidationException("jwt", seEx.getMessage());
        } catch (ExpiredJwtException ejEx) {
            // No longer valid
            log.info( "Unable to use jwt " , ejEx );
            throw new JwtValidationException("jwt", ejEx.getMessage());
        }
        return jtwClaims;
    }

    /*
        Look at generic mechanism to get a date.  Need to be sure it's provided, is a date, and the right side of now
        NOTE : parser checks expiration date BUT only if one is given ...
        futureNotPast - true -> must be future, false -> must be past
     */
    private static  boolean checkTokenDateValidity( String token, String dateName) {

        Date dt = null;
        boolean future = false;
        String errMsg = null;
        LocalDateTime ldt=null;

        switch ( dateName ){
            case EXPIRATION:
                dt = claims.getExpiration();
                future = true;
                errMsg = EXPIRATION_TXT + " must be a future date/time";
                break;
            case ISSUED_AT:
                dt = claims.getIssuedAt();
                future = false;
                errMsg = ISSUED_AT_TXT + " must be a historic date/time";
                break;
            default:
                // TODO : improve?
                throw new ApiValidationException(dateName,"Not a recognised date to request");
        }

        // No date given = instant fail.  Throw an exception
        if ( dt == null ) {
            log.info(dateName + " not found in token");
            throw new JwtValidationException(dateName,errMsg);
        } else {
            // Got a date (and which side of Now it needs to be).  Check it's as expected ...
            ldt = LocalDateTime.ofInstant(Instant.ofEpochMilli(dt.getTime()), ZoneOffset.UTC);
            if( ldt.isAfter(now()) != future ) {
                // mismatch between what we want & what we got
                throw new JwtValidationException(dateName,errMsg);
            }
        }

        // In the absence of exception we're OK
        return true;
    }

    /*
        Check that now is between issue & expiry date times.  Throw exception if not
        NOTE : default parser/checker looks for EXPIRED if given date, but is fine if no expiry is given :(
     */
    private static boolean checkTokenDatesValidity( String token) {

        LocalDateTime expires, issued;
        ArrayList<ApiError> apiErrors = new ArrayList<>();

        // Expiry is checked for free.  Do we really care about when the token was issued??
        // IssuedAt
        try {
            checkTokenDateValidity(token, ISSUED_AT );
        } catch (ApiValidationException avEx) {
            apiErrors.addAll(avEx.getApiErrors());
        }

        // Expiry
        try {
            checkTokenDateValidity(token, EXPIRATION );
        } catch (ApiValidationException avEx) {
            apiErrors.addAll(avEx.getApiErrors());
        }

        // If we gathered any issues, then raise them
        if( apiErrors.size() > 0 ) {
            throw new JwtValidationException(apiErrors);
        }

        // No exceptions = OK
        return true;
    }

    /*
        Check issuer is as expected
     */
    private static  boolean checkTokenIssuer( String token ) {

        String iss = getClaimValue(ISSUER);
        if( iss == null || iss.isEmpty() ){
            throw new JwtValidationException( ISSUER, ISSUER + " cannot be null/empty");
        } else {
            // If we don't recognise the issuer then defer
            if( ! iss.equals(ISSUER_ID)) {
                throw new JwtValidationException( ISSUER, "'" + iss + "' is not a recognised issuer");
            }
        }
        // Unexceptional, so OK
        return true;
    }



    /*
        This will be interesting.  Need to check if source, expiry, etc etc seem valid
     */
    private static boolean checkTokenValidity( String token ) {

        LocalDateTime expiry;

        // ?? Could check for exceptions and collate them in to single exception ??


        // Easy one is to check dates. But the important one (expiry) we get for free
        checkTokenDatesValidity( token );

        // Other things we can check?
        checkTokenIssuer( token );

        // TODO : aud / audience ??

        // sub / subject ??

        // non-standard : sid ??

        // non-standard : username/user-id ??

        return true;
    }

    /*
        End game for CRSRW is to get the providers and services from the token.
        Will throw exceptions ...
     */
    private static List<String> getListForClaim(String claimName) {

        String[] claimArray=null;
        ObjectMapper mapper = new ObjectMapper();

        // Might not have a claim to try & get data from
        String claimValue = getClaimValue(claimName);
        if( claimValue==null || claimValue.isEmpty() ){
            throw new JwtValidationException(claimName,"must be present in jwt");
        }

        try {
            claimArray = mapper.readValue(claimValue, String[].class);

        } catch (JsonParseException jpEx) {
            log.info( "Unable to get " + claimName, jpEx );
            throw new ApiValidationException(claimName, jpEx.getMessage());
        } catch (JsonMappingException jmEx) {
            log.info( "Unable to get " + claimName, jmEx );
            throw new ApiValidationException(claimName, jmEx.getMessage());
        } catch (IOException ioEx) {
            log.info( "Unable to get " + claimName, ioEx );
            throw new ApiValidationException(claimName,"");
        }

        // ArrayList offers contains option (in a way that array doesn't)
        ArrayList<String> claimList = new ArrayList<>();
        Collections.addAll(claimList, claimArray);

        return claimList;
    }

    /*
        Try & keep things less complex.  May well be better names than "get lists"
     */
    private static boolean getClaimLists() {

        // Allow option of building up errors
        ArrayList<ApiError> apiErrors = new ArrayList<>();

        // If we can't get the services or the providers then it's all pointless
        try {
            services = getListForClaim( SERVICE );

        } catch (ApiValidationException avEx) {
            // Strip the issues out and add to any list we're building up
            log.info("Unable to get " + SERVICE + " from token",avEx);
            apiErrors.addAll(avEx.getApiErrors());
        }

        try {
            provider = getListForClaim( PROVIDER );

        } catch (ApiValidationException avEx) {
            // Strip the issues out and add to any list we're building up
            log.info("Unable to get " + PROVIDER + " from token",avEx);
            apiErrors.addAll(avEx.getApiErrors());
        }

        // If we had issues, then batch them and throw them.  Default to Jwt(forbidden) rather than Api(bad request)
        if (apiErrors.size() > 0 ){
            throw new JwtValidationException(apiErrors);
        }

        // If we haven't thrown any exception(s) then assume OK
        return true;
    }





    /*
        Take the request and check that it has the authorisation token (obviously a fail if not present)
        If have the header then check it looks ok
        Finally can check it's from where we expect, still valid, etc etc
        ?? Gather the various issues together (and return a set) or just return first one ??
     */
    public static boolean checkRequestAuthorisation(HttpServletRequest request) throws ApiValidationException {

        System.out.println("called checkRequestAuthorisation");

        // See if there's a payload.  May throw exception (ApiValidation)
        jwt = checkHeadersForJwt(request);

        // Get what we can from the jwt.  May throw exception - ApiValidation or JwtValidation
        // TODO : should we intercept/rethrow any exceptions here?
        claims = parseJwtIntoClaims( jwt );

        // Next step is to check validity of the token
        checkTokenValidity( jwt );

        // Need to populate the service and provider collections.  May well throw an ApiValidationException (or Jwt one)
        getClaimLists();

        // Probably thrown exception by the time we get here.  If not there's no reason to assume it failed
        return true;
    }

    /*
        Want to know if stuff is on the list(s)
     */
    public static boolean providerAllowed(String providerName) {
        return provider.contains( providerName );
    }
    public static boolean serviceAllowed(String serviceName) {
        return services.contains( serviceName );
    }





    /*
        At some point need to deal with a JWT. Pull out the claims. NOTE : Will object to blindingly obvious problems ONLY
     */
    public static Claims parseJWT(String jwt) throws ApiValidationException {

        return parseJwtIntoClaims( jwt );

    }


    /*
        Just in case we change the way we handle (or name) claims
     */
    private static String getClaimValue( String key) {
        return claims.get(key,String.class);
    }



}
