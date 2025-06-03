package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.mpa.Mpa;
import ru.yandex.practicum.filmorate.storage.rating.MpaDbStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class MpaService {
    private final MpaDbStorage mpaDbStorage;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MpaService.class);

    @Autowired
    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    /**
     * Получение объект класса Mpa (модель) по переданному id из БД.
     * Если рейтинг с переданным id отсутствует в БД, то выбрасываем исключение NotFoundException.
     *
     * @param id - id рейтинга
     */
    public Mpa getById(Long id) {
        return mpaDbStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден.", log));
    }

    public MpaDto getByIdMpaDto(Long id) {
        Mpa mpa = mpaDbStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + id + " не найден.", log));

        return MpaMapper.mapToMpaDto(mpa);
    }

    public Collection<MpaDto> findAll() {
        return mpaDbStorage.findAll().stream()
                .map(MpaMapper::mapToMpaDto)
                .collect(Collectors.toList());
    }
}
