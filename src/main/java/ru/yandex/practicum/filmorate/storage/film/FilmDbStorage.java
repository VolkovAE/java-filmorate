package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.converters.DurationToIntegerConverter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.Reflection;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Qualifier("FilmDbStorage")
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM film;";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM film WHERE id = ?;";
    private static final String INSERT_FILM_QUERY = "INSERT INTO film (name, description, releaseDate, duration, rating)" +
            " VALUES (?, ?, ?, ?, ?);";
    private static final String INSERT_FILM_GENRES_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?);";
    private static final String UPDATE_QUERY = "UPDATE film" +
            " SET name = ?," +
            " description = ?," +
            " releaseDate = ?," +
            " duration = ?," +
            " rating = ?" +
            " WHERE id = ?";
    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genre WHERE film_id = ?;";
    private static final String DELETE_FILM_LIKES_QUERY = "DELETE FROM likes WHERE film_id = ?;";
    private static final String DELETE_FILM_QUERY = "DELETE FROM film WHERE id = ?;";
    private static final String INSERT_FILM_USER_LIKES_QUERY = "INSERT INTO likes (film_id, user_id)" +
            " SELECT :film_id, :user_id" +
            " WHERE" +
            "   NOT EXISTS (SELECT * FROM likes WHERE film_id = :film_id AND user_id = :user_id);";
    private static final String DELETE_FILM_USER_LIKES_QUERY = "DELETE FROM likes" +
            " WHERE likes.film_id = :film_id AND likes.user_id = :user_id;";
    private static final String FIND_NUMBER_LIKES_BY_FILM_QUERY = "SELECT count(*)" +
            " FROM LIKES" +
            " WHERE film_id = ?";

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmDbStorage.class);

    @Autowired
    private DurationToIntegerConverter durationToIntegerConverter;

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    @Qualifier("UserDbStorage")
    private UserStorage userStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper) {
        super(jdbc, mapper);
    }

    @Override
    @Transactional
    public Film add(Film film) {
        // сохраняем новый фильм в БД приложения
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                Timestamp.from(film.getReleaseDate()),
                durationToIntegerConverter.convert(film.getDuration()),
                film.getRating().getId());
        film.setId(id);

        // сохраняем информацию о жанрах, которым принадлежит фильм, в БД приложения
        insertBatch(INSERT_FILM_GENRES_QUERY, getBatchPreparedStatementSetter(film));

        log.info("Добавлен новый фильм {}.", film);

        return film;
    }

    private static BatchPreparedStatementSetter getBatchPreparedStatementSetter(Film film) {
        return new BatchPreparedStatementSetter() {
            final List<Genre> genreList = new ArrayList<>(film.getGenre());

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genreList.get(i);
                ps.setObject(1, film.getId());
                ps.setObject(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genreList.size();
            }
        };
    }

    @Override
    public Optional<Film> getById(Long id) {
        log.info("Запрошена информация по фильму с {}.", id);

        // получаем данные из БД о фильме.
        Optional<Film> optionalFilm = findOne(FIND_BY_ID_QUERY, id);

        if (optionalFilm.isPresent()) {
            // получаем данные о жанрах фильма.
            Film film = optionalFilm.get();

            Collection<Genre> genreCollection = genreDbStorage.getAllByFilm(film);
            if (!genreCollection.isEmpty()) film.setGenre(new HashSet<>(genreCollection));

            // получаем пользователей поставивших фильму лайк и размещаем его в объекте film
            film.setLikes(new HashSet<>(userStorage.getUsersLikesByFilm(film)));
        }

        return optionalFilm;
    }

    @Override
    @Transactional
    public Film update(Film newFilm) {
        // получаем данные по фильму с id из БД (oldFilm)
        Film oldFilm = getById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден.", log));

        // обновляем содержимое в объекте oldFilm
        BeanUtils.copyProperties(newFilm, oldFilm, Reflection.getIgnoreProperties(newFilm));

        // обновляем данные о фильме в БД (oldFilm)
        update(
                UPDATE_QUERY,
                oldFilm.getName(),
                oldFilm.getDescription(),
                Timestamp.from(oldFilm.getReleaseDate()),
                durationToIntegerConverter.convert(oldFilm.getDuration()),
                oldFilm.getRating().getId(),
                oldFilm.getId());

        // обновляем данные о жанрах фильма в БД (oldFilm.genre)
        delete(DELETE_FILM_GENRES_QUERY, oldFilm.getId());  // удаляем записи о текущих жанрах фильма
        oldFilm.setGenre(newFilm.getGenre());
        insertBatch(INSERT_FILM_GENRES_QUERY, getBatchPreparedStatementSetter(oldFilm));    // вставляем записи о новых жанрах фильма

        log.info("Обновлены данные фильма {}.", oldFilm);

        return oldFilm;
    }

    @Override
    @Transactional
    public Film delete(Film film) {
        // удаляем данные о жанрах фильма в БД (film.genre)
        delete(DELETE_FILM_GENRES_QUERY, film.getId()); // удаляем записи о текущих жанрах фильма

        // удаляем данные о лайках фильма в БД (film.likes)
        delete(DELETE_FILM_LIKES_QUERY, film.getId()); // удаляем записи о текущих жанрах фильма

        // удаляем данные о фильме из БД.
        delete(DELETE_FILM_QUERY, film.getId());

        return film;
    }

    @Override
    public Collection<Film> findAll() {
        log.info("Получен список фильмов.");

        return findMany(FIND_ALL_QUERY).stream()
                .peek(film -> film.setGenre(new HashSet<>(genreDbStorage.getAllByFilm(film))))  //жанры фильма
                .peek(film -> film.setLikes(new HashSet<>(userStorage.getUsersLikesByFilm(film))))  //пользователи поставившие фильму лайк
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(Film film, User user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("user_id", user.getId());

        // добавляем лайк фильм от пользователя
        updateParameterSource(INSERT_FILM_USER_LIKES_QUERY, parameters);

        // получаем пользователей поставивших фильму лайк и размещаем его в объекте film
        film.setLikes(new HashSet<>(userStorage.getUsersLikesByFilm(film)));

        log.info("Фильму с id {} поставлен лайк пользователем с id {}.", film.getId(), user.getId());
    }

    @Override
    public void deleteLike(Film film, User user) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("film_id", film.getId())
                .addValue("user_id", user.getId());

        // удаляем лайк фильму от пользователя
        updateParameterSource(DELETE_FILM_USER_LIKES_QUERY, parameters);

        // получаем пользователей поставивших фильму лайк и размещаем его в объекте film
        film.setLikes(new HashSet<>(userStorage.getUsersLikesByFilm(film)));

        log.info("Фильму с id {} удален лайк пользователя с id {}.", film.getId(), user.getId());
    }

    @Override
    public int getNumberLikes(Film film) {
        return findOne(FIND_NUMBER_LIKES_BY_FILM_QUERY, Integer.class, film.getId());
    }
}
