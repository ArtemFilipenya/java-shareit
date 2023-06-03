package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {

    long id;
    @Size(max = 256, message = "max length 256")
    @NotBlank(message = "text cannot be empty")
    String text;
}