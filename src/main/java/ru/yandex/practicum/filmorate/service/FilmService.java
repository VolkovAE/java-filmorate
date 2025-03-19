package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
//@Slf4j(topic = "Сервис оценки фильмов")
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void add(Long filmId, Long userId) {
        //Проверяем, что фильм и пользователь существуют и получаем их.
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));

        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        //Фильму ставим лайк от пользователя.
        Set<Long> likes = film.getLikes();
        likes.add(userId);
        film.setLikes(likes);

        log.info("Фильму с id {} поставлен лайк пользователем с id {}.", filmId, userId);
    }

    public void delete(Long filmId, Long userId) {
        //Проверяем, что фильм и пользователь существуют и получаем их.
        Film film = filmStorage.getById(filmId).orElseThrow(
                () -> new NotFoundException("Фильм с id = " + filmId + " не найден.", log));

        User user = userStorage.getById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с id = " + userId + " не найден.", log));

        //Удаляем у фильма лайк пользователя.
        Set<Long> likes = film.getLikes();
        likes.remove(userId);
        film.setLikes(likes);

        log.info("Фильму с id {} удален лайк пользователем с id {}.", filmId, userId);
    }

    public Collection<Film> getPopular(final long count) {
        //Получить список фильмов, у которых количество лайков больше или равно count.
//        return filmStorage.findAll().stream()
//                .filter(film -> film.getLikes().size() >= count)
//                .sorted(Comparator.comparing(film -> film.getLikes().size()))
//                .toList();
//        return filmStorage.findAll().stream()
//                .filter(film -> film.getLikes().size() >= count)
//                .sorted(Comparator.comparing(film -> film))
//                .toList();

        //Возвращает список из первых count фильмов по количеству лайков.
        Collection<Film> filmsByPopular = filmStorage.findAll().stream()
                .collect(Collectors.toMap(film -> film, film -> film.getLikes().size()))
                .entrySet()
                .stream()
                .sorted((filmLikes1, filmLikes2) ->
                        //filmLikes1.getValue() - filmLikes2.getValue())
                        filmLikes2.getValue() - filmLikes1.getValue())  //по убыванию
                .limit(count)   //выводит количество элементов
                .map(Map.Entry::getKey)
                .toList();

        log.info("Получен список фильмов отсортированный по кол-ву лайков:{}", logGetPopular(filmsByPopular));

        return filmsByPopular;
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
