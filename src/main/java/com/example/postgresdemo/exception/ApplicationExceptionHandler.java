package com.example.postgresdemo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    // Might be better defined in more central location
    public static final String UNEXPECTED_ERROR = "Unexpected Error";

    // Create a vestigial ApiError object & pass it back
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> processApplicationException(ApplicationException ex) {

        // Generate a standard response
        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(UNEXPECTED_ERROR, ex.getMessage()));

        return new ResponseEntity<>(apiErrorsList, ex.getHeaders(), ex.getStatus() );
    }

    // This will handle any CCException (unless we choose to write a dedicated flavour)
    @ExceptionHandler(BBException.class)
    public ResponseEntity<Object> processBBException(BBException ex) {
        return new ResponseEntity<>(ex.getApiErrors(), ex.getHeaders(), ex.getStatus());
    }

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Object> processApiValidationException(ApiValidationException ex) {
        return new ResponseEntity<>(ex.getApiErrors(), ex.getHeaders(), ex.getStatus());
    }



}
