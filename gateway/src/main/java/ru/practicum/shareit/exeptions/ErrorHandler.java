package ru.practicum.shareit.exeptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("IllegalArgumentException occurred: {}", e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("Unknown state: UNSUPPORTED_STATUS"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(Throwable t) {
        log.debug("Unhandled exception occurred: {}", t.getMessage(), t);
        return new ResponseEntity<>(new ErrorResponse("Internal Server Error"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.debug("Validation error occurred: {}", e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse("Validation error");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}
