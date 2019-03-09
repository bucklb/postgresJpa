package com.example.postgresdemo.Validator;

import org.junit.Test;
import uk.gov.dwp.tuo.gen.domain.BirthCase;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public class ValidatorTests {

    @Test
    public void NodAndSmile(){

    }

    static Validator validator;

    // This might be fun with the complex regex we use ??
    // Likely we will want to replicate (reuse?) the prettification we used for ApiError
    @Test
    public void testValidator(){

        BirthCase bc = new BirthCase();
        bc.setName("OK");
        bc.setDateOfBirth("2010-10-20");

        // Play with validation type stuff
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        Set<ConstraintViolation<BirthCase>> v = validator.validate(bc);

        // Could look to build this in to ApoErrors??
        for( ConstraintViolation c: v){
            String field= c.getPropertyPath().toString() + " - " + c.getInvalidValue();
            System.out.println(field);
            System.out.println(c.getMessage());
        }

        System.out.println(v.size());





    }




}
