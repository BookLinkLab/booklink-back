package com.booklink.backend.exception;

public class MemberAlreadyJoinedForumException extends RuntimeException {
    public MemberAlreadyJoinedForumException(String message) {
        super(message);
    }
}
