package com.example.postgresdemo;

import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.service.DeathDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.apache.log4j.Logger;
import uk.gov.dwp.tuo.gen.domain.BirthCase;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Set;


@Component
public class BootRunner  implements CommandLineRunner {

//    private static Logger log = LoggerFactory.getLogger(PostgresDemoApplication.class);
    private static final Logger log = Logger.getLogger(BootRunner.class);

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



        log.info("Runner is running ...");

        if( 2 > 21) {


            BirthCase bc = new BirthCase();
            bc.setName("fred");
            bc.setDateOfBirth("2010-10-20");

            // Play with validation type bits ...
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            Validator validator = factory.getValidator();

            Set<ConstraintViolation<BirthCase>> v = validator.validate(bc);

            System.out.println(v.size());









       }



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
