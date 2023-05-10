package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 255;

    private long id;
    @NotBlank(groups = {Create.class})
    @Size(max = MAX_NAME_LENGTH, groups = {Create.class, Update.class})
    private String name;
    @NotEmpty(groups = {Create.class})
    @Email(groups = {Create.class, Update.class})
    @Size(max = MAX_EMAIL_LENGTH, groups = {Create.class, Update.class})
    private String email;
}