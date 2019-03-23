package com.example.postgresdemo.exception;

import org.omg.SendingContext.RunTime;

/**
 * Allow interaction id to be part of exceptions
 */
public class ApplicationException extends RuntimeException {

    private String interactionId;

    public ApplicationException(String interactionId, Throwable e) {
        super(e);
        setInteractionId(interactionId);
    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }
}
