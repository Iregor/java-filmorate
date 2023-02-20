package controller;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.Film;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.*;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private HashMap <Integer, Film> films = new HashMap<>();
    int currentId = 0;

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        grantId(film);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: " + film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.info("Фильма id = " + film.getId() + " нет в базе.");
            throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
        }
        films.put(film.getId(), film);
        log.info("Фильм успешно обновлен: " + film);
        return film;
    }

    @GetMapping
    public List<Film> films(){
        log.info("Список фильмов отправлен.");
        return new ArrayList<>(films.values());
    }

    public int getCurrentId(){
        return currentId;
    }

    private void grantId(Film film){
        film.setId(++currentId);
    }
}