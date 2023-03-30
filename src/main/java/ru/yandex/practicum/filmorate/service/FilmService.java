/*
package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("userDb")
    private final UserStorage userStorage;
    @Qualifier("filmDb")
    private final FilmStorage filmStorage;
    @Qualifier("genreDb")
    private final GenreStorage genreStorage;
    @Qualifier("likesDb")
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
        Film result = filmStorage.create(film);
        log.info("Film {} {} added.", result.getId(), result.getName());
        return result;
    }

    public Film update(Film film) {
        if (filmStorage.findById(film.getId()).isEmpty()) {
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", film.getId()));
        }
        Collection<Genre> genreFromDb = genreStorage.readRowByFilmId(film.getId());
        genreFromDb.removeAll(film.getGenres());
        genreFromDb.forEach(genre -> filmStorage.deleteFilmGenres(film.getId(), genre.getId()));
        Film result = filmStorage.update(film);
        log.info("Film {} updated.", result.getId());
        return result;
    }

    public Collection<Film> getPopularFilms(Integer size) {
        return filmStorage.getPopularFilms(size);
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
        likesStorage.writeRow(filmId, userId);
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
        likesStorage.deleteRow(filmId, userId);
        log.info("User {} disliked film {}.", userId, filmId);
    }
}
*/
