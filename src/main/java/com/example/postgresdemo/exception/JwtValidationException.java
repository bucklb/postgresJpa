package com.example.postgresdemo.exception;

import java.util.ArrayList;
import java.util.List;

/*
    JWT will have various possible issues (in same way as Api validation).  With JWT we want a "forbidden" response
 */
public class JwtValidationException extends ApiValidationException {

    // Construct with errors
    public JwtValidationException(List<ApiError> apiErrors) {
        super(apiErrors);   // is this good enough or do we need the HttpInputMessage too??
    }

    public JwtValidationException(String fieldName, String fieldMessage) {
        super(fieldName, fieldMessage);
    }

}
