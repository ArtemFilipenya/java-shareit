package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingAllDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemAllDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collections;
import java.util.List;

public class ItemMapper {

    public static Item convertDtoToModel(ItemAllDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDto convertModelToDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public static Item convertDtoToModel(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    //    public static ItemAllDto toItemAllFieldsDto(Item item, BookingAllDto lastBooking, BookingAllDto nextBooking,
//                                                List<CommentDto> comments) {
//        return ItemAllDto.builder()
//                .id(item.getId())
//                .name(item.getName())
//                .description(item.getDescription())
//                .available(item.getAvailable())
//                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
//                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
//                .lastBooking(lastBooking != null ? new BookingDto(lastBooking.getId(), lastBooking.getBooker().getId()) : null)
//                .nextBooking(nextBooking != null ? new BookingDto(nextBooking.getId(), nextBooking.getBooker().getId()) : null)
//                .comments(comments != null ? comments : List.of())
//                .build();
//    }
    public static ItemAllDto convertToItemWithAllFields(Item item, BookingAllDto lastBooking, BookingAllDto nextBooking,
                                                        List<CommentDto> comments) {
        BookingDto lastBookingDto = lastBooking != null ? new BookingDto(lastBooking.getId(),
                lastBooking.getBooker().getId()) : null;
        BookingDto nextBookingDto = nextBooking != null ? new BookingDto(nextBooking.getId(),
                nextBooking.getBooker().getId()) : null;
        List<CommentDto> commentDtos = comments != null ? comments : Collections.emptyList();

        return ItemAllDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(lastBookingDto)
                .nextBooking(nextBookingDto)
                .comments(commentDtos)
                .build();
    }

    public static ItemShortDto convertModelToShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}