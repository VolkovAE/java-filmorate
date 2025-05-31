package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.converters.InstantToStringConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.validation.FieldDescription;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.time.Instant;

@Data
@EqualsAndHashCode(of = {"email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    @NotNull(message = "При обновлении данных о пользователе должен быть указан его id.",
            groups = {Marker.OnUpdate.class, Marker.OnDelete.class})
    @FieldDescription(value = "Уникальный идентификатор пользователя", changeByCopy = false)
    Long id;

    @Email(message = "Email is not valid")
    @FieldDescription("Электронная почта")
    String email;

    //изменил верхнюю границу логина с 16 до 116 символов
    @Pattern(regexp = "^[^\\s]{3,116}$", message = "Длина логина не менее 3 и не более 116 символов. Логин не может содержать пробелы.")
    @FieldDescription("Логин пользователя")
    String login;

    @FieldDescription("Имя для отображения")
    String name;

    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @FieldDescription("Дата рождения")
    Instant birthday;
}
