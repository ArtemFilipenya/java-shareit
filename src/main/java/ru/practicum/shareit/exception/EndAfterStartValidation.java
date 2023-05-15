package ru.practicum.shareit.exception;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target(ElementType.TYPE_USE)
@Retention(RUNTIME)
@Constraint(validatedBy = DateValidator.class)
public @interface EndAfterStartValidation {
    String message() default "Time validation error";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
