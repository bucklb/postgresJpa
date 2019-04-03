package com.example.postgresdemo;


import com.example.postgresdemo.exception.ApiValidationException;
import com.example.postgresdemo.exception.ApplicationException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

public class MockRequest {

    @Test
    public void InteractionIdViaHeaderTest(){

        String test_id = "big long string";

        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addHeader("interactionId",test_id);

        ApiValidationException avEx = new ApiValidationException("key","value");
        System.out.println(avEx.getMessage());
//        ApplicationException aEx = new ApplicationException(mockHttpServletRequest, avEx);
        ApplicationException aEx = new ApplicationException(avEx, mockHttpServletRequest);
        System.out.println(aEx.getMessage());

        aEx = new ApplicationException(aEx, mockHttpServletRequest);
        aEx = new ApplicationException(aEx, mockHttpServletRequest);
        aEx = new ApplicationException(aEx, mockHttpServletRequest);
        aEx = new ApplicationException(aEx, mockHttpServletRequest);

        String s = aEx.getInteractionId();

        Assert.assertEquals(test_id, aEx.getInteractionId());
        Assert.assertEquals(HttpStatus.BAD_REQUEST, aEx.getStatus());

        System.out.println(aEx.getMessage());


    }

}
