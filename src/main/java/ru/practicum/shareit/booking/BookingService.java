package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse create(BookingDtoRequest bookingDto, long userId);

    BookingDtoResponse approve(long bookingId, long userId, boolean approved);

    BookingDtoResponse findById(long bookingId, long userId);

    List<BookingDtoResponse> findByBooker(long userId, String state);

    List<BookingDtoResponse> findByOwner(long userId, String state);
}

