package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
//@Slf4j(topic = "Сервис друзей")
public class UserService {
    private final UserStorage userStorage;

    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void add(Long userId, Long friendId) {
        //Проверяем, что пользователи существуют и получаем их.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        User friend = userStorage.getById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + friendId + " не найден.", log));

        //Делаем друзьями.
        linkFriends(user, friend);

        log.info("Пользователи с id {} и {} добавлены в друзья друг другу.", userId, friendId);
    }

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

    public Collection<User> find(Long userId) {
        //Проверяем, что пользователь существуют и получаем его.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        log.info("Запрошен список друзей пользователи с id {}.", userId);

        return user.getFriends().stream()
                .map((id) ->
                        userStorage.getById(id).orElseThrow(
                                () -> new NotFoundException("Пользователь с id = " + id + " не найден.", log)))
                .toList();
    }

    public Collection<User> findCommon(Long userId1, Long userId2) {
        //Проверяем, что пользователи существуют и получаем их.
        User user1 = userStorage.getById(userId1).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId1 + " не найден.", log));

        User user2 = userStorage.getById(userId2).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId2 + " не найден.", log));

        log.info("Запрошен список общих друзей пользователей с id {} и {}.", userId1, userId2);

        return Stream
                .concat(user1.getFriends().stream(), user2.getFriends().stream())
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

    private void linkFriends(User user1, User user2) {
        //Добавляем пользователей в друзья друг другу.
        // Проверка, что являются друзьями не нужна, так как использую Set.

        //Пользователю 1 добавляем друга (пользователь 2).
        Set<Long> friends = user1.getFriends();
        friends.add(user2.getId());
        user1.setFriends(friends);

        //Пользователю 2 добавляем друга (пользователь 1).
        friends = user2.getFriends();
        friends.add(user1.getId());
        user2.setFriends(friends);
    }

    private void deleteLinkFriends(User user1, User user2) {
        //Удаляем пользователей из друзей друг у друга.
        // Проверка, что являются друзьями не нужна, так как использую Set.

        //Пользователю 1 удаляю друга (пользователь 2).
        Set<Long> friends = user1.getFriends();
        friends.remove(user2.getId());
        user1.setFriends(friends);

        //Пользователю 2 удаляю друга (пользователь 1).
        friends = user2.getFriends();
        friends.remove(user1.getId());
        user2.setFriends(friends);
    }
}
