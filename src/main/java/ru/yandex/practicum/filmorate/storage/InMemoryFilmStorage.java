package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long filmId = 1;
    private final HashMap<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Collection<Film> filterFilms(String name, LocalDate after, LocalDate before) {
        return findAll()
                .stream()
                .filter(film -> {
                    if (name == null) {
                        return true;
                    }
                    return film.getName().equals(name);
                })
                .filter(film -> (film.getReleaseDate().isAfter(after) && (film.getReleaseDate().isBefore(before))))
                .collect(Collectors.toList());
    }

    @Override
    public Film findFilmById(Long id) {
        if(!films.containsKey(id)) {
            log.warn(String.format("Film %d is not found.", filmId));
            throw new NullPointerException(String.format("Film %d is not found.", filmId));
        }
        return films.get(id);
    }

    @Override
    public Film create(Film film) {
        FilmValidator.validate(film);

        for (Film filmFromBase : films.values()) {
            if(filmFromBase.getName().equals(film.getName())
                    && filmFromBase.getReleaseDate().equals(film.getReleaseDate())) {
                log.info("Movie added earlier at ID {}.", filmFromBase.getId());
                return null;
            }
        }

        film.setId(filmId++);
        films.put(film.getId(), film);
        log.info("Movie \"{}\" added. The database contains {} film(s).",film.getName(), films.size());
        return film;
    }

    @Override
    public Film update(Film film) {
        FilmValidator.validate(film);

        if(film.getId() != null && !films.containsKey(film.getId())) {
            log.info("Movie ID {} missing. ", film.getId());
            throw new NullPointerException("Movie ID " + film.getId() + " missing.");
        }

        films.put(film.getId(), film);
        log.info("Movie ID {} updated.", film.getId());
        return film;
    }
}
