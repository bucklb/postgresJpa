package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
//    private static BspmLogger bspmLogger = BspmLogger.getLogger(ApplicationExceptionHandler.class);

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
        ArrayList<ApiError> apiErrorsList = new ArrayList<>();

        apiErrorsList.add(new ApiError(UNEXPECTED_ERROR, ex.getMessage()));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(INTERACTION_ID, ex.getInteractionId());

//        bspmLogger.error(ex.getMessage(), null, ex.getInteractionId(), apiErrorsList);

        return new ResponseEntity<>(apiErrorsList, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
