package com.example.postgresdemo.service;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Want to have half a play with jsonPath in terms of getting subset(s) of info out from the whole.
 */

@Component
public class DeathDetailsService {

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







}
