package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(UserStorage userStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public void like(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().add(userId);
        userStorage.findById(userId).getLikeFilms().add(filmId);
    }

    public void dislike(Long filmId, Long userId) {
        filmStorage.findFilmById(filmId).getLikes().remove(userId);
        userStorage.findById(userId).getLikeFilms().remove(filmId);
    }

    public Collection<Film> getMostPopularFilms(Integer size) {
        return filmStorage
                .findAll()
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }
}
