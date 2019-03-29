package com.example.postgresdemo.onReflection;


import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

public class OtherBeanPropertyTest {


    // https://stackoverflow.com/questions/13764280/how-do-i-exclude-fields-with-jackson-not-using-annotations
    //
//    @Test
    public void test() throws Exception {
        ObjectMapper om = new ObjectMapper();
        om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String[] excludedFields = new String[]{"surname", "forename"};


        FilterProvider fp = new SimpleFilterProvider()
                .addFilter("big dummy filter",
                        com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
                                .serializeAllExcept("surname"));
//                                .serializeAllExcept(excludedFields));

        ObjectWriter writer = om.writer(fp);

//        BigDummy.Dummy dummy = new BigDummy.Dummy();
//        System.out.println(
//                writer.writeValueAsString(dummy)
//        );
    }

//    @Test
    public void testToo() throws Exception{
        BigDummy bd=new BigDummy();
        bd.testMe();
    }




    @JsonFilter("big dummy filter")
    private class BigDummy {

        @JsonFilter("dumby filter")
        private class Dummy {
            private String forename="Test";
            public  String surname="TestSS";
            private  String child_DOB = "20-09-2018";
            private  String date_Of_Registration = "19-02-2019";
            private  String registration_District = "Cardiff";
            private  String reference_num="5c65894fc305d2b5dbe98d01";
            private  String sex_Of_Child ="Male";

        }

        public void testMe() throws Exception {
            ObjectMapper om=null;
                    om = new ObjectMapper();
            om.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            String[] includedFields = new String[]{"surname", "forename"};


            FilterProvider fp = new SimpleFilterProvider()
                    .addFilter("big dummy filter",
                            com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
                                    .serializeAllExcept("none"));
//                                .serializeAllExcept(includedFields));

            ObjectWriter writer = om.writer(fp);

            Dummy dummy = new Dummy();
            System.out.println(
                    writer.writeValueAsString(dummy)
            );
        }



    }






}
