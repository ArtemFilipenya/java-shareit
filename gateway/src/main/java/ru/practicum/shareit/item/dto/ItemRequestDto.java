package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class ItemRequestDto {
    long id;
    @Size(groups = {Create.class, Update.class}, max = 20, message = "max size 20")
    @NotBlank(groups = {Create.class}, message = "Name cannot be empty")
    String name;
    @Size(groups = {Create.class, Update.class}, max = 200, message = "max size 200")
    @NotBlank(groups = {Create.class}, message = "Description cannot be empty")
    String description;
    boolean available;
    Long requestId;
}
