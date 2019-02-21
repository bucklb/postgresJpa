package com.example.postgresdemo.exception;

/**
 * Allow interaction id to be part of exceptions
 */
public class ApplicationException extends Exception {

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
