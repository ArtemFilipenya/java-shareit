package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.exception.EndAfterStartValidation;
import ru.practicum.shareit.exception.OnCreate;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@EndAfterStartValidation(groups = {OnCreate.class})
public class BookingDtoRequest {

    private long id;

    @FutureOrPresent(groups = OnCreate.class)
    private LocalDateTime start;

    private LocalDateTime end;

    private long itemId;
}
