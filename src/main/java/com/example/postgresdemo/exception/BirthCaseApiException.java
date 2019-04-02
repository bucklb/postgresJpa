package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;

public class BirthCaseApiException extends ApplicationException {

    // TODO : can we really forego all this??
//    @Override
//    public HttpStatus getStatus() { return statusCode; }
//
//    private HttpStatus statusCode;
//    private String description;

    public BirthCaseApiException(HttpStatus statusCode, String description ) {
        super(description, null, statusCode);
//        this.statusCode = statusCode;
//        this.description = description;
    }

    public BirthCaseApiException(HttpStatus statusCode, String description, Throwable cause ) {
//        super(cause.getMessage(), cause);
        super(description, cause, statusCode);
//        this.statusCode = statusCode;
//        this.description = description;
    }

//    public HttpStatus getStatusCode() {
//        return statusCode;
//    }
//
//    public String getDescription() {
//        return description;
//    }
}
