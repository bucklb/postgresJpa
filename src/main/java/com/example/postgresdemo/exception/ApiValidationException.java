package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
    In validation can be multiple issues raised, so try and raise many at once, not one a time
 */
public class ApiValidationException extends ApplicationException {

    // Default STATUS
    private static final HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    // Testing is easier if we can get a list of individual issuettes. Allows more specified handling, potentially
    public List<ApiError> getApiErrors() { return apiErrors; }
    private List<ApiError> apiErrors;

    /*
        PRIVATE constructors. Parameters back to front to avoid clashes
     */
    private ApiValidationException(HttpStatus httpStatus, List<ApiError> apiErrors) {
        super(apiErrors.toString(), httpStatus);
        // record the apiErrors in case anyone wants them
        this.apiErrors = apiErrors;
    }
    private ApiValidationException(HttpStatus httpStatus, String fieldName, String fieldMessage) {
        super((new ApiError(fieldName,fieldMessage)).toString(), STATUS);
        // record as apiErrors in case anyone wants it/them
        this.apiErrors = new ArrayList<>();
        this.apiErrors.add(new ApiError(fieldName,fieldMessage));
    }

    /*
        "Default" constructors
     */
    public ApiValidationException(List<ApiError> apiErrors) {
        this(STATUS, apiErrors);
    }
    public ApiValidationException(String fieldName, String fieldMessage) {
        this(STATUS, fieldName, fieldMessage);
    }

    /*
        Not sure this will ever be needed (we plan the final throw to be via an AppEx proper)
    */
    public ApiValidationException(HttpServletRequest httpServletRequest, ApiValidationException e) {
        super(e,httpServletRequest);
        // Other constructors go via private super constructors, that retain apiErrors.  We don't so retain here
        this.apiErrors = e.getApiErrors();
    }


    /*
        To allow super stuff from JwtValidationException (amd its inheritors)
        Might make sense to allow outside world to use it?? Depends if we want caller to tell us what status to use!
     */
    protected ApiValidationException(List<ApiError> apiErrors, HttpStatus httpStatus) {
        this(httpStatus, apiErrors);
    }
    protected ApiValidationException(String fieldName, String fieldMessage, HttpStatus httpStatus) {
        this(httpStatus, fieldName, fieldMessage);
    }

}