package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    // работаем с другими сущностями через их сервисы, а не обращаемся напрямую к их хранилищу.

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(@Qualifier("FilmDbStorage") FilmStorage filmStorage,
                       @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public FilmDto add(NewFilmRequest filmRequest) {
        // преобразуем NewFilmRequest->Film, также будут выполнены проверки:
        //  - что указанный рейтинг с id существует
        //  - что указанные жанры с id существуют
        Film film = FilmMapper.mapToFilm(filmRequest);

        // записываем объект класса Film в хранилище (БД, память).
        film = filmStorage.add(film);

        // Film->FilmDto
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto getById(Long filmId) {
        // получение данных о фильме из хранилища (БД, память).
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));

        // Film->FilmDto
        return FilmMapper.mapToFilmDto(film);
    }

    public FilmDto update(UpdateFilmRequest filmRequest) {
        // преобразуем UpdateFilmRequest->Film, если переданы рейтинг, жанры, то будут выполнены проверки:
        //  - что указанный рейтинг с id существует
        //  - что указанные жанры с id существуют
        Film newFilm = FilmMapper.mapToFilm(filmRequest);

        // записываем (обновляем) объект класса Film в хранилище (БД, память).
        newFilm = filmStorage.update(newFilm);

        // Film->FilmDto
        return FilmMapper.mapToFilmDto(newFilm);
    }

    public FilmDto delete(UpdateFilmRequest filmRequest) {
        // получение данных о фильме из хранилища (БД, память).
        Film removeFilm = filmStorage.getById(filmRequest.getId()).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmRequest.getId() + " не найден.", log));

        // удаление фильма из хранилища (БД, память).
        removeFilm = filmStorage.delete(removeFilm);

        // Film->FilmDto
        return FilmMapper.mapToFilmDto(removeFilm);
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    /**
     * Метод регистрирует в хранилище (БД, память) лайк фильму от пользователя.
     *
     * @param filmId - id фильма, которому пользователь ставит лайк
     * @param userId - id пользователя, который ставит фильму лай
     */
    public void add(Long filmId, Long userId) {
        //Проверяем, что фильм и пользователь существуют и получаем их.
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));

        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        //Фильму ставим лайк от пользователя (это в памяти).
        filmStorage.addLike(film, user);
    }

    public void delete(Long filmId, Long userId) {
        //Проверяем, что фильм и пользователь существуют и получаем их.
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));

        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        //Удаляем у фильма лайк пользователя.
        filmStorage.deleteLike(film, user);
    }

    public Collection<FilmDto> getPopular(final long count) {
        //Возвращает список из первых count фильмов по количеству лайков.
        Collection<Film> filmsByPopular = filmStorage.findAll().stream()
                .collect(Collectors.toMap(film -> film, film -> film.getLikes().size()))
                //.collect(Collectors.toMap(film -> film, filmStorage::getNumberLikes))
                .entrySet()
                .stream()
                .sorted((filmLikes1, filmLikes2) ->
                        //filmLikes1.getValue() - filmLikes2.getValue())
                        filmLikes2.getValue() - filmLikes1.getValue())  //по убыванию
                .limit(count)   //выводит количество элементов
                .map(Map.Entry::getKey)
                .toList();

        log.info("Получен список фильмов отсортированный по кол-ву лайков:{}", logGetPopular(filmsByPopular));

        return filmsByPopular.stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    private String logGetPopular(Collection<Film> filmCollection) {
        final int[] i = {0};

        StringBuilder stringBuilder = new StringBuilder();
        filmCollection.forEach(film ->
                stringBuilder.append(
                        String.format("\n  %d) Фильм с id %d с кол-вом лайков %d.", ++i[0], film.getId(), film.getLikes().size())));

        return stringBuilder.toString();
    }
}
