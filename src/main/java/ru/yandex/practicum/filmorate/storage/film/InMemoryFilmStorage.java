package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.FilmorateUtils;
import ru.yandex.practicum.filmorate.util.Reflection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
//@Slf4j(topic = "Фильмы")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(InMemoryFilmStorage.class);

    @Override
    public Film add(Film film) {
        // формируем дополнительные данные
        film.setId(FilmorateUtils.getNextId(films));

        // сохраняем новый фильм в памяти приложения
        films.put(film.getId(), film);

        log.info("Добавлен новый фильм {}.", film);

        return film;
    }

    @Override
    public Film update(Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            // обновляем содержимое
            BeanUtils.copyProperties(newFilm, oldFilm, Reflection.getIgnoreProperties(newFilm));

            log.info("Обновлены данные фильма {}.", oldFilm);

            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден.", log);
    }

    @Override
    public Film delete(Film delFilm) {
        if (films.containsKey(delFilm.getId())) {
            // удаляем фильм из памяти приложения
            Film removeFilm = films.remove(delFilm.getId());

            log.info("Удален фильм {}.", removeFilm);

            return removeFilm;
        }
        throw new NotFoundException("Фильм с id = " + delFilm.getId() + " не найден.", log);
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Получен список фильмов.");

        return films.values();
    }

    @Override
    public Optional<Film> getById(Long id) {
        log.info("Запрошена информация по фильму с {}.", id);

        return Optional.ofNullable(films.get(id));
    }
}
