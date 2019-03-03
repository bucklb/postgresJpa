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
public class ApiValidationExceptionHandler  extends ResponseEntityExceptionHandler  {

    private MessageSource messageSource;

    @Autowired
    public ApiValidationExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(ApiValidationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> ApiValidationExceptionException(ApiValidationException ex) {

        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(ex.getFieldName(), ex.getFieldMessage()));
        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.add(INTERACTION_ID, ex.getInteractionId());

        return new ResponseEntity<>(apiErrorsList, httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
    }




}
