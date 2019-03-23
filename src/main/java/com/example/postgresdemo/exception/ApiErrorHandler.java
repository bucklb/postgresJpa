package com.example.postgresdemo.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.instrument.classloading.jboss.JBossLoadTimeWeaver;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Be a little more informative than just "Bad request".  Give clues as to why the failure ...
 */

@RestControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {
//    private static BspmLogger bspmLogger = BspmLogger.getLogger(ApplicationExceptionHandler.class);

    // TODO : put these in a dedicated constants class
    public static final String FIELD_REJECTION_SEPARATOR = " : ";
    public static final String INTERACTION_ID = "interactionId";
    public static final String REQUEST_ERROR = "Request Error";
    public static final String UNREADABLE_MESSAGE = "Unreadable Message";
    public static final String UNEXPECTED_ERROR = "Unexpected Error";



    private MessageSource messageSource;

    @Autowired
    public ApiErrorHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        insertHeaders(request, headers);

        System.out.println("~~~~");
        System.out.println(ex.getBindingResult().getTarget().getClass().getName());
        System.out.println("~~~~");


        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<ApiError> apiErrors = processFieldErrors(fieldErrors);

        ResponseEntity<Object> responseEntity = createResponseEntity(apiErrors, headers, status, request);

//        bspmLogger.error("handleMethodArgumentNotValid", null, request.getHeader(Constants.INTERACTION_ID), apiErrors);

        return responseEntity;
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        insertHeaders(request, headers);

        ArrayList<ApiError> apiErrorsList = new ArrayList<>();

        String compositeField = ex.getErrorCode()
                + FIELD_REJECTION_SEPARATOR + ex.getValue();
        apiErrorsList.add(new ApiError(compositeField, ex.getCause().toString()));

        ResponseEntity<Object> responseEntity = createResponseEntity(apiErrorsList, headers, status, request);

//        bspmLogger.error("handleTypeMismatch", null, request.getHeader(Constants.INTERACTION_ID), apiErrorsList);

        return responseEntity;
    }

    // Will have apiErrors baked in
    private ResponseEntity<Object> handleApiValidationException(ApiValidationException ex,
                                                                HttpHeaders headers,
                                                                HttpStatus status,
                                                                WebRequest request) {
        if( JwtValidationException.class.getName().equals( ex.getClass().getName()) ){
            // TODO : should this be elsewhere ??  Jwt checks raise bad request (via ApiValEx) or forbidden (via JwtValEx)
            status = HttpStatus.FORBIDDEN;
        }
        ResponseEntity<Object> responseEntity = createResponseEntity(ex.getApiErrors(), headers, status, request);
        return responseEntity;
    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {


    // If looking to add in JwtValidation, so loosen the checks a tad ...
    if ( ex instanceof ApiValidationException ) {
//    if( ApiValidationException.class.getName().equals( ex.getClass().getName()) ){
        // One of ours
        return handleApiValidationException((ApiValidationException)ex,headers,status,request);
    } else {
        // Vanilla
        insertHeaders(request, headers);

        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        // Not about the field.  Use field to report whole message (and use message for details)
        apiErrorsList.add(new ApiError(UNREADABLE_MESSAGE, ex.getMostSpecificCause().getMessage()));

        ResponseEntity<Object> responseEntity = createResponseEntity(apiErrorsList, headers, status, request);

//        bspmLogger.error("handleHttpMessageNotReadable", null, request.getHeader(Constants.INTERACTION_ID), apiErrorsList);

        return responseEntity;
    }
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatus status,
                                                             WebRequest request) {
        insertHeaders(request, headers);

        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(UNEXPECTED_ERROR, ex.getMessage()));

        ResponseEntity<Object> responseEntity = createResponseEntity(apiErrorsList, headers, status, request);

//        bspmLogger.error("handleExceptionInternal", null, request.getHeader(Constants.INTERACTION_ID), apiErrorsList);

        return responseEntity;
    }

    @Override
    protected ResponseEntity<java.lang.Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                                    HttpHeaders headers,
                                                                                    HttpStatus status,
                                                                                    WebRequest request) {
        insertHeaders(request, headers);

        ArrayList<ApiError> apiErrorsList = new ArrayList<>();
        apiErrorsList.add(new ApiError(REQUEST_ERROR, ex.getMessage()));

        ResponseEntity<Object> responseEntity = createResponseEntity(apiErrorsList, headers, status, request);

//        bspmLogger.error("handleServletRequestBindingException", null, request.getHeader(Constants.INTERACTION_ID), apiErrorsList);

        return responseEntity;
    }

    private List<ApiError> processFieldErrors(List<FieldError> fieldErrors) {
        ArrayList<ApiError> apiErrorsList = new ArrayList<>();

        for (FieldError fieldError : fieldErrors) {

//            System.out.println(resolveLocalizedErrorMessage(fieldError));
//            System.out.println(fieldError.getDefaultMessage());


            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
//            String localizedErrorMessage = fieldError.getDefaultMessage();
            String rejectedValue = (fieldError.getRejectedValue() == null) ? "null" : fieldError.getRejectedValue().toString();
            String compositeField = fieldError.getField()
                    + FIELD_REJECTION_SEPARATOR + rejectedValue;
            apiErrorsList.add(new ApiError(compositeField, localizedErrorMessage));
        }

        return apiErrorsList;
    }

    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale = LocaleContextHolder.getLocale();

        //If the message was not found, return the most accurate field error code instead.
        //You can remove this check if you prefer to get the default error message.
        /*if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }*/

        return messageSource.getMessage(fieldError, currentLocale);
    }

    private ResponseEntity<Object> createResponseEntity(List<ApiError> apiErrorsList,
                                                        HttpHeaders headers,
                                                        HttpStatus status,
                                                        WebRequest request) {
        return new ResponseEntity<>(apiErrorsList, headers, status);
    }


    private void insertHeaders(WebRequest request, HttpHeaders headers) {
        String interactionId = request.getHeader(INTERACTION_ID);

        // Important not to put a null value into the header.
        // Junit tests behave strangely if you do!
        if (interactionId != null) {
            headers.add(INTERACTION_ID, interactionId);
        } else {
            headers.add(INTERACTION_ID, "nonesuch");
        }
    }
}

