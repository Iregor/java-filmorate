package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validation.Exist;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
public class FilmController {
    FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable @Exist("film") Integer id) {
        return filmService.findFilmById(id);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid @Exist("film") Film film) {
        return filmService.updateFilm(film);
    }

    @DeleteMapping
    public Film deleteFilmById(@Exist("film") Integer id) {
        return filmService.deleteFilmById(id);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Set<Integer> addLike(@PathVariable @Exist("film") Integer id, @PathVariable @Exist("film") Integer userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Set<Integer> deleteLike(@PathVariable @Exist("film") Integer filmId, @PathVariable @Exist("film") Integer userId) {
        return filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> showFilmRate(@RequestParam Optional<Integer> count) {
        return filmService.showFilmRate(count.orElse(10));
    }
}