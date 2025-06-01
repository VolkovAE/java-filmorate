package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto add(NewUserRequest userRequest) {
        User user = UserMapper.mapToUser(userRequest);

        user = userStorage.add(user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest userRequest) {
        User user = UserMapper.mapToUser(userRequest);

        user = userStorage.update(user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto delete(UpdateUserRequest userRequest) {
        User removeUser = userStorage.getById(userRequest.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userRequest.getId() + " не найден.", log));

        removeUser = userStorage.delete(removeUser);

        return UserMapper.mapToUserDto(removeUser);
    }

    public UserDto getById(Long userId) {
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        return UserMapper.mapToUserDto(user);
    }

    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Запрос на добавления в друзья.
     *
     * @param userId   - к кому идет запрос на добавление в друзья
     * @param friendId - кто инициировал запрос на добавление в друзья
     */
    public void add(Long userId, Long friendId) {
        // проверка на запрос к самому себе
        if (userId.equals(friendId)) return;

        //Проверяем, что пользователи существуют и получаем их.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        User friend = userStorage.getById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + friendId + " не найден.", log));

        //Делаем дружескую связь.
        userStorage.linkFriends(user, friend);
    }

    /**
     * Удаление из друзей.
     *
     * @param userId   - id пользователя, у кого удалить из друзей пользователя с id friendId
     * @param friendId - id пользователя, у кого удалить из друзей пользователя с id userId
     */
    public void delete(Long userId, Long friendId) {
        // проверка на запрос к самому себе
        if (userId.equals(friendId)) return;

        //Проверяем, что пользователи существуют и получаем их.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        User friend = userStorage.getById(friendId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + friendId + " не найден.", log));

        //Исключаем из друзей друг у друга.
        userStorage.deleteLinkFriends(user, friend);
    }

    /**
     * Метод возвращает друзей пользователя.
     *
     * @param userId - id пользователя
     * @return - список друзей пользователя
     */
    public Collection<UserDto> find(Long userId) {
        //Проверяем, что пользователь существуют и получаем его.
        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        log.info("Запрошен список друзей пользователи с id {}.", userId);
        //todo определиться возвращать только подтвержденных друзей или запросы тоже учитывать (сейчас с ними)
        return userStorage.getFriendsByUser(user).keySet().stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод возвращает общих друзей двух пользователей.
     *
     * @param userId1 - id пользователя 1
     * @param userId2 - id пользователя 2
     * @return - список общих друзей
     */
    public Collection<UserDto> findCommon(Long userId1, Long userId2) {
        //Проверяем, что пользователи существуют и получаем их.
        User user1 = userStorage.getById(userId1).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId1 + " не найден.", log));

        User user2 = userStorage.getById(userId2).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId2 + " не найден.", log));

        return userStorage.findCommon(user1, user2).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
