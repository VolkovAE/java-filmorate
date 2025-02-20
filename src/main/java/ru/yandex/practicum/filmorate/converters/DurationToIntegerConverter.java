package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.time.Duration;

public class DurationToIntegerConverter extends StdConverter<Duration, Integer> {
    @Override
    public Integer convert(Duration value) {
        return (int) value.toSeconds();
    }
}
