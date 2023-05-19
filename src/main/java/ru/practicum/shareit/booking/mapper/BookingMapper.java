package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.enums.Status;
import ru.practicum.shareit.booking.dto.BookingControllerDto;
import ru.practicum.shareit.booking.dto.BookingAllDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

public class BookingMapper {

    public static BookingAllDto convertModelToDto(Booking booking) {
        return BookingAllDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .item(booking.getItem() != null ? ItemMapper.convertModelToShortDto(booking.getItem()) : null)
                .end(booking.getEnd())
                .status(Status.valueOf(booking.getStatus().name()))
                .booker(booking.getBooker() != null ? UserMapper.convertModelToShortDto(booking.getBooker()) : null)
                .build();
    }

    public static Booking convertDtoToModel(BookingControllerDto bookingDto) {
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();
    }

}