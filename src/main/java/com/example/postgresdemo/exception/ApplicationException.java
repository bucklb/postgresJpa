package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/*
    Want a basic mechanism to handle our errors.  If codegen allowed Exception(s) might be better to use checked Exceptions
 */
public class ApplicationException extends RuntimeException {

    public static final String INTERACTION_ID = "interactionId";

    // Will generally want this to be overridden as we progress through the profile
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    private String interactionId;
    public String getInteractionId() { return interactionId; }


    /*
        If we don't get given an interaction id then look at grabbing from headers (if any) auto-injected for us
     */
    private void setInteractionIdFromHeader(HttpServletRequest httpServletRequest) {
        if( httpServletRequest != null ){
            String id = httpServletRequest.getHeader(INTERACTION_ID);
            this.interactionId = id;
        }
    }

    /*
        Might be that we want many inheritors to get the interaction id
     */
    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(INTERACTION_ID, interactionId);
        return httpHeaders;
    }

    /*
        Allow id to be passed in explicitly
     */
    public ApplicationException(String interactionId, Throwable e) {
        super(e);
        this.interactionId = interactionId;
    }

    /*
        We will look to glean the interaction id from headers (if any)
     */
    public ApplicationException(HttpServletRequest httpServletRequest, Throwable e) {
        super(e);
        setInteractionIdFromHeader(httpServletRequest);
    }

    /*
        We will look to glean the interaction id from headers (if any)
     */
    public ApplicationException(HttpServletRequest httpServletRequest, String errMsg) {
        super(errMsg);
        setInteractionIdFromHeader(httpServletRequest);
    }

    /*
        For when we create something that inherits from us
    */
    public ApplicationException(String errMsg) {
        super(errMsg);
    }


}
