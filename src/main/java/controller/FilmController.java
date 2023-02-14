package controller;

import exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import model.Film;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public Film add(@RequestBody Film film) {
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
    public Film update(@RequestBody Film film) {
        try {
            validateFilmData(film);
        } catch (ValidationException exc){
            log.info(exc.getMessage());
            throw new ResponseStatusException(BAD_REQUEST, "Ошибка валидации фильма.");
        }
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм успешно обновлен" + film);
            return film;
        }
        log.info("Фильма id = " + film.getId() + " нет в базе.");
        throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
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
        if (film.getName() == null || film.getDescription() == null ||
                film.getReleaseDate() == null || film.getDuration() == null) {
            throw new ValidationException("Ошибка валидации фильма: " + film + ". " + "Ошибки: Не указаны требуемые поля.");
        }

        StringBuilder sb = new StringBuilder();
        boolean notValidated = false;

        if (film.getName().isBlank()) {
            sb.append("Пустое название фильма. ");
            notValidated = true;
        }
        if (film.getDescription().length() >= 200) {
            sb.append("Длина описания больше 200 символов. ");
            notValidated = true;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            sb.append("Дата предшествует первому кинопоказу. ");
            notValidated = true;
        }
        if (film.getDuration() < 0){
            sb.append("Отрицательная продолжительность фильма. ");
            notValidated = true;
        }
        if (notValidated){
            throw new ValidationException("Ошибка валидации фильма: " + film + ". " + "Ошибки: " + sb);
        }
    }
}
