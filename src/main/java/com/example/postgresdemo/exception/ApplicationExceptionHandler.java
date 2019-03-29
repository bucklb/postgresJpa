package com.example.postgresdemo.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@RestControllerAdvice
@Service
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    // Might be better defined in more central location
    public static final String UNEXPECTED_ERROR = "Unexpected Error";

//    @Autowired
//    public ApplicationExceptionHandler(){
//    }



    // Create a vestigal ApiError object & pass it back
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> processApplicationException(ApplicationException ex) {

        // Generate a standard response
        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(UNEXPECTED_ERROR, ex.getMessage()));

        return new ResponseEntity<>(apiErrorsList, ex.getHeaders(), ex.getStatus() );
    }

    // This will handle any CCException (unless we choose to write a dedicated flavour)
    // Just pass back the
    @ExceptionHandler(BBException.class)
    public ResponseEntity<Object> processBBException(BBException ex) {

        return new ResponseEntity<>(ex.getApiErrors(), ex.getHeaders(), ex.getStatus());
    }

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Object> processApiValidationException(ApiValidationException ex) {

        System.out.println(ex.getApiErrors() == null);
        System.out.println(ex.getHeaders() == null);
        System.out.println(ex.getStatus() == null);

        System.out.println(ex.getHeaders().size());

        HttpHeaders h = ex.getHeaders();
        HttpHeaders hh = new HttpHeaders();

//        return new ResponseEntity<>(ex.getApiErrors(), ex.getHeaders(), ex.getStatus());
        return new ResponseEntity<>(ex.getApiErrors(), hh, ex.getStatus());
    }



}
