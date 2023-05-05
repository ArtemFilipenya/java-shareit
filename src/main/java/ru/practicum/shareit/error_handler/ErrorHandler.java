package ru.practicum.shareit.error_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.DeniedAccessException;
import ru.practicum.shareit.item.exception.OwnerNotFoundException;
import ru.practicum.shareit.user.exception.EmailConflictException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handle(EmailConflictException e) {
        log.debug("EmailConflictException: {}", e.getMessage());
        return new ErrorResponse("Email is already in use by another user", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(UserNotFoundException e) {
        log.debug("UserNotFoundException: {}", e.getMessage());
        return new ErrorResponse("Invalid id", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handle(OwnerNotFoundException e) {
        log.debug("OwnerNotFoundException: {}", e.getMessage());
        return new ErrorResponse("Item owner not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handle(DeniedAccessException e) {
        log.debug("DeniedAccessException: {}", e.getMessage());
        return new ErrorResponse("Access denied", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handle(MethodArgumentNotValidException e) {
        log.debug("MethodArgumentNotValidException: {}", e.getMessage());
        return new ErrorResponse("Validation error (400):", e.getMessage());
    }
}
