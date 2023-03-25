package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> readAll();

    Collection<Genre> readRowByFilmId(Long filmId);

    Optional<Genre> readById(Long id);

    Genre writeRow(Genre genre);

    Genre updateRow(Genre genre);
}
