package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.converters.InstantToStringConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.validation.FieldDescription;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.time.Instant;

/**
 * User.
 */
@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    @Null(message = "При создании пользователя id формируется автоматически.", groups = Marker.OnCreate.class)
    @NotNull(message = "При обновлении данных о пользователе должен быть указан его id.", groups = Marker.OnUpdate.class)
    @FieldDescription(value = "Уникальный идентификатор пользователя", changeByCopy = false)
    private Long id;    //уникальный идентификатор пользователя

    @Email(message = "Email is not valid")
    @NotBlank(message = "Электронная почта не может быть пустой.", groups = Marker.OnCreate.class)
    @FieldDescription("Электронная почта")
    private String email;   //электронная почта

    @Pattern(regexp = "^[^\\s]{3,16}$", message = "Длина логина не менее 3 и не более 16 символов. Логин не может содержать пробелы.")
    @NotBlank(message = "Логин не может быть пустым.", groups = Marker.OnCreate.class)
    @FieldDescription("Логин пользователя")
    private String login;   //логин пользователя

    @FieldDescription("Имя для отображения")
    private String name;    //имя для отображения

    @JsonDeserialize(converter = StringToInstantConverter.class)
    @JsonSerialize(converter = InstantToStringConverter.class)
    @NotNull(message = "Нужно указать дату рождения.", groups = Marker.OnCreate.class)
    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    @FieldDescription("Дата рождения")
    private Instant birthday;   //дата рождения
}
