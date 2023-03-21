package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.validator.FilmValidator.OLDEST_DATE_RELEASE;

@Slf4j
@Service
public class FilmService {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(@Qualifier("userDb") UserStorage userStorage,
                       @Qualifier("filmDb") FilmStorage filmStorage,
                       @Qualifier("mpaDb") MpaStorage mpaStorage,
                       @Qualifier("genreDb") GenreStorage genreStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public Collection<Film> findAll(String name, LocalDate after, LocalDate before) {
        Collection<Film> result = filmStorage.findAll(name, after, before);
        result.forEach(this::makeData);
        return result;
    }

    public Optional<Film> findById(Long filmId) {
        Optional<Film> result = filmStorage.findById(filmId);
        if (result.isEmpty()) {
            return result;
        }
        makeData(result.get());
        return result;
    }

    public Film create(Film film) { //// добить оснастку или перенести в другой метод
        Film result = filmStorage.create(film);
        makeData(result);
        return result;
    }

    public Film update(Film film) {
        Collection<Genre> genreFromDb = genreStorage.findAllByFilmId(film.getId());
        genreFromDb.removeAll(film.getGenres());
        genreFromDb.forEach(genre -> genreStorage.delFilmGenre(film.getId(), genre.getId()));
        Film result = filmStorage.update(film);
        makeData(result);
        return result;
    }

    public Collection<Film> getMostPopularFilms(Integer size) {
        Collection<Film> result = filmStorage
                .findAll(null, OLDEST_DATE_RELEASE, LocalDate.now())
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
        result.forEach(this::makeData);
        return result;
    }

    public Map<String, Long> like(Long filmId, Long userId) { // переделать
        Map<String, Long> result = validateFilmDataRequest(filmId, userId);
        if (!result.isEmpty()) {
            return result;
        }
        filmStorage.findById(filmId).get().getLikes().add(userId);
        userStorage.findById(userId).get().getLikeFilms().add(filmId);
        return null;
    }

    public Map<String, Long> dislike(Long filmId, Long userId) { // переделать
        Map<String, Long> result = validateFilmDataRequest(filmId, userId);
        if (!result.isEmpty()) {
            return result;
        }
        filmStorage.findById(filmId).get().getLikes().remove(userId);
        userStorage.findById(userId).get().getLikeFilms().remove(filmId);
        return null;
    }

    private Map<String, Long> validateFilmDataRequest(Long filmId, Long userId) {
        Map<String, Long> result = new HashMap<>();
        if (filmStorage.findById(filmId).isEmpty()) {
            result.put("filmId", filmId);
        }
        if (userStorage.findById(userId).isEmpty()) {
            result.put("userId", userId);
        }
        return result;
    }

    private void makeData(Film film) {
        film.setMpa(mpaStorage.findById(film.getMpa().getId()).get());
        film.setGenres(new HashSet<>(genreStorage.findAllByFilmId(film.getId())));
    }
}
