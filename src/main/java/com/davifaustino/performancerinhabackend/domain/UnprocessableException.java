package com.davifaustino.performancerinhabackend.domain;

public class UnprocessableException extends RuntimeException {
    
    public UnprocessableException(String message) {
        super(message);
    }
}
