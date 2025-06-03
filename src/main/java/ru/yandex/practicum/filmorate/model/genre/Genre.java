package ru.yandex.practicum.filmorate.model.genre;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FieldDescription;

@Data
@EqualsAndHashCode(of = {"id", "name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre {
    @FieldDescription(value = "Уникальный идентификатор жанра")
    Long id;

    @FieldDescription("Название жанра")
    String name;

    @FieldDescription("Описание жанра")
    String description;
}
