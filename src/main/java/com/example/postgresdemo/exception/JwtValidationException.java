package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
    JWT will have various possible issues (in same way as Api validation).  With JWT we want a "forbidden" response
 */
public class JwtValidationException extends ApiValidationException {

    // Probably a good idea to have the status rather closer to the exception (rather than in the handler itself)
    @Override
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.FORBIDDEN;

    // Construct with errors (that get recorded for later use by BBException)
    public JwtValidationException(List<ApiError> apiErrors) {
        super(apiErrors);   // is this good enough or do we need the HttpInputMessage too??
    }

    // Allow caller to be spared the pain of creating an arrayList for a single message (BBException does the work/storing)
    public JwtValidationException(String fieldName, String fieldMessage) {
        super(fieldName,fieldMessage);
    }

    // Expect this to be called from a controller and just add the interactionId
    public JwtValidationException(HttpServletRequest httpServletRequest, JwtValidationException e) {
        super(httpServletRequest, e);
    }







}
