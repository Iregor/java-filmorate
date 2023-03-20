package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
        return filmStorage.findAll(name, after, before);
    }

    public Optional<Film> findById(Long id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) { //// добить оснастку или перенести в другой метод
        Film result = filmStorage.create(film);
        result.setMpa(mpaStorage.findById(result.getMpa().getId()).get());
        return result;
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Map<String, Long> like(Long filmId, Long userId) {
        Map<String, Long> result = validateFilmDataRequest(filmId, userId);
        if (!result.isEmpty()) {
            return result;
        }
        filmStorage.findById(filmId).get().getLikes().add(userId);
        userStorage.findById(userId).get().getLikeFilms().add(filmId);
        return null;
    }

    public Map<String, Long> dislike(Long filmId, Long userId) {
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

    public Collection<Film> getMostPopularFilms(Integer size) {
        return filmStorage
                .findAll(null, OLDEST_DATE_RELEASE, LocalDate.now())
                .stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(size)
                .collect(Collectors.toList());
    }
}
