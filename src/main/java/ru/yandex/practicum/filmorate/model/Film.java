package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
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
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film implements Comparable<Film> {
    @Null(message = "При создании фильма id формируется автоматически.", groups = Marker.OnCreate.class)
    @NotNull(message = "При обновлении данных о фильме должен быть указан его id.",
            groups = {Marker.OnUpdate.class, Marker.OnDelete.class})
    @FieldDescription(value = "Уникальный идентификатор фильма", changeByCopy = false)
    Long id;

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

    @JsonIgnore
    @FieldDescription(value = "id пользователей, которые поставили фильму лайк", changeByCopy = false)
    Set<Long> likes = new HashSet<>();

    @Override
    public int compareTo(Film o) {
        return Long.compare(this.likes.size(), o.likes.size());
    }
}
