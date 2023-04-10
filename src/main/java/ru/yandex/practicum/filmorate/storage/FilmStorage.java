package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> findAll();

    Collection<Film> findPopularFilms(int size);

    Collection<Film> findPopularFilmsByGenreId(int size, Long genreId);

    Collection<Film> findPopularFilmsByYear(int size, String year);

    Collection<Film> findPopularFilmsByGenreIdAndYear(int size, Long genreId, String year);

    Collection<Film> searchFilms(String subString, List<String> by);

    Collection<Film> findCommonFilms(Long userId, Long friendId);

    Optional<Film> findById(Long filmId);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    void remove(Long filmId);

    Collection<Film> findFilmsDirectorByYear(Long directorId);

    Collection<Film> findFilmsDirectorByLikes(Long directorId);
}
