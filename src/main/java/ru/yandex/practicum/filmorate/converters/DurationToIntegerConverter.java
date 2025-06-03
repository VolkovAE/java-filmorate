package ru.yandex.practicum.filmorate.converters;

import com.fasterxml.jackson.databind.util.StdConverter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class DurationToIntegerConverter extends StdConverter<Duration, Integer> {
    @Override
    public Integer convert(Duration value) {
        return (int) value.toSeconds();
    }
}
