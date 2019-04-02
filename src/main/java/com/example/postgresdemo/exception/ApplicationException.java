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

    // May be a call for description ...
    public String getDescription() { return description; }
    private String description;


    // =============================================== CONSTRUCTORS =================================================


    /*
        "super" constructors.  Other constructors will call them. If passed throwable, check it for useful bits
     */
    private ApplicationException(HttpServletRequest httpServletRequest, String interactionId, Throwable e) {
        super(e.getMessage(), e);
        decorate( httpServletRequest, interactionId);
        populateFromApplicationException( e );
    }
    private ApplicationException(HttpServletRequest httpServletRequest, String interactionId, String errMsg) {
        super(errMsg);
        decorate( httpServletRequest, interactionId);
    }

    /*
        Allow id to be passed in explicitly.
     */
    public ApplicationException(String interactionId, Throwable e) {
        this(null, interactionId, e);
    }
    public ApplicationException(String interactionId, String errMsg) {
        this(null, interactionId, errMsg);
    }

    /*
        Allow request & its headers (so can check them for stuff)
     */
    public ApplicationException(HttpServletRequest httpServletRequest, Throwable e) {
        this( httpServletRequest, null, e);
    }
    public ApplicationException(HttpServletRequest httpServletRequest, String errMsg) {
        this( httpServletRequest, null, errMsg);
    }

    /*
        May not always be an interaction id (or headers) so don't insist on it
    */
    public ApplicationException(String errMsg) {
        this(null, null, errMsg);
    }
    public ApplicationException(Throwable e) {
        this(null, null, e);
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

    // TODO : is system happy that a null throwable might get passed?
    protected ApplicationException(String desc, Throwable e, HttpStatus httpStatus) {
        super( desc, e );
        this.description = desc;
        this.status = httpStatus;
    }


    // ================================================================================================================

    /*
        If we have decorations for the exception, glean and record them
     */
    private void decorate(HttpServletRequest httpServletRequest, String interactionId) {
        // Populate interaction id
        if ( interactionId != null ) {
            this.interactionId = interactionId;
        } else {
            this.interactionId = getInteractionIdFromRequest(httpServletRequest);
        }
    }

    /*
        If we don't get given an interaction id then look at grabbing from the Request, if we get one handed in
     */
    private String getInteractionIdFromRequest(HttpServletRequest httpServletRequest) {
        String id = null;
        if( httpServletRequest != null ){
            // We will happily accept a null response if there's nothing real to record
            id = httpServletRequest.getHeader(INTERACTION_ID);
        }
        return id;
    }

    /*
        If we're to be based on an exception passed in, then get the good stuff from it
     */
    private void populateFromApplicationException(Throwable e) {
        // If it SHOULD have apiErrors, then cast it so we get the methods and the data
        if( e instanceof  ApplicationException ){
            ApplicationException aEx = (ApplicationException)e;
            apiErrors = aEx.getApiErrors();
            status = aEx.getStatus();
        }
    }

    /*
        Outcome of exception will be a response that needs header(s).  Generate here, rather than in handler
     */
    public HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        // Only add a header if there's a payload for it to carry
        if ( interactionId != null ) {
            httpHeaders.add(INTERACTION_ID, interactionId);
        }
        return httpHeaders;
    }
}
