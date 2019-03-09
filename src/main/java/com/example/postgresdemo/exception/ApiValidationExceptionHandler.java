package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;

@RestControllerAdvice
public class ApiValidationExceptionHandler  extends ResponseEntityExceptionHandler  {

    private MessageSource messageSource;
    HttpServletRequest httpServletRequest;

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

        String S="";
        Enumeration<String> s=httpServletRequest.getHeaders("random");
        if (s!=null){S=s.toString();}

        System.out.println(s + " " + S);

//        httpHeaders.add(INTERACTION_ID, ex.getInteractionId());

        // Failed to validate, so that's kind of about the request,
        return new ResponseEntity<>(apiErrorsList, httpHeaders, HttpStatus.BAD_REQUEST);
    }




}
