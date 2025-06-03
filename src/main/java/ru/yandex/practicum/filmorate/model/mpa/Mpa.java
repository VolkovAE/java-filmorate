package ru.yandex.practicum.filmorate.model.mpa;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FieldDescription;

@Data
@EqualsAndHashCode(of = {"id", "name"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Mpa {
    @FieldDescription(value = "Уникальный идентификатор рейтинга")
    Long id;

    @FieldDescription("Название рейтинга")
    String name;

    @FieldDescription("Описание рейтинга")
    String description;
}
