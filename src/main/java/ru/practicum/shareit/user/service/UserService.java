package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserDto user);

    List<UserDto> findAll();

    UserDto get(Long id);

    void delete(Long id);

    UserDto update(UserDto user, Long id);
}