package com.example.postgresdemo.onReflection;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

@JsonFilter("exclude fields")
public class SimpleBeanPropertyFilter {

    // https://stackoverflow.com/questions/13764280/how-do-i-exclude-fields-with-jackson-not-using-annotations
    //
    @Test
    public void test() throws Exception {
        ObjectMapper om=new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String[] excludedFields = new String[]{"surname","forename"};

        FilterProvider fp =null;
        fp=new SimpleFilterProvider();
        fp=null;
        fp = new SimpleFilterProvider()
                .addFilter("dummy filter",
                        com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
                                .filterOutAllExcept("forename"));
//                                .serializeAllExcept(excludedFields));

        ObjectWriter writer = om.writer(fp);

        Dummy dummy = new Dummy();
        System.out.println(
                writer.writeValueAsString(dummy)
        );


    }














    // =========================================== CLASS TO MELD ================================================
    // Want something to play with ...

    // NOTE that the filter name at the top of the class matches that in the addFilter command above

    @JsonFilter("dummy filter")
    private class Dummy {
        private String forename="Test";
        public  String surname="TestSS";
        private  String child_DOB = "20-09-2018";
        private  String date_Of_Registration = "19-02-2019";
        private  String registration_District = "Cardiff";
        private  String reference_num="5c65894fc305d2b5dbe98d01";
        private  String sex_Of_Child ="Male";

    }

}
