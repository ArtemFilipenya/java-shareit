package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDtoResponse create(long userId, ItemDtoRequest itemDtoRequest);

    ItemDtoResponse update(long userId, long itemId, ItemDtoRequest itemDtoRequest);

    ItemDtoResponse getById(long userId, long itemId);

    List<ItemDtoResponse> findAll(long userId);

    void deleteById(long userId, long itemId);

    List<ItemDtoResponse> search(String text);
}