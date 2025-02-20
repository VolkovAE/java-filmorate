package ru.yandex.practicum.filmorate.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class Violation {
    //private final Integer codeError;    //код ошибки (сделать через перечисление), на будущее
    private final String fieldName; //имя поля не прошедшее валидацию
    private final String message;   //сообщение о нарушении валидации
}
