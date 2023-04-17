package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Set;

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

    @GetMapping(value = "/popular")
    public Collection<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count,
            @RequestParam(required = false) Long genreId,
            @RequestParam(required = false) String year) {
        return filmService.getPopularFilms(count, genreId, year);
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

    @DeleteMapping(value = "{filmId}")
    public void delete(@PathVariable Long filmId) {
        filmService.delete(filmId);
    }

    @PutMapping("{filmId}/like/{userId}")
    public void like(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.like(filmId, userId);
    }

    @DeleteMapping("{filmId}/like/{userId}")
    public void dislike(@PathVariable Long filmId, @PathVariable Long userId) {
        filmService.dislike(filmId, userId);
    }

    @GetMapping(value = "/common")
    public Collection<Film> getCommonFilms(@RequestParam Long userId,
                                           @RequestParam Long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public Set<Film> searchFilms(@RequestParam(name = "query") String subString,
                                 @RequestParam(defaultValue = "title") Set<String> by) {
        return filmService.searchFilms(subString, by);
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsSortedByDirector(@PathVariable Long directorId, @RequestParam String sortBy) {
        return filmService.getFilmDirectorSorted(directorId, sortBy);
    }
}
