package com.example.postgresdemo;

import com.example.postgresdemo.service.JWTHelper;
import io.jsonwebtoken.Claims;
import org.junit.Test;

import java.util.concurrent.ForkJoinWorkerThread;

/*
    Test the JWT stuff
 */
public class JWTokenTests {

    private void soutByName(Claims c, String nm ){
        System.out.println("name : " + nm + "     value : " + c.get(nm,String.class));
    }


    @Test
    public void testCreate () {

        String tkn = JWTHelper.getTestJWT();
        System.out.println(tkn);

        Claims c = JWTHelper.decodeJWT( tkn );

        System.out.println("Get stuff by claim name directly");
        soutByName(c,"name");
        soutByName(c,"iss");

        soutByName(c,"noneSuch!");

        System.out.println("Get stuff by claim methods/directly");
        System.out.println(c.getIssuer());

    }


    @Test
    public void testHelper () throws Exception {

        String tkn = JWTHelper.getTestJWT();

        // See the helper in readiness
        JWTHelper.doIt( tkn );




    }







    /*
        Ought to be a number of reasons why it won't work.  Easiest way to force it is to play with the token !!
     */
    @Test (expected = Exception.class)
    public void testBreak () {

        String tkn = JWTHelper.getTestJWT();
        System.out.println(tkn);

        // Break the token
        Claims c = JWTHelper.decodeJWT( tkn+"69" );

        System.out.println("Get stuff by claim name directly");
        soutByName(c,"name");
        soutByName(c,"iss");

        System.out.println("Get stuff by claim methods/directly");
        System.out.println(c.getIssuer());

    }


}
