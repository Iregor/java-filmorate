package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidateException;
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
    private HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        if(!FilmValidator.validate(film)) {
            return null;
        }
        if(films.containsValue(film)) {
            throw new ValidateException("Фильм уже в базе.");
        }
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if(!FilmValidator.validate(film)) {
            return null;
        }
        if(!films.containsKey(film.getId())) {
            throw new NullPointerException("Фильм с таким идентификатором отсутствует.");
        }
        films.put(film.getId(), film);
        return film;
    }
}
