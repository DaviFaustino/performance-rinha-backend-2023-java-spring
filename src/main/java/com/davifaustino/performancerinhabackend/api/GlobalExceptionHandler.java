package com.davifaustino.performancerinhabackend.api;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.davifaustino.performancerinhabackend.business.BadRequestException;
import com.davifaustino.performancerinhabackend.business.UnprocessableException;

import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<String>> handleBadRequestException(BadRequestException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }

    @ExceptionHandler(UnprocessableException.class)
    public Mono<ResponseEntity<String>> handleUnprocessableException(UnprocessableException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Mono<ResponseEntity<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Mono<ResponseEntity<String>> handleDuplicateKeyException(DuplicateKeyException e) {
        return Mono.just(ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(null));
    }
}
