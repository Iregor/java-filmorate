package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Collection<Genre> findAllByFilmId(Long filmId);

    Optional<Genre> findById(Long id);

    Genre create(Genre genre);

    Genre update(Genre genre);

    void delFilmGenre(Long filmId, Long genreId);
}
