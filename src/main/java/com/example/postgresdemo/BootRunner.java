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

    @Override
    public void run(String... strings) throws Exception {

        // Very basic way to exercise things
        System.out.println("Runner is running ...");
        System.out.println(dds.getJsonPath("$['benefits'][1]"));
        System.out.println(dds.getJsonPath("$['name']"));

//
//        Configuration configuration = Configuration.builder().options(Option.ALWAYS_RETURN_LIST).build();
//        // Get summat to display
//        json = readFile( jsonFile, StandardCharsets.UTF_8 );
//        System.out.println(json);
//
//        Object dataObject = JsonPath.parse(json)
//                .read("$[?(@.id == 2)]['director']");
//
//        System.out.println(dataObject.toString());
//
//        dataObject = JsonPath.parse(json)
//                .read("");
//
//        System.out.println(dataObject.toString());

    }

}
