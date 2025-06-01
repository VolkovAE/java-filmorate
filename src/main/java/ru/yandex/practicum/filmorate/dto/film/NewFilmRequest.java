package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
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
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewFilmRequest {
    @NotBlank(message = "Название фильма не может быть пустым.", groups = Marker.OnCreate.class)
    @FieldDescription("Название фильма")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    @FieldDescription("Описание фильма")
    String description;

    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @NotNull(message = "Нужно указать дату выхода фильма.", groups = Marker.OnCreate.class)
    @ReleaseDate
    @FieldDescription("Дата релиза")
    Instant releaseDate;

    @JsonDeserialize(converter = IntegerToDurationConverter.class)
    @JsonSerialize(converter = DurationToIntegerConverter.class)
    @NotNull(message = "Нужно указать длительность фильма в сек.", groups = Marker.OnCreate.class)
    @DurationPositive
    @FieldDescription("Продолжительность фильма")
    Duration duration;

    @JsonProperty(value = "genres")
    //@NotNull(message = "Нужно указать жанры, к которым принадлежит фильм.", groups = Marker.OnCreate.class)
    @FieldDescription(value = "Жанры, к которым принадлежит фильм (может быть несколько)")
    //Set<GenreDto> genre = new HashSet<>();
    List<GenreDto> genre = new ArrayList<>();

    @JsonProperty(value = "mpa")
    @NotNull(message = "Нужно указать рейтинг фильма.", groups = Marker.OnCreate.class)
    @FieldDescription("Рейтинг фильма согласно Ассоциации кинокомпаний")
    MpaDto rating;
}
