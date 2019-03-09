package com.example.postgresdemo.exception;

import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.servlet.http.HttpServletRequest;
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

    // Construct with errors
    public ApiValidationException(List<ApiError> apiErrors) {
        super("exceptional");   // is this good enough or do we need the HttpInputMessage too??
        this.apiErrors=apiErrors;
    }



    private String fieldName;
    private String fieldMessage;
    public ApiValidationException(String fieldName, String fieldMessage) {
        super("exceptional");
        this.fieldMessage=fieldMessage;
        this.fieldName=fieldName;
    }




    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldMessage() {
        return fieldMessage;
    }

    public void setFieldMessage(String fieldMessage) {
        this.fieldMessage = fieldMessage;
    }


}