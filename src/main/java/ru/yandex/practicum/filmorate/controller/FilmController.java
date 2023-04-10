package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping(value = "{filmId}")
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

    @GetMapping(value = "/common", params = {"userId", "friendId"})
    public Collection<Film> getCommonFilms(@RequestParam("userId") Long userId,
                                           @RequestParam("friendId") Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
