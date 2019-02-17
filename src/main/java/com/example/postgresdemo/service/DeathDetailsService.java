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
    public String getJsonPath(String qString) throws Exception {


        // Get what we are working from
        json = readFile( jsonFile, StandardCharsets.UTF_8 );

        // Grab the info
        Configuration configuration = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();
        Object dataObject = JsonPath.using(configuration).parse(json)
                .read(qString);

        return dataObject.toString();

    }




}
