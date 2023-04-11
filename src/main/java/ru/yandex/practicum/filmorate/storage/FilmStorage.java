package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Collection<Film> findPopularFilms(int size);

    Collection<Film> findCommonFilms(Long userId, Long friendId);

    Optional<Film> findById(Long filmId);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    Collection<Film> findFilmsDirectorByYear(Long directorId);

    Collection<Film> findFilmsDirectorByLikes(Long directorId);
}
