package ru.yandex.practicum.filmorate.util;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

public final class Constants {
    public static final Instant DATE_BIRTH_CINEMA = Instant.ofEpochSecond(-2335564800L);
    //public static final DateTimeFormatter DATE_TIME_FORMATTER_DD_MM_YYYY = DateTimeFormatter.ofPattern("dd.MM.yyyy"); хотел использовать свой формат, но практикум в тесте зашил формат даты ISO 8601
    public static final DateTimeFormatter DATE_TIME_FORMATTER_YYYY_DD_MM = DateTimeFormatter.ofPattern("yyyy-MM-dd");   //сделал, чтобы проходил тест практикума

    private Constants() {
    }
}
