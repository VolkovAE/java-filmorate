package ru.yandex.practicum.filmorate.mapper;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.genre.Genre;
import ru.yandex.practicum.filmorate.model.mpa.Mpa;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {
    @Autowired
    private MpaService nonStaticMpaService;
    @Autowired
    private GenreService nonStaticGenreService;

    private static MpaService mpaService;
    private static GenreService genreService;

    @PostConstruct
    private void initStatic() {
        FilmMapper.mpaService = nonStaticMpaService;
        FilmMapper.genreService = nonStaticGenreService;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        Mpa mpa = mpaService.getById(request.getRating().getId());

        Collection<Genre> genreCollection = genreService.getGenresByParameterId(request.getGenre());

        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setGenre(new HashSet<>(genreCollection));
        film.setRating(mpa);

        return film;
    }

    public static Film mapToFilm(UpdateFilmRequest request) {
        Film film = new Film();
        film.setId(request.getId());
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());

        if (!request.getGenre().isEmpty())
            film.setGenre(new HashSet<>(genreService.getGenresByParameterId(request.getGenre())));

        if (request.getRating() != null) film.setRating(mpaService.getById(request.getRating().getId()));

        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {
        Set<GenreDto> genreDtoSet = film.getGenre().stream()
                .map(GenreMapper::mapToGenreDto)
                .collect(Collectors.toSet());

        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());
        dto.setGenre(genreDtoSet);
        dto.setRating(MpaMapper.mapToMpaDto(film.getRating()));

        return dto;
    }
}
