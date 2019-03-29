package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpInputMessage;
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
public class ApiValidationException extends HttpMessageNotReadableException {

    private List<ApiError> apiErrors;
    public List<ApiError> getApiErrors() {        return apiErrors;    }
    public void setApiErrors(List<ApiError> apiErrors) {        this.apiErrors = apiErrors;    }

    @Autowired
    HttpInputMessage httpInputMessage;

    // Construct with errors
    public ApiValidationException(List<ApiError> apiErrors) {
        super("exceptional");   // is this good enough or do we need the HttpInputMessage too??
        this.apiErrors=apiErrors;
    }

    // Allow caller to be spared the pain of creating an array for a single message
    public ApiValidationException(String fieldName, String fieldMessage) {
        super("exceptional");
        this.apiErrors = new ArrayList<>();
        this.apiErrors.add(new ApiError(fieldName,fieldMessage));
    }


}