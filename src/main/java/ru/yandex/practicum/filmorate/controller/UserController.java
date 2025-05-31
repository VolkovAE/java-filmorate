package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public UserDto add(@RequestBody @Valid NewUserRequest userRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return userService.add(userRequest);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public UserDto update(@RequestBody @Valid UpdateUserRequest userRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return userService.update(userRequest);
    }

    @DeleteMapping
    @Validated(Marker.OnDelete.class)
    public UserDto delete(@RequestBody @Valid UpdateUserRequest userRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return userService.delete(userRequest);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable(name = "id") Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        userService.add(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable(name = "id") Long userId, @PathVariable(name = "friendId") Long friendId) {
        userService.delete(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> findFriends(@PathVariable(name = "id") Long userId) {
        return userService.find(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> findCommonFriends(@PathVariable(name = "id") Long userId1,
                                                 @PathVariable(name = "otherId") Long userId2) {
        return userService.findCommon(userId1, userId2);
    }
}
