package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static ru.yandex.practicum.filmorate.util.Constants.DATE_TIME_FORMATTER_DD_MM_YYYY;

public class InstantToStringConverter extends StdConverter<Instant, String> {
    @Override
    public String convert(Instant instant) {
        LocalDate localDate = LocalDate.ofInstant(instant, ZoneOffset.UTC);
        return localDate.format(DATE_TIME_FORMATTER_DD_MM_YYYY);
    }
}
