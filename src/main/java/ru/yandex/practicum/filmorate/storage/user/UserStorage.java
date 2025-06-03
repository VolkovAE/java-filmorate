package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.StatusFriendship;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    public User add(User user);

    public User update(User user);

    public User delete(User user);

    public Collection<User> findAll();

    public Optional<User> getById(Long id);

    public Collection<User> getUsersLikesByFilm(Film film);

    public void linkFriends(User user, User friend);

    public Map<User, StatusFriendship> getFriendsByUser(User user);

    public void deleteLinkFriends(User user, User friend);

    public Collection<User> findCommon(User user1, User user2);
}
