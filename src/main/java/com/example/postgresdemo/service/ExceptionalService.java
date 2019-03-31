package com.example.postgresdemo.service;

import com.example.postgresdemo.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/*
    Keep the stuff relating to exception together (bounded context ;p and all)
 */
@Service
public class ExceptionalService {

    Logger logger = LoggerFactory.getLogger(ExceptionalService.class);

    /*
        Generate a very simple list of dummy errors
     */
    private ArrayList<ApiError> getApiErrorList(String src){
        ArrayList<ApiError> apiErrors = new ArrayList<>();
        apiErrors.add(new ApiError("one",src));
        apiErrors.add(new ApiError("two","too"));
        return apiErrors;
    }

    /*
        Call thrower to throw the requested exception (in a try block that will kick it on as ApplicationException)
     */
    public void rethrower(String sExTyp){

        // Now decide which of the many options to throw !!!
        try {
            // throw the requested exception
            thrower(sExTyp);
        } catch (Exception exception    ) {
            // RE-Throw in appEx wrapper
            throw (new ApplicationException( exception));
        }

    }


    /*
        Simply throw the requested type of exception
     */
    public void thrower(String sExTyp){

        // Have various exceptions we can throw

        // A number of exceptions we might want to throw
        CCException ccEx = new CCException(getApiErrorList("ccEx"));
        BBException bbEx = new BBException(getApiErrorList("bbEx"));
        ApiValidationException avEx = new ApiValidationException(getApiErrorList("avEx"));
        JwtValidationException jwEx = new JwtValidationException(getApiErrorList("jwEx"));
        RuntimeException rtEx=new RuntimeException("a run time exception");
        Exception ex = new Exception("a straightforward exception");

        JwtValidationException jwExToo=new JwtValidationException("some field","some reason");

        // Create an ApplicationException from each exception,
        ApplicationException appbbEx = new ApplicationException("contains bbEx", bbEx);
        ApplicationException appccEx = new ApplicationException("contains ccEx", ccEx);
        ApplicationException appavEx = new ApplicationException("contains avEx",avEx);
        ApplicationException appjwEx = new ApplicationException("contains jwEx",jwEx);
        ApplicationException apprtEx = new ApplicationException("contains rtEx",rtEx);
        ApplicationException appEx = new ApplicationException("contains ex",ex);

        ApplicationException appjwExToo = new ApplicationException("contains jwExToo",jwExToo);

        // Now decide which of the many options to throw !!!

        // No need for break
        switch (sExTyp) {
            case "ccEx":
                throw ccEx;
            case "bbEx":
                throw bbEx;
            case "avEx":
                throw avEx;
            case "jwEx":
                throw jwEx;
            case "jwExToo":
                throw jwExToo;
            case "rtEx":
                throw rtEx;
            case "appccEx":
                throw appccEx;
            case "appbbEx":
                throw appbbEx;
            case "appavEx":
                throw appavEx;
            case "appjwEx":
                throw appjwEx;
            case "appjwExToo":
                throw appjwExToo;
            case "apprtEx":
                throw apprtEx;
            case "appEx":
                throw appEx;

            default:
                // Force a divide by zero!!
                System.out.println(1 / 0);
        }
    }

    /*
        Start down a rabbit hole so can see how stack trace looks with different log options
     */
    public void throwLine(String sExTyp) {
        thrower_i(sExTyp);
    }
    private void thrower_i(String sExTyp) {
        thrower_ii(sExTyp);
    }
    private void thrower_ii(String sExTyp) {
        thrower_iii(sExTyp);
    }
    private void thrower_iii(String sExTyp) {
        thrower_iv(sExTyp);
    }
    private void thrower_iv(String sExTyp) {
        thrower_v(sExTyp);
    }
    private void thrower_v(String sExTyp) {
        // generate an exception somehow
        thrower(sExTyp);
    }




}
