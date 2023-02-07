package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static int filmId = 1;
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        FilmValidator.validate(film);

        for (Film filmFromBase : films.values()) {
            if(filmFromBase.getName().equals(film.getName())
                    && filmFromBase.getReleaseDate().equals(film.getReleaseDate())) {
                log.info("Movie added earlier at ID {}.", filmFromBase.getId());
                return null;
            }
        }

        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Movie \"{}\" added. The database contains {} film(s).",film.getName(), films.size());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        FilmValidator.validate(film);

        if(film.getId() != null && !films.containsKey(film.getId())) {
            log.info("Movie ID {} missing. ", film.getId());
            throw new NullPointerException("Movie ID " + film.getId() + " missing.");
        }

        films.put(film.getId(), film);
        log.info("Movie ID {} updated.", film.getId());
        return film;
    }
}
