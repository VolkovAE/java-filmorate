package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
import ru.yandex.practicum.filmorate.validation.DurationPositive;
import ru.yandex.practicum.filmorate.validation.FieldDescription;
import ru.yandex.practicum.filmorate.validation.Marker;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateFilmRequest {
    @NotNull(message = "При обновлении данных о фильме должен быть указан его id.",
            groups = {Marker.OnUpdate.class, Marker.OnDelete.class})
    @FieldDescription(value = "Уникальный идентификатор фильма", changeByCopy = false)
    Long id;

    @FieldDescription("Название фильма")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    @FieldDescription("Описание фильма")
    String description;

    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @ReleaseDate
    @FieldDescription("Дата релиза")
    Instant releaseDate;

    @JsonDeserialize(converter = IntegerToDurationConverter.class)
    @JsonSerialize(converter = DurationToIntegerConverter.class)
    @DurationPositive
    @FieldDescription("Продолжительность фильма")
    Duration duration;

    @JsonProperty(value = "genres")
    @FieldDescription(value = "Жанры, к которым принадлежит фильм (может быть несколько)")
    //Set<GenreDto> genre = new HashSet<>();
    List<GenreDto> genre = new ArrayList<>();

    @JsonProperty(value = "mpa")
    @FieldDescription("Рейтинг фильма согласно Ассоциации кинокомпаний")
    MpaDto rating;
}
