package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    public Film add(Film film);

    public Film update(Film film);

    public Film delete(Film film);

    public Collection<Film> findAll();

    public Optional<Film> getById(Long id);
}
