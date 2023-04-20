package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

public interface GenreStorage {

    List<Genre> findAll();

    List<Genre> findByFilmId(Long filmId);

    Map<Long, Set<Genre>> findByFilms(Set<Long> filmIds);

    Optional<Genre> findById(Long genreId);

    Optional<Genre> create(Genre genre);

    Optional<Genre> update(Genre genre);

    void add(Long filmId, Long genreId);

    void remove(Long filmId, Long genreId);
}
