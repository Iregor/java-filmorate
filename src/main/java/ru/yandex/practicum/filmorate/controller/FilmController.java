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
        if(FilmValidator.validate(film, films)) {
            film.setId(filmId++);
            films.put(film.getId(), film);
            log.info("Фильм \"{}\" добавлен. В базе {} фильм{}.",film.getName(), films.size(), ending());
            return film;
        }
        return null;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if(FilmValidator.validate(film, films)) {
            films.put(film.getId(), film);
            log.info("Фильм c идентификатором \"{}\" обновлен.",film.getId());
            return film;
        }
        return null;
    }

    private String ending() {
        String[] ends = new String[]{"", "а", "ов"};
        if ((films.size() > 4) & (films.size() < 21)) return ends[2];
        else if ((films.size() % 10) == 1) return ends[0];
        else if (((films.size() % 10) > 1) & ((films.size() % 10) < 5)) return ends[1];
        else return ends[0];
    }
}
