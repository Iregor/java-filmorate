package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

public interface FilmStorage {
    Film createFilm(Film film);

    Film findFilmById(Integer id);

    Film updateFilm(Film film);

    Film deleteFilmById(Integer id);

    Collection<Film> findAllFilms();

    Set<Integer> findAllFilmsIds();

    void createFilmLikeSet(Integer id);

    Set<Integer> findFilmLikeSet(Integer id);

    Set<Integer> updateFilmLikeSet(Integer id, Set<Integer> LikeSet);

    Set<Integer> deleteFilmLikeSet(Integer id);

    Map<Integer, Set<Integer>> findFilmsLikes();

    Integer findCurrentId();

    Integer updateCurrentId(Integer id);
}
