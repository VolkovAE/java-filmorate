package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Validated
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    private static final Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(MpaController.class);

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping("/{id}")
    public MpaDto findById(@PathVariable(name = "id") Long mpaId) {
        return mpaService.getByIdMpaDto(mpaId);
    }

    @GetMapping
    public Collection<MpaDto> findAll() {
        return mpaService.findAll();
    }
}
