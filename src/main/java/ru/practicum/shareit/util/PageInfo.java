package ru.practicum.shareit.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.errors.exception.BadParameterException;

public class PageInfo {

    public static PageRequest createPageRequest(Integer from, Integer size, Sort sort) {
        if (size == null || from == null) {
            return null;
        }
        if (size <= 0 || from < 0) {
            throw new BadParameterException("BadParameterException");
        }
        return PageRequest.of(from / size, size, sort);
    }

}