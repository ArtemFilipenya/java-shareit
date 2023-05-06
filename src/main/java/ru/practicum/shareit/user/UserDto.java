package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.exception.OnCreate;
import ru.practicum.shareit.exception.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private long id;

    @NotBlank(groups = {OnCreate.class})
    @Size(max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotEmpty(groups = {OnCreate.class})
    @Email(groups = {OnCreate.class, OnUpdate.class})
    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String email;
}