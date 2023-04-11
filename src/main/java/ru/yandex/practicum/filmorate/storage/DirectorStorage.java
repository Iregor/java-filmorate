package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> findById(Long id);

    Optional<Director> createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void deleteDirector(Long id);

    Collection<Director> findByFilmId(Long filmId);

    Map<Long, Set<Director>> findByFilms(Set<Long> filmIds);

    void add(Long filmId, Long directorId);

    void remove(Long filmId, Long directorId);
}
