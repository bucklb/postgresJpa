package com.example.postgresdemo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {

    // Might be better defined in more central location
    public static final String UNEXPECTED_ERROR = "Unexpected Error";

    // If we haven't been gifted an error list then create a rather basic one as best we can
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Object> processApplicationException(ApplicationException ex) {

        return new ResponseEntity<>(ex.getMessage(), ex.getHeaders(), ex.getStatus() );
    }

    // If needed, this returns the validation bits in an array
    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<Object> processApplicationException(ApiValidationException ex) {

        // If we have a list then use it, else knock one together
        List<ApiError> apiErrors = ex.getApiErrors();
        if( apiErrors == null ) {
            apiErrors = new ArrayList<>();
            apiErrors.add(new ApiError(UNEXPECTED_ERROR + ">>>>>>>", ex.getMessage()));
        }

        return new ResponseEntity<>(apiErrors, ex.getHeaders(), ex.getStatus() );
    }

    // REMARKABLY similar to the default handler. Does it need to be different
    @ExceptionHandler(BirthCaseApiException.class)
    public ResponseEntity<Object> processApplicationException(BirthCaseApiException ex) {

        return new ResponseEntity<>(ex.getMessage(), ex.getHeaders(), ex.getStatus() );
    }

}
