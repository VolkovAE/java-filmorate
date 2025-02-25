package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.util.Config;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_TIME_FORMATTER_YYYY_DD_MM;

@Slf4j
public class StringToInstantConverter extends StdConverter<String, Instant> {
    public StringToInstantConverter() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Config.getLevelLog());
    }

    @Override
    public Instant convert(String value) {
        LocalDate localDate;
        try {
            //localDate = LocalDate.parse(value, DATE_TIME_FORMATTER_DD_MM_YYYY);     //использую свой формат даты
            localDate = LocalDate.parse(value, DATE_TIME_FORMATTER_YYYY_DD_MM); //сделал, чтобы проходил тест практикума
        } catch (DateTimeParseException e) {
            throw new ValidationException(e.getMessage(), log);
        }

        return localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
