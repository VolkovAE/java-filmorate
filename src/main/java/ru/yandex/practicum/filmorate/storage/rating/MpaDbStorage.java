package ru.yandex.practicum.filmorate.storage.rating;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.mpa.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM Rating WHERE id = ?;";

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MpaDbStorage.class);

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbc, MpaRowMapper mapper) {
        super(jdbc, mapper);
    }

    public Optional<Mpa> getById(Long id) {
        log.info("Запрошена информация по рейтингу с id {}.", id);

        return findOne(FIND_BY_ID_QUERY, id);
    }
}
