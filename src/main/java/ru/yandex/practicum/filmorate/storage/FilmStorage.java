package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;

public interface FilmStorage {
    Collection<Film> findAll();

    Collection<Film> filterFilms(String name, LocalDate after, LocalDate before);

    Film findFilmById(Long id);

    Film create(Film film);

    Film update(Film film);
}
