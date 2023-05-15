package ru.practicum.shareit.item;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public static ItemDtoResponse toItemDtoResponse(Item item) {
        return ItemDtoResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(BookingDtoResponse.UserForResponse.builder()
                        .id(item.getOwner().getId())
                        .name(item.getOwner().getName())
                        .build())
                .lastBooking(null)
                .nextBooking(null)
                .comments(new ArrayList<>())
                .build();
    }

    public static Item fromItemDtoRequest(ItemDtoRequest itemDtoRequest, User owner) {
        return Item.builder()
                .id(itemDtoRequest.getId())
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .owner(owner)
                .build();
    }

    public static List<ItemDtoResponse> toItemDtoList(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDtoResponse).collect(Collectors.toList());
    }
}