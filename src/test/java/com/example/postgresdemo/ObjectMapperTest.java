package com.example.postgresdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class ObjectMapperTest {

    @Test
    public void NodAndSmile(){

    }

    // Part of the JWT will be assisted if we can pull from a string containing an array to a corresponding array
    // Thereafter might want to put it in to something where we can do a "contains" check
    @Test
    public void ArrayListFromString() throws Exception{

        String test = "[\"name,and,other,stuff\",\"address\",\"address\",\"address\",\"address\",\"address\"]";
        System.out.println(test);

        ObjectMapper mapper = new ObjectMapper();

        // Get it as a string array
        String[] a = mapper.readValue( test, String[].class);
        System.out.println(a[0]);

        // Feed it to a collection
        ArrayList<String> l = new ArrayList<>();
        Collections.addAll(l,a);
        System.out.println(l.get(0));

        // Verify we can do a contains
        System.out.println(l.contains("address"));

    }



}
