package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
//@Slf4j(topic = "Фильмы")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public Film add(@RequestBody @Valid Film film) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmStorage.add(film);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public Film update(@RequestBody @Valid Film newFilm) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmStorage.update(newFilm);
    }

    @DeleteMapping
    @Validated(Marker.OnDelete.class)
    public Film delete(@RequestBody @Valid Film delFilm) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmStorage.delete(delFilm);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable(name = "id") Long filmId) {
        return filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) {
        filmService.add(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable(name = "id") Long filmId, @PathVariable(name = "userId") Long userId) {
        filmService.delete(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopular(@RequestParam(name = "count", defaultValue = "10") long count) {
        return filmService.getPopular(count);
    }
}
