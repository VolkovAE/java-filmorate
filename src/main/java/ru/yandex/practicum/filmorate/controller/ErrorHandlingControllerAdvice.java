package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.util.Config;
import ru.yandex.practicum.filmorate.validation.ValidationErrorResponse;
import ru.yandex.practicum.filmorate.validation.Violation;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ErrorHandlingControllerAdvice {
    public ErrorHandlingControllerAdvice() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Config.getLevelLog());
    }

    /**
     * Отлавливаю исключения типа ConstraintViolationException, ошибка в параметрах запроса, параметрах пути.
     *
     * @param e - исключение типа ConstraintViolationException
     * @return - возвращаем объект класса ValidationErrorResponse со списком нарушений
     */

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onConstraintValidationException(ConstraintViolationException e) {
        final List<Violation> violations = e.getConstraintViolations().stream()
                .map(
                        violation -> new Violation(
                                violation.getPropertyPath().toString(),
                                violation.getMessage()
                        )
                )
                .collect(Collectors.toList());

        log.error("Ошибка валидации: {}", violations);

        return new ValidationErrorResponse(violations);
    }

    /**
     * Отлавливаю исключения типа MethodArgumentNotValidException, объект не прошел валидацию.
     *
     * @param e - исключение типа ConstraintViolationException
     * @return - возвращаем объект класса ValidationErrorResponse со списком нарушений
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        final List<Violation> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> new Violation(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        log.error("Объект не прошел валидацию: {}", violations);

        return new ValidationErrorResponse(violations);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onValidationException(ValidationException e) {
        Violation violation = new Violation("-", e.getMessage());
        List<Violation> violationList = List.of(violation);

        return new ValidationErrorResponse(violationList);
    }

    @ExceptionHandler(NotFoundException.class)
    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseStatus(HttpStatus.NOT_FOUND)   //заменил как более подходящее (по результату теста практикума)
    @ResponseBody
    public ValidationErrorResponse onNotFoundException(NotFoundException e) {
        Violation violation = new Violation("-", e.getMessage());
        List<Violation> violationList = List.of(violation);

        return new ValidationErrorResponse(violationList);
    }

    @ExceptionHandler(DuplicatedDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ValidationErrorResponse onDuplicatedDataException(DuplicatedDataException e) {
        Violation violation = new Violation("-", e.getMessage());
        List<Violation> violationList = List.of(violation);

        return new ValidationErrorResponse(violationList);
    }
}
