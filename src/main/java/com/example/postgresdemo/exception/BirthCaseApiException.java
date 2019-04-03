package com.example.postgresdemo.exception;

import org.springframework.http.HttpStatus;

public class BirthCaseApiException extends ApplicationException {


    public BirthCaseApiException(HttpStatus statusCode, String description ) {
        super(description, statusCode);
    }

    public BirthCaseApiException(HttpStatus statusCode, String description, Throwable cause ) {
        super(description, cause, statusCode);
    }

}
