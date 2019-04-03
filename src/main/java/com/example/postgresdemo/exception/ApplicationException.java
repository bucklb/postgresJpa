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

    // If we are raising exceptions they probably need a status for when they get back to the controller
    public HttpStatus getStatus() { return status; }
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    // NOTE : this could become the traceId from Sleuth
    public String getInteractionId() { return interactionId; }
    private String interactionId;

    /*
    // ================================= PRIVATE "SUPER" CONSTRUCTORS =================================================
        "super" constructors.  Only available within this class
     */
    private ApplicationException(String msg, Throwable e,
                                 HttpStatus httpStatus, HttpServletRequest httpServletRequest, String interactionId ) {
        super(msg, e);

        record(e, httpStatus, httpServletRequest, interactionId);
    }

    private ApplicationException(String msg,
                                 HttpStatus httpStatus, HttpServletRequest httpServletRequest, String interactionId ) {
        super(msg);

        record(null, httpStatus, httpServletRequest, interactionId);
    }

    private ApplicationException(            Throwable e,
                                 HttpStatus httpStatus,HttpServletRequest httpServletRequest, String interactionId ) {
        super(e.getMessage(), e);  // ?? Would it be enough to do super(e)

        record(e, httpStatus, httpServletRequest, interactionId);
    }


    /*
        basic constructors.  So pass lots of nulls "extras"
     */
    public ApplicationException(String msg, Throwable e) {
        this(msg, e, null, null, null);
    }
    public ApplicationException(String msg) {
        this(msg,    null, null, null);    // throwable  free super constructor
    }
    public ApplicationException(            Throwable e) {
        this(     e, null, null, null);      // errMessage free super constructor
    }

    /*
        often we will want to pass a status
     */
    public ApplicationException(String msg, Throwable e, HttpStatus httpStatus) {
        this(msg, e, httpStatus, null, null);
    }
    public ApplicationException(String msg,              HttpStatus httpStatus) {
        this(msg,    httpStatus, null, null);  // throwable  free super constructor
    }
    public ApplicationException(            Throwable e, HttpStatus httpStatus) {
        this(     e, httpStatus, null, null);    // errMessage free super constructor
    }

    /*
        if we are just rethrowing to decorate won't need/want status, probs don't want a message either
        !! ALL of these call the "MESSAGE FREE super constructor !!
     */
    public ApplicationException(Throwable e,              HttpServletRequest httpServletRequest) {
        this(e, null,          httpServletRequest, null);
    }
    public ApplicationException(ApplicationException e,   HttpServletRequest httpServletRequest) {
        this(e, e.getStatus(), httpServletRequest, null);
    }

    public ApplicationException(Throwable e,              String interactionId) {
        this(e,          null, null, interactionId);
    }
    public ApplicationException(ApplicationException e,   String interactionId) {
        this(e, e.getStatus(), null, interactionId);
    }


    /*
    // ================================================================================================================
        record anything useful that came in ...
     */
    private void record(Throwable e, HttpStatus httpStatus, HttpServletRequest httpServletRequest, String interactionId ) {
        // Status might be given explicitly.  Otherwise if we have a suitable "cause" we can glean a status from it
        if ( httpStatus != null ) {
            this.status = httpStatus;
        } else {
            // Can we "inherit" status from a (non-null) cause
            if ( e != null ) {
                if (e instanceof ApplicationException ) {
                    this.status = ((ApplicationException) e).getStatus();
                }
            }
        }
        // Would be good to have an interactiond id, provided explicitly or via the request's headers
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
