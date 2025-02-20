package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.Config;
import ru.yandex.practicum.filmorate.util.FilmorateUtils;
import ru.yandex.practicum.filmorate.util.Reflection;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j(topic = "Фильмы")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    public FilmController() {
        ((ch.qos.logback.classic.Logger) log).setLevel(Config.getLevelLog());
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Обработан запрос на получение списка фильмов.");

        return films.values();
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film add(@RequestBody @Valid Film film) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        // формируем дополнительные данные
        film.setId(FilmorateUtils.getNextId(films));
        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);

        log.info("Обработан запрос на добавление нового фильма {}.", film);

        return film;
    }

    @Validated(Marker.OnUpdate.class)
    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            // обновляем содержимое
            BeanUtils.copyProperties(newFilm, oldFilm, Reflection.getIgnoreProperties(newFilm));

            log.info("Обработан запрос на изменение данных фильма {}.", oldFilm);

            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден.", log);
    }
}
