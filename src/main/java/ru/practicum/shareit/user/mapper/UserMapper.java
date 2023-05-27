package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserShortDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User convertDtoToModel(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail()).build();
    }

    public static UserShortDto convertModelToShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName()).build();
    }

    public static UserDto convertModelToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail()).build();
    }

}