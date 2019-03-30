package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

public class CCException extends BBException {

    // Probably a good idea to have the status rather closer to the exception (rather than in the handler itself)
    @Override
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.FORBIDDEN;

    // Construct with errors (that get recorded for later use by BBException)
    public CCException(List<ApiError> apiErrors) {
        super(apiErrors);   // is this good enough or do we need the HttpInputMessage too??
    }

    // Allow caller to be spared the pain of creating an arrayList for a single message (BBException does the work/storing)
    public CCException(String fieldName, String fieldMessage) {
        super(fieldName,fieldMessage);
    }

    // Expect this to be called from a controller and just add the interactionId
    public CCException(HttpServletRequest httpServletRequest, CCException e) {
        super(httpServletRequest, e);
    }



}
