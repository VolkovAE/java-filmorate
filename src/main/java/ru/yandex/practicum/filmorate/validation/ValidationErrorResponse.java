package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@RequiredArgsConstructor
public class ValidationErrorResponse {
    private final List<Violation> violations;   //список нарушений
}
