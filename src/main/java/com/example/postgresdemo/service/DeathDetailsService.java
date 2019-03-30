package com.example.postgresdemo.service;

import com.example.postgresdemo.exception.*;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Want to have half a play with jsonPath in terms of getting subset(s) of info out from the whole.
 */

@Component
public class DeathDetailsService {

    Logger logger = LoggerFactory.getLogger(DeathDetailsService.class);

    private static String json;
    private static File jsonFile = new File("src/main/resources/death.json");
//    Configuration configuration = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();

    private static String readFile(File file, Charset charset) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }

    /**
     *
     * @param qString - the string to be used in locating the data
     * @return
     * @throws Exception
     */
    public String getJsonPathValue(String qString, boolean doTrim) {
        String ansa="notfound";

        // Get what we are working from
        try {
            json = readFile(jsonFile, StandardCharsets.UTF_8);

            // Grab the info
            Configuration configuration = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();
            Object dataObject = JsonPath.using(configuration).parse(json)
                    .read(qString);

            ansa = dataObject.toString();

            // For now, strip off the start [ & end ], but not sure it's safe realistically
            if( doTrim ) {
                ansa = ansa.substring(1, ansa.length() - 1);
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return ansa;
    }

    /**
     * Default will be to trim it of outer [+]
     * @param qString
     * @return
     */
    public String getJsonPathValue(String qString) {
        return getJsonPathValue(qString, true);
    }


    /**
     * Given a request "template" do what we can to
     * @param t
     * @return
     */
    public String getDetails(String t){

        // what did we get asked for
        System.out.println(t);

        String[] S = t.split("!!");

        String T="";
        for(String x:S){
//            System.out.println(x);
            if( x.startsWith("$")){
                x = getJsonPathValue(x);
            }
            T=T+x;
        }

        // reflect what we got
        System.out.println(T);

        return T;
    }


    private ArrayList<ApiError> getApiErrorList(String src){
        ArrayList<ApiError> apiErrors = new ArrayList<>();
        apiErrors.add(new ApiError("one",src));
        apiErrors.add(new ApiError("two","too"));
        return apiErrors;
    }


    /*
        Want a vaguely flexible way to test the exception handling
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



        // Down a rabbit hole to see what stack trace looks like ...
        try {
            thrower_i();
        } catch (Exception e) {
            logger.info("caught in service", e);
        }



        try {

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
                    System.out.println(1 / 0);
                    ;

            }
        } catch (Exception exception    ) {
            // Throw in appEx wrapper
            throw (new ApplicationException( exception));
        }

    }

    private void thrower_i() {
        thrower_ii();
    }

    private void thrower_ii() {
        thrower_iii();
    }
    private void thrower_iii() {
        thrower_iv();
    }
    private void thrower_iv() {
        thrower_v();
    }
    private void thrower_v() {
        // generate an exception somehow

        if ( 2>1 ) {
            throw new JwtValidationException("thrown", "in thrower v");
        }

        System.out.println(2/0);
    }






}
