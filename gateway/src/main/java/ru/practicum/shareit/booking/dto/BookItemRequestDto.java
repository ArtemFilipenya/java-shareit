package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validator.EndTimeAfterStartTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EndTimeAfterStartTime
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent(message = "FutureOrPresent")
    @NotNull(message = "start cannot be null")
    private LocalDateTime start;
    @Future(message = "Future")
    @NotNull(message = "end cannot be null")
    private LocalDateTime end;
}
