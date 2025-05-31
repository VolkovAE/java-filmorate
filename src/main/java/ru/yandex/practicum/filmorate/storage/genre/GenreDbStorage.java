package ru.yandex.practicum.filmorate.storage.genre;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Genre WHERE id = ?;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM Genre;";
    private static final String FIND_ALL_BY_ID_QUERY = "SELECT * FROM Genre WHERE id IN (:ids);";
    private static final String FIND_ALL_GENRES_BY_FILM_QUERY = "SELECT g.id, g.name, g.description " +
            " FROM GENRE AS g" +
            " INNER JOIN FILM_GENRE AS fg ON fg.genre_id = g.id" +
            " AND fg.film_id = ?;";

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GenreDbStorage.class);

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbc, GenreRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<Genre> getById(Long id) {
        log.info("Запрошена информация по жанру с {}.", id);

        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Collection<Genre> findAll() {
        log.info("Получен список жанров.");

        return findMany(FIND_ALL_QUERY);
    }

    public Collection<Genre> getAllByParameterId(Collection<Long> longCollection) {
        log.info("Получен список жанров с заданными id.");

        SqlParameterSource parameters = new MapSqlParameterSource("ids", longCollection);

        return findManyParameterSource(FIND_ALL_BY_ID_QUERY, parameters);
    }

    public Collection<Genre> getAllByFilm(Film film) {
        log.info("Получен список жанров для фильма с id {}.", film.getId());

        return findMany(FIND_ALL_GENRES_BY_FILM_QUERY, film.getId());
    }
}
