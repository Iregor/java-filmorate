package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Collection<Film> findPopularFilms(int size);

    Collection<Film> findFilmsByParams(String name, LocalDate after, LocalDate before);

    Collection<Long> getFilmLikes(Long filmId);

    Optional<Film> findById(Long id);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    void deleteFilmGenres(Long filmId, Long genreId);
}
