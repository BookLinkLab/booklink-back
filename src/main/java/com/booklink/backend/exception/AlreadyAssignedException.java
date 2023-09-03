package com.booklink.backend.exception;

public class AlreadyAssignedException extends RuntimeException{
    public AlreadyAssignedException(String message) {
        super(message);
    }
}
