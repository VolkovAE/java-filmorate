package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    public User add(User user);

    public User update(User user);

    public User delete(User user);

    public Collection<User> findAll();

    public Optional<User> getById(Long id);
}
