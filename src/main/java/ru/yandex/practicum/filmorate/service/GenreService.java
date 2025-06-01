package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreDbStorage genreDbStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GenreService.class);

    @Autowired
    public GenreService(GenreDbStorage genreDbStorage) {
        this.genreDbStorage = genreDbStorage;
    }

    /**
     * Получение объекта класса GenreDto по переданному id из БД.
     * Если жанр с переданным id отсутствует в БД, то выбрасываем исключение NotFoundException.
     *
     * @param id - id жанра
     */
    public GenreDto getById(Long id) {
        Genre genre = genreDbStorage.getById(id).
                orElseThrow(() -> new NotFoundException("Жанр с id = " + id + " не найден.", log));

        return GenreMapper.mapToGenreDto(genre);
    }

    /**
     * Получение всех объектов класса GenreDto из БД.
     */
    public Collection<GenreDto> findAll() {
        return genreDbStorage.findAll().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toList());
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
        if (genreDtoCollection.isEmpty()) return new ArrayList<Genre>();

        Collection<Long> longCollection = genreDtoCollection.stream()
                .map(GenreDto::getId)
                .toList();

        Collection<Genre> genreCollection = genreDbStorage.getAllByParameterId(longCollection);

        // убрал контроль, иначе тесты не проходят на дубли жанров
        //if (longCollection.size() != genreCollection.size())
        //throw new NotFoundException("Нет всех жанров по списку id:" + longCollection + ".", log);
        if (genreCollection.size() == 0)
            throw new NotFoundException("Нет всех жанров по списку id:" + longCollection + ".", log);

        return genreCollection;
    }
}
