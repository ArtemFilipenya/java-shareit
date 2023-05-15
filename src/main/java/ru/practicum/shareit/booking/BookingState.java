package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BookingStateException;

public enum BookingState {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static BookingState isStateExist(String state) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equals(state)) {
                return bookingState;
            }
        }
        throw new BookingStateException("Unknown state: " + state);
    }
}
