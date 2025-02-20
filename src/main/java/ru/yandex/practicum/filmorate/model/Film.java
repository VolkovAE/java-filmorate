package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.converters.DurationToIntegerConverter;
import ru.yandex.practicum.filmorate.converters.InstantToStringConverter;
import ru.yandex.practicum.filmorate.converters.IntegerToDurationConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.validation.DurationPositive;
import ru.yandex.practicum.filmorate.validation.FieldDescription;
import ru.yandex.practicum.filmorate.validation.Marker;
import ru.yandex.practicum.filmorate.validation.ReleaseDate;

import java.time.Duration;
import java.time.Instant;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    @Null(message = "При создании фильма id формируется автоматически.", groups = Marker.OnCreate.class)
    @NotNull(message = "При обновлении данных о фильме должен быть указан его id.", groups = Marker.OnUpdate.class)
    @FieldDescription(value = "Уникальный идентификатор фильма", changeByCopy = false)
    private Long id;    //уникальный идентификатор фильма

    @NotBlank(message = "Название фильма не может быть пустым.", groups = Marker.OnCreate.class)
    @FieldDescription("Название фильма")
    private String name;    //название фильма

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    @FieldDescription("Описание фильма")
    private String description; //описание фильма

    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @NotNull(message = "Нужно указать дату выхода фильма.", groups = Marker.OnCreate.class)
    @ReleaseDate
    @FieldDescription("Дата релиза")
    private Instant releaseDate;    //дата релиза

    @JsonDeserialize(converter = IntegerToDurationConverter.class)
    @JsonSerialize(converter = DurationToIntegerConverter.class)
    @NotNull(message = "Нужно указать длительность фильма в сек.", groups = Marker.OnCreate.class)
    @DurationPositive
    @FieldDescription("Продолжительность фильма")
    private Duration duration;  //продолжительность фильма
}
