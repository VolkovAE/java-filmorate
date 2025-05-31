package ru.yandex.practicum.filmorate.dto.genre;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.validation.FieldDescription;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenreDto {
    @NotNull(message = "При получении данных о жанре должен быть указан его id.")
    @FieldDescription(value = "Уникальный идентификатор жанра")
    Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Название жанра")
    String name;
}
