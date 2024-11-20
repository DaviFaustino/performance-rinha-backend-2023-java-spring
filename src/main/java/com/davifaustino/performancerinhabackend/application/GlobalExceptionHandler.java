package com.davifaustino.performancerinhabackend.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.davifaustino.performancerinhabackend.domain.BadRequestException;
import com.davifaustino.performancerinhabackend.domain.UnprocessableException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<String>> handleBadRequestException(BadRequestException e) {

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message: " + e.getMessage()));
    }

    @ExceptionHandler(UnprocessableException.class)
    public Mono<ResponseEntity<String>> handleUnprocessableException(UnprocessableException e) {
        
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Error message: " + e.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error message: " + e.getMessage()));
    }
}
