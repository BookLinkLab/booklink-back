package com.booklink.backend.exception;

public class MemberDoesntBelongForumException extends RuntimeException {
    public MemberDoesntBelongForumException(String message) {
        super(message);
    }
}
