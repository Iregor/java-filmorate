package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.OLDEST_DATE_RELEASE;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Collection<Film> findAll(String name, LocalDate after, LocalDate before) {
        return filmStorage.findAll(name, after, before);
    }

    public Film findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void like(Long filmId, Long userId) {
        filmStorage.findById(filmId).getLikes().add(userId);
        userStorage.findById(userId).getLikeFilms().add(filmId);
    }

    public void dislike(Long filmId, Long userId) {
        filmStorage.findById(filmId).getLikes().remove(userId);
        userStorage.findById(userId).getLikeFilms().remove(filmId);
    }

    public Collection<Film> getMostPopularFilms(Integer size) {
        return filmStorage
                .findAll(null, OLDEST_DATE_RELEASE, LocalDate.now())
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }
}
