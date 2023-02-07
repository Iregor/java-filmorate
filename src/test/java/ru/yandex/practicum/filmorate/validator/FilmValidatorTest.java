package ru.yandex.practicum.filmorate.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.filmorate.validator.FilmValidator.*;

class FilmValidatorTest {
    private HashMap<Integer, Film> films;

    @BeforeEach
    void beforeEach() {
        films = new HashMap<>();
    }

    @Test
    void validateFilm_returnException_nullName() {
        Film film = new Film(null, "Описание фильма 1", "1994-11-25", 140);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Названия фильма не может быть пустым. ");
    }

    @Test
    void validateFilm_returnException_blankName() {
        Film film = new Film("    ", "Описание фильма 1", "1994-11-25", 140);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Названия фильма не может быть пустым. ");
    }

    @Test
    void validateFilm_returnException_longDescription() {
        Film film = new Film("Фильм 1", "Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1", "1994-11-25", 140);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Описание фильма не может превышать " + MAX_LENGTH_DESCRIPTION + " знаков. ");
    }

    @Test
    void validateFilm_returnException_oldestDate() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "1884-11-25", 140);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Дата релиза не может быть раньше "
                        + OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        + "г. ");
    }

    @Test
    void validateFilm_returnException_zeroDuration() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 0);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_returnException_negativeDuration() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", -100);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(),
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_returnException_filmWithEqualsHash() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        film.setId(25);
        films.put(film.getId(), film);
        Film newFilm = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(newFilm, films)
        );
        assertEquals(exception.getMessage(),
                "Фильм уже числится в базе под идентификатором " + film.getId() + ". ");
    }

    @Test
    void validateFilm_returnException_wrongFilmId() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        film.setId(25);
        films.put(film.getId(), film);
        Film newFilm = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        newFilm.setId(26);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(newFilm, films)
        );
        assertEquals(exception.getMessage(),
                "Фильм с таким идентификатором отсутствует. ");
    }

    @Test
    void validateFilm_returnManyMessageOfException_wrongFilm() {
        Film film = new Film("   ", "Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1", "1224-11-25", -100);
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film, films)
        );
        assertEquals(exception.getMessage(), "Названия фильма не может быть пустым. " +
                "Описание фильма не может превышать " + MAX_LENGTH_DESCRIPTION + " знаков. " +
                "Дата релиза не может быть раньше "
                + OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "г. " +
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_addingFilm_correctlyFilm() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        if(FilmValidator.validate(film, films)) {
            film.setId(25);
            films.put(film.getId(), film);
        }
        assertEquals(film, films.get(25));
    }

    @Test
    void validateFilm_updateFilm_correctlyFilm() {
        Film film = new Film("Фильм 1", "Описание фильма 1", "2004-11-25", 140);
        if(FilmValidator.validate(film, films)) {
            film.setId(25);
            films.put(film.getId(), film);
        }

        String newName = "Обновленный фильм";
        String newDescription = "Режиссерская версия";

        film.setName(newName);
        film.setDescription(newDescription);

        if(FilmValidator.validate(film, films)) {
            films.put(film.getId(), film);
        }
        assertEquals(newName, films.get(25).getName());
        assertEquals(newDescription, films.get(25).getDescription());
    }
}