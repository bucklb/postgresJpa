package com.example.postgresdemo.Validator;

import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.exception.BirthCaseApiException;
import org.bouncycastle.cms.jcajce.JceKTSKeyTransAuthenticatedRecipient;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;

/*
    Looking to use the application stuff in more serious way ...
 */
public class AppExceptionTest {

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




}
