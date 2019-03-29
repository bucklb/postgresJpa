package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * At the moment there are things that we can't expect codegen/Spring to hanld for us, but we want treated as bad request
 * - no birt details given
 * - date strings that are not dates
 * - dates in the future (birth date & reg date)
 */
//public class ApiValidationException extends HttpMessageNotReadableException {
public class ApiValidationException extends ApplicationException {

    // Probably a good idea to have the status rather closer to the exception (rather than in the handler itself)
    private List<ApiError> apiErrors;
    public List<ApiError> getApiErrors() { return apiErrors; }

    @Override   // if not overridden then will be raised with 500
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.BAD_REQUEST;

    /*
        Error list should be enough, without any extra explanation
     */
    public ApiValidationException(List<ApiError> apiErrors) {
        super("apiValidation");   // is this good enough or do we need the HttpInputMessage too??
        this.apiErrors=apiErrors;
    }

    /*
        Allow caller to be spared the pain of creating an arrayList for a single error/message
     */
    public ApiValidationException(String fieldName, String fieldMessage) {
        super("apiValidation");
        this.apiErrors = new ArrayList<>();
        this.apiErrors.add(new ApiError(fieldName,fieldMessage));
    }

    /*
        Expect this to be called from a controller and just add the interactionId for further transmission
    */
    public ApiValidationException(HttpServletRequest httpServletRequest, ApiValidationException e) {
        super(httpServletRequest, e);
        this.apiErrors = e.getApiErrors();
    }

}