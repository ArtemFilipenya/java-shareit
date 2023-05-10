package ru.practicum.shareit.exception.model;

public class ErrorResponse {

    private final String error;
    private final String description;

    public ErrorResponse(String error, String description) {
        this.error = error;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getError() {
        return error;
    }
}

