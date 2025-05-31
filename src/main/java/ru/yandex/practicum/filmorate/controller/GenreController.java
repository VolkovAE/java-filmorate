package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(GenreController.class);

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public GenreDto findById(@PathVariable(name = "id") Long genreId) {
        return genreService.getById(genreId);
    }

    @GetMapping
    public Collection<GenreDto> findAll() {
        return genreService.findAll();
    }
}
