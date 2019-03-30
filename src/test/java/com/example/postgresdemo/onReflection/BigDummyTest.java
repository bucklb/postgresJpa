package com.example.postgresdemo.onReflection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class BigDummyTest {

    @Test
    public void test() throws Exception {
        BigDummy bd=new BigDummy();
        bd.testMe();
    }

//    @Test
//    public void testToo() throws Exception {
//        BigDummy bd=new BigDummy();
//        bd.testMeToo(new ObjectMapper());
//    }

}
