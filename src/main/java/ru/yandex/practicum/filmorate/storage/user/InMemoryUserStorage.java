package ru.yandex.practicum.filmorate.storage.user;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.FilmorateUtils;
import ru.yandex.practicum.filmorate.util.Reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
//@Slf4j(topic = "Пользователи")
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(InMemoryUserStorage.class);

    @Override
    public User add(User user) {
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

        log.info("Добавлен новый пользователь {}.", user);

        return user;
    }

    @Override
    public User update(User newUser) {
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

            log.info("Обновлены данные пользователя {}.", oldUser);

            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден.", log);
    }

    @Override
    public User delete(User delUser) {
        if (users.containsKey(delUser.getId())) {
            // удаляем фильм из памяти приложения
            User removeUser = users.remove(delUser.getId());

            log.info("Удален пользователь {}.", removeUser);

            return removeUser;
        }
        throw new NotFoundException("Фильм с id = " + delUser.getId() + " не найден.", log);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получен список пользователей.");

        return users.values();
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private boolean isEmailFree(final String email) {
        return users.values().stream().noneMatch(curUser -> curUser.getEmail().equals(email));
    }

    private boolean isLoginFree(final String login) {
        return users.values().stream().noneMatch(curUser -> curUser.getLogin().equals(login));
    }
}
