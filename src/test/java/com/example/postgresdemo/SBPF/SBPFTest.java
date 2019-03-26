package com.example.postgresdemo.SBPF;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;

/*
    Toy with the filtering when we know we can stick annotation at class level.

    Appears to work as wanted, but assumes we can add the annotation to class.  Otherwise may need a MixIt ?
 */
public class SBPFTest {

    @JsonFilter("filterAClass")
    class AClass
    {
        public String id = "42";
        public String name = "Fred";
        public String[] color = {"black", "blue"};
        public int sal = 56;
        public BClass[] bclass = {new BClass(), new BClass(), new BClass()};
    }

    @JsonFilter("filterBClass")
    class BClass
    {

        public String id = "99";
        public String size = "90";
        public String height = "tall";
        public String nulCheck =null;
    }

    // Presumably the lot
    @Test
    public void SbpfTest_raw() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String[] ignorableFieldNames = {  };
        String[] ignorableFieldNames1 = {  };
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("filterAClass",SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames))
                .addFilter("filterBClass", SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames1));
        ObjectWriter writer = mapper.writer(filters);
        System.out.println(writer.writeValueAsString(new AClass()));
    }

    // Exclude all contents of class B
    @Test
    public void SbpfTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String[] ignorableFieldNames = { "id","stuff", "color", "height"};
        String[] ignorableFieldNames1 = { "size", "height","nulCheck","id"  };
        FilterProvider filters = new SimpleFilterProvider()
                .addFilter("filterAClass",SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames))
                .addFilter("filterBClass", SimpleBeanPropertyFilter.serializeAllExcept(ignorableFieldNames1));
        ObjectWriter writer = mapper.writer(filters);
        System.out.println(writer.writeValueAsString(new AClass()));
    }






}
