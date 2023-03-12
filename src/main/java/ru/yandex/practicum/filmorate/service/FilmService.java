package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film createFilm(Film film) {
        Integer currentId = filmStorage.findCurrentId();
        film.setId(++currentId);
        filmStorage.updateCurrentId(currentId);
        log.info(film + " was successfully created.");
        filmStorage.createFilmLikeSet(film.getId());
        return filmStorage.createFilm(film);
    }

    public Film findFilmById (Integer filmId) {
        log.info("Film with id: " + filmId + " was successfully provided.");
        return filmStorage.findFilmById(filmId);
    }

    public Film updateFilm(Film film) {
        log.info(film + " was successfully updated.");
        return filmStorage.updateFilm(film);
    }

    public Film deleteFilmById (Integer filmId) {
        filmStorage.deleteFilmLikeSet(filmId);
        log.info("Film with id: " + filmId + " was successfully deleted.");
        return filmStorage.deleteFilmById(filmId);
    }

    public Collection<Film> findAllFilms() {
        log.info("All films list was successfully provided.");
        return filmStorage.findAllFilms();
    }

    public Set<Integer> addLike (Integer filmId, Integer userId) {
        Set<Integer> filmLikeSet = filmStorage.findFilmLikeSet(filmId);
        boolean likeAdded = filmLikeSet.add(userId);
        if (!likeAdded) {
            log.warn("User with id: {} already conveyed his like to film with id: {}.", userId, filmId);
            throw new ResponseStatusException(CONFLICT, String.format("User with id: %d already conveyed his like to film with id: %d.", userId, filmId));
        }
        log.info("Film with id: {} obtained like from user with id: {} successfully.", filmId, userId);
        return filmStorage.updateFilmLikeSet(filmId, filmLikeSet);
    }

    public Set<Integer> deleteLike (Integer filmId, Integer userId) {
        Set<Integer> filmLikeSet = filmStorage.findFilmLikeSet(filmId);
        if (filmLikeSet.remove(userId)) {
            log.info("Film with id: {} dropped like from user with id: {} successfully.", filmId, userId);
            return filmStorage.updateFilmLikeSet(filmId, filmLikeSet);
        } else {
            log.warn("Film with id {} doesn't contain like from user with id: {}.", filmId, userId);
            throw new ResponseStatusException(CONFLICT, "No like of such user found.");
        }
    }

    public List<Film> showFilmRate (int count) {
        Map<Integer, Set<Integer>> filmsLikes = filmStorage.findFilmsLikes();
        log.info("Film rate list was successfully provided.");
        return filmsLikes.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().size() - entry1.getValue().size())
                .limit(count)
                .map(entry -> filmStorage.findFilmById(entry.getKey()))
                .collect(Collectors.toList());
    }
}
