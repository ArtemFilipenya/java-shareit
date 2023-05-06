package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

public interface CommentService {

    CommentDto create(long userId, long itemId, CommentDto commentDto);
}
