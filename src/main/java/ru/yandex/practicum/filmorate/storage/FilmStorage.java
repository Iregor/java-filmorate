package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    List<Film> findAll();

    List<Film> findPopularFilms(int size);

    List<Film> findPopularFilmsByGenreId(int size, Long genreId);

    List<Film> findPopularFilmsByYear(int size, String year);

    List<Film> findPopularFilmsByGenreIdAndYear(int size, Long genreId, String year);

    List<Film> searchFilmsByTitle(String subString);

    List<Film> searchFilmsByDirector(String subString);

    List<Film> findCommonFilms(Long userId, Long friendId);

    Optional<Film> findById(Long filmId);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    void remove(Long filmId);

    List<Film> findFilmsDirectorByYear(Long directorId);

    List<Film> findFilmsDirectorByLikes(Long directorId);
}
