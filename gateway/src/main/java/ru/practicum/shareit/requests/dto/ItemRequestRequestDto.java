package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestRequestDto {
    private long id;
    @Size(max = 256, message = "max length 256")
    @NotBlank(message = "description cannot be empty")
    private String description;
}