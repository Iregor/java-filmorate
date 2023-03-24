package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.IncorrectObjectIdException;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.OLDEST_DATE_RELEASE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    @Qualifier("userDb")
    private final UserStorage userStorage;
    @Qualifier("filmDb")
    private final FilmStorage filmStorage;
    @Qualifier("mpaDb")
    private final MpaStorage mpaStorage;
    @Qualifier("genreDb")
    private final GenreStorage genreStorage;
    @Qualifier("likesDb")
    private final LikesStorage likesStorage;

    public Collection<Film> findAll(String name, LocalDate after, LocalDate before) {
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
        Collection<Film> result = filmStorage.findAll(name, after, before);
        result.forEach(this::makeData);
        log.info("Found {} movie(s).", result.size());
        return result;
    }

    public Film findById(Long filmId) {
        Optional<Film> result = filmStorage.findById(filmId);
        if (result.isEmpty()) {
            log.warn("Film {} is not found.", filmId);
            throw new IncorrectObjectIdException(String.format("Film %d is not found.", filmId));
        }
        makeData(result.get());
        log.info("Film {} is found.", result.get().getId());
        return result.get();
    }

    public Film create(Film film) {
        Film result = filmStorage.create(film);
        makeData(result);
        log.info("Film {} {} added.", result.getId(), result.getName());
        return result;
    }

    public Film update(Film film) {
        Collection<Genre> genreFromDb = genreStorage.findGenresByFilmId(film.getId());
        genreFromDb.removeAll(film.getGenres());
        genreFromDb.forEach(genre -> genreStorage.deleteFilmGenres(film.getId(), genre.getId()));
        Film result = filmStorage.update(film);
        makeData(result);
        log.info("Film {} updated.", result.getId());
        return result;
    }

    public Collection<Film> getMostPopularFilms(Integer size) {
        Collection<Film> result = filmStorage
                .findAll(null, OLDEST_DATE_RELEASE, LocalDate.now());
        result.forEach(this::makeData);
        result = result.stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
        log.info("Found {} movie(s).", result.size());
        return result;
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
        likesStorage.like(filmId, userId);
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
        likesStorage.dislike(filmId, userId);
        log.info("User {} disliked film {}.", userId, filmId);
    }

    private void makeData(Film film) {
        Optional<Mpa> mpa = mpaStorage.findById(film.getMpa().getId());
        mpa.ifPresent(film::setMpa);
        film.setGenres(new HashSet<>(genreStorage.findGenresByFilmId(film.getId())));
        film.setLikes(new HashSet<>(likesStorage.getFilmLikes(film.getId())));
    }
}
