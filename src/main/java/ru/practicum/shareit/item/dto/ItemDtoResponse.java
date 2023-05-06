package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoResponse.UserForResponse;
import ru.practicum.shareit.booking.dto.ShortBookingDto;

import java.util.List;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoResponse {

    private long id;

    private String name;

    private String description;

    private Boolean available;

    private UserForResponse owner;

    private ShortBookingDto lastBooking;

    private ShortBookingDto nextBooking;

    private List<CommentDto> comments;
}
