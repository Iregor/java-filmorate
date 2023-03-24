package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "after", required = false, defaultValue = "1895-12-28")
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate after,
            @RequestParam(value = "before", required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate before) {
        return filmService.findAll(name, after, before);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getMostPopularFilms(count);
    }

    @GetMapping(value ="{filmId}")
    public Film findById(@PathVariable Long filmId) {
        return filmService.findById(filmId);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        return filmService.update(film);
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
