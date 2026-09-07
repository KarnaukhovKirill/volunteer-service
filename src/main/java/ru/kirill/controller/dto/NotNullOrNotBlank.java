package ru.kirill.controller.dto;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(value = ElementType.FIELD)
@Retention(value = RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NotNullOrNotBlankValidator.class)
public @interface NotNullOrNotBlank {
    String message() default "Поле должно быть null или не пустым.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
