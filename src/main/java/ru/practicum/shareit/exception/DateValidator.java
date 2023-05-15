package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<EndAfterStartValidation, BookingDtoRequest> {
    @Override
    public void initialize(EndAfterStartValidation constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDtoRequest bookingDto, ConstraintValidatorContext context) {
        return bookingDto.getStart() != null && bookingDto.getEnd() != null &&
                bookingDto.getEnd().isAfter(bookingDto.getStart());
    }
}
