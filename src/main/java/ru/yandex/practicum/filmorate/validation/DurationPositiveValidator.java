package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class DurationPositiveValidator implements ConstraintValidator<DurationPositive, java.time.Duration> {
    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) return true; //для этого случая, считаю валидацию успешной

        return value.isPositive();
    }
}
