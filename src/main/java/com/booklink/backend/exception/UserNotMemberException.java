package com.booklink.backend.exception;

public class UserNotMemberException extends RuntimeException{
    public UserNotMemberException(String message) {
        super(message);
    }
}
