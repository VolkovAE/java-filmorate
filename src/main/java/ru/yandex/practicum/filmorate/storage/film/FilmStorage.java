package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public Film delete(Film film);

    public Collection<Film> findAll();

    public Optional<Film> getById(Long id);

    public void addLike(Film film, User user);

    public void deleteLike(Film film, User user);

    public int getNumberLikes(Film film);
}
