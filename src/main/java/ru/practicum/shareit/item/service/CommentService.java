package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.CommentDtoResponse;

public interface CommentService {

    CommentDtoResponse create(long userId, long itemId, CommentDtoRequest commentDtoRequest);
}
