package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndTimeAfterStartTimeValidator implements ConstraintValidator<EndTimeAfterStartTime, BookItemRequestDto> {

    @Override
    public void initialize(EndTimeAfterStartTime constraintAnnotation) {
        // Не требуется
    }

    @Override
    public boolean isValid(BookItemRequestDto requestDto, ConstraintValidatorContext context) {
        return requestDto.getStart() == null || requestDto.getEnd() == null || !requestDto.getStart().isAfter(requestDto.getEnd());
    }
}

