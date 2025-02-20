package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.Config;
import ru.yandex.practicum.filmorate.util.FilmorateUtils;
import ru.yandex.practicum.filmorate.util.Reflection;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/users")
@Slf4j(topic = "Пользователи")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    public UserController() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Config.getLevelLog());
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Обработан запрос на получение списка пользователей.");

        return users.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public User create(@RequestBody @Valid User user) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        //Если пользователь с указанным адресом электронной почты уже был добавлен ранее,
        // то генерируется исключение DuplicatedDataException с описанием: "Этот имейл уже используется".
        if (!isEmailFree(user.getEmail()))
            throw new DuplicatedDataException("Нельзя создать пользователя по причине: " +
                    "этот имейл уже используется.", log);

        //Если пользователь с указанным логином уже был добавлен ранее,
        // то генерируется исключение DuplicatedDataException с описанием: "Этот логин уже используется".
        if (!isLoginFree(user.getLogin()))
            throw new DuplicatedDataException("Нельзя создать пользователя по причине: " +
                    "этот логин уже используется.", log);

        // формируем дополнительные данные
        user.setId(FilmorateUtils.getNextId(users));
        if (StringUtils.isBlank(user.getName()))
            user.setName(user.getLogin());  //имя для отображения может быть пустым — в таком случае будет использован логин
        // сохраняем новый фильм в памяти приложения
        users.put(user.getId(), user);

        log.info("Обработан запрос на добавление нового пользователя {}.", user);

        return user;
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public User update(@RequestBody @Valid User newUser) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (!StringUtils.isBlank(newUser.getEmail())) {
                if (!newUser.getEmail().equals(oldUser.getEmail())) {
                    //Если при обновлении данных пользователя, указан новый адрес электронной почты и
                    // в приложении уже есть пользователь с таким адресом,
                    // то должно генерироваться исключение DuplicatedDataException с описанием: "Этот имейл уже используется".
                    if (!isEmailFree(newUser.getEmail()))
                        throw new DuplicatedDataException("Нельзя обновить пользователя по причине: " +
                                "нельзя использовать имейл, который уже используется.", log);
                }
            }

            if (!StringUtils.isBlank(newUser.getLogin())) {
                if (!newUser.getLogin().equals(oldUser.getLogin())) {
                    //Если при обновлении данных пользователя, указан новый логин и
                    // в приложении уже есть пользователь с таким логином,
                    // то должно генерироваться исключение DuplicatedDataException с описанием: "Этот логин уже используется".
                    if (!isLoginFree(newUser.getLogin()))
                        throw new DuplicatedDataException("Нельзя обновить пользователя по причине: " +
                                "нельзя использовать логин, который уже используется.", log);
                }
            }

            if (StringUtils.isBlank(newUser.getName()))
                newUser.setName(newUser.getLogin());    //имя для отображения может быть пустым — в таком случае будет использован логин

            // обновляем содержимое
            BeanUtils.copyProperties(newUser, oldUser, Reflection.getIgnoreProperties(newUser));

            log.info("Обработан запрос на изменение данных пользователя {}.", oldUser);

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден.", log);
    }

    private boolean isEmailFree(final String email) {
        return users.values().stream().noneMatch(curUser -> curUser.getEmail().equals(email));
    }

    private boolean isLoginFree(final String login) {
        return users.values().stream().noneMatch(curUser -> curUser.getLogin().equals(login));
    }
}
