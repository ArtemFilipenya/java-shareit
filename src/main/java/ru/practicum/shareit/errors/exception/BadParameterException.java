package ru.practicum.shareit.errors.exception;

public class BadParameterException extends RuntimeException {
    public BadParameterException(String parameter) {

        super(parameter);
    }
}