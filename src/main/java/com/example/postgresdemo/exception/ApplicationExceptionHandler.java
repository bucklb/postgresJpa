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
    //public class ApplicationExceptionHandler  {
//    private static BspmLogger bspmLogger = BspmLogger.getLogger(ApplicationExceptionHandler.class);
    Logger logger = LoggerFactory.getLogger("exception");

    // TODO : put these in a dedicated constants class
    public static final String INTERACTION_ID = "interactionId";
    public static final String UNEXPECTED_ERROR = "Unexpected Error";


    private MessageSource messageSource;

    @Autowired
    public ApplicationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApplicationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> processApplicationException(ApplicationException ex) {

        // Generate a standard response
        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(UNEXPECTED_ERROR, ex.getMessage()));

        return new ResponseEntity<>(apiErrorsList, ex.getHeaders(), ex.getStatus() );
    }

    @ExceptionHandler(BBException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> processBBException(BBException ex) {

        return new ResponseEntity<>(ex.getApiErrors(), ex.getHeaders(), ex.getStatus());
    }





}
