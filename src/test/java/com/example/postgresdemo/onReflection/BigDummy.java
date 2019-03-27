package com.example.postgresdemo.onReflection;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;

@JsonFilter("big dummy filter")
public class BigDummy {


    @JsonFilter("dummy filter")
    private class Dummy {
        public String forename="Test";
        private  String surname="TestSS";
        private  String child_DOB = "20-09-2018";
        public  String date_Of_Registration = "19-02-2019";
        private  String registration_District = "Cardiff";
        public  String reference_num="5c65894fc305d2b5dbe98d01";
        private  String sex_Of_Child ="Male";

        public String getSurname() {
            return surname;
        }

        public String getRegistration_District() {
            return registration_District;
        }
    }

    /*
        NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !! NOTE !!

        The writer will only ever write out the public fields!!

        Don't assume it's just the "filter" that matters

     */


    public void testMeToo(ObjectMapper om) throws Exception {
        Dummy dummy = new Dummy();
        System.out.println(om.writeValueAsString(dummy));


    }



    public void testMe() throws Exception {
        ObjectMapper om=null;
        om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);


//        om.getSerializationConfig().mixInCount()

        System.out.println("Here !!!");

        String[] includedFields = new String[]{"surname", "forename"};


        FilterProvider fp = new SimpleFilterProvider()
                .addFilter("dummy filter",
                        SimpleBeanPropertyFilter
//                                .serializeAll());
                                .serializeAllExcept("reference_num"));
//                                .serializeAllExcept(includedFields));

        ObjectWriter newWriter;
        newWriter = om.writer(fp);

        Dummy dummy = new Dummy();
        System.out.println(
                newWriter.writeValueAsString(dummy)
        );



    }

}
