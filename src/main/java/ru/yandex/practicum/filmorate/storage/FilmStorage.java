package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll(String name, LocalDate after, LocalDate before);

    Film findById(Long id);

    Film create(Film film);

    Film update(Film film);
}
