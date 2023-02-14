package ru.yandex.practicum.filmorate;

import controller.FilmController;
import model.Film;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FilmControllerValidationTest {

    FilmController filmController;
    Film film1, film2, film3, film4, film5, film6, film7, film8, film9, film10;
    ResponseStatusException valExc;

    @BeforeEach
    public void createTestInitialData() {
        filmController = new FilmController();
        film1 = new Film(null, "abc", LocalDate.of(1991, 1,1), 100);
        film2 = new Film("abc", null, LocalDate.of(1991, 1,1), 100);
        film3 = new Film("abc", "abc", null, 100);
        film4 = new Film("abc", "abc", LocalDate.of(1991, 1,1), null);
        film5 = new Film(" ", "abc", LocalDate.of(1991, 1,1), 100);

        char[] charArr = new char[201];
        Arrays.fill(charArr, 'a');
        film6 = new Film("abc", new String(charArr), LocalDate.of(1991, 1,1), 100);

        film7 = new Film("abc", "abc", LocalDate.of(1800, 1,1), 100);
        film8 = new Film("abc", "abc", LocalDate.of(1991, 1,1), -100);
        film9 = new Film("abc", "abc", LocalDate.of(1991, 1,1), 100);
    }

    @Test
    public void nameValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film1));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film5));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void descriptionValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film2));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film6));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void releaseDateValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film3));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film7));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void durationDateValidationTest(){
        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film4));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));

        valExc = assertThrows(ResponseStatusException.class, () -> filmController.add(film8));
        assertTrue(valExc.getMessage().contains("Ошибка валидации фильма."));
    }

    @Test
    public void correctFilmValidationTest(){
        filmController.add(film9);
        assertTrue(filmController.films().contains(film9));
    }
}
