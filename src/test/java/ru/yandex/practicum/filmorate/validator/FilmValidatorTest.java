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
    private HashMap<Long, Film> films;

    @BeforeEach
    void beforeEach() {
        films = new HashMap<>();
    }

    @Test
    void validateFilm_throwValidateException_nullName() {
        Film film = new Film(null, "Описание фильма 1",
                "1994-11-25", 140, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Названия фильма не может быть пустым. ");
    }

    @Test
    void validateFilm_throwValidateException_blankName() {
        Film film = new Film("    ", "Описание фильма 1",
                "1994-11-25", 140, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Названия фильма не может быть пустым. ");
    }

    @Test
    void validateFilm_throwValidateException_longDescription() {
        Film film = new Film("Фильм 1", "Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1",
                "1994-11-25", 140, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Описание фильма не может превышать " + MAX_LENGTH_DESCRIPTION + " знаков. ");
    }

    @Test
    void validateFilm_throwValidateException_oldestDate() {
        Film film = new Film("Фильм 1", "Описание фильма 1",
                "1884-11-25", 140,  "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Дата релиза не может быть раньше "
                        + OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        + "г. ");
    }

    @Test
    void validateFilm_throwValidateException_zeroDuration() {
        Film film = new Film("Фильм 1", "Описание фильма 1",
                "2004-11-25", 0, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_throwValidateException_negativeDuration() {
        Film film = new Film("Фильм 1", "Описание фильма 1",
                "2004-11-25", -100, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(),
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_throwManyMessageOfValidateException_wrongFilm() {
        Film film = new Film("   ", "Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1 " +
                "Описание фильма 1 Описание фильма 1 Описание фильма 1",
                "1224-11-25", -100, "NC-17");
        final ValidateException exception = assertThrows(
                ValidateException.class,
                () -> FilmValidator.validate(film)
        );
        assertEquals(exception.getMessage(), "Названия фильма не может быть пустым. " +
                "Описание фильма не может превышать " + MAX_LENGTH_DESCRIPTION + " знаков. " +
                "Дата релиза не может быть раньше "
                + OLDEST_DATE_RELEASE.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "г. " +
                "Продолжительность фильма должна быть больше " + MIN_DURATION_OF_FILM + ". ");
    }

    @Test
    void validateFilm_addingFilm_correctlyFilm() {
        Film film = new Film("Фильм 1", "Описание фильма 1",
                "2004-11-25", 140, "NC-17");
        FilmValidator.validate(film);
        film.setId(25L);
        films.put(film.getId(), film);
        assertEquals(film, films.get(25L));
    }
}