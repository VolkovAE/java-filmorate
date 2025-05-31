package ru.yandex.practicum.filmorate.storage.mappers;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.converters.IntegerToDurationConverter;
import ru.yandex.practicum.filmorate.converters.StringToInstantConverter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.mpa.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDbStorage;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmRowMapper.class);

    private final MpaDbStorage mpaDbStorage;

    private final IntegerToDurationConverter integerToDurationConverter;

    private final StringToInstantConverter stringToInstantConverter;

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));

        //Timestamp releaseDate = resultSet.getTimestamp("releaseDate");
        //film.setReleaseDate(releaseDate.toInstant());
        film.setReleaseDate(stringToInstantConverter.convert(resultSet.getString("releaseDate")));

        film.setDuration(integerToDurationConverter.convert(resultSet.getInt("duration")));

        Long mpaId = resultSet.getLong("rating");
        Mpa mpa = mpaDbStorage.getById(mpaId).orElseThrow(
                () -> new NotFoundException("Рейтинг с id = " + mpaId + " не найден."));
        film.setRating(mpa);

        return film;
    }
}
