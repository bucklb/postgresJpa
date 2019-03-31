package com.example.postgresdemo.controller;

import com.example.postgresdemo.exception.ApplicationException;
import com.example.postgresdemo.service.ExceptionalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
    Better segrregate the things I'm playing with.  Laely that's been exceptions ...
 */

@RequestMapping("exception/")
@RestController
public class ExceptionalController {

    Logger logger = LoggerFactory.getLogger(ExceptionalController.class);

    // A copy of the request will allow us to check headers passed in (for correlation/interaction ids, jwt's etc)
    @Autowired
    HttpServletRequest httpServletRequest;

    @Autowired
    ExceptionalService exceptionalService;

    // Throw the requested exception and don't handle it (beyond the controllerAdvice stuff).
    // Unless something has been explicitly thrown as an AppException it won't be handled nicely
    @GetMapping("exception/clean/{exType}")
    public String doCleanThrow(@PathVariable String exType) {

        exceptionalService.thrower(exType);

        // Should never hit this
        return "Missed an exception???";

    }

    // Use get ../exception/throw/<exType> to trigger.  Causes an exception of relevant flavour to be thrown
    // but we then catch all exceptions and rethrow as AppExceptions here.
    // Exception handler(s) should only ever encounter AppExceptions (that might be wrappers to other exception types)
    @GetMapping("throw/{exType}")
    public String doThrow(@PathVariable String exType) {

        try {
            exceptionalService.thrower(exType);
        } catch (Exception ex) {
            // LOG IT & the rethrow it such that it gets any given interactionId on the way out to caller
            logger.info("exception/", ex);
            throw new ApplicationException(httpServletRequest, ex);
        }

        // Should never hit this
        return "Missed an exception???";

    }

    // Use get ../exception/throw/<exType> to trigger.  Causes an exception of relevant flavour to be thrown
    // and then captured and rethrown at low level.
    // We then catch all exceptions and rethrow as AppExceptions here.
    // Exception handler(s) should only ever encounter AppExceptions based on AppExceptions
    // (that might be wrappers to other exception types)
    @GetMapping("rethrow/{exType}")
    public String doReThrow(@PathVariable String exType) {

        try {
            exceptionalService.rethrower(exType);
        } catch (Exception ex) {
            // LOG IT & the rethrow it such that it gets any given interactionId on the way out to caller
            logger.info("exception/", ex);
            throw new ApplicationException(httpServletRequest, ex);
        }

        // Should never hit this
        return "Missed an exception???";

    }

    // Look at effect of stack on logging
    @GetMapping("stack/{exType}")
    public String doThrowLine(@PathVariable String exType) {

        try {
            exceptionalService.throwLine(exType);
        } catch (Exception ex) {
            // LOG IT & the rethrow it such that it gets any given interactionId on the way out to caller
            logger.info("exception/", ex);
            throw new ApplicationException(httpServletRequest, ex);
        }

        // Should never hit this
        return "Missed an exception???";

    }



}
