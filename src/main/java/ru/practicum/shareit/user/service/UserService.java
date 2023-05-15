package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    User getById(long id);

    List<User> findAll();

    UserDto update(long id, UserDto userDto);

    void deleteById(long id);
}
