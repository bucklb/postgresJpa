package com.example.postgresdemo.exception;

/**
 * At the moment there are things that we can't expect codegen/Spring to hanld for us, but we want treated as bad request
 * - no birt details given
 * - date strings that are not dates
 * - dates in the future (birth date & reg date)
 */
public class ApiValidationException extends Exception {

    private String fieldName;
    private String fieldMessage;

    public ApiValidationException(String fieldName, String fieldMessage) {
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