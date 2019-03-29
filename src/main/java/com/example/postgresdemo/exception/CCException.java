package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class CCException extends BBException {

    // Probably a good idea to have the status rather closer to the exception (rather than in the handler itself)
//    private List<ApiError> apiErrors;
    private HttpStatus status = HttpStatus.FORBIDDEN;

//    public List<ApiError> getApiErrors() { return apiErrors; }
//    public void setApiErrors(List<ApiError> apiErrors) { this.apiErrors = apiErrors; }

    @Override
    public HttpStatus getStatus() { return status; }

    // Construct with errors
    public CCException(List<ApiError> apiErrors) {
        super(apiErrors);   // is this good enough or do we need the HttpInputMessage too??
//        this.apiErrors=apiErrors;
    }

    // Allow caller to be spared the pain of creating an arrayList for a single message
    public CCException(String fieldName, String fieldMessage) {
        super(fieldName,fieldMessage);
//        this.apiErrors = new ArrayList<>();
//        this.apiErrors.add(new ApiError(fieldName,fieldMessage));
    }

    // Expect this to be called from a controller and just add the interactionId
    public CCException(HttpServletRequest httpServletRequest, CCException e) {
        super(httpServletRequest, e);
//        this.apiErrors=e.getApiErrors();
    }



}
