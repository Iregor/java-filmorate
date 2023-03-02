package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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

    public Map<String, Long> like(Long filmId, Long userId) {
        Map<String, Long> result = validateFilmDataRequest(filmId, userId);
        if (!result.isEmpty()) {
            return result;
        }
        filmStorage.findById(filmId).getLikes().add(userId);
        userStorage.findById(userId).getLikeFilms().add(filmId);
        return null;
    }

    public Map<String, Long> dislike(Long filmId, Long userId) {
        Map<String, Long> result = validateFilmDataRequest(filmId, userId);
        if (!result.isEmpty()) {
            return result;
        }
        filmStorage.findById(filmId).getLikes().remove(userId);
        userStorage.findById(userId).getLikeFilms().remove(filmId);
        return null;
    }

    private Map<String, Long> validateFilmDataRequest(Long filmId, Long userId) {
        Map<String, Long> result = new HashMap<>();
        if (filmStorage.findById(filmId) == null) {
            result.put("filmId", filmId);
        }
        if (userStorage.findById(userId) == null) {
            result.put("userId", userId);
        }
        return result;
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
