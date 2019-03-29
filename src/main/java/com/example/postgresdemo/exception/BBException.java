package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
 * Suspect the issues with the ApiExceptionHandling might be a function of handling in two places ??
 * So try a cleaner start point !!
 */
public class BBException extends ApplicationException {

    // Probably a good idea to have the status rather closer to the exception (rather than in the handler itself)
    private List<ApiError> apiErrors;
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    public List<ApiError> getApiErrors() { return apiErrors; }
    public void setApiErrors(List<ApiError> apiErrors) { this.apiErrors = apiErrors; }

    @Override
    public HttpStatus getStatus() { return status; }

    // Construct with errors
    public BBException(List<ApiError> apiErrors) {
        super("apiValidation");   // is this good enough or do we need the HttpInputMessage too??
        this.apiErrors=apiErrors;
    }

    // Allow caller to be spared the pain of creating an arrayList for a single message
    public BBException(String fieldName, String fieldMessage) {
        super("apiValidation");
        this.apiErrors = new ArrayList<>();
        this.apiErrors.add(new ApiError(fieldName,fieldMessage));
    }

    // Expect this to be called from a controller and just add the interactionId
    public BBException(HttpServletRequest httpServletRequest, BBException e) {
        super(httpServletRequest, e);
        this.apiErrors=e.getApiErrors();
    }
}