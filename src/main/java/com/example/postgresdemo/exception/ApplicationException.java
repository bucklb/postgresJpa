package com.example.postgresdemo.exception;

import net.bytebuddy.implementation.bytecode.Throw;
import org.omg.SendingContext.RunTime;

/**
 * Allow interaction id to be part of exceptions
 */
//public class ApplicationException extends Exception {
    public class ApplicationException extends RuntimeException {

    private String interactionId;


    public ApplicationException(String interactionId, Throwable e) {
        super(e);
        setInteractionId(interactionId);
    }

    //
//    // Need to have a (cheked) exception at core in order for Handler to work as expected.
//    // If we get an RTE then morph it in to a "normal" Exception
//    public ApplicationException(String interactionId, RuntimeException e) {
//
//        // Create with blank Exception (or as raw throwable)
//        super(e);
////        super(new Exception());
//        this.setStackTrace(e.getStackTrace());
//
//        System.out.println("Taking in RTE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        setInteractionId(interactionId);
//    }

    public String getInteractionId() {
        return interactionId;
    }

    public void setInteractionId(String interactionId) {
        this.interactionId = interactionId;
    }
}
