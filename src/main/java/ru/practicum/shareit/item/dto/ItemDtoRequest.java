package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation_markers.Create;
import ru.practicum.shareit.validation_markers.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class ItemDtoRequest {
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_DESCRIPTION_LENGTH = 255;
    private long id;

    @NotBlank(groups = Create.class)
    @Size(max = MAX_NAME_LENGTH, groups = {Create.class, Update.class})
    private String name;
    @NotNull(groups = Create.class)
    private Boolean available;
    @NotBlank(groups = Create.class)
    @Size(max = MAX_DESCRIPTION_LENGTH, groups = {Create.class, Update.class})
    private String description;
}