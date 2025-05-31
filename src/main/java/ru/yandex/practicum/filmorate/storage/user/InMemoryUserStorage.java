package ru.yandex.practicum.filmorate.storage.user;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.util.FilmorateUtils;
import ru.yandex.practicum.filmorate.util.Reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@Qualifier("InMemoryUserStorage")
@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(InMemoryUserStorage.class);

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
    public Collection<User> getUsersLikesByFilm(Film film) {
        log.info("Получен список пользователей поставивших лайк фильму с id {}.", film.getId());

        return film.getLikes();
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

    @Override
    public void linkFriends(User user, User friend) {
        //Добавляем пользователей в друзья друг другу.
        // Проверка, что являются друзьями не нужна, так как использую Map.

        //Пользователю user добавляем друга friend.
        Map<User, StatusFriendship> friends = getFriendsByUser(user);
        if (!friends.containsKey(friend)) {
            // если ранее не было дружеской связи (в любом статусе)
            friends.put(friend, StatusFriendship.NOT_CONFIRM);  //не подтвердил свою дружбу, только запрос
        }
        user.setFriends(friends);

        //Пользователю friend добавляем друга user.
        friends = getFriendsByUser(friend);
        friends.put(user, StatusFriendship.CONFIRM);    //направив запрос, он готов дружить
        friend.setFriends(friends);

        log.info("Пользователем с id {} сделан запрос в друзья к пользователю с id {}.", friend.getId(), user.getId());
    }

    /**
     * Удаление дружеской связи.
     *
     * @param user   - пользователь, у которого нужно удалить друга friend
     * @param friend - пользователь, у которого нужно удалить друга user
     */
    @Override
    public void deleteLinkFriends(User user, User friend) {
        //Удаляем пользователей из друзей друг у друга.
        // Проверка, что являются друзьями не нужна, так как использую Map.

        //Пользователю user удаляю друга friend.
        Map<User, StatusFriendship> friends = getFriendsByUser(user);
        friends.remove(friend);
        user.setFriends(friends);

        //Пользователю friend удаляю друга user.
        friends = getFriendsByUser(friend);
        friends.remove(user);
        friend.setFriends(friends);

        log.info("Пользователи с id {} и {} исключены из друзей друг у друга.", user.getId(), friend.getId());
    }

    @Override
    public Collection<User> findCommon(User user1, User user2) {
        log.info("Запрошен список общих друзей пользователей с id {} и {}.", user1.getId(), user2.getId());
        //todo определиться возвращать только подтвержденных друзей или запросы тоже учитывать (сейчас с ними)
        return Stream
                .concat(user1.getFriends().keySet().stream(), user2.getFriends().keySet().stream())
                .collect(
                        Collectors.groupingBy(
                                Function.identity(),
                                Collectors.counting()))
                .entrySet() //результат первого потока, после группировки: друг - количество повторений
                .stream()
                .filter(m -> m.getValue() > 1)  //общий друг
                .map(Map.Entry::getKey) //получаю пользователя
                .toList();
    }

    @Override
    public Map<User, StatusFriendship> getFriendsByUser(User user) {
        return user.getFriends();
    }
}
