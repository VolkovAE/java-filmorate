package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserService {
    private final UserStorage userStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    /**
     * Запрос на добавления в друзья.
     *
     * @param userId   - к кому идет запрос на добавление в друзья
     * @param friendId - кто инициировал запрос на добавление в друзья
     */
    public void add(Long userId, Long friendId) {
        //Проверяем, что пользователи существуют и получаем их.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        User friend = userStorage.getById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + friendId + " не найден.", log));

        //Делаем дружескую связь.
        linkFriends(user, friend);

        log.info("Пользователем с id {} сделан запрос в друзья к пользователю с id {}.", friendId, userId);
    }

    /**
     * Удаление из друзей.
     *
     * @param userId   - id пользователя, у кого удалить из друзей пользователя с id friendId
     * @param friendId - id пользователя, у кого удалить из друзей пользователя с id userId
     */
    public void delete(Long userId, Long friendId) {
        //Проверяем, что пользователи существуют и получаем их.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        User friend = userStorage.getById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + friendId + " не найден.", log));

        //Исключаем из друзей друг у друга.
        deleteLinkFriends(user, friend);

        log.info("Пользователи с id {} и {} исключены из друзей друг у друга.", userId, friendId);
    }

    /**
     * Метод возвращает друзей пользователя.
     *
     * @param userId - id пользователя
     * @return - список друзей пользователя
     */
    public Collection<User> find(Long userId) {
        //Проверяем, что пользователь существуют и получаем его.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        log.info("Запрошен список друзей пользователи с id {}.", userId);
        //todo определиться возвращать только подтвержденных друзей или запросы тоже учитывать (сейчас с ними)
        return user.getFriends().keySet().stream()
                .map((id) ->
                        userStorage.getById(id).orElseThrow(
                                () -> new NotFoundException("Пользователь с id = " + id + " не найден.", log)))
                .toList();
    }

    /**
     * Метод возвращает общих друзей двух пользователей.
     *
     * @param userId1 - id пользователя 1
     * @param userId2 - id пользователя 2
     * @return - список общих друзей
     */
    public Collection<User> findCommon(Long userId1, Long userId2) {
        //Проверяем, что пользователи существуют и получаем их.
        User user1 = userStorage.getById(userId1).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId1 + " не найден.", log));

        User user2 = userStorage.getById(userId2).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId2 + " не найден.", log));

        log.info("Запрошен список общих друзей пользователей с id {} и {}.", userId1, userId2);
        //todo определиться возвращать только подтвержденных друзей или запросы тоже учитывать (сейчас с ними)
        return Stream
                .concat(user1.getFriends().keySet().stream(), user2.getFriends().keySet().stream())
                .map((id) -> userStorage.getById(id).orElseThrow(
                        () -> new NotFoundException("Пользователь с id = " + id + " не найден.", log)))
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

    /**
     * Запрос в друзья.
     *
     * @param user   - пользователь, кому сделан запрос
     * @param friend - пользователь, который сделал запрос
     */
    private void linkFriends(User user, User friend) {
        //Добавляем пользователей в друзья друг другу.
        // Проверка, что являются друзьями не нужна, так как использую Map.

        //Пользователю user добавляем друга friend.
        Map<Long, StatusFriendship> friends = user.getFriends();
        if (!friends.containsKey(friend.getId())) {
            // если ранее не было дружеской связи (в любом статусе)
            friends.put(friend.getId(), StatusFriendship.NOT_CONFIRM);  //не подтвердил свою дружбу, только запрос
        }
        user.setFriends(friends);

        //todo переделать с учетом таблицы БД
        //Пользователю friend добавляем друга user.
        friends = friend.getFriends();
        friends.put(user.getId(), StatusFriendship.CONFIRM);    //направив запрос, он готов дружить
        friend.setFriends(friends);
    }

    /**
     * Удаление дружеской связи.
     *
     * @param user   - пользователь, у которого нужно удалить друга friend
     * @param friend - пользователь, у которого нужно удалить друга user
     */
    private void deleteLinkFriends(User user, User friend) {
        //Удаляем пользователей из друзей друг у друга.
        // Проверка, что являются друзьями не нужна, так как использую Map.

        //Пользователю user удаляю друга friend.
        Map<Long, StatusFriendship> friends = user.getFriends();
        friends.remove(friend.getId());
        user.setFriends(friends);

        //Пользователю friend удаляю друга user.
        friends = friend.getFriends();
        friends.remove(user.getId());
        friend.setFriends(friends);
    }
}
