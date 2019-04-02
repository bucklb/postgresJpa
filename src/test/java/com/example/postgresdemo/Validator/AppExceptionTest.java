package com.example.postgresdemo.Validator;

import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.exception.BirthCaseApiException;
import org.bouncycastle.cms.jcajce.JceKTSKeyTransAuthenticatedRecipient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;



/*
    Looking to use the application stuff in more serious way ...
 */
public class AppExceptionTest {

    Logger logger = LoggerFactory.getLogger("playTime");


    @Test
    public void BirthCaseTest() {

        BirthCaseApiException bcEx=new BirthCaseApiException(HttpStatus.ALREADY_REPORTED,"already reported");
        Assert.assertEquals(HttpStatus.ALREADY_REPORTED, bcEx.getStatus());
        Assert.assertEquals("already reported", bcEx.getDescription());


        ApplicationException aEx = new ApplicationException("id",bcEx);
        Assert.assertEquals(HttpStatus.ALREADY_REPORTED, aEx.getStatus());
        Assert.assertEquals("already reported", aEx.getMessage());
        Assert.assertEquals("already reported", aEx.getDescription());


    }


    @Test
    public void play() {
        try{
            throw new Exception("first");
        } catch (Exception ex1) {

            try{
                throw new Exception("second",ex1);

            } catch (Exception ex2) {
                try{
                    throw new Exception("third",ex2);

                } catch (Exception ex3) {
                    try{
                        throw new Exception("fourth",ex3);

                    } catch (Exception ex4) {

                        System.out.println(ex4.getMessage());
                        System.out.println(ex4.getStackTrace());
//                        System.out.println(ex4.getMessage());

                        logger.warn("playing", ex4);

                    }

                }

            }

        }
    }







}
