package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_BIRTH_CINEMA;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDate, Instant> {
    @Override
    public boolean isValid(Instant value, ConstraintValidatorContext context) {
        if (value == null) return true; //для этого случая, считаю валидацию успешной

        return !value.isBefore(DATE_BIRTH_CINEMA);
    }
}
