package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> findAll();

    Optional<Genre> findById(Long id);

    Optional<Genre> create(Genre genre);

    Optional<Genre> update(Genre genre);
}
