package com.fintech.eod.exception;

import org.springframework.http.HttpStatus;

public class BusinessRuleException extends RuntimeException{
    private final HttpStatus httpStatus;

    public BusinessRuleException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
