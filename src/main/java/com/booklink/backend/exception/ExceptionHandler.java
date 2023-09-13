package com.booklink.backend.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected ResponseEntity<?> handleDuplicateKey(DataIntegrityViolationException e) {
        this.logger.info(e.getMessage());
        Matcher matcher = Pattern.compile("Detail: Key \\((.*)\\)").matcher(e.getMostSpecificCause().getMessage());
        if (matcher.find()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Valor duplicado: \"" + matcher.group(1).replaceAll("[)(]", " ") + "\"");
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
}
