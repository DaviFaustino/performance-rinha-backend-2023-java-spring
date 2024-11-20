package com.davifaustino.performancerinhabackend.domain;

public class BadRequestException extends RuntimeException {
    
    public BadRequestException(String message) {
        super(message);
    }
}
