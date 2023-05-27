package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

public class ItemRequestMapper {
    public static ItemRequest convertDtoToModel(ItemRequestDto itemRequestDto, UserDto userDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .requester(UserMapper.convertDtoToModel(userDto))
                .build();
    }

    public static ItemRequest convertDtoToModel(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public static ItemRequestDto convertModelToDto(ItemRequest itemRequest, List<ItemDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(items != null ? items : List.of())
                .build();
    }

    public static ItemRequestDto convertModelToDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requesterId(itemRequest.getRequester().getId())
                .created(itemRequest.getCreated())
                .items(List.of())
                .build();
    }
}