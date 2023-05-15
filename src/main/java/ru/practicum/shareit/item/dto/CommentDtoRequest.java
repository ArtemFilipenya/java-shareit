package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDtoRequest {

    private static final int MAX_TEXT_LENGTH = 255;

    @NotBlank
    @Size(max = MAX_TEXT_LENGTH)
    private String text;
}
