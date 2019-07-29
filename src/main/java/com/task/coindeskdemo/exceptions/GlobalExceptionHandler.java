package com.task.coindeskdemo.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

//@ControllerAdvice
//@Component
//@Slf4j
public class GlobalExceptionHandler {

    /**
     * Global Exception handler for all exceptions.
     */
    @ExceptionHandler
    public void handle(Exception exception) {
        //log.error(exception.getMessage());
    }

}