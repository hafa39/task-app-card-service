package com.example.cardservice.web.api;

import com.example.cardservice.web.exc.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CardServiceGlobalExcHandler {
    private static final Logger log = LoggerFactory.getLogger(CardServiceGlobalExcHandler.class);

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExc(NotFoundException e) {
        log.error("NotFoundException: " + e.getMessage());
        return Map.of("message", e.getMessage());
    }
}
