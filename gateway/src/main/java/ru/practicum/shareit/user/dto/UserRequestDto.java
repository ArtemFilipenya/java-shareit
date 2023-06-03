package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserRequestDto {

    private long id;
    @NotNull(groups = {Create.class}, message = "Name cannot be empty")
    private String name;
    @Email(groups = {Update.class, Create.class}, message = "Email cannot be empty")
    @NotNull(groups = {Create.class}, message = "Email cannot be empty")
    private String email;

}
