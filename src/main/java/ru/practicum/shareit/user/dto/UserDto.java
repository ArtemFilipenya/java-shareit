package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotEmpty(groups = {Create.class}, message = "Cannot be empty")
    private String name;
    @Email(groups = {Update.class, Create.class}, message = "Cannot be empty")
    @NotEmpty(groups = {Create.class}, message = "АCannot be empty")
    private String email;
}