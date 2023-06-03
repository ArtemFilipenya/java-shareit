package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SimpleItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getItems()
        );
    }

    public static ItemRequest fromDto(ItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(dto.getCreated());

        return itemRequest;
    }

    public static SimpleItemRequestDto toSimpleDto(ItemRequest itemRequest) {
        SimpleItemRequestDto dto = new SimpleItemRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }

    public static ItemRequest fromSimpleDto(SimpleItemRequestDto dto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(dto.getId());
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setCreated(dto.getCreated());
        return itemRequest;
    }
}
