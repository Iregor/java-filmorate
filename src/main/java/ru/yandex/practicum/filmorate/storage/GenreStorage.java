package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    Collection<Genre> findAll();

    Collection<Genre> findByFilmId(Long filmId);

    Map<Long, Set<Genre>> findByFilms(Set<Long> filmIds);

    Optional<Genre> findById(Long genreId);

    Optional<Genre> create(Genre genre);

    Optional<Genre> update(Genre genre);

    void add(Long filmId, Long genreId);

    void remove(Long filmId, Long genreId);
}
