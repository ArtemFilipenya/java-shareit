package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.exception.model.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final NotFoundException e) {
        log.debug("404: {}", e.getMessage());
        return new ErrorResponse("NotFoundException:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleOtherExceptions(final Throwable throwable) {
        log.debug("500: {}", throwable.getMessage());
        return new ErrorResponse("Error:", throwable.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestExceptions(final BadRequestException e) {
        log.debug("400: {}", e.getMessage());
        return new ErrorResponse("BadRequestException:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {
        log.debug("400: {}", e.getMessage());
        return new ErrorResponse("MethodArgumentNotValidException:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExist(final AlreadyExistException e) {
        log.debug("409: {}", e.getMessage());
        return new ErrorResponse("AlreadyExistException:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleStateException(final BookingStateException e) {
        log.debug("400: {}", e.getMessage());
        return new ErrorResponse(e.getMessage(), "State not found.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExist(final EmailConflictException e) {
        log.debug("409: {}", e.getMessage());
        return new ErrorResponse("EmailConflictException:", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessException(final AccessException e) {
        log.debug("403: {}", e.getMessage());
        return new ErrorResponse("AccessException", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExceptions(final InternalError e) {
        log.debug("500: {}", e.getMessage());
        return new ErrorResponse("InternalError:", e.getMessage());
    }
}