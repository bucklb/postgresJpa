package com.example.postgresdemo.exception;

/**
 * pinched from CM
 */
public class ApiError {
    private String field;
    private String localizedErrorMessage;

    public ApiError(){
    }

    public ApiError(String field, String localizedErrorMessage){
        this.field = field;
        this.localizedErrorMessage = localizedErrorMessage;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLocalizedErrorMessage() {
        return localizedErrorMessage;
    }

    public void setLocalizedErrorMessage(String localizedErrorMessage) {
        this.localizedErrorMessage = localizedErrorMessage;
    }

    // Aim to get something helpful if someone wants a string (like in exception handling)
//    @Override
//    public String toString() {
//        return "{\"" + this.getField() + "\" : \"" + this.getLocalizedErrorMessage() + "\"}";
//    }

    @Override
    public String toString() {
        return "{\"field\": \"" + this.getField() + "\", " +
                "\"localizedErrorMessage\": \"" + this.getLocalizedErrorMessage() + "\"}";
    }


}
