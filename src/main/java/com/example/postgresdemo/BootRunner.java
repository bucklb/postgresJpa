package com.example.postgresdemo;

import com.example.postgresdemo.service.DeathDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

@Component
public class BootRunner  implements CommandLineRunner {

    private static String json;
    private static File jsonFile = new File("src/main/resources/films.json");

    private static String readFile(File file, Charset charset) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), charset);
    }

    @Autowired
    DeathDetailsService dds;

    private String q(String s){
        return "\""+s+"\"";
    }

    private String r(String s){
        return "!!"+s+"!!";
    }

    @Override
    public void run(String... strings) throws Exception {

        // Very basic way to exercise things
        System.out.println("Runner is running ...");

        String c=":";
        String t="";

        // build up some form of template ...
        t=t + "{ " + q( "name of deceased")+ c + r("$.['name']");
        t=t + ", " + q( "date of death")+ c + r("$.['dates']['death']");
        t=t + " }";

        // Pass constructed query
        System.out.println(dds.getDetails(t));

//        t=q("names")+ c + dds.getJsonPathValue("$['dates']['death']");
//        System.out.println(t);










    }

}
