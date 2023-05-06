package ru.practicum.shareit.user;

import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(long id, UserDto userDto);

    User getById(long id);

    List<User> getAll();

    void deleteById(long id);

    void deleteAll();
}
