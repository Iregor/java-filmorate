package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.OLDEST_DATE_RELEASE;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    public final FilmStorage filmStorage;
    public final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/search")
    public Collection<Film> filterFilms(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "after", required = false, defaultValue = "1895-12-28")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate after,
            @RequestParam(value = "before", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate before) {
        if (after.isBefore(OLDEST_DATE_RELEASE) && after.isAfter(LocalDate.now())) {
            log.warn("Incorrect value in field \"after\". ");
            throw new IncorrectParameterException("after");
        }

        if (before == null) {
            before = LocalDate.now();
        }

        if (before.isAfter(after) && before.isAfter(LocalDate.now())) {
            log.warn("Incorrect value in field \"before\". ");
            throw new IncorrectParameterException("before");
        }
        return filmStorage.filterFilms(name, after, before);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping("{filmId}")
    public Film like(@PathVariable Long filmId) {
        return filmStorage.findFilmById(filmId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmStorage.update(film);
    }

    @PutMapping("{filmId}/like/{userId}")
    public void like(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.like(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void dislike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.dislike(filmId, userId);
    }
}
