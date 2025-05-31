package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.converters.InstantToStringConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.validation.FieldDescription;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription(value = "Уникальный идентификатор пользователя")
    Long id;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Электронная почта")
    String email;

    //изменил верхнюю границу логина с 16 до 116 символов
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Логин пользователя")
    String login;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @FieldDescription("Имя для отображения")
    String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @FieldDescription("Дата рождения")
    Instant birthday;
}
