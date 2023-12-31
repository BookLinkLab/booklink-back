package com.booklink.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class ExceptionHandler {
    Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    @org.springframework.web.bind.annotation.ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<?> handleEntityNotFound(NotFoundException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleInvalidArguments(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleDuplicateKey(DataIntegrityViolationException e) {
        this.logger.info(e.getMessage());
        Matcher matcher = Pattern.compile("Detail: Key \\((.*)\\)").matcher(e.getMostSpecificCause().getMessage());
        if (matcher.find()) {
            String key = matcher.group(1).replaceAll("[)(]", " ");
            String[] keyParts = key.split(" ");
            String value = String.join(" ", Arrays.copyOfRange(keyParts, 2, keyParts.length));
            if (key.contains("username")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario " + value + " ya existe");
            } else if (key.contains("email")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email " + value + " ya existe");
            } else if (key.contains("name")){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre " + value + " ya existe");
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
            }
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MemberAlreadyJoinedForumException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleMemberAlreadyJoinedForum(MemberAlreadyJoinedForumException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(JoinOwnForumException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleJoinOwnForum(JoinOwnForumException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(AlreadyAssignedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleAlreadyAssigned(AlreadyAssignedException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotAdminException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handleUserNotAdmin(UserNotAdminException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(MemberDoesntBelongForumException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected ResponseEntity<?> handleMemberDoesntBelongToForum(MemberDoesntBelongForumException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidImageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<?> handleInvalidImage(InvalidImageException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handleUserNotOwner(UserNotOwnerException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
    @org.springframework.web.bind.annotation.ExceptionHandler(UserNotMemberException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected ResponseEntity<?> handleUserNotMember(UserNotMemberException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(WrongCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<?> handleWrongCredentials(WrongCredentialsException e) {
        this.logger.info(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

}
