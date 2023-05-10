package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.exception.EndAfterStartValidation;
import ru.practicum.shareit.validation_markers.Create;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@EndAfterStartValidation(groups = {Create.class})
public class BookingDtoRequest {
    private long id;
    @FutureOrPresent(groups = Create.class)
    private LocalDateTime start;
    private LocalDateTime end;
    private long itemId;
}