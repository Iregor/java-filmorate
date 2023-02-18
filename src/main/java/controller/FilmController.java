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
        try {
            validateFilmData(film);
        } catch (ValidationException exc){
            log.info(exc.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, "Ошибка валидации фильма.");
        }
        grantId(film);
        films.put(film.getId(), film);
        log.info("Фильм успешно добавлен: " + film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        try {
            validateFilmData(film);
        } catch (ValidationException exc){
            log.info(exc.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, "Ошибка валидации фильма.");
        }
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

    private void validateFilmData(Film film) throws ValidationException {
 /*       if (film.getName() == null || film.getDescription() == null ||
                film.getReleaseDate() == null || film.getDuration() == null) {
            throw new ValidationException("Не указаны требуемые поля: " + film);
        }
        if (film.getName().isBlank()) {
            throw new ValidationException("Указано пустое название: " + film);
        }
        if (film.getDescription().length() >= 200) {
            throw new ValidationException("Длина описания больше 200 символов: " + film);
        }*/
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата предшествует первому кинопоказу: " + film);
        }
/*        if (film.getDuration() < 0){
            throw new ValidationException("Отрицательная продолжительность фильма: " + film);
        }*/
    }
}