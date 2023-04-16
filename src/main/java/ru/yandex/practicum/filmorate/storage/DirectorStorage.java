package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> findById(Long directorId);

    Optional<Director> addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void removeDirector(Long directorId);

    Collection<Director> findByFilmId(Long filmId);

    Map<Long, Set<Director>> findByFilms(Set<Long> filmIds);

    void addInFilm(Long filmId, Long directorId);

    void removeFromFilm(Long filmId, Long directorId);
}
