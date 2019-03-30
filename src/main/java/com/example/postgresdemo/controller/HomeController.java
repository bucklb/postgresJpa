package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.*;
import com.example.postgresdemo.service.DeathDetailsService;
import com.example.postgresdemo.service.JWTHelper;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uk.gov.dwp.tuo.gen.domain.BirthCase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;

@RequestMapping("")
@RestController
public class HomeController {

    Logger logger = LoggerFactory.getLogger(BirthController.class);

    @Autowired
    DeathDetailsService dds;

    // A copy of the request will allow us to check headers passed in
    @Autowired
    HttpServletRequest httpServletRequest;


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


    // To fit in, continue the ping-pong convention
    @GetMapping("/ping")
    public String getPing(HttpServletRequest request) throws ApiValidationException {

        String token="";
        System.out.println("Ping has been hit");

        // Want to run through headers we may have got. But not necessarily ALL the time
        if (10>1) { zipThruRequestHeaders(request); }


        // Devolve request checking to JwtHelper
        JWTHelper.checkRequestAuthorisation(request);








        // One we care about is the authorization (for now)
        token = request.getHeader("authorization");
        System.out.println(token);

        if( token != null ) {

            System.out.println("authorisation token = " + token);

            // Need to decide on a pattern.  Lots of things suggest preceded by Bearer ...
            String[] tokens = token.split(" ");
            token = tokens[tokens.length-1];

            // We should now have the real token in hand. Query it
            Claims c = JWTHelper.parseJWT(token);

            String nm =  c.get("name",String.class);
            System.out.println("Name from token is : " + nm);

        } else {
            System.out.println("No authorization token found");

        }




        if(0>1) {

            // TODO : could we throw a ResponseEntityException (so it gets picked up by the stuff that handles other validation)
            // throw new MethodArgumentNotValidException();
            throw new HttpMessageNotReadableException("registration detail array should not be empty");

//            throw new ApiValidationException("registration details", "must not be empty array");
        }

        return "pong" + token;
    }


    // Dummy up something that will allow generating different response (according to headers)
    @GetMapping("/details")
    public String getCase(@RequestHeader(value="jSonPath") String jSonPath){

        String ansa = dds.getJsonPathValue(jSonPath);
        System.out.println("get details called with jSonPath = " + jSonPath + " gets -> " + ansa);

        return ansa;
    }

    // Dummy up something that will allow generating different response (according to headers)
    @GetMapping("/details/test")
    public String getTest()  throws Exception
    {//HttpServletRequest request){

        String ansa ="";

        ansa= ansa + "{  \"name\" :" + dds.getJsonPathValue("$['name']");
        ansa= ansa + ",  \"benefit\" :" + dds.getJsonPathValue("$['benefits']");

        ansa = ansa + "  }";
        System.out.println(ansa);

//        httpServletRequest.getHeaders()
        System.out.println(httpServletRequest.getHeader("random"));

        if(10>1) {
            throw new ApplicationException("test", new ApiValidationException("stuff", "nonsense"));
        }


        try {
            thrower();
        } catch (Exception Ex) {
            throw (MethodArgumentNotValidException)Ex;
        }

        System.out.println("");

        return ansa;
    }

    private void thrower() throws MethodArgumentNotValidException {

        // Dummy up a request ... the dateOfBirth value will appear as a rejected value (after the colon)
        BirthCase bc=new BirthCase();
        bc.dateOfBirth("far far ahead");

        // First parameter should be the object that's being validated.  Create the exception in validator OR do validation here ??
        // We have an array to deal with, so need to be sure on an item by item basis ((or create a dummy object to do this??))
        // Not sure the name (2nd parameter) is relevant
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(bc, "j arthur rank");

        // Need to specify a REAL field name to reject.  The third parameter will be visible as localizedErrorMessage
        result.rejectValue("dateOfBirth","no future","no future date");
        // The MethodParameter seems to cope with being Null (for now at least)
        throw new MethodArgumentNotValidException(
                null, result
        );

    }





    // Dummy up something that will allow generating different response (according to headers)
    @GetMapping("/details/multi")
    public String getMulti(@RequestHeader(value="jSonPath") String jSonPath){

        String ansa = null;
        if(2>10) {
            // What the call used to do
            ansa = dds.getDetails(jSonPath);
            System.out.println("get details called with jSonPath = " + jSonPath + " gets -> " + ansa);

        } else {
            // More descriptive name
            String sExTyp = jSonPath;





        /*
            Learning point here is that I can get AppExHandler to kick in the way I want it to
            BUT ONLY if at its heart it has an Exception (as opposed to an RTE
         */

            try {
                // Should throw the exception type we want ...
                dds.thrower(sExTyp);

            } catch (Exception ex) {
                // New approach to exceptions means we just need this one catch to (re)throw
                // and append anything of use from the headers (like interteractionId)
                logger.info("some log stuff",ex);
                throw(new ApplicationException(httpServletRequest,ex));
            }
        }




        return ansa;

    }

    // Headers??
    @GetMapping("/details/header")
    public ResponseEntity<String> getHeader() throws Exception
    {

        String s="Yo!";
        HttpHeaders hdrs=null;

        hdrs = new HttpHeaders();
        hdrs.add("Key", "Value");

        ArrayList<ApiError> apiErrors = new ArrayList<>();
        apiErrors.add(new ApiError("field1","msg1"));
        apiErrors.add(new ApiError("field2","msg2"));

        // Check Chris's concerns
        ApiValidationException aEx=new ApiValidationException( new ArrayList<>() );
        aEx.getApiErrors().add(new ApiError("1","2"));
        aEx.getApiErrors().add(new ApiError("3","4"));
        aEx.getApiErrors().add(new ApiError("5","6"));
        System.out.println(" check -> " + aEx.getApiErrors().size());

        HttpMessageNotReadableException e=new HttpMessageNotReadableException("");


        // Interested in what handler
        if (2>1) {

            ApiValidationException avEx = new JwtValidationException("key", "value");
            Exception exception = new Exception("work, dammit ");
            throw new ApplicationException("jgpsodgaihgpodifodi", exception);

        }

        if(2>1) {

            try {

//                int i=1/0;
//                throw new JwtValidationException("key", "value");

            } catch (ApiValidationException ex) {
                // Want the exception to get to the AdviceStuff, so rethrow it
                System.out.println(e.getMessage());
                throw (ex);
//            } catch (Exception e) {
//
//                System.out.println("Handling it here");
//                System.out.println(e.getMessage());

            }
        }




        if(apiErrors.size()>0){
            throw new ApiValidationException(apiErrors);
        }

//            throw new ApiValidationException("stuff","and Nonsense");
        ResponseEntity<String> ansa = new ResponseEntity<String>(s, hdrs, HttpStatus.OK);


        try {
            thrower();
        } catch (Exception Ex) {
            throw (MethodArgumentNotValidException)Ex;
        }

        return ansa;
    }



}
