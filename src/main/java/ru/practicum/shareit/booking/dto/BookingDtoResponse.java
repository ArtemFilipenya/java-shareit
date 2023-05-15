package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoResponse {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private ItemForResponse item;

    private UserForResponse booker;

    private BookingStatus status;

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class ItemForResponse {
        private final long id;
        private final String name;
    }

    @Data
    @Builder
    @RequiredArgsConstructor
    public static class UserForResponse {
        private final long id;
        private final String name;
    }
}
