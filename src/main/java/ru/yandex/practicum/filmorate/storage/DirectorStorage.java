package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Collection<Director> findAll();

    Optional<Director> findById(Long id);

    Optional<Director> createDirector(Director director);

    Optional<Director> updateDirector(Director director);

    void deleteDirector(Long id);
}
