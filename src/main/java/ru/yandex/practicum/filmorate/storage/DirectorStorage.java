package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;

public interface DirectorStorage {
    List<Director> findAll();

    Optional<Director> findById(Long directorId);

    Optional<Director> addDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void removeDirector(Long directorId);

    List<Director> findByFilmId(Long filmId);

    Map<Long, Set<Director>> findByFilms(Set<Long> filmIds);

    void addInFilm(Long filmId, Long directorId);

    void removeFromFilm(Long filmId, Long directorId);
}
