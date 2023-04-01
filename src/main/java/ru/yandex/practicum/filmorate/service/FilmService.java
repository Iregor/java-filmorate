package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    public Collection<Film> findAll() {
        Collection<Film> result = filmStorage.findAll();
        log.info("Found {} movie(s).", result.size());
        return result;
    }

    public Film findById(Long filmId) {
        Optional<Film> result = filmStorage.findById(filmId);
        if (result.isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", filmId));
        }
        log.info("Film {} is found.", result.get().getId());
        return result.get();
    }

    public Film create(Film film) {
        Optional<Film> result = filmStorage.create(film);
        if (result.isEmpty()) {
            log.warn("Film {} is not created.",
                    film.getName());
            throw new IncorrectObjectIdException(String.format("Film %s is not created.",
                    film.getName()));
        }
        log.info("Film {} {} created.",
                result.get().getId(), result.get().getName());
        return result.get();
    }

    public Film update(Film film) {
        if (filmStorage.findById(film.getId()).isEmpty()) {
            log.warn("Film {} {} is not updated.",
                    film.getId(), film.getName());
            throw new IncorrectObjectIdException(String.format("Film %d %s is not updated.",
                    film.getId(), film.getName()));
        }
        Optional<Film> result = filmStorage.update(film);
        log.info("Film {} {} updated.",
                result.get().getId(), result.get().getName());
        return result.get();
    }

    public Collection<Film> getPopularFilms(Integer size) {
        return filmStorage.findPopularFilms(size);
    }

    public void like(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %s is not found.", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("Friend %s is not found.", userId));
        }
        likesStorage.add(filmId, userId);
        log.info("User {} liked film {}.", userId, filmId);
    }

    public void dislike(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %s is not found.", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            log.warn("User {} is not found.", userId);
            throw new IncorrectObjectIdException(String.format("User %s is not found.", userId));
        }
        likesStorage.remove(filmId, userId);
        log.info("User {} disliked film {}.", userId, filmId);
    }
}

