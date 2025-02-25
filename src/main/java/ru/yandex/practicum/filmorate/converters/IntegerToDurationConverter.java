package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Duration;

public class IntegerToDurationConverter extends StdConverter<Integer, Duration> {
    @Override
    public Duration convert(Integer value) {
        return Duration.ofSeconds(value);
    }
}
