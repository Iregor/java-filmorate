package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Collection<Film> getPopularFilms(int size);

    Optional<Film> findById(Long id);

    Film create(Film film);

    Film update(Film film);

    void deleteFilmGenres(Long filmId, Long genreId);

    Collection<Long> getFilmLikes(Long filmId);
}
