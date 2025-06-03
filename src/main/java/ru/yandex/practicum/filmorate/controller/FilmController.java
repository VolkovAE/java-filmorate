package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Marker;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmController.class);

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @Validated(Marker.OnCreate.class)
    public FilmDto add(@RequestBody @Valid NewFilmRequest filmRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmService.add(filmRequest);
    }

    @PutMapping
    @Validated(Marker.OnUpdate.class)
    public FilmDto update(@RequestBody @Valid UpdateFilmRequest filmRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmService.update(filmRequest);
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable(name = "id") Long filmId) {
        return filmService.getById(filmId);
    }

    @DeleteMapping
    @Validated(Marker.OnDelete.class)
    public FilmDto delete(@RequestBody @Valid UpdateFilmRequest filmRequest) {
        // проверку выполнения необходимых условий осуществил через валидацию полей
        // обработчик выполняется после успешной валидации полей

        return filmService.delete(filmRequest);
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
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
    public Collection<FilmDto> getPopular(@RequestParam(name = "count", defaultValue = "10") @Positive long count) {
        return filmService.getPopular(count);
    }
}
