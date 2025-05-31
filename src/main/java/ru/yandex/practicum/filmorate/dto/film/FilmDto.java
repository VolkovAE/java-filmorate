package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.converters.DurationToIntegerConverter;
import ru.yandex.practicum.filmorate.converters.InstantToStringConverter;
import ru.yandex.practicum.filmorate.converters.IntegerToDurationConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.validation.FieldDescription;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription(value = "Уникальный идентификатор фильма")
    Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Название фильма")
    String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Описание фильма")
    String description;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @FieldDescription("Дата релиза")
    Instant releaseDate;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonDeserialize(converter = IntegerToDurationConverter.class)
    @JsonSerialize(converter = DurationToIntegerConverter.class)
    @FieldDescription("Продолжительность фильма")
    Duration duration;

    @JsonProperty(value = "genres", access = JsonProperty.Access.READ_ONLY)
    @FieldDescription(value = "Жанры, к которым принадлежит фильм (может быть несколько)")
    Set<GenreDto> genre = new HashSet<>();

    @JsonProperty(value = "mpa", access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Рейтинг фильма согласно Ассоциации кинокомпаний")
    MpaDto rating;
}
