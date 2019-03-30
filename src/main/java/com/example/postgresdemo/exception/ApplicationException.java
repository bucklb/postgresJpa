package com.example.postgresdemo.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/*
    Want a basic mechanism to handle our errors.  If codegen allowed Exception(s) might be better to use checked Exceptions
 */
public class ApplicationException extends RuntimeException {

    public static final String INTERACTION_ID = "interactionId";

    // If we rely on apiErrors across the board ....
    public List<ApiError> getApiErrors() { return apiErrors; }
    private List<ApiError> apiErrors;

    // Will generally want this to be overridden as we progress through the profile.  We can't insist that anyone uses it anyway
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    // Not clear if the need for this is obviated if we use Sleuth!!
    public String getInteractionId() { return interactionId; }
    private String interactionId;

    // =============================================== CONSTRUCTORS =================================================

    /*
        Allow id to be passed in explicitly, though need to think if likely to be really needed.
        Ought to only happen as (last) re-throw of an exception.
     */
    public ApplicationException(String interactionId, Throwable e) {
        super(e);
        this.interactionId = interactionId;
        if( e instanceof  ApplicationException ){
            populateFromApplicationException( (ApplicationException)e);
        }
    }
    public ApplicationException(String interactionId, String errMsg) {
        super(errMsg);
        this.interactionId = interactionId;
    }

    /*
        We will look to glean the interaction id from headers (if any)
        Ought to only happen as (last) re-throw of an exception.
     */
    public ApplicationException(HttpServletRequest httpServletRequest, Throwable e) {
        super(e);
        setInteractionIdFromHeader(httpServletRequest);
        if( e instanceof  ApplicationException ){
            populateFromApplicationException( (ApplicationException)e);
        }
    }
    public ApplicationException(HttpServletRequest httpServletRequest, String errMsg) {
        super(errMsg);
        setInteractionIdFromHeader(httpServletRequest);
    }

    /*
        May not always be an interaction id (or headers) so don't insist on it
    */
    public ApplicationException(String errMsg) {
        super(errMsg);
    }

    public ApplicationException(Throwable e) {
        super(e);
        if( e instanceof  ApplicationException ){
            populateFromApplicationException( (ApplicationException)e);
        }
    }

    /*
        Allow only other exceptions access to these constructors.  General public don't get access
     */
    protected ApplicationException(List<ApiError> apiErrors) {
        super( apiErrors.toString() );
        this.apiErrors = apiErrors;
    }
    // Already have a two string constructor, otherwise would use field, message
    protected ApplicationException(ApiError apiError) {
        super( apiError.toString() );
        this.apiErrors = new ArrayList<>();
        this.apiErrors.add(apiError);
    }



    // ================================================================================================================

    /*
    Outcome of exception will be a response that needs header(s).  Centralise the production
     */
    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        // Only add a header if there's a payload for it to carry
        if ( interactionId != null ) {
            httpHeaders.add(INTERACTION_ID, interactionId);
        }
        return httpHeaders;
    }

    /*
        If we don't get given an interaction id then look at grabbing from the ServletRequest, if we get one handed in
     */
    private void setInteractionIdFromHeader(HttpServletRequest httpServletRequest) {
        if( httpServletRequest != null ){
            // We will happily accept a null response if there's nothing real to record
            String id = httpServletRequest.getHeader(INTERACTION_ID);
            this.interactionId = id;
        }
    }

    /*
        If we're to be based on an exception passed in, then get the good stuff from it
     */
    private void populateFromApplicationException(ApplicationException e) {
        apiErrors = e.getApiErrors();
        status = e.getStatus();
    }

}
