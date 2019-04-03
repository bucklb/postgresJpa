package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
    JWT will have various possible issues (in same way as Api validation).  With JWT we want a "forbidden" response
    NOTE - if we ever have anything inheriting from this then will probably want it to look more like ApiValidationException
 */
public class JwtValidationException extends ApiValidationException {

    // Default is status of forbidden
    private static final HttpStatus STATUS = HttpStatus.FORBIDDEN;

    // Construct with errors (that get recorded for later use)
    public JwtValidationException(List<ApiError> apiErrors) {
        super(apiErrors, STATUS);   // is this good enough or do we need the HttpInputMessage too??
    }

    // Allow caller to be spared the pain of creating an arrayList for a single message
    public JwtValidationException(String fieldName, String fieldMessage) {
        super(fieldName,fieldMessage, STATUS);
    }

    // May never get used (as we plan to do it via AppEx)
    public JwtValidationException(HttpServletRequest httpServletRequest, JwtValidationException e) {
        super(httpServletRequest, e);
    }
}
