package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage{
    private HashMap<Integer, Film> films = new HashMap<>();
    private HashMap<Integer, Set<Integer>> filmsLikes = new HashMap<>();
    private int currentId = 0;

    public Film createFilm (Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public Film findFilmById (Integer id) {
        return films.get(id);
    }

    public Film updateFilm (Film film) {
        films.put(film.getId(), film);
        return film;
    }

    public Film deleteFilmById (Integer id) {
        return films.remove(id);
    }

    public Collection<Film> findAllFilms(){
        return films.values();
    }

    public Set<Integer> findAllFilmsIds(){
        return films.keySet();
    }

    public void createFilmLikeSet(Integer id) {
        filmsLikes.put(id, new HashSet<>());
    }

    public Set<Integer> findFilmLikeSet(Integer id) {
        return filmsLikes.get(id);
    }

    public Set<Integer> updateFilmLikeSet(Integer id, Set<Integer> LikeSet) {
        filmsLikes.put(id, LikeSet);
        return LikeSet;
    }

    public Set<Integer> deleteFilmLikeSet(Integer id) {
        return filmsLikes.remove(id);
    }

    public HashMap<Integer, Set<Integer>> findFilmsLikes() {
        return filmsLikes;
    }

    public Integer findCurrentId(){
        return currentId;
    }

    public Integer updateCurrentId(Integer id) {
        return currentId = id;
    }
}
