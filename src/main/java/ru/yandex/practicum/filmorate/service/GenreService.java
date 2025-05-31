package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.Collection;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GenreService.class);

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    /**
     * Получение объект класса Genre (модель) по переданному id из БД.
     * Если жанр с переданным id отсутствует в БД, то выбрасываем исключение NotFoundException.
     *
     * @param id - id жанра
     */
    public Genre getById(Long id) {
        return genreDbStorage.getById(id).
                orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден.", log));
    }

    /**
     * Получение всех объектов класса Genre (модель) из БД.
     */
    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    /**
     * Получение объектов класса Genre (модель) по переданному списку id (список объектов класса GenreDto) из БД.
     * Если количество полученных объектов класса Genre не равно количеству объектов класса GenreDto, то
     * выбрасываем исключение NotFoundException.
     *
     * @param genreDtoCollection - список объектов класса GenreDto, по id которым нужно получить из БД
     *                           объекты класса Genre.
     * @return - список объектов класса Genre полученных по переданным id.
     */
    public Collection<Genre> getGenresByParameterId(Collection<GenreDto> genreDtoCollection) {
        Collection<Long> longCollection = genreDtoCollection.stream()
                .map(GenreDto::getId)
                .toList();

        Collection<Genre> genreCollection = genreDbStorage.getAllByParameterId(longCollection);

        if (longCollection.size() != genreCollection.size())
            throw new NotFoundException("Нет всех жанров по списку id:" + longCollection + ".", log);

        return genreCollection;
    }
}
